package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaRequest
import sttp.client4.Backend
import zio.{Clock, IO, Random, Task, UIO, ZIO}

import java.time.{Duration, Instant}

final class GiteaRequestExecutor(backend: Backend[Task], maxRetries: Int):
  def send[A](request: GiteaRequest[A]): IO[GiteaError, A] =
    val retries = math.max(0, maxRetries)
    if request.retryable && retries > 0 then sendWithRetries(request, attempt = 1, remainingRetries = retries)
    else sendOnce(request)

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
    request.typedRequest
      .send(backend)
      .mapError(GiteaError.TransportError.apply)
      .flatMap(response => ZIO.fromEither(request.decodeTyped(response)))

  private def retryDelay(error: GiteaError, attempt: Int): UIO[Option[Duration]] =
    error match
      case GiteaError.TransportError(_) =>
        jitteredBackoff(attempt).map(Some(_))
      case GiteaError.RateLimited(Some(resetAt), _) =>
        Clock.instant.map(now => Some(nonNegativeDelay(now, resetAt)))
      case GiteaError.RateLimited(None, _) =>
        jitteredBackoff(attempt).map(Some(_))
      case GiteaError.ServerError(status, _) if retryableServerStatuses.contains(status) =>
        jitteredBackoff(attempt).map(Some(_))
      case _ =>
        ZIO.succeed(None)

  private def jitteredBackoff(attempt: Int): UIO[Duration] =
    val multiplier = 1L << math.min(attempt - 1, 10)
    val baseMillis = math.min(baseBackoff.toMillis * multiplier, maxBackoff.toMillis)

    Random.nextDoubleBetween(0.8, 1.2).map { jitter =>
      Duration.ofMillis(math.max(1L, math.round(baseMillis.toDouble * jitter)))
    }

  private def nonNegativeDelay(now: Instant, retryAt: Instant): Duration =
    val delay = Duration.between(now, retryAt)
    if delay.isNegative then Duration.ZERO else delay

  private val retryableServerStatuses: Set[Int] =
    Set(500, 502, 503, 504)

  private val baseBackoff: Duration =
    Duration.ofMillis(100)

  private val maxBackoff: Duration =
    Duration.ofSeconds(5)
