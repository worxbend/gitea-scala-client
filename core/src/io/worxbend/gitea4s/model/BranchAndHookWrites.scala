package io.worxbend.gitea4s.model

import zio.json.*

final case class UpdateBranchRepoOption(
    @jsonField("new_commit_id") newCommitId: String,
    @jsonField("old_commit_id") oldCommitId: Option[String] = None,
    force: Option[Boolean] = None
)

object UpdateBranchRepoOption:
  given JsonCodec[UpdateBranchRepoOption] = DeriveJsonCodec.gen[UpdateBranchRepoOption]

final case class RenameBranchRepoOption(name: String)

object RenameBranchRepoOption:
  given JsonCodec[RenameBranchRepoOption] = DeriveJsonCodec.gen[RenameBranchRepoOption]

final case class GitHook(
    content: Option[String] = None,
    @jsonField("is_active") isActive: Option[Boolean] = None,
    name: Option[String] = None
)

object GitHook:
  given JsonCodec[GitHook] = DeriveJsonCodec.gen[GitHook]

final case class EditGitHookOption(content: Option[String] = None)

object EditGitHookOption:
  given JsonCodec[EditGitHookOption] = DeriveJsonCodec.gen[EditGitHookOption]
