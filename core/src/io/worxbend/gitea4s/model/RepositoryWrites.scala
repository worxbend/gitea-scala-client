package io.worxbend.gitea4s.model

import zio.Chunk
import zio.json.*

enum RepoTrustModel(val jsonValue: String):
  case Default extends RepoTrustModel("default")
  case Collaborator extends RepoTrustModel("collaborator")
  case Committer extends RepoTrustModel("committer")
  case CollaboratorCommitter extends RepoTrustModel("collaboratorcommitter")

object RepoTrustModel:
  private val json = JsonValueLookup(RepoTrustModel.values, "repository trust model", _.jsonValue)
  def fromString(value: String): Either[String, RepoTrustModel] = json.fromString(value)
  given JsonCodec[RepoTrustModel] = json.codec

final case class CreateRepoOption(
    name: String,
    @jsonField("auto_init") autoInit: Option[Boolean] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    description: Option[String] = None,
    gitignores: Option[String] = None,
    @jsonField("issue_labels") issueLabels: Option[String] = None,
    license: Option[String] = None,
    @jsonField("object_format_name") objectFormatName: Option[ObjectFormatName] = None,
    @jsonField("private") isPrivate: Option[Boolean] = None,
    readme: Option[String] = None,
    template: Option[Boolean] = None,
    @jsonField("trust_model") trustModel: Option[RepoTrustModel] = None
)

object CreateRepoOption:
  given JsonCodec[CreateRepoOption] = DeriveJsonCodec.gen[CreateRepoOption]

final case class CreateForkOption(name: Option[String] = None, organization: Option[String] = None)

object CreateForkOption:
  given JsonCodec[CreateForkOption] = DeriveJsonCodec.gen[CreateForkOption]

final case class CreateBranchRepoOption(
    @jsonField("new_branch_name") newBranchName: String,
    @jsonField("old_branch_name") oldBranchName: Option[String] = None,
    @jsonField("old_ref_name") oldRefName: Option[String] = None
)

object CreateBranchRepoOption:
  given JsonCodec[CreateBranchRepoOption] = DeriveJsonCodec.gen[CreateBranchRepoOption]

final case class TransferRepoOption(
    @jsonField("new_owner") newOwner: String,
    @jsonField("team_ids") teamIds: Option[Chunk[Long]] = None
)

object TransferRepoOption:
  given JsonCodec[TransferRepoOption] = DeriveJsonCodec.gen[TransferRepoOption]

final case class ExternalTracker(
    @jsonField("external_tracker_format") format: Option[String] = None,
    @jsonField("external_tracker_regexp_pattern") regexpPattern: Option[String] = None,
    @jsonField("external_tracker_style") style: Option[String] = None,
    @jsonField("external_tracker_url") url: Option[String] = None
)

object ExternalTracker:
  given JsonCodec[ExternalTracker] = DeriveJsonCodec.gen[ExternalTracker]

final case class ExternalWiki(@jsonField("external_wiki_url") url: Option[String] = None)

object ExternalWiki:
  given JsonCodec[ExternalWiki] = DeriveJsonCodec.gen[ExternalWiki]

final case class InternalTracker(
    @jsonField("allow_only_contributors_to_track_time") allowOnlyContributorsToTrackTime: Option[Boolean] = None,
    @jsonField("enable_issue_dependencies") enableIssueDependencies: Option[Boolean] = None,
    @jsonField("enable_time_tracker") enableTimeTracker: Option[Boolean] = None
)

object InternalTracker:
  given JsonCodec[InternalTracker] = DeriveJsonCodec.gen[InternalTracker]

/** All optional fields in Gitea's EditRepoOption schema. Mirror credentials are redacted from logs. */
final case class EditRepoOption(
    @jsonField("allow_fast_forward_only_merge") allowFastForwardOnlyMerge: Option[Boolean] = None,
    @jsonField("allow_manual_merge") allowManualMerge: Option[Boolean] = None,
    @jsonField("allow_merge_commits") allowMergeCommits: Option[Boolean] = None,
    @jsonField("allow_merge_update") allowMergeUpdate: Option[Boolean] = None,
    @jsonField("allow_rebase") allowRebase: Option[Boolean] = None,
    @jsonField("allow_rebase_explicit") allowRebaseExplicit: Option[Boolean] = None,
    @jsonField("allow_rebase_update") allowRebaseUpdate: Option[Boolean] = None,
    @jsonField("allow_squash_merge") allowSquashMerge: Option[Boolean] = None,
    archived: Option[Boolean] = None,
    @jsonField("autodetect_manual_merge") autodetectManualMerge: Option[Boolean] = None,
    @jsonField("default_allow_maintainer_edit") defaultAllowMaintainerEdit: Option[Boolean] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    @jsonField("default_delete_branch_after_merge") defaultDeleteBranchAfterMerge: Option[Boolean] = None,
    @jsonField("default_merge_style") defaultMergeStyle: Option[String] = None,
    @jsonField("default_update_style") defaultUpdateStyle: Option[String] = None,
    description: Option[String] = None,
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
    name: Option[String] = None,
    @jsonField("private") isPrivate: Option[Boolean] = None,
    @jsonField("projects_mode") projectsMode: Option[String] = None,
    template: Option[Boolean] = None,
    website: Option[String] = None
):
  override def toString: String = "EditRepoOption(<redacted>)"

object EditRepoOption:
  given JsonCodec[EditRepoOption] = DeriveJsonCodec.gen[EditRepoOption]
