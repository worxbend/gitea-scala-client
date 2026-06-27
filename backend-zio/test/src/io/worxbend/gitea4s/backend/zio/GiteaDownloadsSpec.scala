package io.worxbend.gitea4s.backend.zio

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.model.Auth
import sttp.capabilities.zio.ZioStreams
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{ResponseStub, StreamBackendStub}
import sttp.model.StatusCode
import zio.{Chunk, Task}
import zio.stream.ZStream
import zio.test.*

object GiteaDownloadsSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret"))

  def spec =
    suite("GiteaDownloads")(
      test("streams raw file bytes from a successful response") {
        val payload = Chunk[Byte](1, 2, 3, 4, 5)
        val body: ZStream[Any, Throwable, Byte] = ZStream.fromChunk(payload)
        val backend =
          StreamBackendStub[Task, ZioStreams](new RIOMonadAsyncError[Any]).whenAnyRequest
            .thenRespond(ResponseStub.adjust(body))

        ZioGiteaDownloads(config, backend)
          .rawFile("alice", "api", "README.md")
          .runCollect
          .map(bytes => assertTrue(bytes == payload))
      },
      test("maps a non-2xx download response to a GiteaError") {
        val backend =
          StreamBackendStub[Task, ZioStreams](new RIOMonadAsyncError[Any]).whenAnyRequest
            .thenRespond(ResponseStub.adjust("missing", StatusCode.NotFound))

        ZioGiteaDownloads(config, backend)
          .archive("alice", "api", "main.zip")
          .runCollect
          .either
          .map(result => assertTrue(result.isLeft))
      }
    )
