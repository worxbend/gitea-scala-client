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

object GeneratedWire08Spec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private def respond(body: String, status: StatusCode) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("generated contract wire requests")(
      test("deleteUserRunner") {
        val built = GeneratedActionsRequests.deleteUserRunner(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/runners/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("updateUserRunner") {
        val built = GeneratedActionsRequests.updateUserRunner(config, "space name", "{\"disabled\":false}".fromJson[contract.EditActionRunnerOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"busy\":false,\"disabled\":false,\"ephemeral\":false,\"id\":3,\"labels\":[{\"id\":3,\"name\":\"example\",\"type\":\"example\"}],\"name\":\"example\",\"status\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/runners/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"disabled\":false}".fromJson[zio.json.ast.Json])
      },
      test("getUserWorkflowRuns") {
        val built = GeneratedActionsRequests.getUserWorkflowRuns(config, Some("example"), Some("example"), Some("example"), Some("example"), Some("example"), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"total_count\":3,\"workflow_runs\":[{\"actor\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"completed_at\":\"2026-09-29T00:00:00Z\",\"conclusion\":\"example\",\"display_title\":\"example\",\"event\":\"example\",\"head_branch\":\"example\",\"head_repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"head_sha\":\"example\",\"html_url\":\"example\",\"id\":3,\"path\":\"example\",\"previous_attempt_url\":\"example\",\"pull_requests\":[{\"base\":{\"ref\":\"example\",\"repo\":{\"id\":3,\"name\":\"example\",\"url\":\"example\"},\"sha\":\"example\"},\"head\":{\"ref\":\"example\",\"repo\":{\"id\":3,\"name\":\"example\",\"url\":\"example\"},\"sha\":\"example\"},\"id\":3,\"number\":3,\"url\":\"example\"}],\"repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"repository_id\":3,\"run_attempt\":3,\"run_number\":3,\"started_at\":\"2026-09-29T00:00:00Z\",\"status\":\"example\",\"trigger_actor\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"url\":\"example\"}]}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/runs"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("event", "example"), ("branch", "example"), ("status", "example"), ("actor", "example"), ("head_sha", "example"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("updateUserSecret") {
        val built = GeneratedActionsRequests.updateUserSecret(config, "space name", "{\"data\":\"example\",\"description\":\"example\"}".fromJson[contract.CreateOrUpdateSecretOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = built.decode(request.send(respond("", sttp.model.StatusCode(204)))).isRight
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/secrets/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"data\":\"example\",\"description\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("deleteUserSecret") {
        val built = GeneratedActionsRequests.deleteUserSecret(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/secrets/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("getUserVariablesList") {
        val built = GeneratedActionsRequests.getUserVariablesList(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"data\":\"example\",\"description\":\"example\",\"name\":\"example\",\"owner_id\":3,\"repo_id\":3}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/variables"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("getUserVariable") {
        val built = GeneratedActionsRequests.getUserVariable(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"data\":\"example\",\"description\":\"example\",\"name\":\"example\",\"owner_id\":3,\"repo_id\":3}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/variables/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("updateUserVariable") {
        val built = GeneratedActionsRequests.updateUserVariable(config, "space name", "{\"description\":\"example\",\"name\":\"example\",\"value\":\"example\"}".fromJson[contract.UpdateVariableOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = built.decode(request.send(respond("", sttp.model.StatusCode(204)))).isRight
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/variables/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"description\":\"example\",\"name\":\"example\",\"value\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("createUserVariable") {
        val built = GeneratedActionsRequests.createUserVariable(config, "space name", "{\"description\":\"example\",\"value\":\"example\"}".fromJson[contract.CreateVariableOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 409).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/variables/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"description\":\"example\",\"value\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("deleteUserVariable") {
        val built = GeneratedActionsRequests.deleteUserVariable(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = built.decode(request.send(respond("", sttp.model.StatusCode(204)))).isRight
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/actions/variables/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userGetOauth2Application") {
        val built = GeneratedUsersRequests.userGetOauth2Application(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"client_id\":\"example\",\"client_secret\":\"example\",\"confidential_client\":false,\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/applications/oauth2"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userCreateOAuth2Application") {
        val built = GeneratedUsersRequests.userCreateOAuth2Application(config, "{\"confidential_client\":false,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}".fromJson[contract.CreateOAuth2ApplicationOptions].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"client_id\":\"example\",\"client_secret\":\"example\",\"confidential_client\":false,\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/applications/oauth2"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"confidential_client\":false,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}".fromJson[zio.json.ast.Json])
      },
      test("userGetOAuth2Application") {
        val built = GeneratedUsersRequests.userGetOAuth2Application(config, 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"client_id\":\"example\",\"client_secret\":\"example\",\"confidential_client\":false,\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/applications/oauth2/11"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userDeleteOAuth2Application") {
        val built = GeneratedUsersRequests.userDeleteOAuth2Application(config, 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/applications/oauth2/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userUpdateOAuth2Application") {
        val built = GeneratedUsersRequests.userUpdateOAuth2Application(config, 11L, "{\"confidential_client\":false,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}".fromJson[contract.CreateOAuth2ApplicationOptions].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"client_id\":\"example\",\"client_secret\":\"example\",\"confidential_client\":false,\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/applications/oauth2/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"confidential_client\":false,\"name\":\"example\",\"redirect_uris\":[\"example\"],\"skip_secondary_authorization\":false}".fromJson[zio.json.ast.Json])
      },
      test("userUpdateAvatar") {
        val built = GeneratedUsersRequests.userUpdateAvatar(config, "{\"image\":\"example\"}".fromJson[contract.UpdateUserAvatarOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/avatar"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"image\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("userDeleteAvatar") {
        val built = GeneratedUsersRequests.userDeleteAvatar(config)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/avatar"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userListBlocks") {
        val built = GeneratedUsersRequests.userListBlocks(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/blocks"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userCheckUserBlock") {
        val built = GeneratedUsersRequests.userCheckUserBlock(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/blocks/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userBlockUser") {
        val built = GeneratedUsersRequests.userBlockUser(config, "space name", Some("example"))
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/blocks/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("note", "example")), request.body == NoBody)
      },
      test("userUnblockUser") {
        val built = GeneratedUsersRequests.userUnblockUser(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/blocks/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userListEmails") {
        val built = GeneratedUsersRequests.userListEmails(config)
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"email\":\"example\",\"primary\":false,\"user_id\":3,\"username\":\"example\",\"verified\":false}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/emails"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userAddEmail") {
        val built = GeneratedUsersRequests.userAddEmail(config, "{\"emails\":[\"example\"]}".fromJson[contract.CreateEmailOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"email\":\"example\",\"primary\":false,\"user_id\":3,\"username\":\"example\",\"verified\":false}]", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/emails"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"emails\":[\"example\"]}".fromJson[zio.json.ast.Json])
      },
      test("userDeleteEmail") {
        val built = GeneratedUsersRequests.userDeleteEmail(config, "{\"emails\":[\"example\"]}".fromJson[contract.DeleteEmailOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/emails"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"emails\":[\"example\"]}".fromJson[zio.json.ast.Json])
      },
      test("userCurrentListFollowers") {
        val built = GeneratedUsersRequests.userCurrentListFollowers(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/followers"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userCurrentListFollowing") {
        val built = GeneratedUsersRequests.userCurrentListFollowing(config, Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/following"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userCurrentCheckFollowing") {
        val built = GeneratedUsersRequests.userCurrentCheckFollowing(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/following/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userCurrentPutFollow") {
        val built = GeneratedUsersRequests.userCurrentPutFollow(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/following/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userCurrentDeleteFollow") {
        val built = GeneratedUsersRequests.userCurrentDeleteFollow(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/following/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("getVerificationToken") {
        val built = GeneratedUsersRequests.getVerificationToken(config)
        val request = built.request
        val decoded = built.decode(request.send(respond("example", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/user/gpg_key_token"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Accept").contains("text/plain"), request.body == NoBody)
      }
    )
