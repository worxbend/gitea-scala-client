package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RenameBranchRepoOption(
    @jsonField("name") name: String
)

object RenameBranchRepoOption:
  given JsonCodec[RenameBranchRepoOption] = DeriveJsonCodec.gen[RenameBranchRepoOption]
