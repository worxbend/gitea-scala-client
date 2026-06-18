package io.worxbend.gitea4s

import io.worxbend.gitea4s.model.Auth
import zio.ZIO
import zio.test.*

import scala.concurrent.duration.*

object GiteaConfigSpec extends ZIOSpecDefault:
  private val baseEnv =
    Map(GiteaConfig.Env.url -> "https://gitea.example/root")

  def spec =
    suite("GiteaConfig")(
      test("loads token auth and optional settings from environment variables") {
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.token -> "token-secret",
              GiteaConfig.Env.pageSize -> "100",
              GiteaConfig.Env.timeout -> "45s"
            )
          )

        result match
          case Right(config) =>
            assertTrue(
              config.baseUrl.toString == "https://gitea.example/root",
              config.auth == Auth.Token("token-secret"),
              config.pageSize == 100,
              config.timeout == 45.seconds,
              config.userAgent.contains("gitea4s"),
              config.maxRetries == 0
            )
          case Left(_) => assertTrue(false)
      },
      test("prefers token credentials over username and password") {
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.token -> "token-secret",
              GiteaConfig.Env.username -> "octo",
              GiteaConfig.Env.password -> "password-secret"
            )
          )

        assertTrue(result.toOption.exists(_.auth == Auth.Token("token-secret")))
      },
      test("loads basic credentials when token is absent") {
        val layer =
          GiteaConfig.layerFromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.username -> "octo",
              GiteaConfig.Env.password -> "password-secret"
            )
          )

        ZIO.serviceWith[GiteaConfig](_.auth).provideLayer(layer).map { auth =>
          assertTrue(auth == Auth.Basic("octo", "password-secret"))
        }
      },
      test("uses anonymous auth when no credentials are present") {
        val result = GiteaConfig.fromEnv(baseEnv)

        assertTrue(result.toOption.exists(_.auth == Auth.Anonymous))
      },
      test("requires GITEA_URL") {
        val result = GiteaConfig.fromEnv(Map.empty)

        assertTrue(result == Left(GiteaConfigError.MissingRequiredEnv(GiteaConfig.Env.url)))
      },
      test("requires username and password to be set together for basic auth") {
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(GiteaConfig.Env.password -> "password-secret")
          )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(
          result.isLeft,
          message.contains(GiteaConfig.Env.username),
          message.contains(GiteaConfig.Env.password),
          !message.contains("password-secret")
        )
      },
      test("keeps secret values out of validation errors") {
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.token -> "token-secret",
              GiteaConfig.Env.pageSize -> "many"
            )
          )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(
          result.isLeft,
          message.contains(GiteaConfig.Env.pageSize),
          !message.contains("token-secret")
        )
      },
      test("rejects relative or non-HTTP base URLs") {
        val relative = GiteaConfig.fromEnv(Map(GiteaConfig.Env.url -> "gitea.local"))
        val ssh = GiteaConfig.fromEnv(Map(GiteaConfig.Env.url -> "ssh://gitea.example"))

        assertTrue(relative.isLeft, ssh.isLeft)
      }
    )
