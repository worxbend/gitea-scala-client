package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Auth, IssueState}
import sttp.client4.*
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Header, Method, StatusCode}
import zio.Chunk
import zio.test.*

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
      test("decodes single issue and paginated user list responses") {
        val issueResponse = """{"id":1,"number":7,"state":"open","title":"First"}"""
        val userListResponse = """[{"id":2,"login":"alice"}]"""
        val backend =
          BackendStub.synchronous
            .whenRequestMatches(_.uri.path.endsWith(List("issues", "7")))
            .thenRespond(ResponseStub.adjust(issueResponse))
            .whenRequestMatches(_.uri.path.endsWith(List("followers")))
            .thenRespond(ResponseStub.adjust(userListResponse, StatusCode.Ok, List(Header("x-total-count", "1"))))
        val issue = GiteaRequests.issue(config, "owner", "repo", 7)
        val followers = GiteaRequests.userFollowers(config, "octo")

        assertTrue(
          issue.decode(issue.request.send(backend)).map(_.number) == Right(Some(7L)),
          followers.decode(followers.request.send(backend)).map(_.data.headOption.flatMap(_.login)) ==
            Right(Some("alice")),
          followers.decode(followers.request.send(backend)).map(_.hasNext) == Right(false)
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
