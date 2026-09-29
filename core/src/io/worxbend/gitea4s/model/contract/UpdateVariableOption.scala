package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UpdateVariableOption(
    @jsonField("description") description: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("value") value: String
)

object UpdateVariableOption:
  given JsonCodec[UpdateVariableOption] = DeriveJsonCodec.gen[UpdateVariableOption]
