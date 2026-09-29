package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class MergePullRequestOption(
    @jsonField("delete_branch_after_merge") deleteBranchAfterMerge: Option[Boolean] = None,
    @jsonField("do") `do`: String,
    @jsonField("force_merge") forceMerge: Option[Boolean] = None,
    @jsonField("head_commit_id") headCommitId: Option[String] = None,
    @jsonField("merge_commit_id") mergeCommitId: Option[String] = None,
    @jsonField("merge_message_field") mergeMessageField: Option[String] = None,
    @jsonField("merge_title_field") mergeTitleField: Option[String] = None,
    @jsonField("merge_when_checks_succeed") mergeWhenChecksSucceed: Option[Boolean] = None
)

object MergePullRequestOption:
  given JsonCodec[MergePullRequestOption] = DeriveJsonCodec.gen[MergePullRequestOption]
