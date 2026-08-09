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
      test("trims whitespace around a token read from the environment") {
        // A secret read out of a file keeps its trailing newline. Untrimmed,
        // the JDK rejects the resulting header value with an exception that
        // quotes the credential, and that failure is classified retryable.
        val result = GiteaConfig.fromEnv(baseEnv + (GiteaConfig.Env.token -> "  ghp_abc123\n"))

        assertTrue(result.map(_.auth) == Right(Auth.Token("ghp_abc123")))
      },
      test("trims whitespace around basic credentials read from the environment") {
        // The basic-auth variant is the quieter failure: a newline inside the
        // credential base64-encodes into a syntactically valid header, so
        // nothing throws and the server simply answers 401 forever.
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.username -> "alice\n",
              GiteaConfig.Env.password -> " hunter2 "
            )
          )

        assertTrue(result.map(_.auth) == Right(Auth.Basic("alice", "hunter2")))
      },
      test("strips userinfo credentials from the base URL") {
        val result = GiteaConfig.fromEnv(Map(GiteaConfig.Env.url -> "https://alice:hunter2@gitea.example/root"))

        result match
          case Right(config) =>
            val rendered = config.baseUrl.toString
            assertTrue(
              !rendered.contains("hunter2"),
              !rendered.contains("alice"),
              rendered == "https://gitea.example/root"
            )
          case Left(_) => assertTrue(false)
      },
      test("does not echo HOCON source text when parsing fails") {
        // The offending text in a syntax error is frequently the credential
        // itself: a missing `=` after `token` puts the token into the message.
        val result =
          GiteaConfig.fromTypesafeString(
            """
              |gitea4s {
              |  url = "https://gitea.example/root"
              |  token ghp_SUPERSECRET123
              |}
              |""".stripMargin
          )

        val message = result.left.map(_.message).left.getOrElse("")
        assertTrue(result.isLeft, !message.contains("ghp_SUPERSECRET123"), message.contains("HOCON"))
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
