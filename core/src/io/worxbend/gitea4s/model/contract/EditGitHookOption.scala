package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditGitHookOption(
    @jsonField("content") content: Option[String] = None
)

object EditGitHookOption:
  given JsonCodec[EditGitHookOption] = DeriveJsonCodec.gen[EditGitHookOption]
