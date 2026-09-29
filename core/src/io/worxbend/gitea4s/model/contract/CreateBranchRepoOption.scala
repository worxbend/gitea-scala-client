package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateBranchRepoOption(
    @jsonField("new_branch_name") newBranchName: String,
    @jsonField("old_branch_name") oldBranchName: Option[String] = None,
    @jsonField("old_ref_name") oldRefName: Option[String] = None
)

object CreateBranchRepoOption:
  given JsonCodec[CreateBranchRepoOption] = DeriveJsonCodec.gen[CreateBranchRepoOption]
