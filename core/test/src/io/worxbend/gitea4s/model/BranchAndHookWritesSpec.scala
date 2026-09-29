package io.worxbend.gitea4s.model

import zio.json.*
import zio.test.*

object BranchAndHookWritesSpec extends ZIOSpecDefault:
  def spec =
    suite("branch and hook models")(
      test("encodes branch preconditions including explicit false") {
        val body = UpdateBranchRepoOption("newsha", Some("oldsha"), Some(false))
        val json = """{"new_commit_id":"newsha","old_commit_id":"oldsha","force":false}"""

        assertTrue(body.toJson == json, json.fromJson[UpdateBranchRepoOption] == Right(body))
      },
      test("encodes branch rename and hook edits") {
        assertTrue(
          RenameBranchRepoOption("new").toJson == """{"name":"new"}""",
          EditGitHookOption(Some("script")).toJson == """{"content":"script"}"""
        )
      },
      test("decodes Git hook activity and content") {
        val json = """{"name":"pre-receive","is_active":false,"content":"script"}"""
        assertTrue(json.fromJson[GitHook] == Right(GitHook(Some("script"), Some(false), Some("pre-receive"))))
      }
    )
