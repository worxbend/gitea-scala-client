package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClientV1273, GiteaConfig}
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Auth, CreateBranchRepoOption, CreateForkOption, CreateRepoOption, EditRepoOption, TransferRepoOption}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.{Chunk, Task}
import zio.test.*

object RepositoryLifecycleSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private val createBody = CreateRepoOption("new-repo", autoInit = Some(true))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("repository lifecycle")(
      test("creates a current-user repository with a schema-typed body") {
        val built = GiteaRequests.createCurrentUserRepo(config, createBody)
        val request = built.request
        val created = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"new-repo"}""", StatusCode.Created)
        )
        val conflict = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"already exists"}""", StatusCode.Conflict)
        )
        val createdResult = built.decode(request.send(created))
        val conflictResult = built.decode(request.send(conflict))

        assertTrue(
          built.endpoint == GiteaEndpoints.createCurrentUserRepo,
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/user/repos",
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          stringBody(request) == """{"name":"new-repo","auto_init":true}""",
          !built.retryable,
          createdResult.map(_.name) == Right(Some("new-repo")),
          conflictResult.left.exists(_.isInstanceOf[GiteaError.Conflict])
        )
      },
      test("creates an organization repository through modern and legacy paths") {
        val modern = GiteaRequests.createOrgRepo(config, "org/name", createBody)
        val legacy = GiteaRequests.createOrgRepoDeprecated(config, "org/name", createBody)
        val modernRequest = modern.request
        val legacyRequest = legacy.request
        val created = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"new-repo"}""", StatusCode.Created)
        )
        val modernResult = modern.decode(modernRequest.send(created))
        val legacyResult = legacy.decode(legacyRequest.send(created))

        assertTrue(
          modernRequest.uri.toString == "https://gitea.example/root/api/v1/orgs/org%2Fname/repos",
          legacyRequest.uri.toString == "https://gitea.example/root/api/v1/org/org%2Fname/repos",
          modernRequest.method == Method.POST,
          legacyRequest.method == Method.POST,
          stringBody(modernRequest) == stringBody(legacyRequest),
          modernResult.map(_.name) == Right(Some("new-repo")),
          legacyResult.map(_.name) == Right(Some("new-repo"))
        )
      },
      test("patches repository settings without losing false and rejects an invalid status") {
        val built = GiteaRequests.editRepository(
          config, "a b", "repo/one",
          EditRepoOption(hasIssues = Some(false), mirrorToken = Some("write-only-secret"))
        )
        val request = built.request
        val ok = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"name":"repo/one"}"""))
        val wrongStatus = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"repo/one"}""", StatusCode.Created)
        )
        val successResult = built.decode(request.send(ok))
        val wrongResult = built.decode(request.send(wrongStatus))

        assertTrue(
          request.method == Method.PATCH,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/a%20b/repo%2Fone",
          stringBody(request) == """{"has_issues":false,"mirror_token":"write-only-secret"}""",
          !built.retryable,
          successResult.map(_.name) == Right(Some("repo/one")),
          wrongResult.isLeft
        )
      },
      test("deletes a repository only on the documented 204") {
        val built = GiteaRequests.deleteRepository(config, "a b", "repo/one")
        val request = built.request
        val noContent = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val login = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("<html>login</html>"))
        val missing = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NotFound))
        val successResult = built.decode(request.send(noContent))
        val loginResult = built.decode(request.send(login))
        val missingResult = built.decode(request.send(missing))

        assertTrue(
          request.method == Method.DELETE,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/a%20b/repo%2Fone",
          request.body == NoBody,
          !built.retryable,
          successResult == Right(()),
          loginResult.isLeft,
          missingResult.left.exists(_.isInstanceOf[GiteaError.NotFound])
        )
      },
      test("forks a repository using the organization option and 202 response") {
        val built = GiteaRequests.createFork(config, "owner", "repo", CreateForkOption(organization = Some("org/name")))
        val request = built.request
        val accepted = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"repo"}""", StatusCode.Accepted)
        )
        val conflict = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"exists"}""", StatusCode.Conflict)
        )
        val result = built.decode(request.send(accepted))
        val conflictResult = built.decode(request.send(conflict))

        assertTrue(
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/forks",
          stringBody(request) == """{"organization":"org/name"}""",
          result.map(_.name) == Right(Some("repo")),
          conflictResult.left.exists(_.isInstanceOf[GiteaError.Conflict])
        )
      },
      test("creates a branch from a ref and deletes an escaped branch name") {
        val created = GiteaRequests.createBranch(
          config, "owner", "repo", CreateBranchRepoOption("release/next", oldRefName = Some("refs/heads/main"))
        )
        val deleted = GiteaRequests.deleteBranch(config, "owner", "repo", "release/next")
        val createRequest = created.request
        val deleteRequest = deleted.request
        val createdBackend = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"release/next"}""", StatusCode.Created)
        )
        val deletedBackend = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("", StatusCode.NoContent)
        )
        val conflictBackend = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"branch exists"}""", StatusCode.Conflict)
        )
        val archivedBackend = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"archived"}""", StatusCode.Locked)
        )
        val createResult = created.decode(createRequest.send(createdBackend))
        val deleteResult = deleted.decode(deleteRequest.send(deletedBackend))
        val conflictResult = created.decode(createRequest.send(conflictBackend))
        val archivedResult = deleted.decode(deleteRequest.send(archivedBackend))

        assertTrue(
          createRequest.method == Method.POST,
          deleteRequest.method == Method.DELETE,
          createRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branches",
          deleteRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branches/release%2Fnext",
          stringBody(createRequest) == """{"new_branch_name":"release/next","old_ref_name":"refs/heads/main"}""",
          deleteRequest.body == NoBody,
          createResult.map(_.name) == Right(Some("release/next")),
          deleteResult == Right(()),
          conflictResult.left.exists(_.isInstanceOf[GiteaError.Conflict]),
          archivedResult.left.exists(_.isInstanceOf[GiteaError.Locked])
        )
      },
      test("requests ownership transfer with the team IDs and only accepts 202") {
        val built = GiteaRequests.transferRepository(
          config, "a b", "repo/one", TransferRepoOption("new-owner", Some(Chunk(3L, 5L)))
        )
        val request = built.request
        val accepted = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"repo/one"}""", StatusCode.Accepted)
        )
        val denied = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"forbidden"}""", StatusCode.Forbidden)
        )
        val successResult = built.decode(request.send(accepted))
        val deniedResult = built.decode(request.send(denied))

        assertTrue(
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/a%20b/repo%2Fone/transfer",
          stringBody(request) == """{"new_owner":"new-owner","team_ids":[3,5]}""",
          !built.retryable,
          successResult.map(_.name) == Right(Some("repo/one")),
          deniedResult.left.exists(_.isInstanceOf[GiteaError.Forbidden])
        )
      },
      test("accepts or rejects transfers using no-body POSTs and their distinct success statuses") {
        val accepted = GiteaRequests.acceptRepositoryTransfer(config, "owner", "repo")
        val rejected = GiteaRequests.rejectRepositoryTransfer(config, "owner", "repo")
        val acceptRequest = accepted.request
        val rejectRequest = rejected.request
        val pending = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"name":"repo"}""", StatusCode.Accepted)
        )
        val ok = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"name":"repo"}"""))
        val acceptedResult = accepted.decode(acceptRequest.send(pending))
        val rejectedResult = rejected.decode(rejectRequest.send(ok))
        val wrongStatus = rejected.decode(rejectRequest.send(pending))

        assertTrue(
          acceptRequest.method == Method.POST,
          rejectRequest.method == Method.POST,
          acceptRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/transfer/accept",
          rejectRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/transfer/reject",
          acceptRequest.body == NoBody,
          rejectRequest.body == NoBody,
          acceptRequest.header("Content-Type").isEmpty,
          rejectRequest.header("Content-Type").isEmpty,
          acceptedResult.map(_.name) == Right(Some("repo")),
          rejectedResult.map(_.name) == Right(Some("repo")),
          wrongStatus.isLeft
        )
      },
      test("versioned facade reaches repository and organization writes") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.uri.path.endsWith(List("user", "repos")))
          .thenRespond(ResponseStub.adjust("""{"name":"new-repo"}""", StatusCode.Created))
          .whenRequestMatches(_.uri.path.endsWith(List("orgs", "team", "repos")))
          .thenRespond(ResponseStub.adjust("""{"name":"new-repo"}""", StatusCode.Created))
        val client = GiteaClientV1273.fromBackend(config, backend)

        for
          own <- client.repos.createForCurrentUser(createBody)
          org <- client.orgs.createRepository("team", createBody)
        yield assertTrue(own.name.contains("new-repo"), org.name.contains("new-repo"))
      },
      test("versioned facade reaches transfer request, acceptance, and rejection") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.uri.path.endsWith(List("transfer", "accept")))
          .thenRespond(ResponseStub.adjust("""{"name":"repo"}""", StatusCode.Accepted))
          .whenRequestMatches(_.uri.path.endsWith(List("transfer", "reject")))
          .thenRespond(ResponseStub.adjust("""{"name":"repo"}"""))
          .whenRequestMatches(_.uri.path.endsWith(List("repo", "transfer")))
          .thenRespond(ResponseStub.adjust("""{"name":"repo"}""", StatusCode.Accepted))
        val client = GiteaClientV1273.fromBackend(config, backend)

        for
          requested <- client.repos.transfer("owner", "repo", TransferRepoOption("new-owner"))
          accepted <- client.repos.acceptTransfer("owner", "repo")
          rejected <- client.repos.rejectTransfer("owner", "repo")
        yield assertTrue(
          requested.name.contains("repo"),
          accepted.name.contains("repo"),
          rejected.name.contains("repo")
        )
      }
    )
