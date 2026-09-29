package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UserMeta(
    @jsonField("id") id: Option[Long] = None,
    @jsonField("login") login: Option[String] = None
)

object UserMeta:
  given JsonCodec[UserMeta] = DeriveJsonCodec.gen[UserMeta]
