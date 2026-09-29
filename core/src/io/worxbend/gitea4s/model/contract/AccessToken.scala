package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class AccessToken(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("last_used_at") lastUsedAt: Option[java.time.Instant] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("scopes") scopes: Option[List[String]] = None,
    @jsonField("sha1") sha1: Option[String] = None,
    @jsonField("token_last_eight") tokenLastEight: Option[String] = None
)

object AccessToken:
  given JsonCodec[AccessToken] = DeriveJsonCodec.gen[AccessToken]
