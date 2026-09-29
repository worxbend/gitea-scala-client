package io.worxbend.gitea4s.model

import zio.json.*

/** Fields shared by the create and edit contracts; absent values are omitted on the wire. */
final case class BranchProtectionSettings(
    @jsonField("approvals_whitelist_teams") approvalsWhitelistTeams: Option[List[String]] = None,
    @jsonField("approvals_whitelist_username") approvalsWhitelistUsernames: Option[List[String]] = None,
    @jsonField("block_admin_merge_override") blockAdminMergeOverride: Option[Boolean] = None,
    @jsonField("block_on_official_review_requests") blockOnOfficialReviewRequests: Option[Boolean] = None,
    @jsonField("block_on_outdated_branch") blockOnOutdatedBranch: Option[Boolean] = None,
    @jsonField("block_on_rejected_reviews") blockOnRejectedReviews: Option[Boolean] = None,
    @jsonField("bypass_allowlist_teams") bypassAllowlistTeams: Option[List[String]] = None,
    @jsonField("bypass_allowlist_usernames") bypassAllowlistUsernames: Option[List[String]] = None,
    @jsonField("dismiss_stale_approvals") dismissStaleApprovals: Option[Boolean] = None,
    @jsonField("enable_approvals_whitelist") enableApprovalsWhitelist: Option[Boolean] = None,
    @jsonField("enable_bypass_allowlist") enableBypassAllowlist: Option[Boolean] = None,
    @jsonField("enable_force_push") enableForcePush: Option[Boolean] = None,
    @jsonField("enable_force_push_allowlist") enableForcePushAllowlist: Option[Boolean] = None,
    @jsonField("enable_merge_whitelist") enableMergeWhitelist: Option[Boolean] = None,
    @jsonField("enable_push") enablePush: Option[Boolean] = None,
    @jsonField("enable_push_whitelist") enablePushWhitelist: Option[Boolean] = None,
    @jsonField("enable_status_check") enableStatusCheck: Option[Boolean] = None,
    @jsonField("force_push_allowlist_deploy_keys") forcePushAllowlistDeployKeys: Option[Boolean] = None,
    @jsonField("force_push_allowlist_teams") forcePushAllowlistTeams: Option[List[String]] = None,
    @jsonField("force_push_allowlist_usernames") forcePushAllowlistUsernames: Option[List[String]] = None,
    @jsonField("ignore_stale_approvals") ignoreStaleApprovals: Option[Boolean] = None,
    @jsonField("merge_whitelist_teams") mergeWhitelistTeams: Option[List[String]] = None,
    @jsonField("merge_whitelist_usernames") mergeWhitelistUsernames: Option[List[String]] = None,
    priority: Option[Long] = None,
    @jsonField("protected_file_patterns") protectedFilePatterns: Option[String] = None,
    @jsonField("push_whitelist_deploy_keys") pushWhitelistDeployKeys: Option[Boolean] = None,
    @jsonField("push_whitelist_teams") pushWhitelistTeams: Option[List[String]] = None,
    @jsonField("push_whitelist_usernames") pushWhitelistUsernames: Option[List[String]] = None,
    @jsonField("require_signed_commits") requireSignedCommits: Option[Boolean] = None,
    @jsonField("required_approvals") requiredApprovals: Option[Long] = None,
    @jsonField("status_check_contexts") statusCheckContexts: Option[List[String]] = None,
    @jsonField("unprotected_file_patterns") unprotectedFilePatterns: Option[String] = None
)

object BranchProtectionSettings:
  given JsonCodec[BranchProtectionSettings] = DeriveJsonCodec.gen[BranchProtectionSettings]

/** Flattens the shared settings into the create payload. */
final case class CreateBranchProtectionOption(
    settings: BranchProtectionSettings = BranchProtectionSettings(),
    branchName: Option[String] = None,
    ruleName: Option[String] = None
):
  def toJsonBody: String =
    val fields = List(branchName.map(value => "\"branch_name\":" + value.toJson), ruleName.map(value => "\"rule_name\":" + value.toJson)).flatten
    val shared = settings.toJson.dropRight(1).drop(1)
    "{" + (if shared.isEmpty then fields else shared :: fields).mkString(",") + "}"

final case class EditBranchProtectionOption(settings: BranchProtectionSettings = BranchProtectionSettings()):
  def toJsonBody: String = settings.toJson

final case class UpdateBranchProtectionPriorities(ids: List[Long])

object UpdateBranchProtectionPriorities:
  given JsonCodec[UpdateBranchProtectionPriorities] = DeriveJsonCodec.gen[UpdateBranchProtectionPriorities]
