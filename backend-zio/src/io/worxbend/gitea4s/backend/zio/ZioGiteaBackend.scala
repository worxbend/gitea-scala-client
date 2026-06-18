package io.worxbend.gitea4s.backend.zio

import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import sttp.client4.httpclient.zio.HttpClientZioBackend
import sttp.model.Uri
import zio.ZLayer

import java.net.http.HttpClient

object ZioGiteaBackend:
  val live: ZLayer[GiteaConfig, Throwable, GiteaClient] =
    ZLayer.scoped {
      for
        config <- zio.ZIO.service[GiteaConfig]
        backend <- HttpClientZioBackend.scoped()
      yield GiteaClient.fromBackend(config, backend)
    }

  def configured(config: GiteaConfig): ZLayer[Any, Throwable, GiteaClient] =
    ZLayer.succeed(config) >>> live

  def withToken(baseUrl: Uri, token: String): ZLayer[Any, Throwable, GiteaClient] =
    configured(GiteaConfig.default(baseUrl, Auth.Token(token)))

  def withBasic(baseUrl: Uri, username: String, password: String): ZLayer[Any, Throwable, GiteaClient] =
    configured(GiteaConfig.default(baseUrl, Auth.Basic(username, password)))

  def anonymous(baseUrl: Uri): ZLayer[Any, Throwable, GiteaClient] =
    configured(GiteaConfig.default(baseUrl, Auth.Anonymous))

  def usingClient(httpClient: HttpClient): ZLayer[GiteaConfig, Nothing, GiteaClient] =
    ZLayer.fromFunction { (config: GiteaConfig) =>
      GiteaClient.fromBackend(config, HttpClientZioBackend.usingClient(httpClient))
    }

  def usingClient(config: GiteaConfig, httpClient: HttpClient): ZLayer[Any, Nothing, GiteaClient] =
    ZLayer.succeed(config) >>> usingClient(httpClient)
