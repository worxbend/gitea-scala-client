package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateBranchProtectionOption(
    @jsonField("approvals_whitelist_teams") approvalsWhitelistTeams: Option[List[String]] = None,
    @jsonField("approvals_whitelist_username") approvalsWhitelistUsername: Option[List[String]] = None,
    @jsonField("block_admin_merge_override") blockAdminMergeOverride: Option[Boolean] = None,
    @jsonField("block_on_official_review_requests") blockOnOfficialReviewRequests: Option[Boolean] = None,
    @jsonField("block_on_outdated_branch") blockOnOutdatedBranch: Option[Boolean] = None,
    @jsonField("block_on_rejected_reviews") blockOnRejectedReviews: Option[Boolean] = None,
    @jsonField("branch_name") branchName: Option[String] = None,
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
    @jsonField("priority") priority: Option[Long] = None,
    @jsonField("protected_file_patterns") protectedFilePatterns: Option[String] = None,
    @jsonField("push_whitelist_deploy_keys") pushWhitelistDeployKeys: Option[Boolean] = None,
    @jsonField("push_whitelist_teams") pushWhitelistTeams: Option[List[String]] = None,
    @jsonField("push_whitelist_usernames") pushWhitelistUsernames: Option[List[String]] = None,
    @jsonField("require_signed_commits") requireSignedCommits: Option[Boolean] = None,
    @jsonField("required_approvals") requiredApprovals: Option[Long] = None,
    @jsonField("rule_name") ruleName: Option[String] = None,
    @jsonField("status_check_contexts") statusCheckContexts: Option[List[String]] = None,
    @jsonField("unprotected_file_patterns") unprotectedFilePatterns: Option[String] = None
)

object CreateBranchProtectionOption:
  given JsonCodec[CreateBranchProtectionOption] = DeriveJsonCodec.gen[CreateBranchProtectionOption]
