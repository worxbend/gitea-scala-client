package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CurrentAccessToken(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("last_used_at") lastUsedAt: Option[java.time.Instant] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("scopes") scopes: Option[List[String]] = None,
    @jsonField("user") user: Option[UserMeta] = None
)

object CurrentAccessToken:
  given JsonCodec[CurrentAccessToken] = DeriveJsonCodec.gen[CurrentAccessToken]
