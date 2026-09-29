package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class DeleteEmailOption(
    @jsonField("emails") emails: Option[List[String]] = None
)

object DeleteEmailOption:
  given JsonCodec[DeleteEmailOption] = DeriveJsonCodec.gen[DeleteEmailOption]
