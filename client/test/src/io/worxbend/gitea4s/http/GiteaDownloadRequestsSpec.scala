package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import zio.Chunk
import zio.test.*

object GiteaDownloadRequestsSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret"))

  def spec =
    suite("GiteaRequests download descriptors")(
      test("rawFileDownload encodes a slash filepath as one segment, sends ref, advertises octet-stream") {
        val req = GiteaRequests.rawFileDownload(config, "alice", "api", "docs/readme.md", ContentsParams(ref = Some("main")))
        assertTrue(
          req.endpoint == GiteaEndpoints.repoGetRawFile,
          req.uri.toString.contains("/api/v1/repos/alice/api/raw/docs%2Freadme.md"),
          req.uri.params.get("ref").contains("main"),
          req.headers.get("Accept").contains("application/octet-stream"),
          req.headers.get("Authorization").contains("token secret")
        )
      },
      test("mediaFileDownload targets the media path") {
        val req = GiteaRequests.mediaFileDownload(config, "alice", "api", "logo.png")
        assertTrue(
          req.endpoint == GiteaEndpoints.repoGetRawFileOrLFS,
          req.uri.toString.contains("/api/v1/repos/alice/api/media/logo.png"),
          req.uri.params.get("ref").isEmpty,
          req.headers.get("Accept").contains("application/octet-stream")
        )
      },
      test("archiveDownload sends repeated path query values") {
        val req = GiteaRequests.archiveDownload(
          config,
          "alice",
          "api",
          "main.zip",
          ArchiveParams(path = Chunk("src", "docs/readme.md"))
        )
        assertTrue(
          req.endpoint == GiteaEndpoints.repoGetArchive,
          req.uri.toString.contains("/api/v1/repos/alice/api/archive/main.zip"),
          req.uri.params.getMulti("path").contains(Seq("src", "docs/readme.md"))
        )
      }
    )
