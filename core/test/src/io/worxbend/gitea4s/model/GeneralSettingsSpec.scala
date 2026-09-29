package io.worxbend.gitea4s.model

import zio.json.*
import zio.test.*

object GeneralSettingsSpec extends ZIOSpecDefault:
  def spec =
    suite("public settings models")(
      test("API limits round-trip with 64-bit values") {
        val json = """{"default_git_trees_per_page":1,"default_max_blob_size":2147483648,"default_max_response_size":3,"default_paging_num":4,"max_response_items":5}"""
        assertTrue(json.fromJson[GeneralAPISettings].flatMap(_.toJson.fromJson[GeneralAPISettings]) == json.fromJson[GeneralAPISettings])
      },
      test("attachment settings preserve disabled state and limits") {
        val json = """{"allowed_types":"image/*","enabled":false,"max_files":0,"max_size":2147483648}"""
        assertTrue(json.fromJson[GeneralAttachmentSettings].map(_.toJson) == Right(json))
      },
      test("repository switches preserve false values") {
        val json = """{"http_git_disabled":false,"lfs_disabled":false,"migrations_disabled":false,"mirrors_disabled":false,"stars_disabled":false,"time_tracking_disabled":false}"""
        assertTrue(json.fromJson[GeneralRepoSettings].map(_.toJson) == Right(json))
      },
      test("UI settings preserve empty and populated lists") {
        val json = """{"allowed_reactions":["+1"],"custom_emojis":[],"default_theme":"auto"}"""
        assertTrue(json.fromJson[GeneralUISettings].map(_.toJson) == Right(json))
      }
    )
