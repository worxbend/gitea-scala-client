package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RenameUserOption(
    @jsonField("new_username") newUsername: String
)

object RenameUserOption:
  given JsonCodec[RenameUserOption] = DeriveJsonCodec.gen[RenameUserOption]
