package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Secret(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("name") name: Option[String] = None
)

object Secret:
  given JsonCodec[Secret] = DeriveJsonCodec.gen[Secret]
