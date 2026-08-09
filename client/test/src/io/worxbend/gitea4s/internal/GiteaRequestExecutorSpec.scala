package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaEndpoints, GiteaRequest, GiteaRequests}
import io.worxbend.gitea4s.observability.{GiteaObserver, RequestEvent, RequestOutcome}
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

        // `result.isLeft` alone would hold with zero retries too — it passed
        // when `sendWithRetries` was replaced by `sendOnce`. The attempt count
        // is what actually says the budget was spent rather than skipped.
        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          result <- GiteaRequestExecutor(backend, maxRetries = 2, recording(ref))
            .send(GiteaRequests.currentUser(config))
            .either
          events <- ref.get
        yield assertTrue(result.isLeft, events.head.attempts == 3)
      } @@ TestAspect.withLiveClock,
      test("backs off further after each failed attempt") {
        // Nothing else in this suite reaches `jitteredBackoff` with attempt > 1,
        // so the doubling was unexercised: replacing the shift with a constant
        // left the whole build green.
        //
        // The boundary is chosen to sit outside the 0.8-1.2 jitter band rather
        // than comparing one wait against the next, which would be flaky.
        // Doubling from 100ms gives waits of 100/200/400, so the fourth attempt
        // cannot be sent before 80+160+320 = 560ms. A flat 100ms would have
        // sent it by 360ms at the latest. At 500ms, exactly three attempts have
        // been made if and only if the delay is growing.
        for
          sent <- Ref.make(0)
          backend = BackendStub[Task](new RIOMonadAsyncError[Any]).whenAnyRequest.thenRespondF { _ =>
            sent.update(_ + 1).as(ResponseStub.adjust("", StatusCode.ServiceUnavailable))
          }
          fiber <- GiteaRequestExecutor(backend, maxRetries = 3)
            .send(GiteaRequests.currentUser(config))
            .either
            .fork
          _ <- TestClock.adjust(Duration.fromMillis(500))
          attempts <- sent.get
          _ <- TestClock.adjust(Duration.fromSeconds(5))
          _ <- fiber.join
        yield assertTrue(attempts == 3)
      },
      test("does not retry an error that cannot be retried") {
        val backend = respondWith(ResponseStub.adjust("""{"message":"nope"}""", StatusCode.NotFound))

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 3, recording(ref))
            .send(GiteaRequests.currentUser(config))
            .either
          events <- ref.get
        yield assertTrue(events.size == 1, events.head.attempts == 1)
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
        yield assertTrue(!request.retryable, events.head.attempts == 1)
      }
    ),
    suite("attempt time budget")(
      test("a response that never arrives fails instead of hanging") {
        // `GiteaConfig.timeout` reaches the JDK as HttpRequest.timeout, which
        // stops applying once response headers arrive, so it cannot bound a
        // body that never ends. This budget can.
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any]).whenAnyRequest.thenRespondF(_ => ZIO.never)

        GiteaRequestExecutor(backend, maxRetries = 0, attemptTimeout = Duration.fromMillis(200))
          .send(GiteaRequests.currentUser(config))
          .either
          .map {
            case Left(GiteaError.TransportError(cause)) =>
              assertTrue(cause.isInstanceOf[java.util.concurrent.TimeoutException])
            case _ => assertTrue(false)
          }
      } @@ TestAspect.withLiveClock,
      test("an exhausted budget is surfaced, not retried") {
        // The budget is applied per attempt, so a retried stall used to be
        // paid for `maxRetries + 1` times over: a five-minute stall against
        // the default configuration hung the caller for twenty. A stalled
        // connection is also the condition least likely to have cleared by
        // the time the retry runs, so the attempts bought nothing.
        val request = GiteaRequests.currentUser(config)

        for
          // Counted at the backend, not on the telemetry event: `attempts` only
          // advances when a response arrives, and here none ever does, so it
          // reads 1 whether the stall was retried or not.
          sent <- Ref.make(0)
          backend = BackendStub[Task](new RIOMonadAsyncError[Any]).whenAnyRequest
            .thenRespondF(_ => sent.update(_ + 1) *> ZIO.never)
          result <- GiteaRequestExecutor(
            backend,
            maxRetries = 3,
            attemptTimeout = Duration.fromMillis(200)
          ).send(request).either
          attempts <- sent.get
        yield assertTrue(
          // A retryable GET with three retries available: without the guard
          // this reaches the backend four times and costs four budgets.
          request.retryable,
          result.isLeft,
          attempts == 1
        )
      } @@ TestAspect.withLiveClock
    ),
    suite("telemetry")(
      test("reports the HTTP status of the response that arrived") {
        val backend = respondWith(ok(user))

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 0, recording(ref)).send(GiteaRequests.currentUser(config))
          events <- ref.get
        yield assertTrue(events.head.status.contains(200), !events.head.retried)
      },
      test("counts every attempt a retried call made") {
        val backend = respondWith(rateLimited("retry-after" -> "0"), ok(user))

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 2, recording(ref)).send(GiteaRequests.currentUser(config))
          events <- ref.get
        yield assertTrue(events.size == 1, events.head.attempts == 2, events.head.retried)
      } @@ TestAspect.withLiveClock,
      test("an observer that never finishes cannot withhold the result") {
        // `catchAllCause` handles an observer that fails, not one that hangs.
        // The recommended escape hatch in the scaladoc — offer to a `Queue` —
        // is precisely what suspends forever once that queue is full, so this
        // is the shape a well-intentioned observer actually fails in.
        val stalling = new GiteaObserver:
          def onComplete(event: RequestEvent): zio.UIO[Unit] = ZIO.never

        GiteaRequestExecutor(respondWith(ok(user)), maxRetries = 0, stalling)
          .send(GiteaRequests.currentUser(config))
          .timeout(zio.Duration.fromSeconds(30))
          .map(result => assertTrue(result.exists(_.login.contains("alice"))))
      } @@ TestAspect.withLiveClock,
      test("emits an event when the call dies with a defect") {
        // `Cause.failureOption` sees only typed failures, so a defect used to
        // produce no event at all and the success and failure counts stopped
        // summing to the real request count.
        val backend = respondWith(ok(user))
        val exploding = GiteaRequest(
          endpoint = GiteaEndpoints.userGetCurrent,
          request = basicRequest.get(uri"https://gitea.example/api/v1/user").response(asStringAlways),
          decode = _ => throw new RuntimeException("decode blew up"),
          retryable = false
        )

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          exit <- GiteaRequestExecutor(backend, maxRetries = 0, recording(ref)).send(exploding).exit
          events <- ref.get
        yield assertTrue(
          exit.isFailure,
          events.size == 1,
          events.head.outcome match
            case RequestOutcome.Failure(GiteaError.TransportError(_)) => true
            case _ => false
        )
      },
      test("reports no status when no response ever arrived") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenAnyRequest
          .thenRespondF(_ => ZIO.fail(new RuntimeException("connection refused")))

        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 0, recording(ref))
            .send(GiteaRequests.currentUser(config))
            .either
          events <- ref.get
        yield assertTrue(events.head.status.isEmpty, events.head.attempts == 1)
      }
    )
  )
