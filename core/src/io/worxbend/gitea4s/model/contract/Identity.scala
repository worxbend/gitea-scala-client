package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Identity(
    @jsonField("email") email: Option[String] = None,
    @jsonField("name") name: Option[String] = None
)

object Identity:
  given JsonCodec[Identity] = DeriveJsonCodec.gen[Identity]
