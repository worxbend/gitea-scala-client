package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditActionRunnerOption(
    @jsonField("disabled") disabled: Boolean
)

object EditActionRunnerOption:
  given JsonCodec[EditActionRunnerOption] = DeriveJsonCodec.gen[EditActionRunnerOption]
