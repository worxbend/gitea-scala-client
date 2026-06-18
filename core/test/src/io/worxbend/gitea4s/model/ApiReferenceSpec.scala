package io.worxbend.gitea4s.model

import zio.json.*
import zio.test.*

object ApiReferenceSpec extends ZIOSpecDefault:
  def spec =
    suite("ApiReference")(
      test("records the local Gitea API contract") {
        assertTrue(
          ApiReference.gitea1262.version == "1.26.2",
          ApiReference.gitea1262.document == "plugin-redoc-2.yaml"
        )
      },
      test("round-trips through zio-json") {
        val decoded = ApiReference.gitea1262.toJson.fromJson[ApiReference]

        assertTrue(decoded == Right(ApiReference.gitea1262))
      }
    )
