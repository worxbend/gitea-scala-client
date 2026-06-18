package io.worxbend.gitea4s.backend.zio

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import zio.ZIO
import zio.test.*

import java.net.http.HttpClient

object ZioGiteaBackendSpec extends ZIOSpecDefault:
  private val baseUrl = uri"https://gitea.example"
  private val config = GiteaConfig.default(baseUrl, Auth.Anonymous)

  def spec =
    suite("ZioGiteaBackend")(
      test("builds a scoped live client layer") {
        ZIO.service[GiteaClient]
          .provideLayer(ZioGiteaBackend.configured(config))
          .map(client => assertTrue(client != null))
      },
      test("builds a client layer from a caller-owned Java HttpClient") {
        val httpClient = HttpClient.newHttpClient()

        ZIO.service[GiteaClient]
          .provideLayer(ZioGiteaBackend.usingClient(config, httpClient))
          .map(client => assertTrue(client != null))
      },
      test("builds token and anonymous convenience layers") {
        for
          tokenClient <- ZIO.service[GiteaClient].provideLayer(ZioGiteaBackend.withToken(baseUrl, "secret"))
          anonymousClient <- ZIO.service[GiteaClient].provideLayer(ZioGiteaBackend.anonymous(baseUrl))
        yield assertTrue(tokenClient != null, anonymousClient != null)
      }
    )
