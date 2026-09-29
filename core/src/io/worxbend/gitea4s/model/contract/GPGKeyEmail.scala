package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GPGKeyEmail(
    @jsonField("email") email: Option[String] = None,
    @jsonField("verified") verified: Option[Boolean] = None
)

object GPGKeyEmail:
  given JsonCodec[GPGKeyEmail] = DeriveJsonCodec.gen[GPGKeyEmail]
