package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PublicKey(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("fingerprint") fingerprint: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("key") key: Option[String] = None,
    @jsonField("key_type") keyType: Option[String] = None,
    @jsonField("last_used_at") lastUsedAt: Option[java.time.Instant] = None,
    @jsonField("read_only") readOnly: Option[Boolean] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("user") user: Option[User] = None
)

object PublicKey:
  given JsonCodec[PublicKey] = DeriveJsonCodec.gen[PublicKey]
