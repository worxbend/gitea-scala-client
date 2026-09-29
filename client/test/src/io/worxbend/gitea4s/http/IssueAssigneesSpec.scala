package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.model.{Auth, IssueAssigneesOption}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.{Chunk, Task}
import zio.test.*

object IssueAssigneesSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private val assignees = IssueAssigneesOption(Chunk("alice", "bob"))

  def spec =
    suite("issue assignees")(
      test("posts and deletes the same typed JSON body") {
        val added = GiteaRequests.issueAddAssignees(config, "a b", "repo/one", 12, assignees)
        val removed = GiteaRequests.issueRemoveAssignees(config, "a b", "repo/one", 12, assignees)
        val addRequest = added.request
        val removeRequest = removed.request
        val addBody = addRequest.body match
          case StringBody(value, _, _) => value
          case other => other.toString
        val removeBody = removeRequest.body match
          case StringBody(value, _, _) => value
          case other => other.toString
        val success = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"number":12,"assignees":[{"login":"alice"}]}""", StatusCode.Created)
        )
        val removedResponse = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"number":12,"assignees":[]}""", StatusCode.Ok)
        )
        val addedResult = added.decode(addRequest.send(success))
        val removedResult = removed.decode(removeRequest.send(removedResponse))

        assertTrue(
          addRequest.method == Method.POST,
          removeRequest.method == Method.DELETE,
          addRequest.uri.toString == "https://gitea.example/root/api/v1/repos/a%20b/repo%2Fone/issues/12/assignees",
          removeRequest.uri.toString == addRequest.uri.toString,
          addRequest.header("Content-Type").exists(_.startsWith("application/json")),
          removeRequest.header("Authorization").contains("token secret"),
          addBody == """{"assignees":["alice","bob"]}""",
          removeBody == addBody,
          !added.retryable,
          !removed.retryable,
          addedResult.map(_.number) == Right(Some(12L)),
          removedResult.map(_.assignees) == Right(Some(Nil))
        )
      },
      test("only 204 confirms issue assignee membership") {
        val built = GiteaRequests.issueCheckAssignee(config, "owner", "repo", 12, "a/b")
        val request = built.request
        val found = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val missing = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NotFound))
        val unexpected = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("<html>login</html>"))
        val foundResult = built.decode(request.send(found))
        val missingResult = built.decode(request.send(missing))
        val unexpectedResult = built.decode(request.send(unexpected))

        assertTrue(
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/issues/12/assignees/a%2Fb",
          built.retryable,
          foundResult == Right(true),
          missingResult == Right(false),
          unexpectedResult.isLeft
        )
      },
      test("checks repository assignee membership with escaped path and exact statuses") {
        val built = GiteaRequests.repoCheckAssignee(config, "owner", "repo", "a/b")
        val request = built.request
        val found = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val missing = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NotFound))
        val unexpected = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("<html>login</html>"))
        val foundResult = built.decode(request.send(found))
        val missingResult = built.decode(request.send(missing))
        val unexpectedResult = built.decode(request.send(unexpected))

        assertTrue(
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/assignees/a%2Fb",
          foundResult == Right(true),
          missingResult == Right(false),
          unexpectedResult.isLeft
        )
      },
      test("the versioned facade reaches the new assignee write") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.method == Method.POST)
          .thenRespond(ResponseStub.adjust("""{"number":12}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        client.issues.addAssignees("owner", "repo", 12, Chunk("alice")).map { issue =>
          assertTrue(issue.number.contains(12L))
        }
      }
    )
