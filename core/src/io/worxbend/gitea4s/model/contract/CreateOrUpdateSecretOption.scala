package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateOrUpdateSecretOption(
    @jsonField("data") data: String,
    @jsonField("description") description: Option[String] = None
)

object CreateOrUpdateSecretOption:
  given JsonCodec[CreateOrUpdateSecretOption] = DeriveJsonCodec.gen[CreateOrUpdateSecretOption]
