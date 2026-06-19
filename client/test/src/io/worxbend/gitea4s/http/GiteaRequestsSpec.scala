package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{
  AddTimeOption,
  Auth,
  ChangedFile,
  CommitDiffType,
  CreateIssue,
  CreateIssueComment,
  CreatePullReviewComment,
  CreatePullReviewOptions,
  CreateStatusOption,
  DismissPullReviewOptions,
  EditDeadlineOption,
  EditIssueComment,
  EditIssue,
  EditReactionOption,
  IssueDeadline,
  IssueLabelsOption,
  IssueMeta,
  IssueState,
  LockIssueOption,
  MergePullRequestMethod,
  MergePullRequestOption,
  NewIssuePinsAllowed,
  NotificationSubjectType,
  CommitStatusState,
  PullReviewState,
  PullReviewRequestOptions,
  CreatePullRequestOption,
  EditPullRequestOption,
  SubmitPullReviewOptions,
  GitBlobResponse,
  WatchInfo
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
      test("builds schema-traceable repository new pin allowed request") {
        val built = GiteaRequests.repoNewPinAllowed(config, "worx bend", "gitea/scala")
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoNewPinAllowed,
          endpoint.method == "GET",
          endpoint.operationId == "repoNewPinAllowed",
          endpoint.path == "/repos/{owner}/{repo}/new_pin_allowed",
          endpoint.parameters.map(_.name) == List("owner", "repo"),
          endpoint.response == "#/responses/RepoNewIssuePinsAllowed",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/new_pin_allowed",
          built.retryable == true
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
      test("builds and decodes schema-traceable combined commit status request") {
        val built = GiteaRequests.repoCombinedStatusByRef(
          config,
          "worx bend",
          "gitea/scala",
          "feature/slash",
          CombinedStatusParams(page = Some(3), limit = Some(11))
        )
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"sha":"abc123","state":"warning","total_count":2}""")
          )

        assertTrue(
          endpoint == GiteaEndpoints.repoGetCombinedStatusByRef,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetCombinedStatusByRef",
          endpoint.path == "/repos/{owner}/{repo}/commits/{ref}/status",
          endpoint.parameters.map(_.name) == List("owner", "repo", "ref", "page", "limit"),
          endpoint.response == "#/responses/CombinedStatus",
          request.method == Method.GET,
          request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/commits/feature%2Fslash/status?"
          ),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("11"),
          request.header("Accept").contains("application/json"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)).map(_.state) == Right(Some(CommitStatusState.Warning)),
          built.decode(request.send(backend)).map(_.totalCount) == Right(Some(2L))
        )
      },
      test("defaults combined commit status pagination to first configured page") {
        val request = GiteaRequests
          .repoCombinedStatusByRef(config, "worx bend", "gitea/scala", "feature/slash")
          .request

        assertTrue(
          request.uri.paramsMap.get("page").contains("1"),
          request.uri.paramsMap.get("limit").contains("25")
        )
      },
      test("builds and decodes schema-traceable commit status list requests") {
        val params = CommitStatusListParams(
          sort = Some(CommitStatusSort.HighestIndex),
          state = Some(CommitStatusListState.Success),
          page = Some(4),
          limit = Some(9)
        )
        val byRef = GiteaRequests.repoStatusesByRef(config, "worx bend", "gitea/scala", "main branch", params)
        val bySha = GiteaRequests.repoStatuses(config, "worx bend", "gitea/scala", "abc/123", params)
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(
              """[{"id":700,"context":"ci/mill","status":"success","target_url":"https://ci.example/builds/700"}]""",
              StatusCode.Ok,
              List(Header("x-total-count", "1"))
            )
          )

        assertTrue(
          byRef.endpoint == GiteaEndpoints.repoListStatusesByRef,
          byRef.endpoint.method == "GET",
          byRef.endpoint.operationId == "repoListStatusesByRef",
          byRef.endpoint.path == "/repos/{owner}/{repo}/commits/{ref}/statuses",
          byRef.endpoint.parameters.map(_.name) == List("owner", "repo", "ref", "sort", "state", "page", "limit"),
          byRef.endpoint.response == "#/responses/CommitStatusList",
          byRef.request.method == Method.GET,
          byRef.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/commits/main%20branch/statuses?"
          ),
          byRef.request.uri.paramsMap.get("sort").contains("highestindex"),
          byRef.request.uri.paramsMap.get("state").contains("success"),
          byRef.request.uri.paramsMap.get("page").contains("4"),
          byRef.request.uri.paramsMap.get("limit").contains("9"),
          byRef.request.header("Content-Type").isEmpty,
          byRef.retryable == true,
          bySha.endpoint == GiteaEndpoints.repoListStatuses,
          bySha.endpoint.method == "GET",
          bySha.endpoint.operationId == "repoListStatuses",
          bySha.endpoint.path == "/repos/{owner}/{repo}/statuses/{sha}",
          bySha.endpoint.parameters.map(_.name) == List("owner", "repo", "sha", "sort", "state", "page", "limit"),
          bySha.endpoint.response == "#/responses/CommitStatusList",
          bySha.request.method == Method.GET,
          bySha.request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/statuses/abc%2F123?"),
          bySha.request.uri.paramsMap.get("sort").contains("highestindex"),
          bySha.request.uri.paramsMap.get("state").contains("success"),
          bySha.request.header("Content-Type").isEmpty,
          bySha.retryable == true,
          byRef.decode(byRef.request.send(backend)).map(_.data.headOption.flatMap(_.state)) ==
            Right(Some(CommitStatusState.Success)),
          byRef.decode(byRef.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          bySha.decode(bySha.request.send(backend)).map(_.data.headOption.flatMap(_.targetUrl)) ==
            Right(Some("https://ci.example/builds/700"))
        )
      },
      test("builds and decodes schema-traceable create commit status request") {
        val body = CreateStatusOption(
          context = Some("ci/mill"),
          description = Some("Mill tests passed"),
          state = Some(CommitStatusState.Success),
          targetUrl = Some("https://ci.example/builds/700")
        )
        val built = GiteaRequests.createStatus(config, "worx bend", "gitea/scala", "abc/123", body)
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"id":700,"context":"ci/mill","status":"success"}""", StatusCode.Created)
          )
        val requestBody =
          request.body match
            case StringBody(value, _, _) => value
            case _ => ""

        assertTrue(
          endpoint == GiteaEndpoints.repoCreateStatus,
          endpoint.method == "POST",
          endpoint.operationId == "repoCreateStatus",
          endpoint.path == "/repos/{owner}/{repo}/statuses/{sha}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha", "body"),
          endpoint.response == "#/responses/CommitStatus",
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/statuses/abc%2F123",
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          requestBody ==
            """{"context":"ci/mill","description":"Mill tests passed","state":"success","target_url":"https://ci.example/builds/700"}""",
          built.retryable == false,
          built.decode(request.send(backend)).map(_.id) == Right(Some(700L)),
          built.decode(request.send(backend)).map(_.state) == Right(Some(CommitStatusState.Success))
        )
      },
      test("builds and decodes schema-traceable commit pull request lookup") {
        val built =
          GiteaRequests.repoCommitPullRequest(config, "worx bend", "gitea/scala", "feature/commit abc")
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"id":91,"number":44,"state":"open","title":"Commit pull"}""")
          )

        assertTrue(
          endpoint == GiteaEndpoints.repoGetCommitPullRequest,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetCommitPullRequest",
          endpoint.path == "/repos/{owner}/{repo}/commits/{sha}/pull",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha"),
          endpoint.response == "#/responses/PullRequest",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/commits/feature%2Fcommit%20abc/pull",
          request.uri.path ==
            List("root", "api", "v1", "repos", "worx bend", "gitea/scala", "commits", "feature/commit abc", "pull"),
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)).map(_.number) == Right(Some(44L)),
          built.decode(request.send(backend)).map(_.state) == Right(Some(IssueState.Open)),
          built.decode(request.send(backend)).map(_.title) == Right(Some("Commit pull"))
        )
      },
      test("builds and decodes schema-traceable single commit lookup") {
        val built =
          GiteaRequests.repoSingleCommit(config, "worx bend", "gitea/scala", "feature/commit abc")
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(
              """{"sha":"abc123","commit":{"message":"Implement single commit"},"stats":{"total":7},"files":[{"filename":"src/Main.scala","status":"modified"}]}"""
            )
          )

        assertTrue(
          endpoint == GiteaEndpoints.repoGetSingleCommit,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetSingleCommit",
          endpoint.path == "/repos/{owner}/{repo}/git/commits/{sha}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha", "stat", "verification", "files"),
          endpoint.response == "#/responses/Commit",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/git/commits/feature%2Fcommit%20abc",
          request.uri.path ==
            List("root", "api", "v1", "repos", "worx bend", "gitea/scala", "git", "commits", "feature/commit abc"),
          request.uri.paramsMap.isEmpty,
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)).map(_.sha) == Right(Some("abc123")),
          built.decode(request.send(backend)).map(_.commit.flatMap(_.message)) == Right(Some("Implement single commit")),
          built.decode(request.send(backend)).map(_.stats.flatMap(_.total)) == Right(Some(7L)),
          built.decode(request.send(backend)).map(_.files.flatMap(_.headOption.flatMap(_.filename))) ==
            Right(Some("src/Main.scala"))
        )
      },
      test("encodes explicit single commit boolean query parameters") {
        val allTrue =
          GiteaRequests.repoSingleCommit(
            config,
            "owner",
            "repo",
            "abc123",
            SingleCommitParams(stat = Some(true), verification = Some(true), files = Some(true))
          )
        val allFalse =
          GiteaRequests.repoSingleCommit(
            config,
            "owner",
            "repo",
            "abc123",
            SingleCommitParams(stat = Some(false), verification = Some(false), files = Some(false))
          )

        assertTrue(
          allTrue.request.uri.toString.contains("/api/v1/repos/owner/repo/git/commits/abc123?"),
          allTrue.request.uri.paramsMap.get("stat").contains("true"),
          allTrue.request.uri.paramsMap.get("verification").contains("true"),
          allTrue.request.uri.paramsMap.get("files").contains("true"),
          allFalse.request.uri.paramsMap.get("stat").contains("false"),
          allFalse.request.uri.paramsMap.get("verification").contains("false"),
          allFalse.request.uri.paramsMap.get("files").contains("false")
        )
      },
      test("builds and decodes schema-traceable commit diff or patch request") {
        val built =
          GiteaRequests.repoCommitDiffOrPatch(
            config,
            "worx bend",
            "gitea/scala",
            "feature/commit abc",
            CommitDiffType.patch
          )
        val endpoint = built.endpoint
        val request = built.request
        val body =
          """diff --git a/src/Main.scala b/src/Main.scala
            |+println("hello")
            |""".stripMargin
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body))

        assertTrue(
          endpoint == GiteaEndpoints.repoDownloadCommitDiffOrPatch,
          endpoint.method == "GET",
          endpoint.operationId == "repoDownloadCommitDiffOrPatch",
          endpoint.path == "/repos/{owner}/{repo}/git/commits/{sha}.{diffType}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha", "diffType"),
          endpoint.response == "#/responses/string",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/git/commits/feature%2Fcommit%20abc.patch",
          request.uri.path ==
            List("root", "api", "v1", "repos", "worx bend", "gitea/scala", "git", "commits", "feature/commit abc.patch"),
          request.uri.paramsMap.isEmpty,
          request.header("Accept").contains("text/plain"),
          request.header("Authorization").contains("token secret"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)) == Right(body)
        )
      },
      test("builds and decodes schema-traceable commit note lookup") {
        val built =
          GiteaRequests.repoCommitNote(
            config,
            "worx bend",
            "gitea/scala",
            "feature/commit abc",
            CommitNoteParams(verification = Some(false), files = Some(true))
          )
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(
              """{"message":"Reviewed-by: Octo","commit":{"sha":"abc123","commit":{"message":"Implement note"},"files":[{"filename":"src/Main.scala","status":"modified"}]}}"""
            )
          )

        assertTrue(
          endpoint == GiteaEndpoints.repoGetNote,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetNote",
          endpoint.path == "/repos/{owner}/{repo}/git/notes/{sha}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha", "verification", "files"),
          endpoint.response == "#/responses/Note",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/git/notes/feature%2Fcommit%20abc?verification=false&files=true",
          request.uri.path ==
            List("root", "api", "v1", "repos", "worx bend", "gitea/scala", "git", "notes", "feature/commit abc"),
          request.uri.paramsMap.get("verification").contains("false"),
          request.uri.paramsMap.get("files").contains("true"),
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)).map(_.message) == Right(Some("Reviewed-by: Octo")),
          built.decode(request.send(backend)).map(_.commit.flatMap(_.sha)) == Right(Some("abc123")),
          built.decode(request.send(backend)).map(_.commit.flatMap(_.commit.flatMap(_.message))) ==
            Right(Some("Implement note")),
          built.decode(request.send(backend)).map(_.commit.flatMap(_.files.flatMap(_.headOption.flatMap(_.filename)))) ==
            Right(Some("src/Main.scala"))
        )
      },
      test("omits absent commit note query parameters and encodes explicit boolean toggles") {
        val default =
          GiteaRequests.repoCommitNote(config, "owner", "repo", "abc123")
        val allTrue =
          GiteaRequests.repoCommitNote(
            config,
            "owner",
            "repo",
            "abc123",
            CommitNoteParams(verification = Some(true), files = Some(true))
          )
        val allFalse =
          GiteaRequests.repoCommitNote(
            config,
            "owner",
            "repo",
            "abc123",
            CommitNoteParams(verification = Some(false), files = Some(false))
          )

        assertTrue(
          default.request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/git/notes/abc123",
          default.request.uri.paramsMap.isEmpty,
          allTrue.request.uri.toString.contains("/api/v1/repos/owner/repo/git/notes/abc123?"),
          allTrue.request.uri.paramsMap.get("verification").contains("true"),
          allTrue.request.uri.paramsMap.get("files").contains("true"),
          allFalse.request.uri.paramsMap.get("verification").contains("false"),
          allFalse.request.uri.paramsMap.get("files").contains("false")
        )
      },
      test("builds and decodes schema-traceable Git tree lookup") {
        val built =
          GiteaRequests.gitTree(
            config,
            "worx bend",
            "gitea/scala",
            "feature/tree abc",
            GitTreeParams(recursive = Some(true), page = Some(2), perPage = Some(50))
          )
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(
              """{"page":2,"sha":"tree123","total_count":1,"tree":[{"mode":"100644","path":"src/Main.scala","sha":"blob123","size":42,"type":"blob","url":"https://gitea.example/api/v1/repos/o/r/git/blobs/blob123"}],"truncated":false,"url":"https://gitea.example/api/v1/repos/o/r/git/trees/tree123"}"""
            )
          )

        assertTrue(
          endpoint == GiteaEndpoints.getTree,
          endpoint.method == "GET",
          endpoint.operationId == "GetTree",
          endpoint.path == "/repos/{owner}/{repo}/git/trees/{sha}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha", "recursive", "page", "per_page"),
          endpoint.parameters.filter(_.in == "path").forall(_.required),
          endpoint.parameters.filter(_.in == "query").forall(parameter => !parameter.required),
          endpoint.response == "#/responses/GitTreeResponse",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/git/trees/feature%2Ftree%20abc?recursive=true&page=2&per_page=50",
          request.uri.path ==
            List("root", "api", "v1", "repos", "worx bend", "gitea/scala", "git", "trees", "feature/tree abc"),
          request.uri.paramsMap.get("recursive").contains("true"),
          request.uri.paramsMap.get("page").contains("2"),
          request.uri.paramsMap.get("per_page").contains("50"),
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)).map(_.page) == Right(Some(2L)),
          built.decode(request.send(backend)).map(_.sha) == Right(Some("tree123")),
          built.decode(request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          built.decode(request.send(backend)).map(_.tree.flatMap(_.headOption.flatMap(_.path))) ==
            Right(Some("src/Main.scala")),
          built.decode(request.send(backend)).map(_.tree.flatMap(_.headOption.flatMap(_.`type`))) ==
            Right(Some("blob")),
          built.decode(request.send(backend)).map(_.truncated) == Right(Some(false))
        )
      },
      test("omits absent Git tree query parameters and encodes explicit values") {
        val default =
          GiteaRequests.gitTree(config, "owner", "repo", "abc123")
        val explicit =
          GiteaRequests.gitTree(
            config,
            "owner",
            "repo",
            "abc123",
            GitTreeParams(recursive = Some(false), page = Some(3), perPage = Some(20))
          )

        assertTrue(
          default.request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/git/trees/abc123",
          default.request.uri.paramsMap.isEmpty,
          explicit.request.uri.toString.contains("/api/v1/repos/owner/repo/git/trees/abc123?"),
          explicit.request.uri.paramsMap.get("recursive").contains("false"),
          explicit.request.uri.paramsMap.get("page").contains("3"),
          explicit.request.uri.paramsMap.get("per_page").contains("20")
        )
      },
      test("builds and decodes schema-traceable Git blob lookup") {
        val built = GiteaRequests.gitBlob(config, "worx bend", "gitea/scala", "blob/abc 123")
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(
              """{"content":"SGVsbG8K","encoding":"base64","lfs_oid":"oid123","lfs_size":1024,"sha":"blob123","size":6,"url":"https://gitea.example/api/v1/repos/o/r/git/blobs/blob123"}"""
            )
          )

        assertTrue(
          endpoint == GiteaEndpoints.getBlob,
          endpoint.method == "GET",
          endpoint.operationId == "GetBlob",
          endpoint.path == "/repos/{owner}/{repo}/git/blobs/{sha}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "sha"),
          endpoint.parameters.forall(parameter => parameter.in == "path" && parameter.required),
          endpoint.response == "#/responses/GitBlobResponse",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/git/blobs/blob%2Fabc%20123",
          request.uri.path ==
            List("root", "api", "v1", "repos", "worx bend", "gitea/scala", "git", "blobs", "blob/abc 123"),
          request.uri.paramsMap.isEmpty,
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("User-Agent").contains("gitea4s-test"),
          request.header("X-Gitea-OTP").contains("123456"),
          request.header("Content-Type").isEmpty,
          built.retryable == true,
          built.decode(request.send(backend)) ==
            Right(
              GitBlobResponse(
                content = Some("SGVsbG8K"),
                encoding = Some("base64"),
                lfsOid = Some("oid123"),
                lfsSize = Some(1024L),
                sha = Some("blob123"),
                size = Some(6L),
                url = Some("https://gitea.example/api/v1/repos/o/r/git/blobs/blob123")
              )
            )
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
      test("builds and decodes schema-traceable pull request create and edit requests") {
        val dueDate = Instant.parse("2026-07-04T00:00:00Z")
        val createBody = CreatePullRequestOption(
          allowMaintainerEdit = Some(true),
          base = Some("main"),
          body = Some("Ready for review"),
          dueDate = Some(dueDate),
          head = Some("alice:feature/pr-create"),
          labels = Some(List(10L, 11L)),
          milestone = Some(12L),
          reviewers = Some(List("reviewer")),
          teamReviewers = Some(List("maintainers")),
          title = Some("Add pull request create API")
        )
        val editBody = EditPullRequestOption(
          allowMaintainerEdit = Some(false),
          base = Some("release/1.0"),
          body = Some("Updated description"),
          contentVersion = Some(9L),
          dueDate = Some(Instant.parse("2026-07-05T00:00:00Z")),
          labels = Some(List(20L, 21L)),
          milestone = Some(22L),
          state = Some(IssueState.Closed),
          title = Some("Retitle pull request"),
          unsetDueDate = Some(false)
        )
        val create = GiteaRequests.createPullRequest(config, "worx bend", "gitea/scala", createBody)
        val edit = GiteaRequests.editPullRequest(config, "worx bend", "gitea/scala", 88, editBody)
        val createBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"id":91,"number":88,"state":"open","title":"Add pull request create API"}""", StatusCode.Created)
          )
        val editBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"id":91,"number":88,"state":"closed","title":"Retitle pull request"}""", StatusCode.Created)
          )
        val createRequestBody =
          create.request.body match
            case StringBody(value, _, _) => value
            case _ => ""
        val editRequestBody =
          edit.request.body match
            case StringBody(value, _, _) => value
            case _ => ""

        assertTrue(
          create.endpoint == GiteaEndpoints.repoCreatePullRequest,
          create.endpoint.method == "POST",
          create.endpoint.operationId == "repoCreatePullRequest",
          create.endpoint.path == "/repos/{owner}/{repo}/pulls",
          create.endpoint.parameters.map(_.name) == List("owner", "repo", "body"),
          create.endpoint.response == "#/responses/PullRequest",
          create.request.method == Method.POST,
          create.request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls",
          create.request.header("Accept").contains("application/json"),
          create.request.header("Authorization").contains("token secret"),
          create.request.header("User-Agent").contains("gitea4s-test"),
          create.request.header("X-Gitea-OTP").contains("123456"),
          create.request.header("Content-Type").exists(_.startsWith("application/json")),
          createRequestBody ==
            """{"allow_maintainer_edit":true,"base":"main","body":"Ready for review","due_date":"2026-07-04T00:00:00Z","head":"alice:feature/pr-create","labels":[10,11],"milestone":12,"reviewers":["reviewer"],"team_reviewers":["maintainers"],"title":"Add pull request create API"}""",
          create.retryable == false,
          create.decode(create.request.send(createBackend)).map(_.number) == Right(Some(88L)),
          create.decode(create.request.send(createBackend)).map(_.state) == Right(Some(IssueState.Open)),
          edit.endpoint == GiteaEndpoints.repoEditPullRequest,
          edit.endpoint.method == "PATCH",
          edit.endpoint.operationId == "repoEditPullRequest",
          edit.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}",
          edit.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          edit.endpoint.response == "#/responses/PullRequest",
          edit.request.method == Method.PATCH,
          edit.request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88",
          edit.request.header("Accept").contains("application/json"),
          edit.request.header("Authorization").contains("token secret"),
          edit.request.header("User-Agent").contains("gitea4s-test"),
          edit.request.header("X-Gitea-OTP").contains("123456"),
          edit.request.header("Content-Type").exists(_.startsWith("application/json")),
          editRequestBody ==
            """{"allow_maintainer_edit":false,"base":"release/1.0","body":"Updated description","content_version":9,"due_date":"2026-07-05T00:00:00Z","labels":[20,21],"milestone":22,"state":"closed","title":"Retitle pull request","unset_due_date":false}""",
          edit.retryable == false,
          edit.decode(edit.request.send(editBackend)).map(_.title) == Right(Some("Retitle pull request")),
          edit.decode(edit.request.send(editBackend)).map(_.state) == Right(Some(IssueState.Closed))
        )
      },
      test("builds schema-traceable repository pinned pull request list request") {
        val built = GiteaRequests.pinnedPullRequests(config, "worx bend", "gitea/scala")
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListPinnedPullRequests,
          endpoint.method == "GET",
          endpoint.operationId == "repoListPinnedPullRequests",
          endpoint.path == "/repos/{owner}/{repo}/pulls/pinned",
          endpoint.parameters.map(_.name) == List("owner", "repo"),
          endpoint.response == "#/responses/PullRequestList",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/pinned",
          built.retryable == true
        )
      },
      test("maps documented pull request create and edit failures") {
        val forbiddenBody = """{"message":"forbidden"}"""
        val notFoundBody = """{"message":"missing pull request"}"""
        val conflictBody = """{"message":"pull request already exists"}"""
        val validationBody = """{"message":"invalid pull request"}"""
        val lockedBody = """{"message":"repository is archived"}"""
        val staleBody = "stale content version"
        val forbiddenBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(forbiddenBody, StatusCode.Forbidden))
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val conflictBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(conflictBody, StatusCode.Conflict))
        val validationBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(validationBody, StatusCode.UnprocessableEntity)
          )
        val lockedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(lockedBody, StatusCode(423)))
        val preconditionBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(staleBody, StatusCode(412)))
        val create =
          GiteaRequests.createPullRequest(
            config,
            "owner",
            "repo",
            CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("Open PR"))
          )
        val edit =
          GiteaRequests.editPullRequest(
            config,
            "owner",
            "repo",
            77,
            EditPullRequestOption(contentVersion = Some(9L), title = Some("Retitle PR"))
          )

        assertTrue(
          create.decode(create.request.send(forbiddenBackend)) ==
            Left(GiteaError.Forbidden("forbidden", forbiddenBody)),
          create.decode(create.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing pull request", notFoundBody)),
          create.decode(create.request.send(conflictBackend)) ==
            Left(GiteaError.Conflict("pull request already exists", conflictBody)),
          create.decode(create.request.send(validationBackend)) ==
            Left(GiteaError.UnprocessableEntity("invalid pull request", validationBody)),
          create.decode(create.request.send(lockedBackend)) ==
            Left(GiteaError.Locked("repository is archived", lockedBody)),
          edit.decode(edit.request.send(forbiddenBackend)) ==
            Left(GiteaError.Forbidden("forbidden", forbiddenBody)),
          edit.decode(edit.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing pull request", notFoundBody)),
          edit.decode(edit.request.send(conflictBackend)) ==
            Left(GiteaError.Conflict("pull request already exists", conflictBody)),
          edit.decode(edit.request.send(validationBackend)) ==
            Left(GiteaError.UnprocessableEntity("invalid pull request", validationBody)),
          edit.decode(edit.request.send(preconditionBackend)) ==
            Left(GiteaError.PreconditionFailed("Precondition Failed", staleBody))
        )
      },
      test("builds schema-traceable get repository pull request by base and head request") {
        val built =
          GiteaRequests.repoPullRequestByBaseHead(config, "worx bend", "gitea/scala", "main branch", "feature/slash")
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoGetPullRequestByBaseHead,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetPullRequestByBaseHead",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{base}/{head}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "base", "head"),
          endpoint.response == "#/responses/PullRequest",
          request.method == Method.GET,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/main%20branch/feature%2Fslash",
          built.retryable == true
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
      test("builds and decodes schema-traceable pull request merge status request") {
        val built = GiteaRequests.repoPullRequestIsMerged(config, "worx bend", "gitea/scala", 88)
        val endpoint = built.endpoint
        val request = built.request
        val mergedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val unmergedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NotFound))

        assertTrue(
          endpoint == GiteaEndpoints.repoPullRequestIsMerged,
          endpoint.method == "GET",
          endpoint.operationId == "repoPullRequestIsMerged",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/merge",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          endpoint.response == "204 merged / 404 not merged",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/merge",
          request.header("Accept").contains("application/json"),
          built.retryable == true,
          built.decode(request.send(mergedBackend)) == Right(true),
          built.decode(request.send(unmergedBackend)) == Right(false)
        )
      },
      test("builds and decodes schema-traceable pull request merge request") {
        val body = MergePullRequestOption(
          mergeMethod = MergePullRequestMethod.Squash,
          mergeCommitId = Some("abc123"),
          mergeMessageField = Some("Squash commits"),
          mergeTitleField = Some("Add feature"),
          deleteBranchAfterMerge = Some(true),
          forceMerge = Some(false),
          headCommitId = Some("def456"),
          mergeWhenChecksSucceed = Some(true)
        )
        val built = GiteaRequests.mergePullRequest(config, "worx bend", "gitea/scala", 88, body)
        val endpoint = built.endpoint
        val request = built.request
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.Ok))
        val requestBody =
          request.body match
            case StringBody(value, _, _) => value
            case _ => ""

        assertTrue(
          endpoint == GiteaEndpoints.repoMergePullRequest,
          endpoint.method == "POST",
          endpoint.operationId == "repoMergePullRequest",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/merge",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          endpoint.response == "#/responses/empty",
          request.method == Method.POST,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/merge",
          request.header("Accept").contains("application/json"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          requestBody ==
            """{"Do":"squash","MergeCommitID":"abc123","MergeMessageField":"Squash commits","MergeTitleField":"Add feature","delete_branch_after_merge":true,"force_merge":false,"head_commit_id":"def456","merge_when_checks_succeed":true}""",
          built.retryable == false,
          built.decode(request.send(backend)) == Right(())
        )
      },
      test("builds and decodes schema-traceable pull request merge/update lifecycle requests") {
        val cancel = GiteaRequests.cancelScheduledAutoMerge(config, "worx bend", "gitea/scala", 88)
        val update = GiteaRequests.updatePullRequest(config, "worx bend", "gitea/scala", 88, PullRequestUpdateStyle.Rebase)
        val cancelBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val updateBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.Ok))

        assertTrue(
          cancel.endpoint == GiteaEndpoints.repoCancelScheduledAutoMerge,
          cancel.endpoint.method == "DELETE",
          cancel.endpoint.operationId == "repoCancelScheduledAutoMerge",
          cancel.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/merge",
          cancel.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          cancel.endpoint.response == "#/responses/empty",
          cancel.request.method == Method.DELETE,
          cancel.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/merge",
          cancel.request.header("Accept").contains("application/json"),
          cancel.request.header("Content-Type").isEmpty,
          cancel.request.body == NoBody,
          cancel.retryable == false,
          cancel.decode(cancel.request.send(cancelBackend)) == Right(()),
          update.endpoint == GiteaEndpoints.repoUpdatePullRequest,
          update.endpoint.method == "POST",
          update.endpoint.operationId == "repoUpdatePullRequest",
          update.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/update",
          update.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "style"),
          update.endpoint.response == "#/responses/empty",
          update.request.method == Method.POST,
          update.request.uri.toString.contains(
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/update?"
          ),
          update.request.uri.paramsMap.get("style").contains("rebase"),
          update.request.header("Accept").contains("application/json"),
          update.request.header("Content-Type").isEmpty,
          update.request.body == NoBody,
          update.retryable == false,
          update.decode(update.request.send(updateBackend)) == Right(()),
          PullRequestUpdateStyle.values.map(_.queryValue).toList == List("merge", "rebase")
        )
      },
      test("builds and decodes schema-traceable pull-review comment resolution requests") {
        val resolve = GiteaRequests.resolvePullReviewComment(config, "worx bend", "gitea/scala", 91)
        val unresolve = GiteaRequests.unresolvePullReviewComment(config, "worx bend", "gitea/scala", 91)
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))

        assertTrue(
          resolve.endpoint == GiteaEndpoints.repoResolvePullReviewComment,
          resolve.endpoint.method == "POST",
          resolve.endpoint.operationId == "repoResolvePullReviewComment",
          resolve.endpoint.path == "/repos/{owner}/{repo}/pulls/comments/{id}/resolve",
          resolve.endpoint.parameters.map(_.name) == List("owner", "repo", "id"),
          resolve.endpoint.response == "#/responses/empty",
          resolve.request.method == Method.POST,
          resolve.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/comments/91/resolve",
          resolve.request.header("Accept").contains("application/json"),
          resolve.request.header("Authorization").contains("token secret"),
          resolve.request.header("User-Agent").contains("gitea4s-test"),
          resolve.request.header("X-Gitea-OTP").contains("123456"),
          resolve.request.header("Content-Type").isEmpty,
          resolve.request.body == NoBody,
          resolve.retryable == false,
          resolve.decode(resolve.request.send(backend)) == Right(()),
          unresolve.endpoint == GiteaEndpoints.repoUnresolvePullReviewComment,
          unresolve.endpoint.method == "POST",
          unresolve.endpoint.operationId == "repoUnresolvePullReviewComment",
          unresolve.endpoint.path == "/repos/{owner}/{repo}/pulls/comments/{id}/unresolve",
          unresolve.endpoint.parameters.map(_.name) == List("owner", "repo", "id"),
          unresolve.endpoint.response == "#/responses/empty",
          unresolve.request.method == Method.POST,
          unresolve.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/comments/91/unresolve",
          unresolve.request.header("Accept").contains("application/json"),
          unresolve.request.header("Authorization").contains("token secret"),
          unresolve.request.header("User-Agent").contains("gitea4s-test"),
          unresolve.request.header("X-Gitea-OTP").contains("123456"),
          unresolve.request.header("Content-Type").isEmpty,
          unresolve.request.body == NoBody,
          unresolve.retryable == false,
          unresolve.decode(unresolve.request.send(backend)) == Right(())
        )
      },
      test("maps documented pull-review comment resolution failures") {
        val badRequestBody = """{"message":"invalid review comment"}"""
        val forbiddenBody = """{"message":"forbidden"}"""
        val notFoundBody = """{"message":"missing review comment"}"""
        val badRequestBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(badRequestBody, StatusCode.BadRequest))
        val forbiddenBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(forbiddenBody, StatusCode.Forbidden))
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val resolve = GiteaRequests.resolvePullReviewComment(config, "owner", "repo", 91)
        val unresolve = GiteaRequests.unresolvePullReviewComment(config, "owner", "repo", 91)

        assertTrue(
          resolve.decode(resolve.request.send(badRequestBackend)) ==
            Left(GiteaError.BadRequest("invalid review comment", badRequestBody)),
          resolve.decode(resolve.request.send(forbiddenBackend)) ==
            Left(GiteaError.Forbidden("forbidden", forbiddenBody)),
          unresolve.decode(unresolve.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing review comment", notFoundBody))
        )
      },
      test("maps documented pull request merge/update failures") {
        val forbiddenBody = """{"message":"forbidden"}"""
        val notFoundBody = """{"message":"missing pull request"}"""
        val methodNotAllowedBody = """{"message":"merge method is not allowed"}"""
        val conflictBody = """{"message":"merge conflict"}"""
        val validationBody = """{"message":"invalid update"}"""
        val lockedBody = """{"message":"repository is archived"}"""
        val forbiddenBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(forbiddenBody, StatusCode.Forbidden))
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val methodNotAllowedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(methodNotAllowedBody, StatusCode(405)))
        val conflictBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(conflictBody, StatusCode.Conflict))
        val validationBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(validationBody, StatusCode.UnprocessableEntity)
          )
        val lockedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(lockedBody, StatusCode(423)))
        val merge = GiteaRequests.mergePullRequest(
          config,
          "owner",
          "repo",
          77,
          MergePullRequestOption(MergePullRequestMethod.Merge)
        )
        val cancel = GiteaRequests.cancelScheduledAutoMerge(config, "owner", "repo", 77)
        val update = GiteaRequests.updatePullRequest(config, "owner", "repo", 77, PullRequestUpdateStyle.Merge)

        assertTrue(
          merge.decode(merge.request.send(forbiddenBackend)) ==
            Left(GiteaError.Forbidden("forbidden", forbiddenBody)),
          cancel.decode(cancel.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing pull request", notFoundBody)),
          merge.decode(merge.request.send(methodNotAllowedBackend)) ==
            Left(GiteaError.MethodNotAllowed("merge method is not allowed", methodNotAllowedBody)),
          merge.decode(merge.request.send(conflictBackend)) ==
            Left(GiteaError.Conflict("merge conflict", conflictBody)),
          update.decode(update.request.send(validationBackend)) ==
            Left(GiteaError.UnprocessableEntity("invalid update", validationBody)),
          update.decode(update.request.send(lockedBackend)) ==
            Left(GiteaError.Locked("repository is archived", lockedBody))
        )
      },
      test("builds and decodes schema-traceable pull review request creation and cancellation") {
        val body = PullReviewRequestOptions(
          reviewers = Some(List("alice", "bob")),
          teamReviewers = Some(List("maintainers"))
        )
        val create = GiteaRequests.createPullReviewRequests(config, "worx bend", "gitea/scala", 88, body)
        val delete = GiteaRequests.deletePullReviewRequests(config, "worx bend", "gitea/scala", 88, body)
        val createBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""[{"id":12,"state":"REQUEST_REVIEW","body":"review requested"}]""", StatusCode.Created)
          )
        val deleteBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))

        val createBody =
          create.request.body match
            case StringBody(value, _, _) => value
            case _ => ""
        val deleteBody =
          delete.request.body match
            case StringBody(value, _, _) => value
            case _ => ""

        assertTrue(
          create.endpoint == GiteaEndpoints.repoCreatePullReviewRequests,
          create.endpoint.method == "POST",
          create.endpoint.operationId == "repoCreatePullReviewRequests",
          create.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/requested_reviewers",
          create.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          create.endpoint.response == "#/responses/PullReviewList",
          create.request.method == Method.POST,
          create.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/requested_reviewers",
          create.request.header("Content-Type").exists(_.startsWith("application/json")),
          createBody == """{"reviewers":["alice","bob"],"team_reviewers":["maintainers"]}""",
          create.retryable == false,
          create.decode(create.request.send(createBackend)).map(_.map(_.state)) ==
            Right(Chunk(Some(PullReviewState.RequestReview))),
          delete.endpoint == GiteaEndpoints.repoDeletePullReviewRequests,
          delete.endpoint.method == "DELETE",
          delete.endpoint.operationId == "repoDeletePullReviewRequests",
          delete.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/requested_reviewers",
          delete.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          delete.endpoint.response == "#/responses/empty",
          delete.request.method == Method.DELETE,
          delete.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/requested_reviewers",
          delete.request.header("Content-Type").exists(_.startsWith("application/json")),
          deleteBody == """{"reviewers":["alice","bob"],"team_reviewers":["maintainers"]}""",
          delete.retryable == false,
          delete.decode(delete.request.send(deleteBackend)) == Right(())
        )
      },
      test("builds schema-traceable paginated pull request reviews request") {
        val built = GiteaRequests.repoPullReviews(config, "worx bend", "gitea/scala", 88, page = 3)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListPullReviews,
          endpoint.method == "GET",
          endpoint.operationId == "repoListPullReviews",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index", "page", "limit"),
          endpoint.response == "#/responses/PullReviewList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews?"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("25"),
          built.retryable == true
        )
      },
      test("builds and decodes schema-traceable pull request review creation request") {
        val body = CreatePullReviewOptions(
          body = Some("Review summary"),
          comments = Some(
            List(
              CreatePullReviewComment(
                body = Some("Use the shared helper here"),
                newPosition = Some(14L),
                oldPosition = Some(0L),
                path = Some("src/Main.scala")
              )
            )
          ),
          commitId = Some("abc123"),
          event = Some(PullReviewState.Comment)
        )
        val built = GiteaRequests.createPullReview(config, "worx bend", "gitea/scala", 88, body)
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"id":12,"state":"COMMENT","body":"Review summary"}""")
          )
        val requestBody =
          built.request.body match
            case StringBody(value, _, _) => value
            case _ => ""

        assertTrue(
          built.endpoint == GiteaEndpoints.repoCreatePullReview,
          built.endpoint.method == "POST",
          built.endpoint.operationId == "repoCreatePullReview",
          built.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews",
          built.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          built.endpoint.response == "#/responses/PullReview",
          built.request.method == Method.POST,
          built.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews",
          built.request.header("Content-Type").exists(_.startsWith("application/json")),
          requestBody ==
            """{"body":"Review summary","comments":[{"body":"Use the shared helper here","new_position":14,"old_position":0,"path":"src/Main.scala"}],"commit_id":"abc123","event":"COMMENT"}""",
          built.retryable == false,
          built.decode(built.request.send(backend)).map(_.state) == Right(Some(PullReviewState.Comment))
        )
      },
      test("builds schema-traceable pull request review detail and comments requests") {
        val review = GiteaRequests.repoPullReview(config, "worx bend", "gitea/scala", 88, 12)
        val comments = GiteaRequests.repoPullReviewComments(config, "worx bend", "gitea/scala", 88, 12)

        assertTrue(
          review.endpoint == GiteaEndpoints.repoGetPullReview,
          review.endpoint.method == "GET",
          review.endpoint.operationId == "repoGetPullReview",
          review.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}",
          review.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id"),
          review.endpoint.response == "#/responses/PullReview",
          review.request.method == Method.GET,
          review.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews/12",
          review.retryable == true,
          comments.endpoint == GiteaEndpoints.repoGetPullReviewComments,
          comments.endpoint.method == "GET",
          comments.endpoint.operationId == "repoGetPullReviewComments",
          comments.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}/comments",
          comments.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id"),
          comments.endpoint.response == "#/responses/PullReviewCommentList",
          comments.request.method == Method.GET,
          comments.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews/12/comments",
          comments.retryable == true
        )
      },
      test("builds and decodes schema-traceable pull request review submit and dismissal requests") {
        val submitBody = SubmitPullReviewOptions(body = Some("Looks good"), event = Some(PullReviewState.Approved))
        val dismissBody = DismissPullReviewOptions(message = Some("Superseded"), priors = Some(true))
        val submit = GiteaRequests.submitPullReview(config, "worx bend", "gitea/scala", 88, 12, submitBody)
        val dismiss = GiteaRequests.dismissPullReview(config, "worx bend", "gitea/scala", 88, 12, dismissBody)
        val undismiss = GiteaRequests.undismissPullReview(config, "worx bend", "gitea/scala", 88, 12)
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"id":12,"state":"APPROVED","dismissed":false}""")
          )
        val submitRequestBody =
          submit.request.body match
            case StringBody(value, _, _) => value
            case _ => ""
        val dismissRequestBody =
          dismiss.request.body match
            case StringBody(value, _, _) => value
            case _ => ""

        assertTrue(
          submit.endpoint == GiteaEndpoints.repoSubmitPullReview,
          submit.endpoint.method == "POST",
          submit.endpoint.operationId == "repoSubmitPullReview",
          submit.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}",
          submit.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id", "body"),
          submit.endpoint.response == "#/responses/PullReview",
          submit.request.method == Method.POST,
          submit.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews/12",
          submit.request.header("Content-Type").exists(_.startsWith("application/json")),
          submitRequestBody == """{"body":"Looks good","event":"APPROVED"}""",
          submit.retryable == false,
          submit.decode(submit.request.send(backend)).map(_.state) == Right(Some(PullReviewState.Approved)),
          dismiss.endpoint == GiteaEndpoints.repoDismissPullReview,
          dismiss.endpoint.method == "POST",
          dismiss.endpoint.operationId == "repoDismissPullReview",
          dismiss.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}/dismissals",
          dismiss.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id", "body"),
          dismiss.endpoint.response == "#/responses/PullReview",
          dismiss.request.method == Method.POST,
          dismiss.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews/12/dismissals",
          dismiss.request.header("Content-Type").exists(_.startsWith("application/json")),
          dismissRequestBody == """{"message":"Superseded","priors":true}""",
          dismiss.retryable == false,
          dismiss.decode(dismiss.request.send(backend)).map(_.state) == Right(Some(PullReviewState.Approved)),
          undismiss.endpoint == GiteaEndpoints.repoUnDismissPullReview,
          undismiss.endpoint.method == "POST",
          undismiss.endpoint.operationId == "repoUnDismissPullReview",
          undismiss.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}/undismissals",
          undismiss.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id"),
          undismiss.endpoint.response == "#/responses/PullReview",
          undismiss.request.method == Method.POST,
          undismiss.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews/12/undismissals",
          undismiss.request.body == NoBody,
          undismiss.retryable == false,
          undismiss.decode(undismiss.request.send(backend)).map(_.dismissed) == Right(Some(false))
        )
      },
      test("builds and decodes schema-traceable pull request review deletion request") {
        val built = GiteaRequests.deletePullReview(config, "worx bend", "gitea/scala", 88, 12)
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))

        assertTrue(
          built.endpoint == GiteaEndpoints.repoDeletePullReview,
          built.endpoint.method == "DELETE",
          built.endpoint.operationId == "repoDeletePullReview",
          built.endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}",
          built.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id"),
          built.endpoint.response == "#/responses/empty",
          built.request.method == Method.DELETE,
          built.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/reviews/12",
          built.retryable == false,
          built.decode(built.request.send(backend)) == Right(())
        )
      },
      test("builds schema-traceable pull request diff or patch request") {
        val built =
          GiteaRequests.repoPullRequestDiffOrPatch(
            config,
            "worx bend",
            "gitea/scala",
            88,
            PullRequestDiffType.Patch,
            binary = Some(true)
          )
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoDownloadPullDiffOrPatch,
          endpoint.method == "GET",
          endpoint.operationId == "repoDownloadPullDiffOrPatch",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}.{diffType}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index", "diffType", "binary"),
          endpoint.response == "#/responses/string",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88.patch?"),
          request.uri.paramsMap.get("binary").contains("true"),
          request.header("Accept").contains("text/plain"),
          built.retryable == true
        )
      },
      test("builds schema-traceable paginated pull request files request") {
        val params = PullRequestFilesParams(
          skipTo = Some("src/Main.scala"),
          whitespace = Some(PullRequestFileWhitespace.IgnoreEol),
          page = Some(3),
          limit = Some(9)
        )
        val built = GiteaRequests.repoPullRequestFiles(config, "worx bend", "gitea/scala", 88, params)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoGetPullRequestFiles,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetPullRequestFiles",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/files",
          endpoint.parameters.map(_.name) ==
            List("owner", "repo", "index", "skip-to", "whitespace", "page", "limit"),
          endpoint.response == "#/responses/ChangedFileList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/files?"),
          request.uri.paramsMap.get("skip-to").contains("src/Main.scala"),
          request.uri.paramsMap.get("whitespace").contains("ignore-eol"),
          request.uri.paramsMap.get("page").contains("3"),
          request.uri.paramsMap.get("limit").contains("9"),
          built.retryable == true
        )
      },
      test("builds schema-traceable paginated pull request commits request") {
        val params = PullRequestCommitsParams(
          verification = Some(false),
          files = Some(true),
          page = Some(4),
          limit = Some(8)
        )
        val built = GiteaRequests.repoPullRequestCommits(config, "worx bend", "gitea/scala", 88, params)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoGetPullRequestCommits,
          endpoint.method == "GET",
          endpoint.operationId == "repoGetPullRequestCommits",
          endpoint.path == "/repos/{owner}/{repo}/pulls/{index}/commits",
          endpoint.parameters.map(_.name) ==
            List("owner", "repo", "index", "page", "limit", "verification", "files"),
          endpoint.response == "#/responses/CommitList",
          request.method == Method.GET,
          request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/pulls/88/commits?"),
          request.uri.paramsMap.get("page").contains("4"),
          request.uri.paramsMap.get("limit").contains("8"),
          request.uri.paramsMap.get("verification").contains("false"),
          request.uri.paramsMap.get("files").contains("true"),
          built.retryable == true
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
      test("builds schema-traceable repository pinned issues request") {
        val built = GiteaRequests.pinnedIssues(config, "worx bend", "gitea/scala")
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.repoListPinnedIssues,
          endpoint.method == "GET",
          endpoint.operationId == "repoListPinnedIssues",
          endpoint.path == "/repos/{owner}/{repo}/issues/pinned",
          endpoint.parameters.map(_.name) == List("owner", "repo"),
          endpoint.response == "#/responses/IssueList",
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/pinned",
          built.retryable == true
        )
      },
      test("builds schema-traceable delete issue request") {
        val built = GiteaRequests.deleteIssue(config, "worx bend", "gitea/scala", 99)
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.issueDelete,
          endpoint.method == "DELETE",
          endpoint.operationId == "issueDelete",
          endpoint.path == "/repos/{owner}/{repo}/issues/{index}",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          endpoint.response == "#/responses/empty",
          request.method == Method.DELETE,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99",
          request.header("Accept").contains("application/json"),
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").isEmpty,
          built.retryable == false
        )
      },
      test("builds schema-traceable issue pin management requests") {
        val pin = GiteaRequests.pinIssue(config, "worx bend", "gitea/scala", 99)
        val unpin = GiteaRequests.unpinIssue(config, "worx bend", "gitea/scala", 99)
        val move = GiteaRequests.moveIssuePin(config, "worx bend", "gitea/scala", 99, 2)

        assertTrue(
          pin.endpoint == GiteaEndpoints.pinIssue,
          pin.endpoint.method == "POST",
          pin.endpoint.operationId == "pinIssue",
          pin.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/pin",
          pin.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          pin.endpoint.response == "#/responses/empty",
          pin.request.method == Method.POST,
          pin.request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/pin",
          pin.request.header("Content-Type").isEmpty,
          pin.retryable == false,
          unpin.endpoint == GiteaEndpoints.unpinIssue,
          unpin.endpoint.method == "DELETE",
          unpin.endpoint.operationId == "unpinIssue",
          unpin.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/pin",
          unpin.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          unpin.endpoint.response == "#/responses/empty",
          unpin.request.method == Method.DELETE,
          unpin.request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/pin",
          unpin.request.header("Content-Type").isEmpty,
          unpin.retryable == false,
          move.endpoint == GiteaEndpoints.moveIssuePin,
          move.endpoint.method == "PATCH",
          move.endpoint.operationId == "moveIssuePin",
          move.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/pin/{position}",
          move.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "position"),
          move.endpoint.response == "#/responses/empty",
          move.request.method == Method.PATCH,
          move.request.uri.toString == "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/pin/2",
          move.request.header("Content-Type").isEmpty,
          move.retryable == false
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
      test("builds schema-traceable issue comment management requests") {
        val since = Instant.parse("2026-06-01T00:00:00Z")
        val before = Instant.parse("2026-06-18T00:00:00Z")
        val issueComments =
          GiteaRequests.issueComments(
            config,
            "worx bend",
            "gitea/scala",
            99,
            IssueCommentListParams(since = Some(since), before = Some(before))
          )
        val repoComments =
          GiteaRequests.repoIssueComments(
            config,
            "worx bend",
            "gitea/scala",
            RepositoryCommentListParams(since = Some(since), before = Some(before), page = Some(2), limit = Some(9))
          )
        val getComment = GiteaRequests.issueComment(config, "worx bend", "gitea/scala", 30)
        val editComment =
          GiteaRequests.editIssueComment(
            config,
            "worx bend",
            "gitea/scala",
            30,
            EditIssueComment("Updated")
          )
        val deleteComment = GiteaRequests.deleteIssueComment(config, "worx bend", "gitea/scala", 30)

        assertTrue(
          issueComments.endpoint == GiteaEndpoints.issueGetComments,
          issueComments.endpoint.method == "GET",
          issueComments.endpoint.operationId == "issueGetComments",
          issueComments.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/comments",
          issueComments.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "since", "before"),
          issueComments.endpoint.response == "#/responses/CommentList",
          issueComments.request.method == Method.GET,
          issueComments.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/comments?"
          ),
          issueComments.request.uri.paramsMap.get("since").contains("2026-06-01T00:00:00Z"),
          issueComments.request.uri.paramsMap.get("before").contains("2026-06-18T00:00:00Z"),
          issueComments.retryable == true,
          repoComments.endpoint == GiteaEndpoints.issueGetRepoComments,
          repoComments.endpoint.method == "GET",
          repoComments.endpoint.operationId == "issueGetRepoComments",
          repoComments.endpoint.path == "/repos/{owner}/{repo}/issues/comments",
          repoComments.endpoint.parameters.map(_.name) == List("owner", "repo", "since", "before", "page", "limit"),
          repoComments.endpoint.response == "#/responses/CommentList",
          repoComments.request.method == Method.GET,
          repoComments.request.uri.toString.contains("/api/v1/repos/worx%20bend/gitea%2Fscala/issues/comments?"),
          repoComments.request.uri.paramsMap.get("page").contains("2"),
          repoComments.request.uri.paramsMap.get("limit").contains("9"),
          repoComments.retryable == true,
          getComment.endpoint == GiteaEndpoints.issueGetComment,
          getComment.endpoint.method == "GET",
          getComment.endpoint.operationId == "issueGetComment",
          getComment.endpoint.path == "/repos/{owner}/{repo}/issues/comments/{id}",
          getComment.endpoint.response == "#/responses/Comment",
          getComment.request.method == Method.GET,
          getComment.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/comments/30",
          getComment.retryable == true,
          editComment.endpoint == GiteaEndpoints.issueEditComment,
          editComment.endpoint.method == "PATCH",
          editComment.endpoint.operationId == "issueEditComment",
          editComment.endpoint.path == "/repos/{owner}/{repo}/issues/comments/{id}",
          editComment.endpoint.parameters.map(_.name) == List("owner", "repo", "id", "body"),
          editComment.endpoint.response == "#/responses/Comment",
          editComment.request.method == Method.PATCH,
          editComment.request.header("Content-Type").exists(_.startsWith("application/json")),
          editComment.retryable == false,
          editComment.request.body match
            case StringBody(json, _, _) => json.contains(""""body":"Updated"""")
            case _ => false,
          deleteComment.endpoint == GiteaEndpoints.issueDeleteComment,
          deleteComment.endpoint.method == "DELETE",
          deleteComment.endpoint.operationId == "issueDeleteComment",
          deleteComment.endpoint.path == "/repos/{owner}/{repo}/issues/comments/{id}",
          deleteComment.endpoint.parameters.map(_.name) == List("owner", "repo", "id"),
          deleteComment.endpoint.response == "#/responses/empty",
          deleteComment.request.method == Method.DELETE,
          deleteComment.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/comments/30",
          deleteComment.retryable == false
        )
      },
      test("builds schema-traceable issue comment reaction requests") {
        val body = EditReactionOption(content = "+1")
        val list = GiteaRequests.issueCommentReactions(config, "worx bend", "gitea/scala", 30)
        val add = GiteaRequests.postIssueCommentReaction(config, "worx bend", "gitea/scala", 30, body)
        val remove = GiteaRequests.deleteIssueCommentReaction(config, "worx bend", "gitea/scala", 30, body)

        assertTrue(
          list.endpoint == GiteaEndpoints.issueGetCommentReactions,
          list.endpoint.method == "GET",
          list.endpoint.operationId == "issueGetCommentReactions",
          list.endpoint.path == "/repos/{owner}/{repo}/issues/comments/{id}/reactions",
          list.endpoint.parameters.map(_.name) == List("owner", "repo", "id"),
          list.endpoint.response == "#/responses/ReactionList",
          list.request.method == Method.GET,
          list.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/comments/30/reactions",
          list.retryable == true,
          add.endpoint == GiteaEndpoints.issuePostCommentReaction,
          add.endpoint.method == "POST",
          add.endpoint.operationId == "issuePostCommentReaction",
          add.endpoint.parameters.map(_.name) == List("owner", "repo", "id", "content"),
          add.endpoint.response == "#/responses/Reaction",
          add.request.method == Method.POST,
          add.request.header("Content-Type").exists(_.startsWith("application/json")),
          add.retryable == false,
          add.request.body match
            case StringBody(json, _, _) => json.contains(""""content":"+1"""")
            case _ => false,
          remove.endpoint == GiteaEndpoints.issueDeleteCommentReaction,
          remove.endpoint.method == "DELETE",
          remove.endpoint.operationId == "issueDeleteCommentReaction",
          remove.endpoint.path == "/repos/{owner}/{repo}/issues/comments/{id}/reactions",
          remove.request.method == Method.DELETE,
          remove.request.header("Content-Type").exists(_.startsWith("application/json")),
          remove.retryable == false,
          remove.request.body match
            case StringBody(json, _, _) => json.contains(""""content":"+1"""")
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
      test("builds schema-traceable issue deadline request with JSON body") {
        val built =
          GiteaRequests.editIssueDeadline(
            config,
            "worx bend",
            "gitea/scala",
            99,
            EditDeadlineOption(dueDate = Some(Instant.parse("2026-07-03T00:00:00Z")))
          )
        val endpoint = built.endpoint
        val request = built.request

        assertTrue(
          endpoint == GiteaEndpoints.issueEditIssueDeadline,
          endpoint.method == "POST",
          endpoint.operationId == "issueEditIssueDeadline",
          endpoint.path == "/repos/{owner}/{repo}/issues/{index}/deadline",
          endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          endpoint.response == "#/responses/IssueDeadline",
          request.method == Method.POST,
          request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/deadline",
          request.header("Content-Type").exists(_.startsWith("application/json")),
          built.retryable == false,
          request.body match
            case StringBody(json, _, _) => json.contains(""""due_date":"2026-07-03T00:00:00Z"""")
            case _ => false
        )
      },
      test("builds schema-traceable issue blocking and dependency requests") {
        val body = IssueMeta(index = 13L, owner = Some("other"), repo = Some("project"))
        val blocks = GiteaRequests.issueBlocks(config, "worx bend", "gitea/scala", 99, page = 2)
        val block = GiteaRequests.createIssueBlocking(config, "worx bend", "gitea/scala", 99, body)
        val unblock = GiteaRequests.removeIssueBlocking(config, "worx bend", "gitea/scala", 99, IssueMeta(13L))
        val dependencies = GiteaRequests.issueDependencies(config, "worx bend", "gitea/scala", 99, page = 3)
        val addDependency = GiteaRequests.createIssueDependency(config, "worx bend", "gitea/scala", 99, body)
        val removeDependency =
          GiteaRequests.removeIssueDependency(config, "worx bend", "gitea/scala", 99, IssueMeta(13L))

        assertTrue(
          blocks.endpoint == GiteaEndpoints.issueListBlocks,
          blocks.endpoint.operationId == "issueListBlocks",
          blocks.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/blocks",
          blocks.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "page", "limit"),
          blocks.endpoint.response == "#/responses/IssueList",
          blocks.request.method == Method.GET,
          blocks.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/blocks?"
          ),
          blocks.request.uri.paramsMap.get("page").contains("2"),
          blocks.request.uri.paramsMap.get("limit").contains("25"),
          blocks.retryable == true,
          block.endpoint == GiteaEndpoints.issueCreateIssueBlocking,
          block.endpoint.method == "POST",
          block.endpoint.operationId == "issueCreateIssueBlocking",
          block.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          block.endpoint.response == "#/responses/Issue",
          block.request.method == Method.POST,
          block.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/blocks",
          block.request.header("Content-Type").exists(_.startsWith("application/json")),
          block.retryable == false,
          block.request.body match
            case StringBody(json, _, _) =>
              json.contains(""""index":13""") &&
                json.contains(""""owner":"other"""") &&
                json.contains(""""repo":"project"""")
            case _ => false,
          unblock.endpoint == GiteaEndpoints.issueRemoveIssueBlocking,
          unblock.endpoint.method == "DELETE",
          unblock.endpoint.operationId == "issueRemoveIssueBlocking",
          unblock.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/blocks",
          unblock.request.method == Method.DELETE,
          unblock.request.header("Content-Type").exists(_.startsWith("application/json")),
          unblock.retryable == false,
          unblock.request.body match
            case StringBody(json, _, _) => json.contains(""""index":13""")
            case _ => false,
          dependencies.endpoint == GiteaEndpoints.issueListIssueDependencies,
          dependencies.endpoint.operationId == "issueListIssueDependencies",
          dependencies.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/dependencies",
          dependencies.endpoint.response == "#/responses/IssueList",
          dependencies.request.method == Method.GET,
          dependencies.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/dependencies?"
          ),
          dependencies.request.uri.paramsMap.get("page").contains("3"),
          dependencies.retryable == true,
          addDependency.endpoint == GiteaEndpoints.issueCreateIssueDependencies,
          addDependency.endpoint.operationId == "issueCreateIssueDependencies",
          addDependency.request.method == Method.POST,
          addDependency.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/dependencies",
          addDependency.request.header("Content-Type").exists(_.startsWith("application/json")),
          addDependency.retryable == false,
          removeDependency.endpoint == GiteaEndpoints.issueRemoveIssueDependencies,
          removeDependency.endpoint.operationId == "issueRemoveIssueDependencies",
          removeDependency.request.method == Method.DELETE,
          removeDependency.request.header("Content-Type").exists(_.startsWith("application/json")),
          removeDependency.retryable == false
        )
      },
      test("builds schema-traceable issue reaction requests") {
        val body = EditReactionOption(content = "heart")
        val list = GiteaRequests.issueReactions(config, "worx bend", "gitea/scala", 99, page = 4)
        val add = GiteaRequests.postIssueReaction(config, "worx bend", "gitea/scala", 99, body)
        val remove = GiteaRequests.deleteIssueReaction(config, "worx bend", "gitea/scala", 99, body)

        assertTrue(
          list.endpoint == GiteaEndpoints.issueGetIssueReactions,
          list.endpoint.method == "GET",
          list.endpoint.operationId == "issueGetIssueReactions",
          list.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/reactions",
          list.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "page", "limit"),
          list.endpoint.response == "#/responses/ReactionList",
          list.request.method == Method.GET,
          list.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/reactions?"
          ),
          list.request.uri.paramsMap.get("page").contains("4"),
          list.request.uri.paramsMap.get("limit").contains("25"),
          list.retryable == true,
          add.endpoint == GiteaEndpoints.issuePostIssueReaction,
          add.endpoint.method == "POST",
          add.endpoint.operationId == "issuePostIssueReaction",
          add.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "content"),
          add.endpoint.response == "#/responses/Reaction",
          add.request.method == Method.POST,
          add.request.header("Content-Type").exists(_.startsWith("application/json")),
          add.retryable == false,
          add.request.body match
            case StringBody(json, _, _) => json.contains(""""content":"heart"""")
            case _ => false,
          remove.endpoint == GiteaEndpoints.issueDeleteIssueReaction,
          remove.endpoint.method == "DELETE",
          remove.endpoint.operationId == "issueDeleteIssueReaction",
          remove.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/reactions",
          remove.request.method == Method.DELETE,
          remove.request.header("Content-Type").exists(_.startsWith("application/json")),
          remove.retryable == false,
          remove.request.body match
            case StringBody(json, _, _) => json.contains(""""content":"heart"""")
            case _ => false
        )
      },
      test("builds schema-traceable issue subscription requests") {
        val list = GiteaRequests.issueSubscriptions(config, "worx bend", "gitea/scala", 99, page = 5)
        val check = GiteaRequests.issueSubscription(config, "worx bend", "gitea/scala", 99)
        val add = GiteaRequests.addIssueSubscription(config, "worx bend", "gitea/scala", 99, "space user/slash")
        val remove = GiteaRequests.deleteIssueSubscription(config, "worx bend", "gitea/scala", 99, "space user/slash")

        assertTrue(
          list.endpoint == GiteaEndpoints.issueSubscriptions,
          list.endpoint.method == "GET",
          list.endpoint.operationId == "issueSubscriptions",
          list.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/subscriptions",
          list.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "page", "limit"),
          list.endpoint.response == "#/responses/UserList",
          list.request.method == Method.GET,
          list.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/subscriptions?"
          ),
          list.request.uri.paramsMap.get("page").contains("5"),
          list.request.uri.paramsMap.get("limit").contains("25"),
          list.retryable == true,
          check.endpoint == GiteaEndpoints.issueCheckSubscription,
          check.endpoint.method == "GET",
          check.endpoint.operationId == "issueCheckSubscription",
          check.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/subscriptions/check",
          check.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          check.endpoint.response == "#/responses/WatchInfo",
          check.request.method == Method.GET,
          check.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/subscriptions/check",
          check.retryable == true,
          add.endpoint == GiteaEndpoints.issueAddSubscription,
          add.endpoint.method == "PUT",
          add.endpoint.operationId == "issueAddSubscription",
          add.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/subscriptions/{user}",
          add.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "user"),
          add.endpoint.response == "#/responses/empty",
          add.request.method == Method.PUT,
          add.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/subscriptions/space%20user%2Fslash",
          add.retryable == false,
          remove.endpoint == GiteaEndpoints.issueDeleteSubscription,
          remove.endpoint.method == "DELETE",
          remove.endpoint.operationId == "issueDeleteSubscription",
          remove.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/subscriptions/{user}",
          remove.request.method == Method.DELETE,
          remove.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/subscriptions/space%20user%2Fslash",
          remove.retryable == false
        )
      },
      test("builds schema-traceable issue tracked-time requests") {
        val since = Instant.parse("2026-06-01T00:00:00Z")
        val before = Instant.parse("2026-06-18T00:00:00Z")
        val list =
          GiteaRequests.issueTrackedTimes(
            config,
            "worx bend",
            "gitea/scala",
            99,
            IssueTrackedTimeListParams(
              user = Some("octo"),
              since = Some(since),
              before = Some(before),
              page = Some(6),
              limit = Some(7)
            )
          )
        val add =
          GiteaRequests.addIssueTrackedTime(
            config,
            "worx bend",
            "gitea/scala",
            99,
            AddTimeOption(time = 3600L, created = Some(since), userName = Some("octo"))
          )
        val reset = GiteaRequests.resetIssueTrackedTime(config, "worx bend", "gitea/scala", 99)
        val remove = GiteaRequests.deleteIssueTrackedTime(config, "worx bend", "gitea/scala", 99, 44)

        assertTrue(
          list.endpoint == GiteaEndpoints.issueTrackedTimes,
          list.endpoint.method == "GET",
          list.endpoint.operationId == "issueTrackedTimes",
          list.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/times",
          list.endpoint.parameters.map(_.name) ==
            List("owner", "repo", "index", "user", "since", "before", "page", "limit"),
          list.endpoint.response == "#/responses/TrackedTimeList",
          list.request.method == Method.GET,
          list.request.uri.toString.contains(
            "/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/times?"
          ),
          list.request.uri.paramsMap.get("user").contains("octo"),
          list.request.uri.paramsMap.get("since").contains("2026-06-01T00:00:00Z"),
          list.request.uri.paramsMap.get("before").contains("2026-06-18T00:00:00Z"),
          list.request.uri.paramsMap.get("page").contains("6"),
          list.request.uri.paramsMap.get("limit").contains("7"),
          list.retryable == true,
          add.endpoint == GiteaEndpoints.issueAddTime,
          add.endpoint.method == "POST",
          add.endpoint.operationId == "issueAddTime",
          add.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "body"),
          add.endpoint.response == "#/responses/TrackedTime",
          add.request.method == Method.POST,
          add.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/times",
          add.request.header("Content-Type").exists(_.startsWith("application/json")),
          add.retryable == false,
          add.request.body match
            case StringBody(json, _, _) =>
              json.contains(""""time":3600""") &&
                json.contains(""""created":"2026-06-01T00:00:00Z"""") &&
                json.contains(""""user_name":"octo"""")
            case _ => false,
          reset.endpoint == GiteaEndpoints.issueResetTime,
          reset.endpoint.method == "DELETE",
          reset.endpoint.operationId == "issueResetTime",
          reset.endpoint.response == "#/responses/empty",
          reset.request.method == Method.DELETE,
          reset.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/times",
          reset.retryable == false,
          remove.endpoint == GiteaEndpoints.issueDeleteTime,
          remove.endpoint.method == "DELETE",
          remove.endpoint.operationId == "issueDeleteTime",
          remove.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/times/{id}",
          remove.endpoint.parameters.map(_.name) == List("owner", "repo", "index", "id"),
          remove.endpoint.response == "#/responses/empty",
          remove.request.method == Method.DELETE,
          remove.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/times/44",
          remove.retryable == false
        )
      },
      test("builds schema-traceable issue stopwatch requests") {
        val start = GiteaRequests.startIssueStopwatch(config, "worx bend", "gitea/scala", 99)
        val stop = GiteaRequests.stopIssueStopwatch(config, "worx bend", "gitea/scala", 99)
        val remove = GiteaRequests.deleteIssueStopwatch(config, "worx bend", "gitea/scala", 99)

        assertTrue(
          start.endpoint == GiteaEndpoints.issueStartStopWatch,
          start.endpoint.method == "POST",
          start.endpoint.operationId == "issueStartStopWatch",
          start.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/stopwatch/start",
          start.endpoint.parameters.map(_.name) == List("owner", "repo", "index"),
          start.endpoint.response == "#/responses/empty",
          start.request.method == Method.POST,
          start.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/stopwatch/start",
          start.request.header("Content-Type").isEmpty,
          start.retryable == false,
          stop.endpoint == GiteaEndpoints.issueStopStopWatch,
          stop.endpoint.method == "POST",
          stop.endpoint.operationId == "issueStopStopWatch",
          stop.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/stopwatch/stop",
          stop.request.method == Method.POST,
          stop.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/stopwatch/stop",
          stop.retryable == false,
          remove.endpoint == GiteaEndpoints.issueDeleteStopWatch,
          remove.endpoint.method == "DELETE",
          remove.endpoint.operationId == "issueDeleteStopWatch",
          remove.endpoint.path == "/repos/{owner}/{repo}/issues/{index}/stopwatch/delete",
          remove.request.method == Method.DELETE,
          remove.request.uri.toString ==
            "https://gitea.example/root/api/v1/repos/worx%20bend/gitea%2Fscala/issues/99/stopwatch/delete",
          remove.retryable == false
        )
      },
      test("builds schema-traceable paginated current-user stopwatch request") {
        val built = GiteaRequests.userStopwatches(config, page = 4)

        assertTrue(
          built.endpoint == GiteaEndpoints.userGetStopWatches,
          built.endpoint.method == "GET",
          built.endpoint.operationId == "userGetStopWatches",
          built.endpoint.path == "/user/stopwatches",
          built.endpoint.parameters.map(_.name) == List("page", "limit"),
          built.endpoint.response == "#/responses/StopWatchList",
          built.request.method == Method.GET,
          built.request.uri.toString.contains("/api/v1/user/stopwatches?"),
          built.request.uri.paramsMap.get("page").contains("4"),
          built.request.uri.paramsMap.get("limit").contains("25"),
          built.retryable == true
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
      test("decodes pinned issues and new pin allowed responses") {
        val pinnedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""[{"id":1,"number":10,"title":"pinned"}]""")
          )
        val allowedBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"issues":true,"pull_requests":false}""")
          )
        val pinnedRequest = GiteaRequests.pinnedIssues(config, "owner", "repo")
        val allowedRequest = GiteaRequests.repoNewPinAllowed(config, "owner", "repo")
        val pinned = pinnedRequest.decode(pinnedRequest.request.send(pinnedBackend))
        val allowed = allowedRequest.decode(allowedRequest.request.send(allowedBackend))

        assertTrue(
          pinned.map(_.headOption.flatMap(_.title)) == Right(Some("pinned")),
          allowed == Right(NewIssuePinsAllowed(issues = Some(true), pullRequests = Some(false)))
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
      test("decodes edited issue deadline responses") {
        val due = Instant.parse("2026-07-03T00:00:00Z")
        val backend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust("""{"due_date":"2026-07-03T00:00:00Z"}""", StatusCode.Created)
          )
        val built =
          GiteaRequests.editIssueDeadline(config, "owner", "repo", 12, EditDeadlineOption(Some(due)))

        assertTrue(
          built.decode(built.request.send(backend)) == Right(IssueDeadline(Some(due)))
        )
      },
      test("decodes issue blocking and dependency responses") {
        val listResponse = """[{"id":1,"number":13,"title":"Blocked"}]"""
        val issueResponse = """{"id":2,"number":99,"title":"Root"}"""
        val headers = List(Header("x-total-count", "1"))
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.method == Method.GET)
            .thenRespond(ResponseStub.adjust(listResponse, StatusCode.Ok, headers))
            .whenRequestMatches(_.method == Method.POST)
            .thenRespond(ResponseStub.adjust(issueResponse, StatusCode.Created))
            .whenRequestMatches(_.method == Method.DELETE)
            .thenRespond(ResponseStub.adjust(issueResponse, StatusCode.Ok))
        val blocks = GiteaRequests.issueBlocks(config, "owner", "repo", 99)
        val dependencies = GiteaRequests.issueDependencies(config, "owner", "repo", 99)
        val block = GiteaRequests.createIssueBlocking(config, "owner", "repo", 99, IssueMeta(13L))
        val removeDependency = GiteaRequests.removeIssueDependency(config, "owner", "repo", 99, IssueMeta(13L))

        assertTrue(
          blocks.decode(blocks.request.send(backend)).map(_.data.headOption.flatMap(_.number)) == Right(Some(13L)),
          dependencies.decode(dependencies.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          block.decode(block.request.send(backend)).map(_.number) == Right(Some(99L)),
          removeDependency.decode(removeDependency.request.send(backend)).map(_.title) == Right(Some("Root"))
        )
      },
      test("decodes issue and comment reaction responses") {
        val listResponse = """[{"content":"+1","user":{"id":42,"login":"octo"}}]"""
        val reactionResponse = """{"content":"heart","created_at":"2026-06-18T09:00:00Z"}"""
        val headers = List(Header("x-total-count", "1"))
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(request => request.method == Method.GET && request.uri.paramsMap.contains("page"))
            .thenRespond(ResponseStub.adjust(listResponse, StatusCode.Ok, headers))
            .whenRequestMatches(_.method == Method.GET)
            .thenRespond(ResponseStub.adjust(listResponse))
            .whenRequestMatches(_.method == Method.POST)
            .thenRespond(ResponseStub.adjust(reactionResponse, StatusCode.Created))
            .whenRequestMatches(_.method == Method.DELETE)
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val issueReactions = GiteaRequests.issueReactions(config, "owner", "repo", 99)
        val commentReactions = GiteaRequests.issueCommentReactions(config, "owner", "repo", 30)
        val add = GiteaRequests.postIssueReaction(config, "owner", "repo", 99, EditReactionOption("heart"))
        val remove = GiteaRequests.deleteIssueCommentReaction(config, "owner", "repo", 30, EditReactionOption("+1"))

        assertTrue(
          issueReactions.decode(issueReactions.request.send(backend)).map(_.data.headOption.flatMap(_.content)) ==
            Right(Some("+1")),
          issueReactions.decode(issueReactions.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          commentReactions.decode(commentReactions.request.send(backend)).map(_.headOption.flatMap(_.content)) ==
            Right(Some("+1")),
          add.decode(add.request.send(backend)).map(_.content) == Right(Some("heart")),
          remove.decode(remove.request.send(backend)) == Right(())
        )
      },
      test("decodes issue subscription responses") {
        val userListResponse = """[{"id":42,"login":"octo"}]"""
        val watchResponse =
          """{
            |  "created_at": "2026-06-18T10:00:00Z",
            |  "ignored": false,
            |  "repository_url": "https://gitea.example/api/v1/repos/owner/repo",
            |  "subscribed": true,
            |  "url": "https://gitea.example/api/v1/repos/owner/repo/subscription"
            |}""".stripMargin
        val headers = List(Header("x-total-count", "1"))
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.uri.path.endsWith(List("subscriptions", "check")))
            .thenRespond(ResponseStub.adjust(watchResponse))
            .whenRequestMatches(_.method == Method.GET)
            .thenRespond(ResponseStub.adjust(userListResponse, StatusCode.Ok, headers))
            .whenRequestMatches(_.method == Method.PUT)
            .thenRespond(ResponseStub.adjust("", StatusCode.Created))
            .whenRequestMatches(_.method == Method.DELETE)
            .thenRespond(ResponseStub.adjust("", StatusCode.Ok))
        val list = GiteaRequests.issueSubscriptions(config, "owner", "repo", 99)
        val check = GiteaRequests.issueSubscription(config, "owner", "repo", 99)
        val add = GiteaRequests.addIssueSubscription(config, "owner", "repo", 99, "octo")
        val remove = GiteaRequests.deleteIssueSubscription(config, "owner", "repo", 99, "octo")

        assertTrue(
          list.decode(list.request.send(backend)).map(_.data.headOption.flatMap(_.login)) == Right(Some("octo")),
          list.decode(list.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          check.decode(check.request.send(backend)).map(_.subscribed) == Right(Some(true)),
          check.decode(check.request.send(backend)).map(_.createdAt) ==
            Right(Some(Instant.parse("2026-06-18T10:00:00Z"))),
          add.decode(add.request.send(backend)) == Right(()),
          remove.decode(remove.request.send(backend)) == Right(())
        )
      },
      test("decodes issue tracked-time responses") {
        val listResponse = """[{"id":44,"time":3600,"user_name":"octo","issue":{"number":12}}]"""
        val trackedTimeResponse = """{"id":45,"time":1800,"user_name":"octo"}"""
        val headers = List(Header("x-total-count", "1"))
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.method == Method.GET)
            .thenRespond(ResponseStub.adjust(listResponse, StatusCode.Ok, headers))
            .whenRequestMatches(_.method == Method.POST)
            .thenRespond(ResponseStub.adjust(trackedTimeResponse, StatusCode.Ok))
            .whenRequestMatches(_.method == Method.DELETE)
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val list = GiteaRequests.issueTrackedTimes(config, "owner", "repo", 99)
        val add = GiteaRequests.addIssueTrackedTime(config, "owner", "repo", 99, AddTimeOption(1800L))
        val reset = GiteaRequests.resetIssueTrackedTime(config, "owner", "repo", 99)
        val remove = GiteaRequests.deleteIssueTrackedTime(config, "owner", "repo", 99, 44)

        assertTrue(
          list.decode(list.request.send(backend)).map(_.data.headOption.flatMap(_.id)) == Right(Some(44L)),
          list.decode(list.request.send(backend)).map(_.data.headOption.flatMap(_.issue.flatMap(_.number))) ==
            Right(Some(12L)),
          list.decode(list.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          add.decode(add.request.send(backend)).map(_.time) == Right(Some(1800L)),
          reset.decode(reset.request.send(backend)) == Right(()),
          remove.decode(remove.request.send(backend)) == Right(())
        )
      },
      test("decodes stopwatch responses") {
        val stopwatchListResponse =
          """[{"created":"2026-06-18T10:30:00Z","issue_index":12,"repo_name":"gitea4s","seconds":3723}]"""
        val headers = List(Header("x-total-count", "1"))
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.method == Method.GET)
            .thenRespond(ResponseStub.adjust(stopwatchListResponse, StatusCode.Ok, headers))
            .whenRequestMatches(_.method == Method.POST)
            .thenRespond(ResponseStub.adjust("", StatusCode.Created))
            .whenRequestMatches(_.method == Method.DELETE)
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val list = GiteaRequests.userStopwatches(config)
        val start = GiteaRequests.startIssueStopwatch(config, "owner", "repo", 99)
        val stop = GiteaRequests.stopIssueStopwatch(config, "owner", "repo", 99)
        val remove = GiteaRequests.deleteIssueStopwatch(config, "owner", "repo", 99)

        assertTrue(
          list.decode(list.request.send(backend)).map(_.data.headOption.flatMap(_.issueIndex)) == Right(Some(12L)),
          list.decode(list.request.send(backend)).map(_.data.headOption.flatMap(_.seconds)) == Right(Some(3723L)),
          list.decode(list.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          start.decode(start.request.send(backend)) == Right(()),
          stop.decode(stop.request.send(backend)) == Right(()),
          remove.decode(remove.request.send(backend)) == Right(())
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
        val pullReviewListResponse =
          """[{"id":50,"state":"APPROVED","body":"Looks good","comments_count":2,"user":{"login":"reviewer"}}]"""
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
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls"))
            }
            .thenRespond(
              ResponseStub.adjust(pullRequestListResponse, StatusCode.Ok, List(Header("x-total-count", "2")))
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":32,"number":3,"state":"open","title":"Created"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":31,"number":2,"state":"closed","title":"Retitled"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2"))
            }
            .thenRespond(ResponseStub.adjust(pullRequestResponse))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "requested_reviewers"))
            }
            .thenRespond(ResponseStub.adjust("""[{"id":12,"state":"REQUEST_REVIEW"}]""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "requested_reviewers"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":13,"state":"COMMENT","body":"Pending notes"}"""))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews"))
            }
            .thenRespond(
              ResponseStub.adjust(pullReviewListResponse, StatusCode.Ok, List(Header("x-total-count", "1")))
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews", "10"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","body":"Looks good"}"""))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews", "10"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","body":"Looks good"}"""))
            .whenRequestMatches(
              _.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews", "10", "dismissals"))
            )
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","dismissed":true}"""))
            .whenRequestMatches(
              _.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews", "10", "undismissals"))
            )
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","dismissed":false}"""))
            .whenRequestMatches(
              _.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "reviews", "10", "comments"))
            )
            .thenRespond(
              ResponseStub.adjust("""[{"id":11,"body":"Please update docs","path":"README.md","position":7}]""")
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2.diff")))
            .thenRespond(ResponseStub.adjust("diff --git a/README.md b/README.md"))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "files")))
            .thenRespond(
              ResponseStub.adjust(
                """[{"filename":"src/Main.scala","status":"modified","additions":4,"deletions":1}]""",
                StatusCode.Ok,
                List(Header("x-total-count", "1"))
              )
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "pulls", "2", "commits")))
            .thenRespond(
              ResponseStub.adjust(
                """[{"sha":"abc123","commit":{"message":"Implement commits"},"stats":{"total":7}}]""",
                StatusCode.Ok,
                List(Header("x-total-count", "1"))
              )
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "octo", "api", "pulls", "pinned")))
            .thenRespond(ResponseStub.adjust("""[{"id":3,"number":3,"title":"Pinned"}]"""))
        val repos = GiteaRequests.userRepos(config, "octo")
        val orgRepos = GiteaRequests.organizationRepos(config, "platform")
        val topics = GiteaRequests.repoTopics(config, "octo", "api")
        val branches = GiteaRequests.repoBranches(config, "octo", "api")
        val tags = GiteaRequests.repoTags(config, "octo", "api")
        val releases = GiteaRequests.repoReleases(config, "octo", "api")
        val release = GiteaRequests.repoRelease(config, "octo", "api", 20)
        val pullRequests = GiteaRequests.repoPullRequests(config, "octo", "api")
        val pinnedPullRequests = GiteaRequests.pinnedPullRequests(config, "octo", "api")
        val pullRequest = GiteaRequests.repoPullRequest(config, "octo", "api", 2)
        val createPullRequest =
          GiteaRequests.createPullRequest(
            config,
            "octo",
            "api",
            CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("Created"))
          )
        val editPullRequest =
          GiteaRequests.editPullRequest(
            config,
            "octo",
            "api",
            2,
            EditPullRequestOption(contentVersion = Some(4L), title = Some("Retitled"))
          )
        val pullReviewRequestOptions = PullReviewRequestOptions(reviewers = Some(List("alice")))
        val createPullReviewRequests =
          GiteaRequests.createPullReviewRequests(config, "octo", "api", 2, pullReviewRequestOptions)
        val deletePullReviewRequests =
          GiteaRequests.deletePullReviewRequests(config, "octo", "api", 2, pullReviewRequestOptions)
        val pullReviews = GiteaRequests.repoPullReviews(config, "octo", "api", 2)
        val createPullReview = GiteaRequests.createPullReview(
          config,
          "octo",
          "api",
          2,
          CreatePullReviewOptions(body = Some("Pending notes"), event = Some(PullReviewState.Comment))
        )
        val pullReview = GiteaRequests.repoPullReview(config, "octo", "api", 2, 10)
        val submitPullReview = GiteaRequests.submitPullReview(
          config,
          "octo",
          "api",
          2,
          10,
          SubmitPullReviewOptions(body = Some("Looks good"), event = Some(PullReviewState.Approved))
        )
        val dismissPullReview =
          GiteaRequests.dismissPullReview(config, "octo", "api", 2, 10, DismissPullReviewOptions(Some("outdated")))
        val undismissPullReview = GiteaRequests.undismissPullReview(config, "octo", "api", 2, 10)
        val pullReviewComments = GiteaRequests.repoPullReviewComments(config, "octo", "api", 2, 10)
        val pullRequestDiff =
          GiteaRequests.repoPullRequestDiffOrPatch(config, "octo", "api", 2, PullRequestDiffType.Diff)
        val pullRequestFiles = GiteaRequests.repoPullRequestFiles(config, "octo", "api", 2)
        val pullRequestCommits = GiteaRequests.repoPullRequestCommits(config, "octo", "api", 2)

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
          pinnedPullRequests.decode(pinnedPullRequests.request.send(backend)).map(_.map(_.title)) ==
            Right(Chunk(Some("Pinned"))),
          pullRequest.decode(pullRequest.request.send(backend)).map(_.title) == Right(Some("Second")),
          createPullRequest.decode(createPullRequest.request.send(backend)).map(_.number) == Right(Some(3L)),
          createPullRequest.decode(createPullRequest.request.send(backend)).map(_.title) == Right(Some("Created")),
          editPullRequest.decode(editPullRequest.request.send(backend)).map(_.number) == Right(Some(2L)),
          editPullRequest.decode(editPullRequest.request.send(backend)).map(_.title) == Right(Some("Retitled")),
          createPullReviewRequests.decode(createPullReviewRequests.request.send(backend)).map(_.map(_.state)) ==
            Right(Chunk(Some(PullReviewState.RequestReview))),
          deletePullReviewRequests.decode(deletePullReviewRequests.request.send(backend)) == Right(()),
          pullReviews.decode(pullReviews.request.send(backend)).map(_.data.map(_.state)) ==
            Right(Chunk(Some(PullReviewState.Approved))),
          pullReviews.decode(pullReviews.request.send(backend)).map(_.data.headOption.flatMap(_.commentsCount)) ==
            Right(Some(2L)),
          createPullReview.decode(createPullReview.request.send(backend)).map(_.state) ==
            Right(Some(PullReviewState.Comment)),
          pullReview.decode(pullReview.request.send(backend)).map(_.body) == Right(Some("Looks good")),
          submitPullReview.decode(submitPullReview.request.send(backend)).map(_.state) ==
            Right(Some(PullReviewState.Approved)),
          dismissPullReview.decode(dismissPullReview.request.send(backend)).map(_.dismissed) == Right(Some(true)),
          undismissPullReview.decode(undismissPullReview.request.send(backend)).map(_.dismissed) == Right(Some(false)),
          pullReviewComments.decode(pullReviewComments.request.send(backend)).map(_.map(_.path)) ==
            Right(Chunk(Some("README.md"))),
          pullReviewComments.decode(pullReviewComments.request.send(backend)).map(_.head.position) ==
            Right(Some(7L)),
          pullRequestDiff.decode(pullRequestDiff.request.send(backend)) ==
            Right("diff --git a/README.md b/README.md"),
          pullRequestFiles.decode(pullRequestFiles.request.send(backend)).map(_.data.map(_.filename)) ==
            Right(Chunk(Some("src/Main.scala"))),
          pullRequestFiles.decode(pullRequestFiles.request.send(backend)).map(_.totalCount) == Right(Some(1L)),
          pullRequestCommits.decode(pullRequestCommits.request.send(backend)).map(_.data.map(_.sha)) ==
            Right(Chunk(Some("abc123"))),
          pullRequestCommits.decode(pullRequestCommits.request.send(backend)).map(_.data.head.commit.flatMap(_.message)) ==
            Right(Some("Implement commits"))
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
      test("maps documented commit status failures") {
        val badRequestBody = """{"message":"invalid ref"}"""
        val notFoundBody = """{"message":"missing commit"}"""
        val badRequestBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(badRequestBody, StatusCode.BadRequest))
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val combined = GiteaRequests.repoCombinedStatusByRef(config, "owner", "repo", "bad ref")
        val byRef = GiteaRequests.repoStatusesByRef(config, "owner", "repo", "missing")
        val bySha = GiteaRequests.repoStatuses(config, "owner", "repo", "missing")
        val create =
          GiteaRequests.createStatus(
            config,
            "owner",
            "repo",
            "missing",
            CreateStatusOption(state = Some(CommitStatusState.Error))
          )

        assertTrue(
          combined.decode(combined.request.send(badRequestBackend)) ==
            Left(GiteaError.BadRequest("invalid ref", badRequestBody)),
          byRef.decode(byRef.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing commit", notFoundBody)),
          bySha.decode(bySha.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing commit", notFoundBody)),
          create.decode(create.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing commit", notFoundBody))
        )
      },
      test("maps documented commit pull request not-found responses") {
        val body = """{"message":"missing commit pull request"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.repoCommitPullRequest(config, "owner", "repo", "missing-sha")

        assertTrue(
          built.decode(built.request.send(backend)) ==
            Left(GiteaError.NotFound("missing commit pull request", body))
        )
      },
      test("maps documented single commit 404 and 422 responses") {
        val notFoundBody = """{"message":"missing commit"}"""
        val validationBody = """{"message":"invalid commit ref"}"""
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val validationBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(validationBody, StatusCode.UnprocessableEntity)
          )
        val missing = GiteaRequests.repoSingleCommit(config, "owner", "repo", "missing-sha")
        val invalid = GiteaRequests.repoSingleCommit(config, "owner", "repo", "bad ref")

        assertTrue(
          missing.decode(missing.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing commit", notFoundBody)),
          invalid.decode(invalid.request.send(validationBackend)) ==
            Left(GiteaError.UnprocessableEntity("invalid commit ref", validationBody))
        )
      },
      test("maps documented commit diff or patch 404 responses") {
        val body = """{"message":"missing commit diff"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val built = GiteaRequests.repoCommitDiffOrPatch(config, "owner", "repo", "missing-sha", CommitDiffType.diff)

        assertTrue(
          built.decode(built.request.send(backend)) ==
            Left(GiteaError.NotFound("missing commit diff", body))
        )
      },
      test("maps documented commit note 404 and 422 responses") {
        val notFoundBody = """{"message":"missing commit note"}"""
        val validationBody = """{"message":"invalid commit note ref"}"""
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val validationBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(
            ResponseStub.adjust(validationBody, StatusCode.UnprocessableEntity)
          )
        val missing = GiteaRequests.repoCommitNote(config, "owner", "repo", "missing-sha")
        val invalid = GiteaRequests.repoCommitNote(config, "owner", "repo", "bad ref")

        assertTrue(
          missing.decode(missing.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing commit note", notFoundBody)),
          invalid.decode(invalid.request.send(validationBackend)) ==
            Left(GiteaError.UnprocessableEntity("invalid commit note ref", validationBody))
        )
      },
      test("maps documented Git tree 400 and 404 responses") {
        val badRequestBody = """{"message":"invalid tree ref"}"""
        val notFoundBody = """{"message":"missing tree"}"""
        val badRequestBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(badRequestBody, StatusCode.BadRequest))
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val invalid = GiteaRequests.gitTree(config, "owner", "repo", "bad ref")
        val missing = GiteaRequests.gitTree(config, "owner", "repo", "missing-sha")

        assertTrue(
          invalid.decode(invalid.request.send(badRequestBackend)) ==
            Left(GiteaError.BadRequest("invalid tree ref", badRequestBody)),
          missing.decode(missing.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing tree", notFoundBody))
        )
      },
      test("maps documented Git blob 400 and 404 responses") {
        val badRequestBody = """{"message":"invalid blob ref"}"""
        val notFoundBody = """{"message":"missing blob"}"""
        val badRequestBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(badRequestBody, StatusCode.BadRequest))
        val notFoundBackend =
          BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val invalid = GiteaRequests.gitBlob(config, "owner", "repo", "bad ref")
        val missing = GiteaRequests.gitBlob(config, "owner", "repo", "missing-sha")

        assertTrue(
          invalid.decode(invalid.request.send(badRequestBackend)) ==
            Left(GiteaError.BadRequest("invalid blob ref", badRequestBody)),
          missing.decode(missing.request.send(notFoundBackend)) ==
            Left(GiteaError.NotFound("missing blob", notFoundBody))
        )
      },
      test("maps repository pull request not-found responses") {
        val body = """{"message":"missing pull request"}"""
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val pullRequests = GiteaRequests.repoPullRequests(config, "owner", "missing")
        val pinnedPullRequests = GiteaRequests.pinnedPullRequests(config, "owner", "missing")
        val pullRequest = GiteaRequests.repoPullRequest(config, "owner", "missing", 77)
        val pullRequestDiff =
          GiteaRequests.repoPullRequestDiffOrPatch(config, "owner", "missing", 77, PullRequestDiffType.Diff)
        val pullRequestIsMerged = GiteaRequests.repoPullRequestIsMerged(config, "owner", "missing", 77)
        val pullReviewRequestOptions = PullReviewRequestOptions(reviewers = Some(List("alice")))
        val createPullReviewRequests =
          GiteaRequests.createPullReviewRequests(config, "owner", "missing", 77, pullReviewRequestOptions)
        val deletePullReviewRequests =
          GiteaRequests.deletePullReviewRequests(config, "owner", "missing", 77, pullReviewRequestOptions)
        val pullReviews = GiteaRequests.repoPullReviews(config, "owner", "missing", 77)
        val createPullReview = GiteaRequests.createPullReview(
          config,
          "owner",
          "missing",
          77,
          CreatePullReviewOptions(body = Some("notes"), event = Some(PullReviewState.Comment))
        )
        val pullReview = GiteaRequests.repoPullReview(config, "owner", "missing", 77, 10)
        val submitPullReview = GiteaRequests.submitPullReview(
          config,
          "owner",
          "missing",
          77,
          10,
          SubmitPullReviewOptions(body = Some("approve"), event = Some(PullReviewState.Approved))
        )
        val deletePullReview = GiteaRequests.deletePullReview(config, "owner", "missing", 77, 10)
        val dismissPullReview =
          GiteaRequests.dismissPullReview(config, "owner", "missing", 77, 10, DismissPullReviewOptions(Some("old")))
        val undismissPullReview = GiteaRequests.undismissPullReview(config, "owner", "missing", 77, 10)
        val pullReviewComments = GiteaRequests.repoPullReviewComments(config, "owner", "missing", 77, 10)
        val pullRequestFiles = GiteaRequests.repoPullRequestFiles(config, "owner", "missing", 77)
        val pullRequestCommits = GiteaRequests.repoPullRequestCommits(config, "owner", "missing", 77)

        assertTrue(
          pullRequests.decode(pullRequests.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pinnedPullRequests.decode(pinnedPullRequests.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullRequest.decode(pullRequest.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullRequestDiff.decode(pullRequestDiff.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullRequestIsMerged.decode(pullRequestIsMerged.request.send(backend)) == Right(false),
          createPullReviewRequests.decode(createPullReviewRequests.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          deletePullReviewRequests.decode(deletePullReviewRequests.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullReviews.decode(pullReviews.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          createPullReview.decode(createPullReview.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullReview.decode(pullReview.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          submitPullReview.decode(submitPullReview.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          deletePullReview.decode(deletePullReview.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          dismissPullReview.decode(dismissPullReview.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          undismissPullReview.decode(undismissPullReview.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullReviewComments.decode(pullReviewComments.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullRequestFiles.decode(pullRequestFiles.request.send(backend)) ==
            Left(GiteaError.NotFound("missing pull request", body)),
          pullRequestCommits.decode(pullRequestCommits.request.send(backend)) ==
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
