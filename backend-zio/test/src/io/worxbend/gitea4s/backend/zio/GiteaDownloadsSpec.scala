package io.worxbend.gitea4s.backend.zio

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Auth
import sttp.capabilities.zio.ZioStreams
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{ResponseStub, StreamBackendStub}
import sttp.model.StatusCode
import zio.{Chunk, Ref, Task, ZIO, durationInt}
import zio.stream.ZStream
import zio.test.*

object GiteaDownloadsSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret"))

  private def stubBackend(body: ZStream[Any, Throwable, Byte]): StreamBackend[Task, ZioStreams] =
    StreamBackendStub[Task, ZioStreams](new RIOMonadAsyncError[Any]).whenAnyRequest
      .thenRespond(ResponseStub.adjust(body))

  def spec =
    suite("GiteaDownloads")(
      test("streams raw file bytes from a successful response") {
        val payload = Chunk[Byte](1, 2, 3, 4, 5)
        val body: ZStream[Any, Throwable, Byte] = ZStream.fromChunk(payload)

        ZioGiteaDownloads(config, stubBackend(body))
          .rawFile("alice", "api", "README.md")
          .runCollect
          .map(bytes => assertTrue(bytes == payload))
      },
      test("releases the response body when the stream is consumed only partially") {
        for
          released <- Ref.make(0)
          reachedEnd <- Ref.make(false)
          // The tail runs only if something keeps pulling past the first chunk, so it
          // detects a release that drains the rest of the body instead of cancelling it.
          body = (ZStream.fromChunk(Chunk[Byte](1, 2, 3)) ++ ZStream.fromZIO(reachedEnd.set(true)).drain)
            .ensuring(released.update(_ + 1))
          head <- ZioGiteaDownloads(config, stubBackend(body)).archive("alice", "api", "main.zip").runHead
          releaseCount <- released.get
          drained <- reachedEnd.get
        yield assertTrue(head.contains(1.toByte), releaseCount == 1, !drained)
      },
      test("fails a download that stops producing instead of hanging") {
        // This path sends straight at the backend, so it never had the
        // executor's attempt budget, and `readTimeout` reaches the JDK as
        // `HttpRequest.timeout` — which stops applying once headers arrive.
        // A server that answered 200 and then went quiet held the consumer
        // open forever, on precisely the path meant for the largest bodies.
        val body: ZStream[Any, Throwable, Byte] = ZStream.fromChunk(Chunk[Byte](1, 2, 3)) ++ ZStream.fromZIO(ZIO.never)

        // A real clock and a short budget, rather than forking and nudging a
        // `TestClock`: that races the forked fiber, so it passes when the
        // fiber happens to reach the sleep first and hangs when it does not.
        ZioGiteaDownloads
          .withStallTimeout(config, stubBackend(body), 200.millis)
          .archive("alice", "api", "main.zip")
          .runCollect
          .either
          .map { result =>
            assertTrue(
              result.left.exists {
                case GiteaError.TransportError(cause) => cause.isInstanceOf[java.util.concurrent.TimeoutException]
                case _ => false
              }
            )
          }
      } @@ TestAspect.withLiveClock,
      test("maps a non-2xx download response to a typed GiteaError") {
        val errorBody = """{"message":"repository does not exist"}"""
        val backend =
          StreamBackendStub[Task, ZioStreams](new RIOMonadAsyncError[Any]).whenAnyRequest
            .thenRespond(ResponseStub.adjust(errorBody, StatusCode.NotFound))

        ZioGiteaDownloads(config, backend)
          .archive("alice", "api", "main.zip")
          .runCollect
          .either
          .map {
            // A typed error, not a TransportError wrapping a stream failure: the error body
            // is read as a string before it ever reaches the stream.
            case Left(GiteaError.NotFound(message, body)) => assertTrue(body == errorBody, message.nonEmpty)
            case other => assertNever(s"expected a GiteaError.NotFound, got $other")
          }
      }
    )
