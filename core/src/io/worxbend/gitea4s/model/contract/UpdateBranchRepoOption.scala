package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UpdateBranchRepoOption(
    @jsonField("force") force: Option[Boolean] = None,
    @jsonField("new_commit_id") newCommitId: String,
    @jsonField("old_commit_id") oldCommitId: Option[String] = None
)

object UpdateBranchRepoOption:
  given JsonCodec[UpdateBranchRepoOption] = DeriveJsonCodec.gen[UpdateBranchRepoOption]
