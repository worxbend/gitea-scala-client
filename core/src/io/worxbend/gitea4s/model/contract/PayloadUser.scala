package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PayloadUser(
    @jsonField("email") email: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("username") username: Option[String] = None
)

object PayloadUser:
  given JsonCodec[PayloadUser] = DeriveJsonCodec.gen[PayloadUser]
