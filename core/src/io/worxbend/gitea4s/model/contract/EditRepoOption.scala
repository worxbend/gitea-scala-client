package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditRepoOption(
    @jsonField("allow_fast_forward_only_merge") allowFastForwardOnlyMerge: Option[Boolean] = None,
    @jsonField("allow_manual_merge") allowManualMerge: Option[Boolean] = None,
    @jsonField("allow_merge_commits") allowMergeCommits: Option[Boolean] = None,
    @jsonField("allow_merge_update") allowMergeUpdate: Option[Boolean] = None,
    @jsonField("allow_rebase") allowRebase: Option[Boolean] = None,
    @jsonField("allow_rebase_explicit") allowRebaseExplicit: Option[Boolean] = None,
    @jsonField("allow_rebase_update") allowRebaseUpdate: Option[Boolean] = None,
    @jsonField("allow_squash_merge") allowSquashMerge: Option[Boolean] = None,
    @jsonField("archived") archived: Option[Boolean] = None,
    @jsonField("autodetect_manual_merge") autodetectManualMerge: Option[Boolean] = None,
    @jsonField("default_allow_maintainer_edit") defaultAllowMaintainerEdit: Option[Boolean] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    @jsonField("default_delete_branch_after_merge") defaultDeleteBranchAfterMerge: Option[Boolean] = None,
    @jsonField("default_merge_style") defaultMergeStyle: Option[String] = None,
    @jsonField("default_update_style") defaultUpdateStyle: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("enable_prune") enablePrune: Option[Boolean] = None,
    @jsonField("external_tracker") externalTracker: Option[ExternalTracker] = None,
    @jsonField("external_wiki") externalWiki: Option[ExternalWiki] = None,
    @jsonField("has_actions") hasActions: Option[Boolean] = None,
    @jsonField("has_code") hasCode: Option[Boolean] = None,
    @jsonField("has_issues") hasIssues: Option[Boolean] = None,
    @jsonField("has_packages") hasPackages: Option[Boolean] = None,
    @jsonField("has_projects") hasProjects: Option[Boolean] = None,
    @jsonField("has_pull_requests") hasPullRequests: Option[Boolean] = None,
    @jsonField("has_releases") hasReleases: Option[Boolean] = None,
    @jsonField("has_wiki") hasWiki: Option[Boolean] = None,
    @jsonField("ignore_whitespace_conflicts") ignoreWhitespaceConflicts: Option[Boolean] = None,
    @jsonField("internal_tracker") internalTracker: Option[InternalTracker] = None,
    @jsonField("mirror_interval") mirrorInterval: Option[String] = None,
    @jsonField("mirror_password") mirrorPassword: Option[String] = None,
    @jsonField("mirror_token") mirrorToken: Option[String] = None,
    @jsonField("mirror_username") mirrorUsername: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("private") `private`: Option[Boolean] = None,
    @jsonField("projects_mode") projectsMode: Option[String] = None,
    @jsonField("template") template: Option[Boolean] = None,
    @jsonField("website") website: Option[String] = None
):
  override def toString: String = "EditRepoOption(<redacted>)"

object EditRepoOption:
  given JsonCodec[EditRepoOption] = DeriveJsonCodec.gen[EditRepoOption]
