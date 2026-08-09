package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaRequest
import io.worxbend.gitea4s.observability.{GiteaObserver, RequestEvent, RequestOutcome}
import sttp.client4.Backend
import zio.{Clock, Exit, IO, Random, Task, UIO, ZIO}

import java.time.{Duration, Instant}
import java.util.concurrent.TimeoutException

final class GiteaRequestExecutor(
    backend: Backend[Task],
    maxRetries: Int,
    observer: GiteaObserver = GiteaObserver.noop,
    totalTimeout: Duration = GiteaRequestExecutor.defaultTotalTimeout
):
  def send[A](request: GiteaRequest[A]): IO[GiteaError, A] =
    val retries = math.max(0, maxRetries)
    if observer eq GiteaObserver.noop then core(request, retries, null)
    else
      // The telemetry holder is allocated only when somebody is listening, so
      // the default configuration keeps the zero-overhead path it documents.
      ZIO.succeed(new GiteaRequestExecutor.Telemetry).flatMap { telemetry =>
        observed(request, telemetry, core(request, retries, telemetry))
      }

  private def core[A](
      request: GiteaRequest[A],
      retries: Int,
      telemetry: GiteaRequestExecutor.Telemetry
  ): IO[GiteaError, A] =
    if request.retryable && retries > 0 then
      sendWithRetries(request, attempt = 1, remainingRetries = retries, telemetry)
    else sendOnce(request, telemetry)

  private def observed[A](
      request: GiteaRequest[A],
      telemetry: GiteaRequestExecutor.Telemetry,
      core: IO[GiteaError, A]
  ): IO[GiteaError, A] =
    for
      start <- Clock.nanoTime
      exit <- core.exit
      stop <- Clock.nanoTime
      outcome = exit match
        case Exit.Success(_) => Some(RequestOutcome.Success)
        case Exit.Failure(cause) =>
          // A defect used to produce no event at all: `failureOption` finds
          // only typed failures, so the observer was skipped and the operator's
          // success and failure counts stopped summing to the request count.
          // Interruption still yields `None` on purpose — the fiber is going
          // away and the runtime skips this handler entirely.
          cause.failureOption
            .map(RequestOutcome.Failure.apply)
            .orElse(cause.dieOption.map(defect => RequestOutcome.Failure(GiteaError.TransportError(defect))))
      _ <- ZIO.foreachDiscard(outcome) { result =>
        observer
          .onComplete(
            RequestEvent(
              endpoint = request.endpoint,
              duration = Duration.ofNanos(stop - start),
              outcome = result,
              status = telemetry.lastStatus,
              attempts = telemetry.attempts
            )
          )
          .catchAllCause(_ => ZIO.unit)
      }
      value <- exit
    yield value

  private def sendWithRetries[A](
      request: GiteaRequest[A],
      attempt: Int,
      remainingRetries: Int,
      telemetry: GiteaRequestExecutor.Telemetry
  ): IO[GiteaError, A] =
    sendOnce(request, telemetry).catchAll { error =>
      if remainingRetries <= 0 then ZIO.fail(error)
      else
        retryDelay(error, attempt).flatMap {
          case Some(delay) =>
            ZIO.sleep(delay) *> sendWithRetries(request, attempt + 1, remainingRetries - 1, telemetry)
          case None =>
            ZIO.fail(error)
        }
    }

  private def sendOnce[A](
      request: GiteaRequest[A],
      telemetry: GiteaRequestExecutor.Telemetry
  ): IO[GiteaError, A] =
    request.request
      .send(backend)
      .mapError(GiteaError.TransportError.apply)
      .flatMap { response =>
        if telemetry ne null then telemetry.record(response.code.code)
        ZIO.fromEither(request.decode(response))
      }
      .timeoutFail(GiteaRequestExecutor.timedOut(totalTimeout))(totalTimeout)

  private def retryDelay(error: GiteaError, attempt: Int): UIO[Option[Duration]] =
    error match
      case GiteaError.TransportError(_) =>
        jitteredBackoff(attempt).map(Some(_))
      case GiteaError.RateLimited(Some(resetAt), _) =>
        Clock.instant.map(now => Some(boundedDelay(now, resetAt)))
      case GiteaError.RateLimited(None, _) =>
        jitteredBackoff(attempt).map(Some(_))
      case GiteaError.ServerError(status, _) if retryableServerStatuses.contains(status) =>
        jitteredBackoff(attempt).map(Some(_))
      case _ =>
        ZIO.succeed(None)

  private def jitteredBackoff(attempt: Int): UIO[Duration] =
    val multiplier = 1L << math.min(math.max(attempt - 1, 0), 10)
    val baseMillis = math.min(baseBackoff.toMillis * multiplier, maxBackoff.toMillis)

    Random.nextDoubleBetween(0.8, 1.2).map { jitter =>
      Duration.ofMillis(math.max(1L, math.round(baseMillis.toDouble * jitter)))
    }

  /** How long to wait before retrying a rate-limited request.
    *
    * Clamped at both ends. The lower clamp keeps an already-elapsed reset time
    * from producing a negative sleep; the upper one keeps a remote header from
    * silently overriding the caller's own timeout. Even a well-formed
    * hour-long reset window would otherwise block a client configured with a
    * 30-second timeout for an hour.
    */
  private def boundedDelay(now: Instant, retryAt: Instant): Duration =
    val delay = Duration.between(now, retryAt)
    if delay.isNegative then Duration.ZERO
    else if delay.compareTo(maxRateLimitWait) > 0 then maxRateLimitWait
    else delay

  private val retryableServerStatuses: Set[Int] =
    Set(500, 502, 503, 504)

  private val baseBackoff: Duration =
    Duration.ofMillis(100)

  private val maxBackoff: Duration =
    Duration.ofSeconds(5)

  private val maxRateLimitWait: Duration =
    Duration.ofSeconds(60)

object GiteaRequestExecutor:
  /** Ceiling on one attempt, end to end.
    *
    * `GiteaConfig.timeout` reaches the JDK as `HttpRequest.timeout`, which the
    * client cancels the moment response *headers* arrive; the body that
    * follows is untimed. A server that answers `200 OK` immediately and then
    * dribbles the body one byte at a time therefore hangs the call forever.
    * This bounds the whole attempt instead.
    *
    * It is deliberately far larger than a typical `GiteaConfig.timeout`: this
    * is a backstop against a stalled connection, not a latency target, and a
    * legitimately large `repos.archive` fetch must still be able to finish.
    */
  val defaultTotalTimeout: Duration = Duration.ofMinutes(5)

  private def timedOut(after: Duration): GiteaError =
    GiteaError.TransportError(new TimeoutException(s"Gitea request exceeded the total time budget of $after"))

  /** Per-call scratch space for the facts an observer wants but the result type
    * does not carry: how many HTTP requests were issued, and what the last one
    * answered.
    *
    * Written from the fiber running the call and read once that call has
    * finished, so the two never race; `@volatile` covers the executor handing
    * the fiber between threads in between.
    */
  private final class Telemetry:
    @volatile private var status: Int = -1
    @volatile private var count: Int = 0

    def record(code: Int): Unit =
      status = code
      count += 1

    def lastStatus: Option[Int] = if status < 0 then None else Some(status)

    /** At least one, so an event always reports a real attempt count even when
      * the call failed before any response arrived.
      */
    def attempts: Int = math.max(1, count)
