package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateEmailOption(
    @jsonField("emails") emails: Option[List[String]] = None
)

object CreateEmailOption:
  given JsonCodec[CreateEmailOption] = DeriveJsonCodec.gen[CreateEmailOption]
