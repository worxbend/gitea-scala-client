package io.worxbend.gitea4s.model

import java.time.Instant
import zio.Chunk
import zio.json.*

/** Metadata about the active token. The API intentionally does not return its secret. */
final case class CurrentAccessToken(
    @jsonField("created_at") createdAt: Option[Instant] = None,
    id: Option[Long] = None,
    @jsonField("last_used_at") lastUsedAt: Option[Instant] = None,
    name: Option[String] = None,
    scopes: Option[Chunk[String]] = None,
    user: Option[UserMeta] = None
)

object CurrentAccessToken:
  given JsonCodec[CurrentAccessToken] = DeriveJsonCodec.gen[CurrentAccessToken]

final case class UserMeta(id: Option[Long] = None, login: Option[String] = None)

object UserMeta:
  given JsonCodec[UserMeta] = DeriveJsonCodec.gen[UserMeta]
