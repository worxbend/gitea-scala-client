package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Email(
    @jsonField("email") email: Option[String] = None,
    @jsonField("primary") primary: Option[Boolean] = None,
    @jsonField("user_id") userId: Option[Long] = None,
    @jsonField("username") username: Option[String] = None,
    @jsonField("verified") verified: Option[Boolean] = None
)

object Email:
  given JsonCodec[Email] = DeriveJsonCodec.gen[Email]
