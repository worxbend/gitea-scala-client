package io.worxbend.gitea4s.observability

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaEndpoint
import zio.metrics.{Metric, MetricKeyType, MetricLabel}
import zio.{Chunk, Duration, UIO, ZIO}

/** The outcome of a completed Gitea request. */
enum RequestOutcome:
  case Success
  case Failure(error: GiteaError)

/** A completed request.
  *
  * `duration` covers the whole call including any retries and the waiting
  * between them, so it is end-to-end latency as the caller experienced it, not
  * the duration of the final attempt.
  *
  * @param endpoint the Gitea operation that was called
  * @param duration how long the whole call took, retries and backoff included
  * @param outcome whether it succeeded, and the error if it did not
  * @param status the HTTP status of the last response, absent when no response
  *               arrived at all (a connection failure, or the time budget
  *               expiring)
  * @param attempts how many HTTP requests were actually issued; greater than
  *                 one means the call was retried
  */
final case class RequestEvent(
    endpoint: GiteaEndpoint,
    duration: Duration,
    outcome: RequestOutcome,
    status: Option[Int] = None,
    attempts: Int = 1
):
  def succeeded: Boolean =
    outcome match
      case RequestOutcome.Success => true
      case RequestOutcome.Failure(_) => false

  /** Whether this call was retried at least once. */
  def retried: Boolean = attempts > 1

/** A hook invoked after every Gitea request completes.
  *
  * Observers are for cross-cutting concerns such as logging, metrics, and
  * tracing. They run on the request's fiber after the result is known and
  * cannot change the result; any failure they raise is swallowed so a faulty
  * observer never breaks a request. The default ([[GiteaObserver.noop]]) does
  * nothing and is fully short-circuited, so it adds no overhead.
  *
  * Because an observer runs inline on the request's fiber, it must not block
  * and must not perform unbounded I/O: a slow observer delays a result that has
  * already been computed, and one that never completes withholds it entirely.
  * Hand work off instead — offer to a `Queue` the application drains elsewhere,
  * or use ZIO's own metrics, which are non-blocking.
  *
  * Set one through `GiteaConfig.withObserver(...)`, for example
  * `config.withObserver(GiteaObserver.logging ++ GiteaObserver.metrics)`.
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
      val retries = if event.retried then s" after ${event.attempts} attempts" else ""
      event.outcome match
        case RequestOutcome.Success =>
          ZIO.logDebug(s"gitea4s ${e.method} ${e.path} (${e.operationId}) succeeded in ${ms}ms$retries")
        case RequestOutcome.Failure(error) =>
          ZIO.logWarning(
            s"gitea4s ${e.method} ${e.path} (${e.operationId}) failed in ${ms}ms$retries: " +
              error.getClass.getSimpleName
          )

  /** Latency buckets, in milliseconds.
    *
    * The previous `exponential(1.0, 2.0, 13)` topped out at 4096 ms while the
    * default request timeout is 30 s and retry backoff can add several seconds
    * more, so every timed-out, retried or rate-limited call fell into the
    * overflow bucket and `histogram_quantile` reported p95 and p99 as 4096 ms
    * whether the truth was five seconds or thirty. Its bottom buckets — 1, 2, 4
    * and 8 ms — were below the floor of any real network round trip. These
    * span the range a Gitea call actually occupies.
    */
  private val durationBoundaries: MetricKeyType.Histogram.Boundaries =
    MetricKeyType.Histogram.Boundaries.fromChunk(
      Chunk(5.0, 10.0, 25.0, 50.0, 100.0, 250.0, 500.0, 1000.0, 2500.0, 5000.0, 10000.0, 30000.0, 60000.0)
    )

  /** Records a `gitea4s_requests_total` counter and a
    * `gitea4s_request_duration_ms` histogram, each tagged with the HTTP method,
    * the Gitea operation id, and the outcome (`success`/`failure`); plus a
    * `gitea4s_request_attempts_total` counter of HTTP requests actually issued,
    * which read against `gitea4s_requests_total` gives the retry amplification.
    *
    * Label cardinality is bounded: `operationId` and `method` both come from
    * the fixed `GiteaEndpoints` table, so no user-supplied value reaches a tag.
    */
  val metrics: GiteaObserver = new GiteaObserver:
    def onComplete(event: RequestEvent): UIO[Unit] =
      val tags = Set(
        MetricLabel("method", event.endpoint.method),
        MetricLabel("operation", event.endpoint.operationId),
        MetricLabel("outcome", if event.succeeded then "success" else "failure")
      )
      Metric.counter("gitea4s_requests_total").tagged(tags).update(1L) *>
        Metric.counter("gitea4s_request_attempts_total").tagged(tags).update(event.attempts.toLong) *>
        Metric
          .histogram("gitea4s_request_duration_ms", durationBoundaries)
          .tagged(tags)
          .update(event.duration.toMillis.toDouble)

  /** Build an observer from a plain effectful callback.
    *
    * The callback runs inline on the request's fiber, so it must not block. To
    * report somewhere slow, offer to a queue the application drains elsewhere:
    * {{{
    * GiteaObserver.fromFunction(event => events.offer(event).unit)
    * }}}
    */
  def fromFunction(f: RequestEvent => UIO[Unit]): GiteaObserver =
    new GiteaObserver:
      def onComplete(event: RequestEvent): UIO[Unit] = f(event)
