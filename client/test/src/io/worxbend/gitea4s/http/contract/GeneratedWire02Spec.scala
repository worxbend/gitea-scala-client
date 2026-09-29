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

object GeneratedWire02Spec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private def respond(body: String, status: StatusCode) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("generated contract wire requests")(
      test("createOrgVariable") {
        val built = GeneratedActionsRequests.createOrgVariable(config, "space name", "space name", "{\"description\":\"example\",\"value\":\"example\"}".fromJson[contract.CreateVariableOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 409, 500).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/actions/variables/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"description\":\"example\",\"value\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("deleteOrgVariable") {
        val built = GeneratedActionsRequests.deleteOrgVariable(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"data\":\"example\",\"description\":\"example\",\"name\":\"example\",\"owner_id\":3,\"repo_id\":3}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(400, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = built.decode(request.send(respond("", sttp.model.StatusCode(201)))).isRight && built.decode(request.send(respond("", sttp.model.StatusCode(204)))).isRight
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/actions/variables/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgListActivityFeeds") {
        val built = GeneratedOrgsRequests.orgListActivityFeeds(config, "space name", Some("example"), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"act_user\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"act_user_id\":3,\"comment\":{\"assets\":[{\"browser_download_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"download_count\":3,\"id\":3,\"name\":\"example\",\"size\":3,\"uuid\":\"example\"}],\"body\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"html_url\":\"example\",\"id\":3,\"issue_url\":\"example\",\"original_author\":\"example\",\"original_author_id\":3,\"pull_request_url\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\",\"user\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}},\"comment_id\":3,\"content\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"id\":3,\"is_private\":false,\"op_type\":\"create_repo\",\"ref_name\":\"example\",\"repo\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"repo_id\":3,\"user_id\":3}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/activities/feeds"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("date", "example"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("orgUpdateAvatar") {
        val built = GeneratedOrgsRequests.orgUpdateAvatar(config, "space name", "{\"image\":\"example\"}".fromJson[contract.UpdateUserAvatarOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/avatar"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"image\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("orgDeleteAvatar") {
        val built = GeneratedOrgsRequests.orgDeleteAvatar(config, "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/avatar"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("organizationListBlocks") {
        val built = GeneratedOrgsRequests.organizationListBlocks(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List().forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/blocks"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("organizationCheckUserBlock") {
        val built = GeneratedOrgsRequests.organizationCheckUserBlock(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/blocks/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("organizationBlockUser") {
        val built = GeneratedOrgsRequests.organizationBlockUser(config, "space name", "space name", Some("example"))
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/blocks/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("note", "example")), request.body == NoBody)
      },
      test("organizationUnblockUser") {
        val built = GeneratedOrgsRequests.organizationUnblockUser(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/blocks/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgListHooks") {
        val built = GeneratedOrgsRequests.orgListHooks(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/hooks"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("orgCreateHook") {
        val built = GeneratedOrgsRequests.orgCreateHook(config, "space name", "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\",\"type\":\"dingtalk\"}".fromJson[contract.CreateHookOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/hooks"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\",\"type\":\"dingtalk\"}".fromJson[zio.json.ast.Json])
      },
      test("orgGetHook") {
        val built = GeneratedOrgsRequests.orgGetHook(config, "space name", 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/hooks/11"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgDeleteHook") {
        val built = GeneratedOrgsRequests.orgDeleteHook(config, "space name", 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/hooks/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgEditHook") {
        val built = GeneratedOrgsRequests.orgEditHook(config, "space name", 11L, "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\"}".fromJson[contract.EditHookOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"created_at\":\"2026-09-29T00:00:00Z\",\"events\":[\"example\"],\"id\":3,\"name\":\"example\",\"type\":\"example\",\"updated_at\":\"2026-09-29T00:00:00Z\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/hooks/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"active\":false,\"authorization_header\":\"example\",\"branch_filter\":\"example\",\"config\":{\"example\":\"example\"},\"events\":[\"example\"],\"name\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("orgListLabels") {
        val built = GeneratedOrgsRequests.orgListLabels(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"id\":3,\"is_archived\":false,\"name\":\"example\",\"url\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/labels"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("orgCreateLabel") {
        val built = GeneratedOrgsRequests.orgCreateLabel(config, "space name", "{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"is_archived\":false,\"name\":\"example\"}".fromJson[contract.CreateLabelOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"id\":3,\"is_archived\":false,\"name\":\"example\",\"url\":\"example\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/labels"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"is_archived\":false,\"name\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("orgGetLabel") {
        val built = GeneratedOrgsRequests.orgGetLabel(config, "space name", 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"id\":3,\"is_archived\":false,\"name\":\"example\",\"url\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/labels/11"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgDeleteLabel") {
        val built = GeneratedOrgsRequests.orgDeleteLabel(config, "space name", 11L)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/labels/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgEditLabel") {
        val built = GeneratedOrgsRequests.orgEditLabel(config, "space name", 11L, "{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"is_archived\":false,\"name\":\"example\"}".fromJson[contract.EditLabelOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"id\":3,\"is_archived\":false,\"name\":\"example\",\"url\":\"example\"}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PATCH, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/labels/11"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"color\":\"example\",\"description\":\"example\",\"exclusive\":false,\"is_archived\":false,\"name\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("orgIsMember") {
        val built = GeneratedOrgsRequests.orgIsMember(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(303, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/members/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgDeleteMember") {
        val built = GeneratedOrgsRequests.orgDeleteMember(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/members/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgIsPublicMember") {
        val built = GeneratedOrgsRequests.orgIsPublicMember(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/public_members/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgPublicizeMember") {
        val built = GeneratedOrgsRequests.orgPublicizeMember(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.PUT, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/public_members/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("orgConcealMember") {
        val built = GeneratedOrgsRequests.orgConcealMember(config, "space name", "space name")
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.DELETE, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/public_members/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.body == NoBody)
      },
      test("renameOrg") {
        val built = GeneratedOrgsRequests.renameOrg(config, "space name", "{\"new_name\":\"example\"}".fromJson[contract.RenameOrgOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("", sttp.model.StatusCode(204))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(403, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/rename"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"new_name\":\"example\"}".fromJson[zio.json.ast.Json])
      },
      test("orgListTeams") {
        val built = GeneratedOrgsRequests.orgListTeams(config, "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/teams"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("orgCreateTeam") {
        val built = GeneratedOrgsRequests.orgCreateTeam(config, "space name", "{\"can_create_org_repo\":false,\"description\":\"example\",\"includes_all_repositories\":false,\"name\":\"example\",\"permission\":\"read\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}".fromJson[contract.CreateTeamOption].toOption.get)
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}", sttp.model.StatusCode(201))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404, 422).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.POST, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/teams"), request.header("Authorization").contains("token secret"), built.retryable == false, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(), request.header("Content-Type").exists(_.startsWith("application/json")), stringBody(request).fromJson[zio.json.ast.Json] == "{\"can_create_org_repo\":false,\"description\":\"example\",\"includes_all_repositories\":false,\"name\":\"example\",\"permission\":\"read\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}".fromJson[zio.json.ast.Json])
      },
      test("teamSearch") {
        val built = GeneratedOrgsRequests.teamSearch(config, "space name", Some("example"), Some(true), Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("{\"data\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}],\"ok\":false}", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/orgs/space%20name/teams/search"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("q", "example"), ("include_desc", "true"), ("page", "11"), ("limit", "11")), request.body == NoBody)
      },
      test("listPackages") {
        val built = GeneratedPackagesRequests.listPackages(config, "space name", Some(11), Some(11), Some(contract.PackageType.Alpine), Some("example"))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"created_at\":\"2026-09-29T00:00:00Z\",\"creator\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"html_url\":\"example\",\"id\":3,\"name\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"type\":\"example\",\"version\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/packages/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11"), ("type", "alpine"), ("q", "example")), request.body == NoBody)
      },
      test("listPackageVersions") {
        val built = GeneratedPackagesRequests.listPackageVersions(config, "space name", "space name", "space name", Some(11), Some(11))
        val request = built.request
        val decoded = built.decode(request.send(respond("[{\"created_at\":\"2026-09-29T00:00:00Z\",\"creator\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"html_url\":\"example\",\"id\":3,\"name\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"repository\":{\"allow_fast_forward_only_merge\":false,\"allow_manual_merge\":false,\"allow_merge_commits\":false,\"allow_merge_update\":false,\"allow_rebase\":false,\"allow_rebase_explicit\":false,\"allow_rebase_update\":false,\"allow_squash_merge\":false,\"archived\":false,\"archived_at\":\"2026-09-29T00:00:00Z\",\"autodetect_manual_merge\":false,\"avatar_url\":\"example\",\"branch_count\":3,\"clone_url\":\"example\",\"created_at\":\"2026-09-29T00:00:00Z\",\"default_allow_maintainer_edit\":false,\"default_branch\":\"example\",\"default_delete_branch_after_merge\":false,\"default_merge_style\":\"example\",\"default_target_branch\":\"example\",\"default_update_style\":\"example\",\"description\":\"example\",\"empty\":false,\"external_tracker\":{\"external_tracker_format\":\"example\",\"external_tracker_regexp_pattern\":\"example\",\"external_tracker_style\":\"example\",\"external_tracker_url\":\"example\"},\"external_wiki\":{\"external_wiki_url\":\"example\"},\"fork\":false,\"forks_count\":3,\"full_name\":\"example\",\"has_actions\":false,\"has_code\":false,\"has_issues\":false,\"has_packages\":false,\"has_projects\":false,\"has_pull_requests\":false,\"has_releases\":false,\"has_wiki\":false,\"html_url\":\"example\",\"id\":3,\"ignore_whitespace_conflicts\":false,\"internal\":false,\"internal_tracker\":{\"allow_only_contributors_to_track_time\":false,\"enable_issue_dependencies\":false,\"enable_time_tracker\":false},\"language\":\"example\",\"languages_url\":\"example\",\"licenses\":[\"example\"],\"link\":\"example\",\"mirror\":false,\"mirror_interval\":\"example\",\"mirror_last_sync_at\":\"2026-09-29T00:00:00Z\",\"mirror_updated\":\"2026-09-29T00:00:00Z\",\"name\":\"example\",\"object_format_name\":\"sha1\",\"open_issues_count\":3,\"open_pr_counter\":3,\"original_url\":\"example\",\"owner\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"parent\":{},\"permissions\":{\"admin\":false,\"pull\":false,\"push\":false},\"private\":false,\"projects_mode\":\"example\",\"release_counter\":3,\"repo_transfer\":{\"doer\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"recipient\":{\"active\":false,\"avatar_url\":\"example\",\"created\":\"2026-09-29T00:00:00Z\",\"description\":\"example\",\"email\":\"example\",\"followers_count\":3,\"following_count\":3,\"full_name\":\"example\",\"html_url\":\"example\",\"id\":3,\"is_admin\":false,\"language\":\"example\",\"last_login\":\"2026-09-29T00:00:00Z\",\"location\":\"example\",\"login\":\"example\",\"login_name\":\"example\",\"prohibit_login\":false,\"restricted\":false,\"source_id\":3,\"starred_repos_count\":3,\"visibility\":\"public\",\"website\":\"example\"},\"teams\":[{\"can_create_org_repo\":false,\"description\":\"example\",\"id\":3,\"includes_all_repositories\":false,\"name\":\"example\",\"organization\":{\"avatar_url\":\"example\",\"description\":\"example\",\"email\":\"example\",\"full_name\":\"example\",\"id\":3,\"location\":\"example\",\"name\":\"example\",\"repo_admin_change_team_access\":false,\"username\":\"example\",\"visibility\":\"public\",\"website\":\"example\"},\"permission\":\"none\",\"units\":[\"example\"],\"units_map\":{\"example\":\"example\"},\"visibility\":\"public\"}]},\"size\":3,\"ssh_url\":\"example\",\"stars_count\":3,\"template\":false,\"topics\":[\"example\"],\"updated_at\":\"2026-09-29T00:00:00Z\",\"url\":\"example\",\"watchers_count\":3,\"website\":\"example\"},\"type\":\"example\",\"version\":\"example\"}]", sttp.model.StatusCode(200))))
        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))
        val documentedFailures = List(404).forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)
        val otherSuccesses = true
        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString.startsWith("https://gitea.example/root/api/v1/packages/space%20name/space%20name/space%20name"), request.header("Authorization").contains("token secret"), built.retryable == true, decoded.isRight, missing.isLeft, documentedFailures, otherSuccesses, request.uri.paramsSeq == Seq(("page", "11"), ("limit", "11")), request.body == NoBody)
      }
    )
