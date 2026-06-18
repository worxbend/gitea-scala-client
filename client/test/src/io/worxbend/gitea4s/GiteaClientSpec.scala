package io.worxbend.gitea4s

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.IssueListParams
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
      }
    )
