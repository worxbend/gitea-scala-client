package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.http.GiteaRequests
import io.worxbend.gitea4s.observability.{GiteaObserver, RequestEvent}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub, StubBody}
import sttp.model.{Header, StatusCode}
import zio.test.*
import zio.{Chunk, Duration, Ref, Task, ZIO}

import java.time.Instant

/** Retry timing, the total time budget, and what an observer is told.
  *
  * Note which clock each case runs on. `Retry-After` states a *delay*, and
  * turning that into the absolute instant `GiteaError.RateLimited` carries has
  * to be anchored to the wall clock, so those cases run on the live clock with
  * a zero delay. `x-ratelimit-reset` is already absolute, so the cases that
  * need to observe a long wait — without waiting it out — state it against the
  * test clock.
  */
object GiteaRequestExecutorSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.anonymous(uri"https://gitea.example")
  private val user = """{"id":1,"login":"alice"}"""

  private def respondWith(responses: Response[StubBody]*): Backend[Task] =
    // BackendStub answers every request from one rule, so walking a sequence of
    // distinct answers needs a counter the rule closes over.
    val issued = new java.util.concurrent.atomic.AtomicInteger(0)
    BackendStub[Task](new RIOMonadAsyncError[Any]).whenAnyRequest.thenRespondF { _ =>
      ZIO.succeed(responses(math.min(issued.getAndIncrement(), responses.size - 1)))
    }

  private def rateLimited(headers: (String, String)*): Response[StubBody] =
    ResponseStub.adjust("", StatusCode.TooManyRequests, headers.map(Header(_, _)))

  private def ok(body: String): Response[StubBody] =
    ResponseStub.adjust(body, StatusCode.Ok)

  private def recording(ref: Ref[Chunk[RequestEvent]]): GiteaObserver =
    GiteaObserver.fromFunction(event => ref.update(_ :+ event))

  def spec: Spec[Any, Any] = suite("GiteaRequestExecutor")(
    suite("retry delays")(
      test("caps the wait when the server names a reset instant in the far future") {
        // A proxy that reports the reset in milliseconds sends a value that is
        // a valid epoch *second* thousands of years out. Waiting for it is
        // indistinguishable from a hang, so the wait is capped at 60 seconds.
        // Observed rather than waited out: at 59 seconds the retry has not
        // happened, two seconds later it has.
        val backend = respondWith(rateLimited("x-ratelimit-reset" -> "99999999999"), ok(user))

        for
          _ <- TestClock.setTime(Instant.parse("2030-01-01T00:00:00Z"))
          fiber <- GiteaRequestExecutor(backend, maxRetries = 1).send(GiteaRequests.currentUser(config)).fork
          _ <- TestClock.adjust(Duration.fromSeconds(59))
          early <- fiber.poll
          _ <- TestClock.adjust(Duration.fromSeconds(2))
          result <- fiber.join
        yield assertTrue(early.isEmpty, result.login.contains("alice"))
      },
      test("waits exactly as long as the server asked when that is under the cap") {
        val now = Instant.parse("2030-01-01T00:00:00Z")
        val backend =
          respondWith(rateLimited("x-ratelimit-reset" -> now.plusSeconds(5).getEpochSecond.toString), ok(user))

        for
          _ <- TestClock.setTime(now)
          fiber <- GiteaRequestExecutor(backend, maxRetries = 1).send(GiteaRequests.currentUser(config)).fork
          _ <- TestClock.adjust(Duration.fromSeconds(4))
          early <- fiber.poll
          _ <- TestClock.adjust(Duration.fromSeconds(2))
          result <- fiber.join
        yield assertTrue(early.isEmpty, result.login.contains("alice"))
      },
      test("honours Retry-After and then succeeds") {
        val backend = respondWith(rateLimited("retry-after" -> "0"), ok(user))

        GiteaRequestExecutor(backend, maxRetries = 2)
          .send(GiteaRequests.currentUser(config))
          .map(u => assertTrue(u.login.contains("alice")))
      } @@ TestAspect.withLiveClock,
      test("gives up once the retry budget is spent instead of retrying forever") {
        val backend = respondWith(rateLimited("retry-after" -> "0"))

        GiteaRequestExecutor(backend, maxRetries = 2)
          .send(GiteaRequests.currentUser(config))
          .either
          .map(result => assertTrue(result.isLeft))
      } @@ TestAspect.withLiveClock,
      test("does not retry an error that cannot be retried") {
        val backend = respondWith(ResponseStub.adjust("""{"message":"nope"}""", StatusCode.NotFound))

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 3, recording(ref))
            .send(GiteaRequests.currentUser(config))
            .either
          events <- ref.get
        yield assertTrue(events.size == 1)
      },
      test("never retries a write, even when the failure would otherwise qualify") {
        // Retryability is decided from the HTTP method, so a POST that comes
        // back rate-limited has to surface rather than be sent a second time.
        val backend = respondWith(rateLimited("retry-after" -> "0"))
        val request =
          GiteaRequests.createIssue(config, "owner", "repo", io.worxbend.gitea4s.model.CreateIssue(title = "t"))

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 3, recording(ref)).send(request).either
          events <- ref.get
        yield assertTrue(!request.retryable, events.size == 1)
      }
    )
  )
