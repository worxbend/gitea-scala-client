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
              GiteaConfig.Env.timeout -> "45s",
              GiteaConfig.Env.maxRetries -> "2"
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
              config.maxRetries == 2
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
      test("rejects invalid environment retry counts") {
        val negative =
          GiteaConfig.fromEnv(baseEnv ++ Map(GiteaConfig.Env.maxRetries -> "-1"))
        val decimal =
          GiteaConfig.fromEnv(baseEnv ++ Map(GiteaConfig.Env.maxRetries -> "1.5"))

        assertTrue(negative.isLeft, decimal.isLeft)
      },
      test("rejects relative or non-HTTP base URLs") {
        val relative = GiteaConfig.fromEnv(Map(GiteaConfig.Env.url -> "gitea.local"))
        val ssh = GiteaConfig.fromEnv(Map(GiteaConfig.Env.url -> "ssh://gitea.example"))

        assertTrue(relative.isLeft, ssh.isLeft)
      },
      test("loads token auth and non-secret settings from Typesafe config") {
        val result =
          GiteaConfig.fromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |  token = "token-secret"
              |  page-size = 75
              |  timeout = 20s
              |  user-agent = "gitea4s-test"
              |  otp = "123456"
              |  max-retries = 3
              |}
              |""".stripMargin
          )

        result match
          case Right(config) =>
            assertTrue(
              config.baseUrl.toString == "https://gitea.example/root",
              config.auth == Auth.Token("token-secret"),
              config.pageSize == 75,
              config.timeout == 20.seconds,
              config.userAgent.contains("gitea4s-test"),
              config.otp.contains("123456"),
              config.maxRetries == 3
            )
          case Left(_) => assertTrue(false)
      },
      test("prefers Typesafe token credentials over username and password") {
        val result =
          GiteaConfig.fromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |  token = "token-secret"
              |  username = "octo"
              |  password = "password-secret"
              |}
              |""".stripMargin
          )

        assertTrue(result.toOption.exists(_.auth == Auth.Token("token-secret")))
      },
      test("loads Typesafe basic auth through a hermetic layer") {
        val layer =
          GiteaConfig.layerFromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |  username = "octo"
              |  password = "password-secret"
              |}
              |""".stripMargin
          )

        ZIO.serviceWith[GiteaConfig](_.auth).provideLayer(layer).map { auth =>
          assertTrue(auth == Auth.Basic("octo", "password-secret"))
        }
      },
      test("uses anonymous auth from Typesafe config when no credentials are present") {
        val result =
          GiteaConfig.fromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |}
              |""".stripMargin
          )

        assertTrue(result.toOption.exists(_.auth == Auth.Anonymous))
      },
      test("keeps Typesafe credential values out of validation errors") {
        val result =
          GiteaConfig.fromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |  password = "password-secret"
              |}
              |""".stripMargin
          )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(
          result.isLeft,
          message.contains("gitea4s.username"),
          message.contains("gitea4s.password"),
          !message.contains("password-secret")
        )
      },
      test("rejects invalid Typesafe retry counts") {
        val result =
          GiteaConfig.fromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |  max-retries = -1
              |}
              |""".stripMargin
          )

        assertTrue(result == Left(GiteaConfigError.InvalidConfig("gitea4s.max-retries", "must be zero or a positive integer")))
      }
    )
