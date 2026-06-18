package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{
  Auth,
  CreateIssue,
  CreateIssueComment,
  EditIssue,
  IssueLabelsOption,
  IssueState,
  LockIssueOption,
  NotificationSubjectType
}
import sttp.client4.*
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Header, Method, StatusCode}
import zio.Chunk
import zio.test.*

import java.time.Instant

object GiteaRequestsSpec extends ZIOSpecDefault:
  private val config =
    GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
      .copy(pageSize = 25, userAgent = Some("gitea4s-test"), otp = Some("123456"))

  def spec =
    suite("Gitea request layer")(
      test("builds /user request with auth and JSON accept headers") {
        val built = GiteaRequests.currentUser(config)
        val request = built.request

        assertTrue(
          built.endpoint == GiteaEndpoints.userGetCurrent,
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/user",
          request.header("Authorization").contains("token secret"),
          request.header("Accept").contains("application/json"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456")
        )
      },
      test("encodes path segments for /users/{username}") {
        val request = GiteaRequests.user(config, "space user/slash").request

        assertTrue(
          request.uri.toString == "https://gitea.example/root/api/v1/users/space%20user%2Fslash",
          request.uri.path == List("root", "api", "v1", "users", "space user/slash")
        )
      },
      test("encodes repository owner and name path segments") {
        val request = GiteaRequests.repository(config, "worx bend", "gitea/scala").request

        assertTrue(
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala",
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret")
        )
      },
      test("builds schema-traceable get organization request") {
        val built = GiteaRequests.organization(config, "space org/slash")
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.orgGet,
          endpoint.operationId == "orgGet",
          endpoint.path == "/orgs/{org}",
          endpoint.parameters.map(_.name) == List("org"),
          endpoint.response == "#/responses/Organization",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/orgs/space%20org%2Fslash",
          request.header("Accept").contains("application/json")
        )
      },
      test("builds schema-traceable paginated organization members request") {
        val built = GiteaRequests.organizationMembers(config, "space org/slash", page = 2)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.orgListMembers,
          endpoint.operationId == "orgListMembers",
          endpoint.path == "/orgs/{org}/members",
          endpoint.parameters.map(_.name) == List("org", "page", "limit"),
          endpoint.response == "#/responses/UserList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/orgs/space%20org%2Fslash/members?"),
          request.uri.paramsMap.get("page").contains("2"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable paginated organization public members request") {
        val built = GiteaRequests.organizationPublicMembers(config, "space org/slash", page = 3)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.orgListPublicMembers,
          endpoint.operationId == "orgListPublicMembers",
          endpoint.path == "/orgs/{org}/public_members",
          endpoint.parameters.map(_.name) == List("org", "page", "limit"),
          endpoint.response == "#/responses/UserList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/orgs/space%20org%2Fslash/public_members?"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable paginated organization repository list request") {
        val built =
          GiteaRequests.organizationRepos(config, "space org/slash", RepoListParams(page = Some(4), limit = Some(15)))
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.orgListRepos,
          endpoint.operationId == "orgListRepos",
          endpoint.path == "/orgs/{org}/repos",
          endpoint.parameters.map(_.name) == List("org", "page", "limit"),
          endpoint.response == "#/responses/RepositoryList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/orgs/space%20org%2Fslash/repos?"),
          request.uri.paramsMap.get("page").contains("4"),
          request.uri.paramsMap.get("limit").contains("15")
        )
      },
      test("builds schema-traceable paginated user repository list request") {
        val built =
          GiteaRequests.userRepos(config, "space user/slash", RepoListParams(page = Some(2), limit = Some(15)))
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.userListRepos,
          endpoint.operationId == "userListRepos",
          endpoint.path == "/users/{username}/repos",
          endpoint.parameters.map(_.name) == List("username", "page", "limit"),
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/users/space%20user%2Fslash/repos?"),
          request.uri.paramsMap.get("page").contains("2"),
          request.uri.paramsMap.get("limit").contains("15")
        )
      },
      test("builds schema-traceable paginated repository topics request") {
        val built = GiteaRequests.repoTopics(config, "worx bend", "gitea/scala", page = 3)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListTopics,
          endpoint.operationId == "repoListTopics",
          endpoint.path == "/repos/{owner}/{repo}/topics",
          endpoint.parameters.map(_.name) == List("owner", "repo", "page", "limit"),
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/topics?"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable paginated repository branches request") {
        val built = GiteaRequests.repoBranches(config, "worx bend", "gitea/scala", page = 3)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListBranches,
          endpoint.operationId == "repoListBranches",
          endpoint.path == "/repos/{owner}/{repo}/branches",
          endpoint.parameters.map(_.name) == List("owner", "repo", "page", "limit"),
          endpoint.response == "#/responses/BranchList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/branches?"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable paginated repository tags request") {
        val built = GiteaRequests.repoTags(config, "worx bend", "gitea/scala", page = 4)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListTags,
          endpoint.operationId == "repoListTags",
          endpoint.path == "/repos/{owner}/{repo}/tags",
          endpoint.parameters.map(_.name) == List("owner", "repo", "page", "limit"),
          endpoint.response == "#/responses/TagList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/tags?"),
          request.uri.paramsMap.get("page").contains("4"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable paginated repository releases request") {
        val built = GiteaRequests.repoReleases(config, "worx bend", "gitea/scala", page = 5)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListReleases,
          endpoint.operationId == "repoListReleases",
          endpoint.path == "/repos/{owner}/{repo}/releases",
          endpoint.parameters.map(_.name) == List("owner", "repo", "draft", "pre-release", "page", "limit"),
          endpoint.response == "#/responses/ReleaseList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/releases?"),
          request.uri.paramsMap.get("page").contains("5"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable get repository release request") {
        val built = GiteaRequests.repoRelease(config, "worx bend", "gitea/scala", 77)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoGetRelease,
          endpoint.operationId == "repoGetRelease",
          endpoint.path == "/repos/{owner}/{repo}/releases/{id}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "id"),
          endpoint.response == "#/responses/Release",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/releases/77"
        )
      },
      test("builds schema-traceable paginated repository pull request list request") {
        val params = PullRequestListParams(
          baseBranch = Some("main"),
          state = Some(PullRequestListState.All),
          sort = Some(PullRequestSort.RecentUpdate),
          milestone = Some(12),
          labels = Chunk(4, 5),
          poster = Some("alice"),
          page = Some(6),
          limit = Some(11)
        )
        val built = GiteaRequests.repoPullRequests(config, "worx bend", "gitea/scala", params)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListPullRequests,
          endpoint.operationId == "repoListPullRequests",
          endpoint.path == "/repos/{owner}/{repo}/pulls",
          endpoint.parameters.map(_.name) ==
            List("owner", "repo", "base_branch", "state", "sort", "milestone", "labels", "poster", "page", "limit"),
          endpoint.response == "#/responses/PullRequestList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/pulls?"),
          request.uri.paramsMap.get("base_branch").contains("main"),
          request.uri.paramsMap.get("state").contains("all"),
          request.uri.paramsMap.get("sort").contains("recentupdate"),
          request.uri.paramsMap.get("milestone").contains("12"),
          request.uri.toString.contains("labels=4"),
          request.uri.toString.contains("labels=5"),
          request.uri.paramsMap.get("poster").contains("alice"),
          request.uri.paramsMap.get("page").contains("6"),
          request.uri.paramsMap.get("limit").contains("11")
        )
      },
      test("builds schema-traceable get repository pull request request") {
        val built = GiteaRequests.repoPullRequest(config, "worx bend", "gitea/scala", 88)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoGetPullRequest,
          endpoint.operationId == "repoGetPullRequest",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          endpoint.response == "#/responses/PullRequest",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88"
        )
      },
      test("encodes issue list query parameters from typed params") {
        val params = IssueListParams(
          state = Some(IssueState.Open),
          labels = Chunk("bug", "api"),
          q = Some("json codec"),
          issueType = Some(IssueListType.Issues),
          milestones = Chunk("v1", "2"),
          createdBy = Some("alice"),
          page = Some(3),
          limit = Some(10)
        )

        val request = GiteaRequests.issues(config, "owner", "repo", params).request

        assertTrue(
          request.uri.toString.contains("/api/v1/repos/owner/repo/issues?"),
          request.uri.paramsMap.get("state").contains("open"),
          request.uri.paramsMap.get("labels").contains("bug,api"),
          request.uri.paramsMap.get("q").contains("json codec"),
          request.uri.paramsMap.get("type").contains("issues"),
          request.uri.paramsMap.get("milestones").contains("v1,2"),
          request.uri.paramsMap.get("created_by").contains("alice"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("10")
        )
      },
      test("builds schema-traceable get issue request") {
        val built = GiteaRequests.issue(config, "worx bend", "gitea/scala", 99)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.issueGetIssue,
          endpoint.operationId == "issueGetIssue",
          endpoint.path == "/repos/{owner}/{repo}/issues/{index}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99"
        )
      },
      test("builds schema-traceable create issue request with JSON body") {
        val body = CreateIssue(
          title = "Implement POST",
          body = Some("Create typed issue"),
          dueDate = Some(Instant.parse("2026-07-01T00:00:00Z")),
          labels = Some(List(1L, 2L)),
          milestone = Some(3L)
        )
        val built = GiteaRequests.createIssue(config, "worx bend", "gitea/scala", body)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.issueCreateIssue,
          endpoint.method == "POST",
          endpoint.operationId == "issueCreateIssue",
          endpoint.path == "/repos/{owner}/{repo}/issues",
          endpoint.parameters.map(_.name) == List("owner", "repo", "body"),
          endpoint.response == "#/responses/Issue",
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues",
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          built.retryable == false,
          request.body match
            case StringBody(json, _, _) =>
              json.contains(""""title":"Implement POST"""") &&
                json.contains(""""due_date":"2026-07-01T00:00:00Z"""") &&
                json.contains(""""labels":[1,2]""")
            case _ => false
        )
      },
      test("builds schema-traceable create issue comment request with JSON body") {
        val built =
          GiteaRequests.createIssueComment(
            config,
            "worx bend",
            "gitea/scala",
            99,
            CreateIssueComment("Looks good")
          )
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.issueCreateComment,
          endpoint.method == "POST",
          endpoint.operationId == "issueCreateComment",
          endpoint.path == "/repos/{owner}/{repo}/issues/{index}/comments",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          endpoint.response == "#/responses/Comment",
          request.method == Method.POST,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/comments",
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          built.retryable == false,
          request.body match
            case StringBody(json, _, _) => json.contains(""""body":"Looks good"""")
            case _ => false
        )
      },
      test("builds schema-traceable issue label requests") {
        val get = GiteaRequests.issueLabels(config, "worx bend", "gitea/scala", 99)
        val replace =
          GiteaRequests.replaceIssueLabels(
            config,
            "worx bend",
            "gitea/scala",
            99,
            IssueLabelsOption(List(1L, 2L))
          )
        val add =
          GiteaRequests.addIssueLabels(
            config,
            "worx bend",
            "gitea/scala",
            99,
            IssueLabelsOption(List(3L))
          )
        val clear = GiteaRequests.clearIssueLabels(config, "worx bend", "gitea/scala", 99)
        val remove = GiteaRequests.removeIssueLabel(config, "worx bend", "gitea/scala", 99, 3)

        assertTrue(
          get.endpoint == GiteaEndpoints.issueGetLabels,
          get.endpoint.operationId == "issueGetLabels",
          get.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/labels",
          get.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          get.endpoint.response == "#/responses/LabelList",
          get.request.method == Method.GET,
          get.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/labels",
          get.retryable == true,
          replace.endpoint == GiteaEndpoints.issueReplaceLabels,
          replace.request.method == Method.PUT,
          replace.request.header("Content-Type").exists(_.startsWith("application/json")),
          replace.retryable == false,
          replace.request.body match
            case StringBody(json, _, _) => json.contains(""""labels":[1,2]""")
            case _ => false,
          add.endpoint == GiteaEndpoints.issueAddLabel,
          add.request.method == Method.POST,
          add.request.header("Content-Type").exists(_.startsWith("application/json")),
          add.retryable == false,
          add.request.body match
            case StringBody(json, _, _) => json.contains(""""labels":[3]""")
            case _ => false,
          clear.endpoint == GiteaEndpoints.issueClearLabels,
          clear.request.method == Method.DELETE,
          clear.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/labels",
          clear.retryable == false,
          remove.endpoint == GiteaEndpoints.issueRemoveLabel,
          remove.request.method == Method.DELETE,
          remove.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/labels/3",
          remove.retryable == false
        )
      },
      test("builds schema-traceable issue lock and unlock requests") {
        val lock =
          GiteaRequests.lockIssue(
            config,
            "worx bend",
            "gitea/scala",
            99,
            LockIssueOption(lockReason = Some("resolved"))
          )
        val unlock = GiteaRequests.unlockIssue(config, "worx bend", "gitea/scala", 99)

        assertTrue(
          lock.endpoint == GiteaEndpoints.issueLockIssue,
          lock.endpoint.method == "PUT",
          lock.endpoint.operationId == "issueLockIssue",
          lock.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/lock",
          lock.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          lock.endpoint.response == "#/responses/empty",
          lock.request.method == Method.PUT,
          lock.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/lock",
          lock.request.header("Content-Type").exists(_.startsWith("application/json")),
          lock.retryable == false,
          lock.request.body match
            case StringBody(json, _, _) => json.contains(""""lock_reason":"resolved"""")
            case _ => false,
          unlock.endpoint == GiteaEndpoints.issueUnlockIssue,
          unlock.endpoint.method == "DELETE",
          unlock.endpoint.operationId == "issueUnlockIssue",
          unlock.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/lock",
          unlock.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          unlock.endpoint.response == "#/responses/empty",
          unlock.request.method == Method.DELETE,
          unlock.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/lock",
          unlock.retryable == false
        )
      },
      test("builds paginated follower and following list requests") {
        val followers = GiteaRequests.userFollowers(config, "space user/slash", page = 2)
        val following = GiteaRequests.userFollowing(config, "space user/slash", page = 3)

        assertTrue(
          followers.endpoint == GiteaEndpoints.userListFollowers,
          followers.endpoint.operationId == "userListFollowers",
          followers.request.uri.toString.contains("/api/v1/users/space%20user%2Fslash/followers?"),
          followers.request.uri.paramsMap.get("page").contains("2"),
          followers.request.uri.paramsMap.get("limit").contains("25"),
          following.endpoint == GiteaEndpoints.userListFollowing,
          following.endpoint.operationId == "userListFollowing",
          following.request.uri.toString.contains("/api/v1/users/space%20user%2Fslash/following?"),
          following.request.uri.paramsMap.get("page").contains("3"),
          following.request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds schema-traceable user search request") {
        val built =
          GiteaRequests.userSearch(config, UserSearchParams(q = Some("space user"), page = Some(2), limit = Some(5)))
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.userSearch,
          endpoint.operationId == "userSearch",
          endpoint.path == "/users/search",
          endpoint.parameters.map(_.name) == List("q", "uid", "page", "limit"),
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/users/search?"),
          request.uri.paramsMap.get("q").contains("space user"),
          request.uri.paramsMap.get("page").contains("2"),
          request.uri.paramsMap.get("limit").contains("5")
        )
      },
      test("builds schema-traceable notification list request with filters") {
        val params = NotificationListParams(
          all = Some(true),
          statusTypes = Chunk(NotificationStatusType.Unread, NotificationStatusType.Pinned),
          subjectTypes = Chunk(NotificationSubjectType.Issue, NotificationSubjectType.Repository),
          since = Some(Instant.parse("2026-06-01T00:00:00Z")),
          before = Some(Instant.parse("2026-06-18T00:00:00Z")),
          page = Some(3),
          limit = Some(9)
        )
        val built = GiteaRequests.notifications(config, params)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.notifyGetList,
          endpoint.operationId == "notifyGetList",
          endpoint.path == "/notifications",
          endpoint.parameters.map(_.name) ==
            List("all", "status-types", "subject-type", "since", "before", "page", "limit"),
          endpoint.response == "#/responses/NotificationThreadList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/notifications?"),
          request.uri.paramsMap.get("all").contains("true"),
          request.uri.toString.contains("status-types=unread"),
          request.uri.toString.contains("status-types=pinned"),
          request.uri.toString.contains("subject-type=issue"),
          request.uri.toString.contains("subject-type=repository"),
          request.uri.paramsMap.get("since").contains("2026-06-01T00:00:00Z"),
          request.uri.paramsMap.get("before").contains("2026-06-18T00:00:00Z"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("9")
        )
      },
      test("builds schema-traceable notification count and thread requests") {
        val count = GiteaRequests.notificationCount(config)
        val thread = GiteaRequests.notificationThread(config, "thread id/slash")

        assertTrue(
          count.endpoint == GiteaEndpoints.notifyNewAvailable,
          count.endpoint.operationId == "notifyNewAvailable",
          count.endpoint.path == "/notifications/new",
          count.endpoint.response == "#/responses/NotificationCount",
          count.request.method == Method.GET,
          count.request.uri.toString == "https://gitea.example/root/api/v1/notifications/new",
          thread.endpoint == GiteaEndpoints.notifyGetThread,
          thread.endpoint.operationId == "notifyGetThread",
          thread.endpoint.path == "/notifications/threads/{id}",
          thread.endpoint.parameters.map(_.name) == List("id"),
          thread.endpoint.response == "#/responses/NotificationThread",
          thread.request.method == Method.GET,
          thread.request.uri.toString ==
            "https://gitea.example/root/api/v1/notifications/threads/thread%20id%2Fslash"
        )
      },
      test("adds JSON content type only when a JSON body is attached") {
        val base = GiteaRequests.currentUser(config).request
        val withBody = GiteaRequests.withJsonBody(config, base, """{"name":"repo"}""")

        assertTrue(
          base.header("Content-Type").isEmpty,
          withBody.header("Content-Type").exists(_.startsWith("application/json")),
          withBody.body match
            case StringBody(body, _, _) => body == """{"name":"repo"}"""
            case _ => false
        )
      },
      test("builds schema-traceable edit issue request with JSON body") {
        val body = EditIssue(
          title = Some("Retitle"),
          body = Some("Updated body"),
          contentVersion = Some(2L),
          state = Some(IssueState.Closed),
          unsetDueDate = Some(true)
        )
        val built = GiteaRequests.editIssue(config, "worx bend", "gitea/scala", 99, body)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.issueEditIssue,
          endpoint.method == "PATCH",
          endpoint.operationId == "issueEditIssue",
          endpoint.path == "/repos/{owner}/{repo}/issues/{index}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          endpoint.response == "#/responses/Issue",
          request.method == Method.PATCH,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99",
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          built.retryable == false,
          request.body match
            case StringBody(json, _, _) =>
              json.contains(""""title":"Retitle"""") &&
                json.contains(""""content_version":2""") &&
                json.contains(""""state":"closed"""") &&
                json.contains(""""unset_due_date":true""")
            case _ => false
        )
      },
      test("decodes a successful user response through BackendStub") {
        val response = """{"id":42,"login":"octo"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(response))
        val built = GiteaRequests.currentUser(config)
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw).map(_.login) == Right(Some("octo")),
          built.decode(raw).map(_.id) == Right(Some(42L))
        )
      },
      test("decodes a successful organization response through BackendStub") {
        val response = """{"id":9,"name":"platform","full_name":"Platform Team"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(response))
        val built = GiteaRequests.organization(config, "platform")
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw).map(_.id) == Right(Some(9L)),
          built.decode(raw).map(_.name) == Right(Some("platform")),
          built.decode(raw).map(_.fullName) == Right(Some("Platform Team"))
        )
      },
      test("decodes paginated issue list response and pagination headers") {
        val response = """[{"id":1,"number":7,"state":"open","title":"First"}]"""
        val headers = List(Header("x-total-count", "31"))
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(response, StatusCode.Ok, headers))
        val built = GiteaRequests.issues(config, "owner", "repo")
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw).map(_.data.headOption.flatMap(_.number)) == Right(Some(7L)),
          built.decode(raw).map(_.totalCount) == Right(Some(31L)),
          built.decode(raw).map(_.page) == Right(1),
          built.decode(raw).map(_.pageSize) == Right(25),
          built.decode(raw).map(_.hasNext) == Right(true)
        )
      },
      test("decodes a created issue response") {
        val response = """{"id":77,"number":12,"state":"open","title":"Created"}"""
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(response, StatusCode.Created))
        val built = GiteaRequests.createIssue(config, "owner", "repo", CreateIssue(title = "Created"))
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw).map(_.id) == Right(Some(77L)),
          built.decode(raw).map(_.number) == Right(Some(12L)),
          built.decode(raw).map(_.title) == Right(Some("Created"))
        )
      },
      test("decodes an edited issue response") {
        val response = """{"id":77,"number":12,"state":"closed","title":"Retitle"}"""
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(response, StatusCode.Created))
        val built =
          GiteaRequests.editIssue(config, "owner", "repo", 12, EditIssue(title = Some("Retitle")))
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw).map(_.id) == Right(Some(77L)),
          built.decode(raw).map(_.number) == Right(Some(12L)),
          built.decode(raw).map(_.state) == Right(Some(IssueState.Closed)),
          built.decode(raw).map(_.title) == Right(Some("Retitle"))
        )
      },
      test("decodes issue label list and empty label mutation responses") {
        val labelResponse = """[{"id":1,"name":"kind/api"},{"id":2,"name":"status/ready"}]"""
        val emptyResponse = ""
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.method == Method.GET)
            .thenRespond(ResponseStub.adjust(labelResponse))
            .whenRequestMatches(_.method == Method.DELETE)
            .thenRespond(ResponseStub.adjust(emptyResponse, StatusCode.NoContent))
        val labels = GiteaRequests.issueLabels(config, "owner", "repo", 12)
        val clear = GiteaRequests.clearIssueLabels(config, "owner", "repo", 12)

        assertTrue(
          labels.decode(labels.request.send(backend)).map(_.map(_.name)) ==
            Right(Chunk(Some("kind/api"), Some("status/ready"))),
          clear.decode(clear.request.send(backend)) == Right(())
        )
      },
      test("decodes empty issue lock and unlock responses") {
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val lock = GiteaRequests.lockIssue(config, "owner", "repo", 12, LockIssueOption(Some("resolved")))
        val unlock = GiteaRequests.unlockIssue(config, "owner", "repo", 12)

        assertTrue(
          lock.decode(lock.request.send(backend)) == Right(()),
          unlock.decode(unlock.request.send(backend)) == Right(())
        )
      },
      test("decodes single issue and paginated user list responses") {
        val issueResponse = """{"id":1,"number":7,"state":"open","title":"First"}"""
        val userListResponse = """[{"id":2,"login":"alice"}]"""
        val orgMembersResponse = """[{"id":4,"login":"member"}]"""
        val orgPublicMembersResponse = """[{"id":5,"login":"public-member"}]"""
        val userSearchResponse = """{"ok":true,"data":[{"id":3,"login":"search-hit"}]}"""
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.uri.path.endsWith(List("issues", "7")))
            .thenRespond(ResponseStub.adjust(issueResponse))
            .whenRequestMatches(_.uri.path.endsWith(List("followers")))
            .thenRespond(ResponseStub.adjust(userListResponse, StatusCode.Ok, List(Header("x-total-count", "1"))))
            .whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "members")))
            .thenRespond(ResponseStub.adjust(orgMembersResponse, StatusCode.Ok, List(Header("x-total-count", "1"))))
            .whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "public_members")))
            .thenRespond(
              ResponseStub.adjust(orgPublicMembersResponse, StatusCode.Ok, List(Header("x-total-count", "1")))
            )
            .whenRequestMatches(_.uri.path.endsWith(List("users", "search")))
            .thenRespond(ResponseStub.adjust(userSearchResponse, StatusCode.Ok, List(Header("x-total-count", "1"))))
        val issue = GiteaRequests.issue(config, "owner", "repo", 7)
        val followers = GiteaRequests.userFollowers(config, "octo")
        val orgMembers = GiteaRequests.organizationMembers(config, "platform")
        val orgPublicMembers = GiteaRequests.organizationPublicMembers(config, "platform")
        val search = GiteaRequests.userSearch(config, UserSearchParams(q = Some("search")))

        assertTrue(
          issue.decode(issue.request.send(backend)).map(_.number) == Right(Some(7L)),
          followers.decode(followers.request.send(backend)).map(_.data.headOption.flatMap(_.login)) ==
            Right(Some("alice")),
          followers.decode(followers.request.send(backend)).map(_.hasNext) == Right(false),
          orgMembers.decode(orgMembers.request.send(backend)).map(_.data.headOption.flatMap(_.login)) ==
            Right(Some("member")),
          orgMembers.decode(orgMembers.request.send(backend)).map(_.hasNext) == Right(false),
          orgPublicMembers.decode(orgPublicMembers.request.send(backend)).map(_.data.headOption.flatMap(_.login)) ==
            Right(Some("public-member")),
          orgPublicMembers.decode(orgPublicMembers.request.send(backend)).map(_.hasNext) == Right(false),
          search.decode(search.request.send(backend)).map(_.data.headOption.flatMap(_.login)) ==
            Right(Some("search-hit")),
          search.decode(search.request.send(backend)).map(_.hasNext) == Right(false)
        )
      },
      test("decodes paginated repository list, topic names, branch, and tag responses") {
        val repoListResponse = """[{"id":10,"name":"api"},{"id":11,"name":"client"}]"""
        val orgRepoListResponse = """[{"id":12,"name":"org-api"},{"id":13,"name":"org-client"}]"""
        val topicsResponse = """{"topics":["scala","zio"]}"""
        val branchListResponse = """[{"name":"main","protected":true},{"name":"release"}]"""
        val tagListResponse = """[{"name":"v1.0.0"},{"name":"v1.1.0"}]"""
        val releaseListResponse =
          """[{"id":20,"tag_name":"v1.0.0","name":"First"},{"id":21,"tag_name":"v1.1.0","name":"Second"}]"""
        val releaseResponse = """{"id":20,"tag_name":"v1.0.0","name":"First"}"""
        val pullRequestListResponse =
          """[{"id":30,"number":1,"state":"open","title":"First"},{"id":31,"number":2,"state":"closed","title":"Second"}]"""
        val pullRequestResponse = """{"id":31,"number":2,"state":"closed","title":"Second"}"""
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.uri.path.endsWith(List("users", "octo", "repos")))
            .thenRespond(ResponseStub.adjust(repoListResponse, StatusCode.Ok, List(Header("x-total-count", "2"))))
            .whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "repos")))
            .thenRespond(ResponseStub.adjust(orgRepoListResponse, StatusCode.Ok, List(Header("x-total-count", "2"))))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "topics")))
            .thenRespond(ResponseStub.adjust(topicsResponse, StatusCode.Ok, List(Header("x-total-count", "2"))))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "branches")))
            .thenRespond(ResponseStub.adjust(branchListResponse, StatusCode.Ok, List(Header("x-total-count", "2"))))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "tags")))
            .thenRespond(ResponseStub.adjust(tagListResponse, StatusCode.Ok, List(Header("x-total-count", "2"))))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "releases")))
            .thenRespond(ResponseStub.adjust(releaseListResponse, StatusCode.Ok, List(Header("x-total-count", "2"))))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "releases", "20")))
            .thenRespond(ResponseStub.adjust(releaseResponse))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "pulls")))
            .thenRespond(
              ResponseStub.adjust(pullRequestListResponse, StatusCode.Ok, List(Header("x-total-count", "2")))
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2")))
            .thenRespond(ResponseStub.adjust(pullRequestResponse))
        val repos = GiteaRequests.userRepos(config, "octo")
        val orgRepos = GiteaRequests.organizationRepos(config, "platform")
        val topics = GiteaRequests.repoTopics(config, "octo", "api")
        val branches = GiteaRequests.repoBranches(config, "octo", "api")
        val tags = GiteaRequests.repoTags(config, "octo", "api")
        val releases = GiteaRequests.repoReleases(config, "octo", "api")
        val release = GiteaRequests.repoRelease(config, "octo", "api", 20)
        val pullRequests = GiteaRequests.repoPullRequests(config, "octo", "api")
        val pullRequest = GiteaRequests.repoPullRequest(config, "octo", "api", 2)

        assertTrue(
          repos.decode(repos.request.send(backend)).map(_.data.map(_.name)) ==
            Right(Chunk(Some("api"), Some("client"))),
          orgRepos.decode(orgRepos.request.send(backend)).map(_.data.map(_.name)) ==
            Right(Chunk(Some("org-api"), Some("org-client"))),
          orgRepos.decode(orgRepos.request.send(backend)).map(_.hasNext) == Right(false),
          topics.decode(topics.request.send(backend)).map(_.data) == Right(Chunk("scala", "zio")),
          branches.decode(branches.request.send(backend)).map(_.data.map(_.name)) ==
            Right(Chunk(Some("main"), Some("release"))),
          branches.decode(branches.request.send(backend)).map(_.totalCount) == Right(Some(2L)),
          tags.decode(tags.request.send(backend)).map(_.data.map(_.name)) ==
            Right(Chunk(Some("v1.0.0"), Some("v1.1.0"))),
          releases.decode(releases.request.send(backend)).map(_.data.map(_.tagName)) ==
            Right(Chunk(Some("v1.0.0"), Some("v1.1.0"))),
          releases.decode(releases.request.send(backend)).map(_.totalCount) == Right(Some(2L)),
          release.decode(release.request.send(backend)).map(_.tagName) == Right(Some("v1.0.0")),
          pullRequests.decode(pullRequests.request.send(backend)).map(_.data.map(_.number)) ==
            Right(Chunk(Some(1L), Some(2L))),
          pullRequests.decode(pullRequests.request.send(backend)).map(_.totalCount) == Right(Some(2L)),
          pullRequest.decode(pullRequest.request.send(backend)).map(_.title) == Right(Some("Second"))
        )
      },
      test("decodes notification count, list, and thread responses") {
        val countResponse = """{"new":2}"""
        val listResponse =
          """[{"id":40,"unread":true,"subject":{"title":"First","state":"open","type":"Issue"}}]"""
        val threadResponse =
          """{"id":41,"unread":false,"subject":{"title":"Second","state":"closed","type":"Pull"}}"""
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.uri.path.endsWith(List("notifications", "new")))
            .thenRespond(ResponseStub.adjust(countResponse))
            .whenRequestMatches(_.uri.path.endsWith(List("notifications")))
            .thenRespond(ResponseStub.adjust(listResponse, StatusCode.Ok, List(Header("x-total-count", "1"))))
            .whenRequestMatches(_.uri.path.endsWith(List("notifications", "threads", "41")))
            .thenRespond(ResponseStub.adjust(threadResponse))
        val count = GiteaRequests.notificationCount(config)
        val notifications = GiteaRequests.notifications(config)
        val thread = GiteaRequests.notificationThread(config, "41")

        assertTrue(
          count.decode(count.request.send(backend)).map(_.unread) == Right(Some(2L)),
          notifications.decode(notifications.request.send(backend)).map(_.data.headOption.flatMap(_.id)) ==
            Right(Some(40L)),
          notifications.decode(notifications.request.send(backend)).map(_.data.headOption.flatMap(_.subject.flatMap(_.subjectType))) ==
            Right(Some(NotificationSubjectType.Issue)),
          thread.decode(thread.request.send(backend)).map(_.subject.flatMap(_.title)) == Right(Some("Second"))
        )
      },
      test("maps Gitea error responses while preserving raw body") {
        val body = """{"message":"missing repo","url":"https://docs.gitea.com/api"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.issue(config, "owner", "missing", 404)
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw) == Left(GiteaError.NotFound("missing repo", body))
        )
      },
      test("maps organization not-found responses") {
        val body = """{"message":"missing org"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.organization(config, "missing")
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw) == Left(GiteaError.NotFound("missing org", body))
        )
      },
      test("maps organization member-list not-found responses") {
        val body = """{"message":"missing org"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.organizationMembers(config, "missing")
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw) == Left(GiteaError.NotFound("missing org", body))
        )
      },
      test("maps organization public member-list not-found responses") {
        val body = """{"message":"missing org"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.organizationPublicMembers(config, "missing")
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw) == Left(GiteaError.NotFound("missing org", body))
        )
      },
      test("maps organization repository-list not-found responses") {
        val body = """{"message":"missing org"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.organizationRepos(config, "missing")
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw) == Left(GiteaError.NotFound("missing org", body))
        )
      },
      test("maps repository branch and tag not-found responses") {
        val body = """{"message":"missing repo"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val branches = GiteaRequests.repoBranches(config, "owner", "missing")
        val tags = GiteaRequests.repoTags(config, "owner", "missing")

        assertTrue(
          branches.decode(branches.request.send(backend)) == Left(GiteaError.NotFound("missing repo", body)),
          tags.decode(tags.request.send(backend)) == Left(GiteaError.NotFound("missing repo", body))
        )
      },
      test("maps repository release not-found responses") {
        val body = """{"message":"missing release"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val releases = GiteaRequests.repoReleases(config, "owner", "missing")
        val release = GiteaRequests.repoRelease(config, "owner", "missing", 77)

        assertTrue(
          releases.decode(releases.request.send(backend)) == Left(GiteaError.NotFound("missing release", body)),
          release.decode(release.request.send(backend)) == Left(GiteaError.NotFound("missing release", body))
        )
      },
      test("maps repository pull request not-found responses") {
        val body = """{"message":"missing pull request"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val pullRequests = GiteaRequests.repoPullRequests(config, "owner", "missing")
        val pullRequest = GiteaRequests.repoPullRequest(config, "owner", "missing", 77)

        assertTrue(
          pullRequests.decode(pullRequests.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullRequest.decode(pullRequest.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body))
        )
      },
      test("maps notification thread not-found responses") {
        val body = """{"message":"missing notification thread"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val thread = GiteaRequests.notificationThread(config, "missing")

        assertTrue(
          thread.decode(thread.request.send(backend)) ==
            Left(GiteaError.NotFound("missing notification thread", body))
        )
      },
      test("maps rate limit reset headers") {
        val headers = List(Header("x-ratelimit-reset", "1781740800"))
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("rate limited", StatusCode.TooManyRequests, headers)
          )
        val built = GiteaRequests.currentUser(config)
        val raw = built.request.send(backend)

        assertTrue(
          built.decode(raw).left.exists {
            case GiteaError.RateLimited(Some(resetAt), "rate limited") =>
              resetAt.toString == "2026-06-18T00:00:00Z"
            case _ => false
          }
        )
      }
    )
