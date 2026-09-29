package io.worxbend.gitea4s.model

import zio.json.*
import zio.test.*

object TagProtectionWritesSpec extends ZIOSpecDefault:
  def spec =
    suite("tag protection write models")(
      test("create and edit encode the same schema field names") {
        val create = CreateTagProtectionOption(Some("v*"), Some(List("release")), Some(Nil))
        val edit = EditTagProtectionOption(Some("v*"), Some(List("release")), Some(Nil))
        val json = """{"name_pattern":"v*","whitelist_teams":["release"],"whitelist_usernames":[]}"""

        assertTrue(
          create.toJson == json,
          edit.toJson == json,
          json.fromJson[CreateTagProtectionOption] == Right(create),
          json.fromJson[EditTagProtectionOption] == Right(edit)
        )
      }
    )
