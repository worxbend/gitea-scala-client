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

object GeneratedWire06Spec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private def respond(body: String, status: StatusCode) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("generated contract wire requests")(
      test("repoMergeUpstream") {
        val built = GeneratedReposRequests.repoMergeUpstream(config, "space name", "space name", "{\"branch\":\"example\",\"ff_only\":false}".fromJson[contract.MergeUpstreamRequest].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"merge_type\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/merge-upstream"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"branch\":\"example\",\"ff_only\":false}".fromJson[zio.json.ast.Json])
      },
      test("issueGetMilestonesList") {
        val built = GeneratedReposRequests.issueGetMilestonesList(config, "space name", "space name", Some("example"), Some("example"), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"closed_at\":\"2026-09-29T00:00:00Z\",\"closed_issues\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"id\":3,\"open_issues\":3,\"state\":\"open\",\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/milestones"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("state", "example"), ("name", "example"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("issueCreateMilestone") {
        val built = GeneratedReposRequests.issueCreateMilestone(config, "space name", "space name", "{\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"state\":\"open\",\"title\":\"example\"}".fromJson[contract.CreateMilestoneOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"closed_at\":\"2026-09-29T00:00:00Z\",\"closed_issues\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"id\":3,\"open_issues\":3,\"state\":\"open\",\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/milestones"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"state\":\"open\",\"title\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("issueGetMilestone") {
        val built = GeneratedReposRequests.issueGetMilestone(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"closed_at\":\"2026-09-29T00:00:00Z\",\"closed_issues\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"id\":3,\"open_issues\":3,\"state\":\"open\",\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/milestones/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("issueDeleteMilestone") {
        val built = GeneratedReposRequests.issueDeleteMilestone(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/milestones/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("issueEditMilestone") {
        val built = GeneratedReposRequests.issueEditMilestone(config, "space name", "space name", "space name", "{\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"state\":\"open\",\"title\":\"example\"}".fromJson[contract.EditMilestoneOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"closed_at\":\"2026-09-29T00:00:00Z\",\"closed_issues\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"id\":3,\"open_issues\":3,\"state\":\"open\",\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/milestones/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"state\":\"open\",\"title\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("repoMirrorSync") {
        val built = GeneratedReposRequests.repoMirrorSync(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/mirror-sync"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("notifyGetRepoList") {
        val built = GeneratedNotificationsRequests.notifyGetRepoList(config, "space name", "space name", Some(true), Some(List("one", "two")), Some(List(contract.NotificationSubjectType.Issue)), Some(java.time.Instant.parse("2026-09-29T00:00:00Z")), Some(java.time.Instant.parse("2026-09-29T00:00:00Z")), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"id\":3,\"pinned\":false,\"repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"subject\":{\"html_url\":\"example\",\"latest_comment_html_url\":\"example\",\"latest_comment_url\":\"example\",\"state\":\"open\",\"title\":\"example\",\"type\":\"Issue\",\"url\":\"example\"},\"unread\":false,\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/notifications"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("all", "true"), ("status-types", "one"), ("status-types", "two"), ("subject-type", "issue"), ("since", "2026-09-29T00:00:00Z"), ("before", "2026-09-29T00:00:00Z"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("notifyReadRepoList") {
        val built = GeneratedNotificationsRequests.notifyReadRepoList(config, "space name", "space name", Some("example"), Some(List("one", "two")), Some("example"), Some(java.time.Instant.parse("2026-09-29T00:00:00Z")))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"id\":3,\"pinned\":false,\"repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"subject\":{\"html_url\":\"example\",\"latest_comment_html_url\":\"example\",\"latest_comment_url\":\"example\",\"state\":\"open\",\"title\":\"example\",\"type\":\"Issue\",\"url\":\"example\"},\"unread\":false,\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\"}]", sttp.model.StatusCode(205))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/notifications"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("all", "example"), ("status-types", "one"), ("status-types", "two"), ("to-status", "example"), ("last_read_at", "2026-09-29T00:00:00Z")), request.body == NoBody)
      },
      test("repoListPushMirrors") {
        val built = GeneratedReposRequests.repoListPushMirrors(config, "space name", "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"created\":\"2026-09-29T00:00:00Z\",\"interval\":\"example\",\"last_error\":\"example\",\"last_update\":\"2026-09-29T00:00:00Z\",\"remote_address\":\"example\",\"remote_name\":\"example\",\"repo_name\":\"example\",\"sync_on_commit\":false}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/push_mirrors"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("repoAddPushMirror") {
        val built = GeneratedReposRequests.repoAddPushMirror(config, "space name", "space name", "{\"interval\":\"example\",\"remote_address\":\"example\",\"remote_password\":\"example\",\"remote_username\":\"example\",\"sync_on_commit\":false}".fromJson[contract.CreatePushMirrorOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"created\":\"2026-09-29T00:00:00Z\",\"interval\":\"example\",\"last_error\":\"example\",\"last_update\":\"2026-09-29T00:00:00Z\",\"remote_address\":\"example\",\"remote_name\":\"example\",\"repo_name\":\"example\",\"sync_on_commit\":false}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/push_mirrors"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"interval\":\"example\",\"remote_address\":\"example\",\"remote_password\":\"example\",\"remote_username\":\"example\",\"sync_on_commit\":false}".fromJson[zio.json.ast.Json])
      },
      test("repoPushMirrorSync") {
        val built = GeneratedReposRequests.repoPushMirrorSync(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/push_mirrors-sync"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoGetPushMirrorByRemoteName") {
        val built = GeneratedReposRequests.repoGetPushMirrorByRemoteName(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"created\":\"2026-09-29T00:00:00Z\",\"interval\":\"example\",\"last_error\":\"example\",\"last_update\":\"2026-09-29T00:00:00Z\",\"remote_address\":\"example\",\"remote_name\":\"example\",\"repo_name\":\"example\",\"sync_on_commit\":false}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/push_mirrors/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoDeletePushMirror") {
        val built = GeneratedReposRequests.repoDeletePushMirror(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/push_mirrors/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoCreateRelease") {
        val built = GeneratedReleasesRequests.repoCreateRelease(config, "space name", "space name", "{\"body\":\"example\",\"draft\":false,\"name\":\"example\",\"prerelease\":false,\"tag_message\":\"example\",\"tag_name\":\"example\",\"target_commitish\":\"example\"}".fromJson[contract.CreateReleaseOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"assets\":[{\"browser_download_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"download_count\":3,\"id\":3,\"name\":\"example\",\"size\":3,\"uuid\":\"example\"}],\"author\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"body\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"draft\":false,\"html_url\":\"example\",\"id\":3,\"name\":\"example\",\"prerelease\":false,\"published_at\":\"2026-09-29T00:00:00Z\",\"tag_name\":\"example\",\"tarball_url\":\"example\",\"target_commitish\":\"example\",\"upload_url\":\"example\",\"url\":\"example\",\"zipball_url\":\"example\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 409, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/releases"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"body\":\"example\",\"draft\":false,\"name\":\"example\",\"prerelease\":false,\"tag_message\":\"example\",\"tag_name\":\"example\",\"target_commitish\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("repoDeleteReleaseByTag") {
        val built = GeneratedReleasesRequests.repoDeleteReleaseByTag(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/releases/tags/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoDeleteRelease") {
        val built = GeneratedReleasesRequests.repoDeleteRelease(config, "space name", "space name", 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/releases/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoEditRelease") {
        val built = GeneratedReleasesRequests.repoEditRelease(config, "space name", "space name", 11L, "{\"body\":\"example\",\"draft\":false,\"name\":\"example\",\"prerelease\":false,\"tag_name\":\"example\",\"target_commitish\":\"example\"}".fromJson[contract.EditReleaseOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"assets\":[{\"browser_download_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"download_count\":3,\"id\":3,\"name\":\"example\",\"size\":3,\"uuid\":\"example\"}],\"author\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"body\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"draft\":false,\"html_url\":\"example\",\"id\":3,\"name\":\"example\",\"prerelease\":false,\"published_at\":\"2026-09-29T00:00:00Z\",\"tag_name\":\"example\",\"tarball_url\":\"example\",\"target_commitish\":\"example\",\"upload_url\":\"example\",\"url\":\"example\",\"zipball_url\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/releases/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"body\":\"example\",\"draft\":false,\"name\":\"example\",\"prerelease\":false,\"tag_name\":\"example\",\"target_commitish\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("repoDeleteReleaseAttachment") {
        val built = GeneratedReleasesRequests.repoDeleteReleaseAttachment(config, "space name", "space name", 11L, 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/releases/11/assets/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoEditReleaseAttachment") {
        val built = GeneratedReleasesRequests.repoEditReleaseAttachment(config, "space name", "space name", 11L, 11L, "{\"name\":\"example\"}".fromJson[contract.EditAttachmentOptions].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"browser_download_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"download_count\":3,\"id\":3,\"name\":\"example\",\"size\":3,\"uuid\":\"example\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/releases/11/assets/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"name\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("repoSigningKeySSH") {
        val built = GeneratedReposRequests.repoSigningKeySSH(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("example", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/signing-key.pub"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Accept").contains("text/plain"), request.body == NoBody)
      },
      test("userCurrentCheckSubscription") {
        val built = GeneratedReposRequests.userCurrentCheckSubscription(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"created_at\":\"2026-09-29T00:00:00Z\",\"ignored\":false,\"reason\":null,\"repository_url\":\"example\",\"subscribed\":false,\"url\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/subscription"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userCurrentPutSubscription") {
        val built = GeneratedReposRequests.userCurrentPutSubscription(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"created_at\":\"2026-09-29T00:00:00Z\",\"ignored\":false,\"reason\":null,\"repository_url\":\"example\",\"subscribed\":false,\"url\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/subscription"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("userCurrentDeleteSubscription") {
        val built = GeneratedReposRequests.userCurrentDeleteSubscription(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/subscription"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoCreateTag") {
        val built = GeneratedReposRequests.repoCreateTag(config, "space name", "space name", "{\"message\":\"example\",\"tag_name\":\"example\",\"target\":\"example\"}".fromJson[contract.CreateTagOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"commit\":{\"created\":\"2026-09-29T00:00:00Z\",\"sha\":\"example\",\"url\":\"example\"},\"id\":\"example\",\"message\":\"example\",\"name\":\"example\",\"tarball_url\":\"example\",\"zipball_url\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 405, 409, 422, 423).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/tags"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"message\":\"example\",\"tag_name\":\"example\",\"target\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("repoDeleteTag") {
        val built = GeneratedReposRequests.repoDeleteTag(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 405, 409, 422, 423).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/tags/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoAddTeam") {
        val built = GeneratedReposRequests.repoAddTeam(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404, 405, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/teams/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoDeleteTeam") {
        val built = GeneratedReposRequests.repoDeleteTeam(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404, 405, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/teams/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("repoTrackedTimes") {
        val built = GeneratedReposRequests.repoTrackedTimes(config, "space name", "space name", Some("example"), Some(java.time.Instant.parse("2026-09-29T00:00:00Z")), Some(java.time.Instant.parse("2026-09-29T00:00:00Z")), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"issue\":{\"assets\":[{\"browser_download_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"download_count\":3,\"id\":3,\"name\":\"example\",\"size\":3,\"uuid\":\"example\"}],\"assignee\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"assignees\":[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}],\"body\":\"example\",\"closed_at\":\"2026-09-29T00:00:00Z\",\"comments\":3,\"content_version\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"due_date\":\"2026-09-29T00:00:00Z\",\"html_url\":\"example\",\"id\":3,\"is_locked\":false,\"labels\":[{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"id\":3,\"is_archived\":false,\"name\":\"example\",\"url\":\"example\"}],\"milestone\":{\"closed_at\":\"2026-09-29T00:00:00Z\",\"closed_issues\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"id\":3,\"open_issues\":3,\"state\":\"open\",\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"},\"number\":3,\"original_author\":\"example\",\"original_author_id\":3,\"pin_order\":3,\"projects\":[{\"closed_at\":\"2026-09-29T00:00:00Z\",\"created_at\":\"2026-09-29T00:00:00Z\",\"creator_id\":3,\"description\":\"example\",\"id\":3,\"is_closed\":false,\"owner_id\":3,\"repo_id\":3,\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}],\"pull_request\":{\"draft\":false,\"html_url\":\"example\",\"merged\":false,\"merged_at\":\"2026-09-29T00:00:00Z\"},\"ref\":\"example\",\"repository\":{\"full_name\":\"example\",\"id\":3,\"name\":\"example\",\"owner\":\"example\"},\"state\":\"open\",\"time_estimate\":3,\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"user\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}},\"issue_id\":3,\"time\":3,\"user_id\":3,\"user_name\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/times"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("user", "example"), ("since", "2026-09-29T00:00:00Z"), ("before", "2026-09-29T00:00:00Z"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("userTrackedTimes") {
        val built = GeneratedReposRequests.userTrackedTimes(config, "space name", "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"issue\":{\"assets\":[{\"browser_download_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"download_count\":3,\"id\":3,\"name\":\"example\",\"size\":3,\"uuid\":\"example\"}],\"assignee\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"assignees\":[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}],\"body\":\"example\",\"closed_at\":\"2026-09-29T00:00:00Z\",\"comments\":3,\"content_version\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"due_date\":\"2026-09-29T00:00:00Z\",\"html_url\":\"example\",\"id\":3,\"is_locked\":false,\"labels\":[{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"id\":3,\"is_archived\":false,\"name\":\"example\",\"url\":\"example\"}],\"milestone\":{\"closed_at\":\"2026-09-29T00:00:00Z\",\"closed_issues\":3,\"created_at\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"due_on\":\"2026-09-29T00:00:00Z\",\"id\":3,\"open_issues\":3,\"state\":\"open\",\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"},\"number\":3,\"original_author\":\"example\",\"original_author_id\":3,\"pin_order\":3,\"projects\":[{\"closed_at\":\"2026-09-29T00:00:00Z\",\"created_at\":\"2026-09-29T00:00:00Z\",\"creator_id\":3,\"description\":\"example\",\"id\":3,\"is_closed\":false,\"owner_id\":3,\"repo_id\":3,\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}],\"pull_request\":{\"draft\":false,\"html_url\":\"example\",\"merged\":false,\"merged_at\":\"2026-09-29T00:00:00Z\"},\"ref\":\"example\",\"repository\":{\"full_name\":\"example\",\"id\":3,\"name\":\"example\",\"owner\":\"example\"},\"state\":\"open\",\"time_estimate\":3,\"title\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"user\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}},\"issue_id\":3,\"time\":3,\"user_id\":3,\"user_name\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/repos/space%20name/space%20name/times/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      }
    )
