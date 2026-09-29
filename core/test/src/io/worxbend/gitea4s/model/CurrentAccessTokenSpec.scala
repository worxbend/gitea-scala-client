package io.worxbend.gitea4s.model

import zio.Chunk
import zio.json.*
import zio.test.*

import java.time.Instant

object CurrentAccessTokenSpec extends ZIOSpecDefault:
  def spec =
    suite("current access token")(
      test("decodes all documented token metadata without a secret") {
        val json = """{"created_at":"2026-09-29T00:00:00Z","id":42,"last_used_at":"2026-09-29T01:00:00Z","name":"automation","scopes":["read:repository"],"user":{"id":7,"login":"alice"}}"""
        val expected = CurrentAccessToken(
          createdAt = Some(Instant.parse("2026-09-29T00:00:00Z")),
          id = Some(42L),
          lastUsedAt = Some(Instant.parse("2026-09-29T01:00:00Z")),
          name = Some("automation"),
          scopes = Some(Chunk("read:repository")),
          user = Some(UserMeta(Some(7L), Some("alice")))
        )

        assertTrue(json.fromJson[CurrentAccessToken] == Right(expected), expected.toJson == json)
      }
    )
