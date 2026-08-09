package io.worxbend.gitea4s

import io.worxbend.gitea4s.model.Auth
import sttp.model.Uri
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
      test("reads the same settings from the environment as from HOCON") {
        // `fromEnv` bound five settings while the HOCON reader bound seven, so
        // an env-configured deployment could not change its User-Agent at all
        // and could not send X-Gitea-OTP.
        val env = GiteaConfig.fromEnv(
          baseEnv ++ Map(
            GiteaConfig.Env.token -> "t",
            GiteaConfig.Env.userAgent -> "my-app/2.0",
            GiteaConfig.Env.otp -> "123456"
          )
        )
        val hocon = GiteaConfig.fromTypesafeString(
          """gitea4s {
            |  url = "https://gitea.example/root"
            |  token = "t"
            |  user-agent = "my-app/2.0"
            |  otp = "123456"
            |}""".stripMargin
        )

        assertTrue(
          env.map(_.userAgent) == Right(Some("my-app/2.0")),
          env.map(_.otp) == Right(Some("123456")),
          // The whole config, not just the two new fields.
          env == hocon
        )
      },
      test("defaults the User-Agent identically from either source") {
        val env = GiteaConfig.fromEnv(baseEnv + (GiteaConfig.Env.token -> "t"))
        val hocon = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example/root", token = "t" }"""
        )

        assertTrue(env.map(_.userAgent) == Right(Some("gitea4s")), env.map(_.userAgent) == hocon.map(_.userAgent))
      },
      test("rejects control characters in the new environment variables too") {
        val agent = GiteaConfig.fromEnv(baseEnv ++ Map(GiteaConfig.Env.userAgent -> "bad\ragent"))
        val otp = GiteaConfig.fromEnv(baseEnv ++ Map(GiteaConfig.Env.otp -> "12\n34"))

        assertTrue(
          agent.isLeft,
          otp.isLeft,
          !otp.left.toOption.map(_.toString).getOrElse("").contains("1234")
        )
      },
      test("builds the basic-auth header the way HTTP specifies") {
        // `Auth.Basic` header construction had no hermetic assertion at all —
        // only the live integration suite would have caught a wrong encoding,
        // and only as a 401.
        val expected =
          "Basic " + java.util.Base64.getEncoder.encodeToString("alice:hunter2".getBytes("UTF-8"))
        val config = GiteaConfig.withBasic(Uri.unsafeParse("https://gitea.example"), "alice", "hunter2")

        assertTrue(config.jsonHeaders.get("Authorization").contains(expected))
      },
      test("rejects a HOCON duration written without a unit") {
        // Typesafe Config reads a bare number in duration position as
        // milliseconds, so `timeout = 30` used to parse as 30ms and give every
        // request a 30-millisecond budget. The identical GITEA_TIMEOUT=30 was
        // rejected, so the same text meant two different things and the wrong
        // one was silent.
        val bare = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", timeout = 30 }"""
        )
        val quoted = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", timeout = "30" }"""
        )

        assertTrue(
          bare.isLeft,
          quoted.isLeft,
          bare.left.toOption.map(_.toString).getOrElse("").contains("30s")
        )
      },
      test("accepts the HOCON duration spellings Typesafe config allows") {
        // The check rejects only the unitless form. HOCON's own spellings must
        // keep working, including ones Scala's Duration parser would reject.
        val seconds = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", timeout = 30s }"""
        )
        val spaced = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", timeout = 2 minutes }"""
        )

        assertTrue(
          seconds.map(_.timeout) == Right(30.seconds),
          spaced.map(_.timeout) == Right(2.minutes)
        )
      },
      test("rejects a setting spelled the way the environment spells it") {
        // `maxRetries` is simply a different key from `max-retries`, so it used
        // to be read by nobody: the config loaded and the setting did nothing.
        val result = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", maxRetries = 9 }"""
        )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(result.isLeft, message.contains("maxRetries"), message.contains("max-retries"))
      },
      test("leaves a genuinely unrelated key alone") {
        // Applications legitimately keep their own settings beside these, so
        // only near misses are rejected.
        val result = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", my-own-setting = 7 }"""
        )

        assertTrue(result.map(_.maxRetries) == Right(GiteaConfig.defaultMaxRetries))
      },
      test("rejects a token with a control character in the middle") {
        // Trimming only strips the ends. An embedded CR/LF used to survive
        // into `Authorization: token ...` and fail later — as an untyped JDK
        // exception quoting the credential, after being retried three times.
        val result = GiteaConfig.fromEnv(baseEnv + (GiteaConfig.Env.token -> "ghp_abc\r\ndef"))
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(
          result.isLeft,
          message.contains(GiteaConfig.Env.token),
          // The error names the setting, never the secret.
          !message.contains("ghp_abc")
        )
      },
      test("rejects basic credentials with a control character in the middle") {
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.username -> "alice",
              GiteaConfig.Env.password -> "hun\u0000ter"
            )
          )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(result.isLeft, message.contains(GiteaConfig.Env.password), !message.contains("hun"))
      },
      test("accepts a password containing spaces") {
        // The guard rejects control characters, not everything unusual. A space
        // is legal in a header value and ordinary in a passphrase, so this pins
        // the boundary that the rejection tests would otherwise let drift.
        val passphrase = "correct horse battery staple"
        val result =
          GiteaConfig.fromEnv(
            baseEnv ++ Map(
              GiteaConfig.Env.username -> "alice",
              GiteaConfig.Env.password -> passphrase
            )
          )

        assertTrue(result.map(_.auth) == Right(Auth.Basic("alice", passphrase)))
      },
      test("rejects a HOCON user-agent with a control character") {
        val result = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", user-agent = "bad\nagent" }"""
        )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(result.isLeft, message.contains("user-agent"))
      },
      test("rejects a HOCON otp with a control character") {
        val result = GiteaConfig.fromTypesafeString(
          """gitea4s { url = "https://gitea.example", token = "t", otp = "12\r\n34" }"""
        )
        val message = result.left.toOption.map(_.toString).getOrElse("")

        assertTrue(result.isLeft, message.contains("otp"), !message.contains("1234"))
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
      test("retries idempotent requests by default") {
        val result = GiteaConfig.fromEnv(baseEnv)

        assertTrue(result.map(_.maxRetries) == Right(GiteaConfig.defaultMaxRetries), GiteaConfig.defaultMaxRetries > 0)
      },
      test("still honours an explicit request for no retries") {
        val result = GiteaConfig.fromEnv(baseEnv + (GiteaConfig.Env.maxRetries -> "0"))

        assertTrue(result.map(_.maxRetries) == Right(0))
      },
      test("redacts the one-time password from toString") {
        val config = GiteaConfig.withToken(uri, "ghp_realSecretValue").withOtp(Some("123456"))
        val rendered = config.toString

        assertTrue(
          !rendered.contains("ghp_realSecretValue"),
          !rendered.contains("123456"),
          rendered.contains("gitea.example")
        )
      },
      test("redacts basic credentials from toString") {
        val rendered = GiteaConfig.withBasic(uri, "alice", "hunter2").toString

        assertTrue(!rendered.contains("hunter2"), rendered.contains("alice"))
      },
      test("builder methods produce the same config as copy") {
        val config = GiteaConfig.withToken(uri, "t")

        assertTrue(
          config.withPageSize(17).pageSize == 17,
          config.withMaxRetries(9).maxRetries == 9,
          config.withTimeout(2.seconds).timeout == 2.seconds,
          config.withUserAgent(None).userAgent.isEmpty,
          config.withOtp(Some("999")).otp.contains("999"),
          config.withAuth(Auth.Anonymous).auth == Auth.Anonymous
        )
      },
      test("derives request headers once per config") {
        val config = GiteaConfig.withBasic(uri, "alice", "hunter2")

        assertTrue(
          config.jsonHeaders eq config.jsonHeaders,
          config.headersAccepting(Accept.Json) eq config.jsonHeaders,
          config.headersAccepting(Accept.OctetStream) eq config.octetStreamHeaders,
          config.headersAccepting(Accept.TextPlain) eq config.textPlainHeaders
        )
      },
      test("sends the Accept value asked for, alongside the credentials") {
        // A two-way switch here would have silently answered text/plain
        // requests with the octet-stream header set. The arbitrary-media-type
        // case this replaces is now unrepresentable: `Accept` is a closed type,
        // so an unlisted content type does not compile.
        val config = GiteaConfig.withToken(uri, "t")

        assertTrue(
          Accept.values.forall(accept =>
            config.headersAccepting(accept).get("Accept").contains(accept.headerValue)
          ),
          config.headersAccepting(Accept.TextPlain).get("Authorization").contains("token t")
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

  private lazy val uri: Uri = Uri.parse("https://gitea.example/root").toOption.get
