package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CommitUser(
    @jsonField("date") date: Option[String] = None,
    @jsonField("email") email: Option[String] = None,
    @jsonField("name") name: Option[String] = None
)

object CommitUser:
  given JsonCodec[CommitUser] = DeriveJsonCodec.gen[CommitUser]
