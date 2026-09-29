package io.worxbend.gitea4s.http.contract

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.http.*
import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.model.contract
import sttp.client4.*
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.StatusCode
import zio.json.*
import zio.test.*

object GeneratedWire00Spec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private def respond(body: String, status: StatusCode) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("generated contract wire requests")(
      test("listAdminWorkflowJobs") {
        val built = GeneratedActionsRequests.listAdminWorkflowJobs(config, Some("example"), Some(11), Some(11), Some("example"), Some("example"))
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"jobs\":[{\"completed_at\":\"2026-09-29T00:00:00Z\",\"conclusion\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"head_branch\":\"example\",\"head_sha\":\"example\",\"html_url\":\"example\",\"id\":3,\"labels\":[\"example\"],\"name\":\"example\",\"run_attempt\":3,\"run_id\":3,\"run_url\":\"example\",\"runner_id\":3,\"runner_name\":\"example\",\"started_at\":\"2026-09-29T00:00:00Z\",\"status\":\"example\",\"steps\":[{\"completed_at\":\"2026-09-29T00:00:00Z\",\"conclusion\":\"example\",\"name\":\"example\",\"number\":3,\"started_at\":\"2026-09-29T00:00:00Z\",\"status\":\"example\"}],\"url\":\"example\"}],\"total_count\":3}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/jobs"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("status", "example"), ("page", "11"), ("limit", "11"), ("sort", "example"), ("order", "example")), request.body == NoBody)
      },
      test("getAdminRunners") {
        val built = GeneratedActionsRequests.getAdminRunners(config, Some(true))
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"runners\":[{\"busy\":false,\"disabled\":false,\"ephemeral\":false,\"id\":3,\"labels\":[{\"id\":3,\"name\":\"example\",\"type\":\"example\"}],\"name\":\"example\",\"status\":\"example\"}],\"total_count\":3}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/runners"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("disabled", "true")), request.body == NoBody)
      },
      test("adminCreateRunnerRegistrationToken") {
        val built = GeneratedActionsRequests.adminCreateRunnerRegistrationToken(config)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/runners/registration-token"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("getAdminRunner") {
        val built = GeneratedActionsRequests.getAdminRunner(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"busy\":false,\"disabled\":false,\"ephemeral\":false,\"id\":3,\"labels\":[{\"id\":3,\"name\":\"example\",\"type\":\"example\"}],\"name\":\"example\",\"status\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/runners/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("deleteAdminRunner") {
        val built = GeneratedActionsRequests.deleteAdminRunner(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/runners/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("updateAdminRunner") {
        val built = GeneratedActionsRequests.updateAdminRunner(config, "space name", "{\"disabled\":false}".fromJson[contract.EditActionRunnerOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"busy\":false,\"disabled\":false,\"ephemeral\":false,\"id\":3,\"labels\":[{\"id\":3,\"name\":\"example\",\"type\":\"example\"}],\"name\":\"example\",\"status\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/runners/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"disabled\":false}".fromJson[zio.json.ast.Json])
      },
      test("listAdminWorkflowRuns") {
        val built = GeneratedActionsRequests.listAdminWorkflowRuns(config, Some("example"), Some("example"), Some("example"), Some("example"), Some("example"), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"total_count\":3,\"workflow_runs\":[{\"actor\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"completed_at\":\"2026-09-29T00:00:00Z\",\"conclusion\":\"example\",\"display_title\":\"example\",\"event\":\"example\",\"head_branch\":\"example\",\"head_repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"head_sha\":\"example\",\"html_url\":\"example\",\"id\":3,\"path\":\"example\",\"previous_attempt_url\":\"example\",\"pull_requests\":[{\"base\":{\"ref\":\"example\",\"repo\":{\"id\":3,\"name\":\"example\",\"url\":\"example\"},\"sha\":\"example\"},\"head\":{\"ref\":\"example\",\"repo\":{\"id\":3,\"name\":\"example\",\"url\":\"example\"},\"sha\":\"example\"},\"id\":3,\"number\":3,\"url\":\"example\"}],\"repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"repository_id\":3,\"run_attempt\":3,\"run_number\":3,\"started_at\":\"2026-09-29T00:00:00Z\",\"status\":\"example\",\"trigger_actor\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"url\":\"example\"}]}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/actions/runs"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("event", "example"), ("branch", "example"), ("status", "example"), ("actor", "example"), ("head_sha", "example"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("adminCronList") {
        val built = GeneratedAdminRequests.adminCronList(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"exec_times\":3,\"name\":\"example\",\"next\":\"2026-09-29T00:00:00Z\",\"prev\":\"2026-09-29T00:00:00Z\",\"schedule\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/cron"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("adminCronRun") {
        val built = GeneratedAdminRequests.adminCronRun(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/cron/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminGetAllEmails") {
        val built = GeneratedAdminRequests.adminGetAllEmails(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"email\":\"example\",\"primary\":false,\"user_id\":3,\"username\":\"example\",\"verified\":false}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/emails"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("adminSearchEmails") {
        val built = GeneratedAdminRequests.adminSearchEmails(config, Some("example"), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"email\":\"example\",\"primary\":false,\"user_id\":3,\"username\":\"example\",\"verified\":false}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/emails/search"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("q", "example"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("adminListHooks") {
        val built = GeneratedAdminRequests.adminListHooks(config, Some(11), Some(11), Some(contract.AdminHookType.System))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/hooks"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11"), ("type", "system")), request.body == NoBody)
      },
      test("adminCreateHook") {
        val built = GeneratedAdminRequests.adminCreateHook(config, "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\",\"type\":\"dingtalk\"}".fromJson[contract.CreateHookOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/hooks"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\",\"type\":\"dingtalk\"}".fromJson[zio.json.ast.Json])
      },
      test("adminGetHook") {
        val built = GeneratedAdminRequests.adminGetHook(config, 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/hooks/11"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminDeleteHook") {
        val built = GeneratedAdminRequests.adminDeleteHook(config, 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/hooks/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminEditHook") {
        val built = GeneratedAdminRequests.adminEditHook(config, 11L, "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\"}".fromJson[contract.EditHookOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/hooks/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("adminGetAllOrgs") {
        val built = GeneratedAdminRequests.adminGetAllOrgs(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/orgs"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("adminUnadoptedList") {
        val built = GeneratedAdminRequests.adminUnadoptedList(config, Some(11), Some(11), Some("example"))
        val request = built.request
        val decoded = built.decode(request.send(respond("[\"example\"]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/unadopted"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11"), ("pattern", "example")), request.body == NoBody)
      },
      test("adminAdoptRepository") {
        val built = GeneratedAdminRequests.adminAdoptRepository(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/unadopted/space%20name/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminDeleteUnadoptedRepository") {
        val built = GeneratedAdminRequests.adminDeleteUnadoptedRepository(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/unadopted/space%20name/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminSearchUsers") {
        val built = GeneratedAdminRequests.adminSearchUsers(config, Some(11L), Some("example"), Some(11), Some(11), Some("example"), Some("example"), Some("example"), Some("example"), Some(true), Some(true), Some(true), Some(true), Some(true))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("source_id", "11"), ("login_name", "example"), ("page", "11"), ("limit", "11"), ("sort", "example"), ("order", "example"), ("q", "example"), ("visibility", "example"), ("is_active", "true"), ("is_admin", "true"), ("is_restricted", "true"), ("is_2fa_enabled", "true"), ("is_prohibit_login", "true")), request.body == NoBody)
      },
      test("adminCreateUser") {
        val built = GeneratedAdminRequests.adminCreateUser(config, "{\"created_at\":\"2026-09-29T00:00:00Z\",\"email\":\"example\",\"full_name\":\"example\",\"login_name\":\"example\",\"must_change_password\":false,\"password\":\"example\",\"restricted\":false,\"send_notify\":false,\"source_id\":3,\"username\":\"example\",\"visibility\":\"public\"}".fromJson[contract.CreateUserOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"created_at\":\"2026-09-29T00:00:00Z\",\"email\":\"example\",\"full_name\":\"example\",\"login_name\":\"example\",\"must_change_password\":false,\"password\":\"example\",\"restricted\":false,\"send_notify\":false,\"source_id\":3,\"username\":\"example\",\"visibility\":\"public\"}".fromJson[zio.json.ast.Json])
      },
      test("adminDeleteUser") {
        val built = GeneratedAdminRequests.adminDeleteUser(config, "space name", Some(true))
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("purge", "true")), request.body == NoBody)
      },
      test("adminEditUser") {
        val built = GeneratedAdminRequests.adminEditUser(config, "space name", "{\"active\":false,\"admin\":false,\"allow_create_organization\":false,\"allow_git_hook\":false,\"allow_import_local\":false,\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"location\":\"example\",\"login_name\":\"example\",\"max_repo_creation\":3,\"must_change_password\":false,\"password\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"visibility\":\"public\",\"website\":\"example\"}".fromJson[contract.EditUserOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"active\":false,\"admin\":false,\"allow_create_organization\":false,\"allow_git_hook\":false,\"allow_import_local\":false,\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"location\":\"example\",\"login_name\":\"example\",\"max_repo_creation\":3,\"must_change_password\":false,\"password\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"visibility\":\"public\",\"website\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("adminListUserBadges") {
        val built = GeneratedAdminRequests.adminListUserBadges(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"description\":\"example\",\"id\":3,\"image_url\":\"example\",\"slug\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name/badges"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminAddUserBadges") {
        val built = GeneratedAdminRequests.adminAddUserBadges(config, "space name", "{\"badge_slugs\":[\"example\"]}".fromJson[contract.UserBadgeOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name/badges"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"badge_slugs\":[\"example\"]}".fromJson[zio.json.ast.Json])
      },
      test("adminDeleteUserBadges") {
        val built = GeneratedAdminRequests.adminDeleteUserBadges(config, "space name", "{\"badge_slugs\":[\"example\"]}".fromJson[contract.UserBadgeOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name/badges"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"badge_slugs\":[\"example\"]}".fromJson[zio.json.ast.Json])
      },
      test("adminCreatePublicKey") {
        val built = GeneratedAdminRequests.adminCreatePublicKey(config, "space name", "{\"key\":\"example\",\"read_only\":false,\"title\":\"example\"}".fromJson[contract.CreateKeyOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"created_at\":\"2026-09-29T00:00:00Z\",\"fingerprint\":\"example\",\"id\":3,\"key\":\"example\",\"key_type\":\"example\",\"last_used_at\":\"2026-09-29T00:00:00Z\",\"read_only\":false,\"title\":\"example\",\"url\":\"example\",\"user\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name/keys"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"key\":\"example\",\"read_only\":false,\"title\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("adminDeleteUserPublicKey") {
        val built = GeneratedAdminRequests.adminDeleteUserPublicKey(config, "space name", 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name/keys/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("adminCreateOrg") {
        val built = GeneratedAdminRequests.adminCreateOrg(config, "space name", "{\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"location\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"}".fromJson[contract.CreateOrgOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/admin/users/space%20name/orgs"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"location\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"}".fromJson[zio.json.ast.Json])
      }
    )
