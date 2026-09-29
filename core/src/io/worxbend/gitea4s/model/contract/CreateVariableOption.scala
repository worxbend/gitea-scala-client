package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateVariableOption(
    @jsonField("description") description: Option[String] = None,
    @jsonField("value") value: String
)

object CreateVariableOption:
  given JsonCodec[CreateVariableOption] = DeriveJsonCodec.gen[CreateVariableOption]
