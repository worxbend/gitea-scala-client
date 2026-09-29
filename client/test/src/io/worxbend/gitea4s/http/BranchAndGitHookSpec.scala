package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Auth, EditGitHookOption, RenameBranchRepoOption, UpdateBranchRepoOption}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object BranchAndGitHookSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  private def respond(body: String, code: StatusCode = StatusCode.Ok) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, code))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("branch and Git hook operations")(
      test("gets one escaped branch and maps not found") {
        val built = GiteaRequests.getBranch(config, "org team", "repo", "release/next")
        val request = built.request
        val found = built.decode(request.send(respond("""{"name":"release/next"}""")))
        val missing = built.decode(request.send(respond("", StatusCode.NotFound)))

        assertTrue(
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/org%20team/repo/branches/release%2Fnext",
          request.header("Authorization").contains("token secret"),
          built.retryable,
          found.map(_.name) == Right(Some("release/next")),
          missing.left.exists(_.isInstanceOf[GiteaError.NotFound])
        )
      },
      test("updates a branch with a typed commit precondition and requires 204") {
        val built = GiteaRequests.updateBranch(
          config, "owner", "repo", "main", UpdateBranchRepoOption("newsha", Some("oldsha"), Some(false))
        )
        val request = built.request
        val updated = built.decode(request.send(respond("", StatusCode.NoContent)))
        val login = built.decode(request.send(respond("<html>login</html>")))
        val conflict = built.decode(request.send(respond("""{"message":"conflict"}""", StatusCode.Conflict)))

        assertTrue(
          request.method == Method.PUT,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branches/main",
          request.header("Content-Type").exists(_.startsWith("application/json")),
          stringBody(request) == """{"new_commit_id":"newsha","old_commit_id":"oldsha","force":false}""",
          !built.retryable,
          updated == Right(()),
          login.isLeft,
          conflict.left.exists(_.isInstanceOf[GiteaError.Conflict])
        )
      },
      test("renames a branch with a JSON body and requires 204") {
        val built = GiteaRequests.renameBranch(config, "owner", "repo", "feature/old", RenameBranchRepoOption("feature/new"))
        val request = built.request
        val renamed = built.decode(request.send(respond("", StatusCode.NoContent)))
        val missing = built.decode(request.send(respond("", StatusCode.NotFound)))

        assertTrue(
          request.method == Method.PATCH,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branches/feature%2Fold",
          stringBody(request) == """{"name":"feature/new"}""",
          !built.retryable,
          renamed == Right(()),
          missing.left.exists(_.isInstanceOf[GiteaError.NotFound])
        )
      },
      test("lists and gets Git hooks with typed responses") {
        val hooks = GiteaRequests.listGitHooks(config, "owner", "repo")
        val hook = GiteaRequests.getGitHook(config, "owner", "repo", "pre/receive")
        val hooksRequest = hooks.request
        val hookRequest = hook.request
        val listed = hooks.decode(hooksRequest.send(respond("""[{"name":"pre-receive","is_active":false}]""")))
        val found = hook.decode(hookRequest.send(respond("""{"name":"pre-receive","content":"script"}""")))
        val missing = hook.decode(hookRequest.send(respond("", StatusCode.NotFound)))

        assertTrue(
          hooksRequest.method == Method.GET,
          hooksRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/hooks/git",
          hookRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/hooks/git/pre%2Freceive",
          listed.map(_.head.isActive) == Right(Some(false)),
          found.map(_.content) == Right(Some("script")),
          missing.left.exists(_.isInstanceOf[GiteaError.NotFound])
        )
      },
      test("edits and deletes Git hooks with the documented statuses") {
        val edit = GiteaRequests.editGitHook(config, "owner", "repo", "pre-receive", EditGitHookOption(Some("#!/bin/sh")))
        val delete = GiteaRequests.deleteGitHook(config, "owner", "repo", "pre-receive")
        val editRequest = edit.request
        val deleteRequest = delete.request
        val edited = edit.decode(editRequest.send(respond("""{"content":"#!/bin/sh","is_active":true}""")))
        val wrongStatus = edit.decode(editRequest.send(respond("""{"content":"script"}""", StatusCode.Created)))
        val deleted = delete.decode(deleteRequest.send(respond("", StatusCode.NoContent)))
        val login = delete.decode(deleteRequest.send(respond("<html>login</html>")))

        assertTrue(
          editRequest.method == Method.PATCH,
          editRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/hooks/git/pre-receive",
          stringBody(editRequest) == """{"content":"#!/bin/sh"}""",
          edited.map(_.isActive) == Right(Some(true)),
          wrongStatus.isLeft,
          deleteRequest.method == Method.DELETE,
          deleteRequest.body == NoBody,
          !delete.retryable,
          deleted == Right(()),
          login.isLeft
        )
      },
      test("versioned facade reaches the branch and hook methods") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.uri.path.endsWith(List("branches", "main")))
          .thenRespond(ResponseStub.adjust("""{"name":"main"}"""))
          .whenRequestMatches(_.uri.path.endsWith(List("hooks", "git")))
          .thenRespond(ResponseStub.adjust("""[{"name":"pre-receive"}]"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          branch <- client.repos.getBranch("owner", "repo", "main")
          hooks <- client.repos.gitHooks("owner", "repo")
        yield assertTrue(branch.name.contains("main"), hooks.head.name.contains("pre-receive"))
      }
    )
