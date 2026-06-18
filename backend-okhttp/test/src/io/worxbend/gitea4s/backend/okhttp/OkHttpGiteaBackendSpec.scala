package io.worxbend.gitea4s.backend.okhttp

import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import okhttp3.OkHttpClient
import sttp.client4.*
import zio.ZIO
import zio.test.*

object OkHttpGiteaBackendSpec extends ZIOSpecDefault:
  private val baseUrl = uri"https://gitea.example"
  private val config = GiteaConfig.default(baseUrl, Auth.Anonymous)

  def spec =
    suite("OkHttpGiteaBackend")(
      test("builds a scoped live client layer") {
        ZIO.service[GiteaClient]
          .provideLayer(OkHttpGiteaBackend.configured(config))
          .map(client => assertTrue(client != null))
      },
      test("builds a client layer from a caller-owned OkHttpClient") {
        val okHttpClient = OkHttpClient()

        ZIO.service[GiteaClient]
          .provideLayer(OkHttpGiteaBackend.usingClient(config, okHttpClient))
          .map(client => assertTrue(client != null))
      },
      test("builds token and anonymous convenience layers") {
        for
          tokenClient <- ZIO.service[GiteaClient].provideLayer(OkHttpGiteaBackend.withToken(baseUrl, "secret"))
          anonymousClient <- ZIO.service[GiteaClient].provideLayer(OkHttpGiteaBackend.anonymous(baseUrl))
        yield assertTrue(tokenClient != null, anonymousClient != null)
      }
    )
