package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RenameOrgOption(
    @jsonField("new_name") newName: String
)

object RenameOrgOption:
  given JsonCodec[RenameOrgOption] = DeriveJsonCodec.gen[RenameOrgOption]
