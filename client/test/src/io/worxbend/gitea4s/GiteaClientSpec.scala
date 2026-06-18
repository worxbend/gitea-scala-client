package io.worxbend.gitea4s

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  GiteaRequests,
  IssueListParams,
  NotificationListParams,
  PullRequestListParams,
  RepoListParams,
  UserSearchParams
}
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.{Auth, CreateIssue, EditIssue, LockIssueOption}
import sttp.capabilities.Effect
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Header, Method, RequestMetadata, StatusCode}
import sttp.monad.MonadError
import zio.{Chunk, Ref, Task, ZIO}
import zio.test.*

import java.time.{Duration, Instant}

object GiteaClientSpec extends ZIOSpecDefault:
  private val config =
    GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret")).copy(pageSize = 1)

  private def taskStub =
    BackendStub[Task](new RIOMonadAsyncError[Any])

  private def stringResponse(
      body: String,
      status: StatusCode = StatusCode.Ok,
      headers: List[Header] = Nil
  ): Response[String] =
    Response(
      body = body,
      code = status,
      statusText = "",
      headers = headers,
      history = Nil,
      request = RequestMetadata(Method.GET, uri"https://gitea.example/api/v1/user", Nil)
    )

  private final class ScriptedBackend(responses: Ref[List[Task[Response[String]]]]) extends Backend[Task]:
    private val taskMonad = new RIOMonadAsyncError[Any]

    override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
      responses.modify {
        case next :: rest =>
          (next.map(_.asInstanceOf[Response[T]]), rest)
        case Nil =>
          (ZIO.fail(IllegalStateException("scripted backend has no remaining responses")), Nil)
      }.flatten

    override def close(): Task[Unit] =
      ZIO.unit

    override def monad: MonadError[Task] =
      taskMonad

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
      test("creates an issue through the IssuesApi create method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.POST &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""title":"Created"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":18,"number":8,"title":"Created"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.create("owner", "repo", CreateIssue(title = "Created")).map(_.number))(
          Assertion.equalTo(Some(8L))
        )
      },
      test("edits an issue through the IssuesApi edit method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.PATCH &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""title":"Retitle"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":18,"number":8,"title":"Retitle"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.edit("owner", "repo", 8, EditIssue(title = Some("Retitle"))).map(_.title))(
          Assertion.equalTo(Some("Retitle"))
        )
      },
      test("closes an issue through the IssuesApi close helper") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.PATCH &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""state":"closed"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":18,"number":8,"state":"closed"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.close("owner", "repo", 8).map(_.state.map(_.jsonValue)))(
          Assertion.equalTo(Some("closed"))
        )
      },
      test("adds an issue comment through the IssuesApi comment method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.POST &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "comments")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""body":"Looks good"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":30,"body":"Looks good"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.comment("owner", "repo", 8, "Looks good").map(comment => comment.id -> comment.body))(
          Assertion.equalTo(Some(30L) -> Some("Looks good"))
        )
      },
      test("manages issue labels through the IssuesApi label methods") {
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels"))
            }
            .thenRespond(ResponseStub.adjust("""[{"id":1,"name":"kind/api"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.PUT &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""labels":[1,2]""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""[{"id":1,"name":"kind/api"},{"id":2,"name":"status/ready"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""labels":[3]""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""[{"id":3,"name":"priority/high"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels", "3"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          existing <- client.labels("owner", "repo", 8)
          replaced <- client.replaceLabels("owner", "repo", 8, Chunk(1L, 2L))
          added <- client.addLabels("owner", "repo", 8, Chunk(3L))
          removed <- client.removeLabel("owner", "repo", 8, 3).either
          cleared <- client.clearLabels("owner", "repo", 8).either
        yield assertTrue(
          existing.map(_.name) == Chunk(Some("kind/api")),
          replaced.map(_.id) == Chunk(Some(1L), Some(2L)),
          added.map(_.name) == Chunk(Some("priority/high")),
          removed == Right(()),
          cleared == Right(())
        )
      },
      test("locks and unlocks an issue through the IssuesApi lock methods") {
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.PUT &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "lock")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""lock_reason":"resolved"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "lock"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          locked <- client.lock("owner", "repo", 8, LockIssueOption(lockReason = Some("resolved"))).either
          unlocked <- client.unlock("owner", "repo", 8).either
        yield assertTrue(
          locked == Right(()),
          unlocked == Right(())
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
      test("retries transport failures for read-only requests") {
        val failure = RuntimeException("connection reset")

        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.fail(failure),
              ZIO.succeed(stringResponse("""{"id":42,"login":"retry"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.me.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          user <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          user.login.contains("retry"),
          remaining.isEmpty
        )
      },
      test("retries selected 5xx responses for read-only requests") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(stringResponse("""{"id":43,"login":"server-retry"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.me.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          user <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          user.login.contains("server-retry"),
          remaining.isEmpty
        )
      },
      test("uses rate-limit reset headers for retry delay") {
        val now = Instant.parse("2030-01-01T00:00:00Z")
        val resetAt = now.plusSeconds(2)

        for
          _ <- TestClock.setTime(now)
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"rate limited"}""",
                  StatusCode.TooManyRequests,
                  List(Header("x-ratelimit-reset", resetAt.getEpochSecond.toString))
                )
              ),
              ZIO.succeed(stringResponse("""{"id":44,"login":"limited"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.me.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          early <- fiber.poll
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          user <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          early.isEmpty,
          user.login.contains("limited"),
          remaining.isEmpty
        )
      },
      test("does not retry requests marked non-retryable") {
        val failure = RuntimeException("connection reset")

        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.fail(failure),
              ZIO.succeed(stringResponse("""{"id":45,"login":"unused"}"""))
            )
          )
          executor = GiteaRequestExecutor(ScriptedBackend(responses), maxRetries = 2)
          result <- executor.send(GiteaRequests.currentUser(config).copy(retryable = false)).either
          remaining <- responses.get
        yield assertTrue(
          result.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          remaining.size == 1
        )
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
      test("loads and streams repository releases") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "releases")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"tag_name":"v1.0.0"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":2,"tag_name":"v1.1.0"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "releases", "2")))
            .thenRespond(ResponseStub.adjust("""{"id":2,"tag_name":"v1.1.0","name":"Second"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          releases <- client.releases("alice", "api").runCollect
          release <- client.release("alice", "api", 2)
        yield assertTrue(
          releases.map(_.tagName) == Chunk(Some("v1.0.0"), Some("v1.1.0")),
          release.id.contains(2L),
          release.name.contains("Second")
        )
      },
      test("loads and streams repository pull requests") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls")))
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"id":1,"number":1,"title":"First","state":"open"}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"id":2,"number":2,"title":"Second","state":"closed"}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2")))
            .thenRespond(ResponseStub.adjust("""{"id":2,"number":2,"title":"Second","state":"closed"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          pullRequests <- client.pullRequests("alice", "api", PullRequestListParams.default).runCollect
          pullRequest <- client.pullRequest("alice", "api", 2)
        yield assertTrue(
          pullRequests.map(_.number) == Chunk(Some(1L), Some(2L)),
          pullRequest.id.contains(2L),
          pullRequest.title.contains("Second")
        )
      },
      test("loads notification count, streams notification threads, and fetches a single thread") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("notifications")))
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"id":1,"unread":true,"subject":{"title":"First","state":"open","type":"Issue"}}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"id":2,"unread":false,"subject":{"title":"Second","state":"closed","type":"Pull"}}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
            .whenRequestMatches(_.uri.path.endsWith(List("notifications", "new")))
            .thenRespond(ResponseStub.adjust("""{"new":5}"""))
            .whenRequestMatches(_.uri.path.endsWith(List("notifications", "threads", "2")))
            .thenRespond(
              ResponseStub.adjust("""{"id":2,"unread":false,"subject":{"title":"Second","type":"Pull"}}""")
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          threads <- client.notificationThreads(NotificationListParams.default).runCollect
          count <- client.unreadNotificationCount
          thread <- client.notificationThread("2")
        yield assertTrue(
          threads.map(_.id) == Chunk(Some(1L), Some(2L)),
          count.unread.contains(5L),
          thread.subject.flatMap(_.title).contains("Second")
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
