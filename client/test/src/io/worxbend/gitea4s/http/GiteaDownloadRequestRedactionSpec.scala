package io.worxbend.gitea4s.http

import sttp.client4.*
import zio.test.*

import java.util.Locale
import scala.concurrent.duration.*

object GiteaDownloadRequestRedactionSpec extends ZIOSpecDefault:
  def spec =
    suite("download request redaction")(
      test("redacts uppercase credential headers independently of the default locale") {
        val request = GiteaDownloadRequest(
          GiteaEndpoints.repoGetRawFile,
          uri"https://gitea.example/api/v1/repos/alice/api/raw/README.md",
          Map("AUTHORIZATION" -> "token secret", "X-GITEA-OTP" -> "123456", "Accept" -> "application/octet-stream"),
          30.seconds
        )
        val originalLocale = Locale.getDefault
        val rendered =
          try
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            request.toString
          finally Locale.setDefault(originalLocale)

        assertTrue(
          rendered.contains("AUTHORIZATION -> ***"),
          rendered.contains("X-GITEA-OTP -> ***"),
          rendered.contains("Accept -> application/octet-stream"),
          !rendered.contains("secret"),
          !rendered.contains("123456")
        )
      }
    )
