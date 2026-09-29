package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Reaction(
    @jsonField("content") content: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("user") user: Option[User] = None
)

object Reaction:
  given JsonCodec[Reaction] = DeriveJsonCodec.gen[Reaction]
