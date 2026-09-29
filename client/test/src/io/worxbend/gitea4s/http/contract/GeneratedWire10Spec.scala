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

object GeneratedWire10Spec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private def respond(body: String, status: StatusCode) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("generated contract wire requests")(
      test("orgListUserOrgs") {
        val built = GeneratedUsersRequests.orgListUserOrgs(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/orgs"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("orgGetUserPermissions") {
        val built = GeneratedUsersRequests.orgGetUserPermissions(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"can_create_repository\":false,\"can_read\":false,\"can_write\":false,\"is_admin\":false,\"is_owner\":false}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/orgs/space%20name/permissions"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userListStarred") {
        val built = GeneratedUsersRequests.userListStarred(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/starred"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userListSubscriptions") {
        val built = GeneratedUsersRequests.userListSubscriptions(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/subscriptions"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userGetTokens") {
        val built = GeneratedUsersRequests.userGetTokens(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"created_at\":\"2026-09-29T00:00:00Z\",\"id\":3,\"last_used_at\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"scopes\":[\"example\"],\"sha1\":\"example\",\"token_last_eight\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/tokens"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userCreateToken") {
        val built = GeneratedUsersRequests.userCreateToken(config, "space name", "{\"name\":\"example\",\"scopes\":[\"example\"]}".fromJson[contract.CreateAccessTokenOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"created_at\":\"2026-09-29T00:00:00Z\",\"id\":3,\"last_used_at\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"scopes\":[\"example\"],\"sha1\":\"example\",\"token_last_eight\":\"example\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/tokens"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"name\":\"example\",\"scopes\":[\"example\"]}".fromJson[zio.json.ast.Json])
      },
      test("userDeleteAccessToken") {
        val built = GeneratedUsersRequests.userDeleteAccessToken(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/users/space%20name/tokens/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("getVersion") {
        val built = GeneratedCatalogRequests.getVersion(config)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"version\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/version"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      }
    )
