package io.worxbend.gitea4s.model

import zio.json.*
import zio.json.ast.Json
import zio.test.*

object BranchProtectionModelSpec extends ZIOSpecDefault:
  val sharedSchemaFields: Set[String] = Set(
    "approvals_whitelist_teams", "approvals_whitelist_username", "block_admin_merge_override",
    "block_on_official_review_requests", "block_on_outdated_branch", "block_on_rejected_reviews",
    "bypass_allowlist_teams", "bypass_allowlist_usernames", "dismiss_stale_approvals",
    "enable_approvals_whitelist", "enable_bypass_allowlist", "enable_force_push", "enable_force_push_allowlist",
    "enable_merge_whitelist", "enable_push", "enable_push_whitelist", "enable_status_check",
    "force_push_allowlist_deploy_keys", "force_push_allowlist_teams", "force_push_allowlist_usernames",
    "ignore_stale_approvals", "merge_whitelist_teams", "merge_whitelist_usernames", "priority",
    "protected_file_patterns", "push_whitelist_deploy_keys", "push_whitelist_teams",
    "push_whitelist_usernames", "require_signed_commits", "required_approvals",
    "status_check_contexts", "unprotected_file_patterns"
  )

  private val settings = BranchProtectionSettings(
    approvalsWhitelistTeams = Some(List("team")),
    approvalsWhitelistUsernames = Some(List("alice")),
    blockAdminMergeOverride = Some(false),
    blockOnOfficialReviewRequests = Some(false),
    blockOnOutdatedBranch = Some(false),
    blockOnRejectedReviews = Some(false),
    bypassAllowlistTeams = Some(Nil),
    bypassAllowlistUsernames = Some(Nil),
    dismissStaleApprovals = Some(false),
    enableApprovalsWhitelist = Some(false),
    enableBypassAllowlist = Some(false),
    enableForcePush = Some(false),
    enableForcePushAllowlist = Some(false),
    enableMergeWhitelist = Some(false),
    enablePush = Some(false),
    enablePushWhitelist = Some(false),
    enableStatusCheck = Some(false),
    forcePushAllowlistDeployKeys = Some(false),
    forcePushAllowlistTeams = Some(Nil),
    forcePushAllowlistUsernames = Some(Nil),
    ignoreStaleApprovals = Some(false),
    mergeWhitelistTeams = Some(Nil),
    mergeWhitelistUsernames = Some(Nil),
    priority = Some(0L),
    protectedFilePatterns = Some("*.lock"),
    pushWhitelistDeployKeys = Some(false),
    pushWhitelistTeams = Some(Nil),
    pushWhitelistUsernames = Some(Nil),
    requireSignedCommits = Some(false),
    requiredApprovals = Some(0L),
    statusCheckContexts = Some(Nil),
    unprotectedFilePatterns = Some("README.md")
  )

  private def keys(json: String): Set[String] =
    json.fromJson[Json] match
      case Right(Json.Obj(fields)) => fields.map(_._1).toSet
      case other => throw new IllegalArgumentException(s"Expected JSON object: $other")

  def spec =
    suite("branch protection models")(
      test("create and edit cover all schema fields and keep false, zero, and empty lists") {
        val editFields = keys(EditBranchProtectionOption(settings).toJsonBody)
        val createFields = keys(CreateBranchProtectionOption(settings, Some("main"), Some("main rule")).toJsonBody)
        assertTrue(
          editFields == sharedSchemaFields,
          createFields == sharedSchemaFields ++ Set("branch_name", "rule_name"),
          keys(CreateBranchProtectionOption().toJsonBody).isEmpty,
          keys(EditBranchProtectionOption().toJsonBody).isEmpty,
          settings.toJson.fromJson[BranchProtectionSettings] == Right(settings)
        )
      },
      test("response decodes bypass allowlist fields") {
        val json = """{"rule_name":"main","bypass_allowlist_teams":[],"bypass_allowlist_usernames":["alice"],"enable_bypass_allowlist":false}"""
        assertTrue(
          json.fromJson[BranchProtection].map(_.enableBypassAllowlist) == Right(Some(false)),
          json.fromJson[BranchProtection].map(_.bypassAllowlistUsernames) == Right(Some(List("alice")))
        )
      },
      test("response round-trips every current response field") {
        val listFields = Set(
          "approvals_whitelist_teams", "approvals_whitelist_username", "bypass_allowlist_teams",
          "bypass_allowlist_usernames", "force_push_allowlist_teams", "force_push_allowlist_usernames",
          "merge_whitelist_teams", "merge_whitelist_usernames", "push_whitelist_teams",
          "push_whitelist_usernames", "status_check_contexts"
        )
        val stringFields = Set("branch_name", "rule_name", "protected_file_patterns", "unprotected_file_patterns")
        val dateFields = Set("created_at", "updated_at")
        val longFields = Set("priority", "required_approvals")
        val allFields = sharedSchemaFields ++ Set("branch_name", "rule_name", "created_at", "updated_at")
        val fields = allFields.toList.sorted.map { name =>
          val value =
            if listFields(name) then Json.Arr(Json.Str("alice"))
            else if stringFields(name) then Json.Str("main")
            else if dateFields(name) then Json.Str("2026-01-01T00:00:00Z")
            else if longFields(name) then Json.Num(2)
            else Json.Bool(false)
          name -> value
        }
        val decoded = Json.Obj(fields*).toJson.fromJson[BranchProtection]
        assertTrue(decoded.map(value => keys(value.toJson)) == Right(allFields))
      },
      test("priority request uses the documented ids field") {
        assertTrue(UpdateBranchProtectionPriorities(List(2L, 1L)).toJson == """{"ids":[2,1]}""")
      }
    )
