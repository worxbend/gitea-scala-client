package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateForkOption(
    @jsonField("name") name: Option[String] = None,
    @jsonField("organization") organization: Option[String] = None
)

object CreateForkOption:
  given JsonCodec[CreateForkOption] = DeriveJsonCodec.gen[CreateForkOption]
