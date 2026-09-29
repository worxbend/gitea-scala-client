package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditReactionOption(
    @jsonField("content") content: Option[String] = None
)

object EditReactionOption:
  given JsonCodec[EditReactionOption] = DeriveJsonCodec.gen[EditReactionOption]
