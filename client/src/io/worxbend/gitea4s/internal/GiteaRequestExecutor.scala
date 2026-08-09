package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaRequest
import io.worxbend.gitea4s.observability.{GiteaObserver, RequestEvent, RequestOutcome}
import sttp.client4.Backend
import zio.{Clock, Exit, IO, Random, Task, UIO, ZIO}

import java.time.{Duration, Instant}

final class GiteaRequestExecutor(
    backend: Backend[Task],
    maxRetries: Int,
    observer: GiteaObserver = GiteaObserver.noop
):
  def send[A](request: GiteaRequest[A]): IO[GiteaError, A] =
    val retries = math.max(0, maxRetries)
    val core =
      if request.retryable && retries > 0 then sendWithRetries(request, attempt = 1, remainingRetries = retries)
      else sendOnce(request)
    if observer eq GiteaObserver.noop then core
    else observed(request, core)

  private def observed[A](request: GiteaRequest[A], core: IO[GiteaError, A]): IO[GiteaError, A] =
    for
      start <- Clock.nanoTime
      exit <- core.exit
      stop <- Clock.nanoTime
      outcome = exit match
        case Exit.Success(_) => Some(RequestOutcome.Success)
        case Exit.Failure(cause) => cause.failureOption.map(RequestOutcome.Failure.apply)
      _ <- ZIO.foreachDiscard(outcome) { result =>
        observer
          .onComplete(RequestEvent(request.endpoint, Duration.ofNanos(stop - start), result))
          .catchAllCause(_ => ZIO.unit)
      }
      value <- exit
    yield value

  private def sendWithRetries[A](
      request: GiteaRequest[A],
      attempt: Int,
      remainingRetries: Int
  ): IO[GiteaError, A] =
    sendOnce(request).catchAll { error =>
      if remainingRetries <= 0 then ZIO.fail(error)
      else
        retryDelay(error, attempt).flatMap {
          case Some(delay) =>
            ZIO.sleep(delay) *> sendWithRetries(request, attempt + 1, remainingRetries - 1)
          case None =>
            ZIO.fail(error)
        }
    }

  private def sendOnce[A](request: GiteaRequest[A]): IO[GiteaError, A] =
    request.request
      .send(backend)
      .mapError(GiteaError.TransportError.apply)
      .flatMap(response => ZIO.fromEither(request.decode(response)))

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
    * silently overriding the caller's own timeout. A proxy that reports the
    * reset in milliseconds sends a value that is a valid epoch second thousands
    * of years out, and waiting for it is indistinguishable from a hang — but
    * even a well-formed hour-long reset window would otherwise block a client
    * configured with a 30-second timeout for an hour.
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
