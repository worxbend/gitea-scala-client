package io.worxbend.gitea4s

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{IssueListParams, RepoListParams, UserSearchParams}
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Header, StatusCode}
import zio.{Chunk, Task}
import zio.test.*

object GiteaClientSpec extends ZIOSpecDefault:
  private val config =
    GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret")).copy(pageSize = 1)

  private def taskStub =
    BackendStub[Task](new RIOMonadAsyncError[Any])

  def spec =
    suite("GiteaClient")(
      test("loads the current user through the UsersApi") {
        val backend =
          taskStub.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"id":42,"login":"octo"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.me.map(user => user.id -> user.login))(
          Assertion.equalTo(Some(42L) -> Some("octo"))
        )
      },
      test("loads a user and repository through overloaded get methods") {
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "alice")))
            .thenRespond(ResponseStub.adjust("""{"id":7,"login":"alice"}"""))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "project")))
            .thenRespond(ResponseStub.adjust("""{"id":11,"name":"project"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          user <- client.get("alice")
          repo <- client.get("alice", "project")
        yield assertTrue(
          user.login.contains("alice"),
          repo.name.contains("project")
        )
      },
      test("loads an issue through the IssuesApi get method") {
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "owner", "repo", "issues", "7")))
            .thenRespond(ResponseStub.adjust("""{"id":17,"number":7,"title":"tracked"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.get("owner", "repo", 7).map(issue => issue.id -> issue.number -> issue.title))(
          Assertion.equalTo(Some(17L) -> Some(7L) -> Some("tracked"))
        )
      },
      test("loads an organization through the OrgsApi facade") {
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform")))
            .thenRespond(ResponseStub.adjust("""{"id":9,"name":"platform","full_name":"Platform Team"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.orgs.get("platform").map(org => org.id -> org.name -> org.fullName))(
          Assertion.equalTo(Some(9L) -> Some("platform") -> Some("Platform Team"))
        )
      },
      test("streams organization members across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "members")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"alice"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":2,"login":"bob"}]""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.orgs.members("platform").runCollect.map { users =>
          assertTrue(users.map(_.login) == Chunk(Some("alice"), Some("bob")))
        }
      },
      test("streams public organization members across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "public_members")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"alice"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":2,"login":"bob"}]""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.orgs.publicMembers("platform").runCollect.map { users =>
          assertTrue(users.map(_.login) == Chunk(Some("alice"), Some("bob")))
        }
      },
      test("streams organization repositories across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "repos")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":10,"name":"api"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":11,"name":"client"}]""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.orgs.repos("platform").runCollect.map { repos =>
          assertTrue(repos.map(_.name) == Chunk(Some("api"), Some("client")))
        }
      },
      test("returns decode failures as GiteaError") {
        val backend =
          taskStub.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"id":"not-a-number"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.me.either)(
          Assertion.isLeft(Assertion.isSubtype[GiteaError.DecodeError](Assertion.anything))
        )
      },
      test("maps backend failures to transport errors") {
        val failure = RuntimeException("connection refused")
        val backend = taskStub.whenAnyRequest.thenThrow(failure)
        val client = GiteaClient.fromBackend(config, backend)

        client.me.either.map { result =>
          assertTrue(
            result.left.exists {
              case GiteaError.TransportError(cause) => cause eq failure
              case _ => false
            }
          )
        }
      },
      test("streams all issues from multiple pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenAnyRequest.thenRespondCyclic(
            ResponseStub.adjust("""[{"id":1,"number":1,"title":"first"}]""", StatusCode.Ok, headers),
            ResponseStub.adjust("""[{"id":2,"number":2,"title":"second"}]""", StatusCode.Ok, headers)
          )
        val client = GiteaClient.fromBackend(config, backend)

        client.list("owner", "repo", IssueListParams.default).runCollect.map { issues =>
          assertTrue(issues.map(_.number) == Chunk(Some(1L), Some(2L)))
        }
      },
      test("streams user repositories and collects repository topics across pages") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "alice", "repos")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":10,"name":"api"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":11,"name":"client"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "topics")))
            .thenRespondCyclic(
              ResponseStub.adjust("""{"topics":["scala"]}""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""{"topics":["zio"]}""", StatusCode.Ok, twoPageHeaders)
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          repos <- client.list("alice", RepoListParams.default).runCollect
          topics <- client.topics("alice", "api")
        yield assertTrue(
          repos.map(_.name) == Chunk(Some("api"), Some("client")),
          topics == Chunk("scala", "zio")
        )
      },
      test("streams repository branches and tags across pages") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "branches")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"name":"main"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"name":"release"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "tags")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"name":"v1.0.0"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"name":"v1.1.0"}]""", StatusCode.Ok, twoPageHeaders)
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          branches <- client.branches("alice", "api").runCollect
          tags <- client.tags("alice", "api").runCollect
        yield assertTrue(
          branches.map(_.name) == Chunk(Some("main"), Some("release")),
          tags.map(_.name) == Chunk(Some("v1.0.0"), Some("v1.1.0"))
        )
      },
      test("streams followers and following users across paginated endpoints") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val onePageHeaders = List(Header("x-total-count", "1"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "alice", "followers")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"bob"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":2,"login":"carol"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("users", "alice", "following")))
            .thenRespond(ResponseStub.adjust("""[{"id":3,"login":"dave"}]""", StatusCode.Ok, onePageHeaders))
        val client = GiteaClient.fromBackend(config, backend)

        for
          followers <- client.followers("alice").runCollect
          following <- client.following("alice").runCollect
        yield assertTrue(
          followers.map(_.login) == Chunk(Some("bob"), Some("carol")),
          following.map(_.login) == Chunk(Some("dave"))
        )
      },
      test("streams user search results across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "search")))
            .thenRespondCyclic(
              ResponseStub.adjust("""{"ok":true,"data":[{"id":1,"login":"alice"}]}""", StatusCode.Ok, headers),
              ResponseStub.adjust("""{"ok":true,"data":[{"id":2,"login":"alicia"}]}""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.search(UserSearchParams(q = Some("ali"))).runCollect.map { users =>
          assertTrue(users.map(_.login) == Chunk(Some("alice"), Some("alicia")))
        }
      }
    )
