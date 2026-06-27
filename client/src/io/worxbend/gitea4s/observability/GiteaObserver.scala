package io.worxbend.gitea4s.observability

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaEndpoint
import zio.metrics.{Metric, MetricKeyType, MetricLabel}
import zio.{Duration, UIO, ZIO}

/** The outcome of a completed Gitea request. */
enum RequestOutcome:
  case Success
  case Failure(error: GiteaError)

/** A completed request, including the endpoint that was called, how long the
  * whole call took (including any retries), and whether it succeeded.
  */
final case class RequestEvent(
    endpoint: GiteaEndpoint,
    duration: Duration,
    outcome: RequestOutcome
):
  def succeeded: Boolean =
    outcome match
      case RequestOutcome.Success => true
      case RequestOutcome.Failure(_) => false

/** A hook invoked after every Gitea request completes.
  *
  * Observers are for cross-cutting concerns such as logging, metrics, and
  * tracing. They run on the request's fiber after the result is known and
  * cannot change the result; any failure they raise is swallowed so a faulty
  * observer never breaks a request. The default ([[GiteaObserver.noop]]) does
  * nothing and is fully short-circuited, so it adds no overhead.
  *
  * Set one through `GiteaConfig.copy(observer = ...)`, for example
  * `config.copy(observer = GiteaObserver.logging ++ GiteaObserver.metrics)`.
  */
trait GiteaObserver:
  def onComplete(event: RequestEvent): UIO[Unit]

  /** Run this observer and then `that` for each event. */
  final def ++(that: GiteaObserver): GiteaObserver =
    val self = this
    new GiteaObserver:
      def onComplete(event: RequestEvent): UIO[Unit] =
        self.onComplete(event) *> that.onComplete(event)

object GiteaObserver:
  /** Does nothing. The executor short-circuits this observer entirely. */
  val noop: GiteaObserver = new GiteaObserver:
    def onComplete(event: RequestEvent): UIO[Unit] = ZIO.unit

  /** Emits a structured log line per request through ZIO's logging system:
    * `debug` on success, `warning` on failure. Only the error type is logged,
    * never response bodies or credentials.
    */
  val logging: GiteaObserver = new GiteaObserver:
    def onComplete(event: RequestEvent): UIO[Unit] =
      val e = event.endpoint
      val ms = event.duration.toMillis
      event.outcome match
        case RequestOutcome.Success =>
          ZIO.logDebug(s"gitea4s ${e.method} ${e.path} (${e.operationId}) succeeded in ${ms}ms")
        case RequestOutcome.Failure(error) =>
          ZIO.logWarning(
            s"gitea4s ${e.method} ${e.path} (${e.operationId}) failed in ${ms}ms: ${error.getClass.getSimpleName}"
          )

  private val durationBoundaries: MetricKeyType.Histogram.Boundaries =
    MetricKeyType.Histogram.Boundaries.exponential(1.0, 2.0, 13)

  /** Records a `gitea4s_requests_total` counter and a
    * `gitea4s_request_duration_ms` histogram, each tagged with the HTTP method,
    * the Gitea operation id, and the outcome (`success`/`failure`).
    */
  val metrics: GiteaObserver = new GiteaObserver:
    def onComplete(event: RequestEvent): UIO[Unit] =
      val tags = Set(
        MetricLabel("method", event.endpoint.method),
        MetricLabel("operation", event.endpoint.operationId),
        MetricLabel("outcome", if event.succeeded then "success" else "failure")
      )
      Metric.counter("gitea4s_requests_total").tagged(tags).update(1L) *>
        Metric
          .histogram("gitea4s_request_duration_ms", durationBoundaries)
          .tagged(tags)
          .update(event.duration.toMillis.toDouble)

  /** Build an observer from a plain effectful callback. */
  def fromFunction(f: RequestEvent => UIO[Unit]): GiteaObserver =
    new GiteaObserver:
      def onComplete(event: RequestEvent): UIO[Unit] = f(event)
