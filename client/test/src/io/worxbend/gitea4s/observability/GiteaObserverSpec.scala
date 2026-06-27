package io.worxbend.gitea4s.observability

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaEndpoints, GiteaRequests}
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.StatusCode
import zio.{Chunk, Ref, Task, ZIO}
import zio.test.*

object GiteaObserverSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.anonymous(uri"https://gitea.example")

  private def stub(body: String, status: StatusCode): Backend[Task] =
    BackendStub[Task](new RIOMonadAsyncError[Any]).whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))

  private def recording(ref: Ref[Chunk[RequestEvent]]): GiteaObserver =
    GiteaObserver.fromFunction(event => ref.update(_ :+ event))

  def spec =
    suite("GiteaObserver")(
      test("notifies a success event with the called endpoint when a request succeeds") {
        val backend = stub("""{"id":1,"login":"alice"}""", StatusCode.Ok)
        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 0, recording(ref)).send(GiteaRequests.currentUser(config))
          events <- ref.get
        yield assertTrue(
          events.size == 1,
          events.head.endpoint == GiteaEndpoints.userGetCurrent,
          events.head.succeeded
        )
      },
      test("notifies a failure event carrying the GiteaError when a request fails") {
        val backend = stub("""{"message":"missing"}""", StatusCode.NotFound)
        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          result <- GiteaRequestExecutor(backend, maxRetries = 0, recording(ref))
            .send(GiteaRequests.currentUser(config))
            .either
          events <- ref.get
        yield assertTrue(
          result.isLeft,
          events.size == 1,
          events.head.outcome match
            case RequestOutcome.Failure(_: GiteaError.NotFound) => true
            case _ => false
        )
      },
      test("the default noop observer records nothing") {
        val backend = stub("""{"id":1,"login":"alice"}""", StatusCode.Ok)
        for
          ref <- Ref.make(Chunk.empty[RequestEvent])
          _ <- GiteaRequestExecutor(backend, maxRetries = 0).send(GiteaRequests.currentUser(config))
          events <- ref.get
        yield assertTrue(events.isEmpty)
      },
      test("a defecting observer never breaks the underlying request") {
        val backend = stub("""{"id":1,"login":"alice"}""", StatusCode.Ok)
        val boom = GiteaObserver.fromFunction(_ => ZIO.die(new RuntimeException("boom")))
        GiteaRequestExecutor(backend, maxRetries = 0, boom)
          .send(GiteaRequests.currentUser(config))
          .either
          .map(result => assertTrue(result.isRight))
      }
    )
