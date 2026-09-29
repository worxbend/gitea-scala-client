package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class DeployKey(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("fingerprint") fingerprint: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("key") key: Option[String] = None,
    @jsonField("key_id") keyId: Option[Long] = None,
    @jsonField("read_only") readOnly: Option[Boolean] = None,
    @jsonField("repository") repository: Option[Repository] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object DeployKey:
  given JsonCodec[DeployKey] = DeriveJsonCodec.gen[DeployKey]
