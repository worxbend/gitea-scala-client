package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object ActionsSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  def spec =
    suite("v1.27.3 workflow runs and attempts")(
      test("encodes all workflow-run filters and decodes nested pull request metadata") {
        val params = WorkflowRunsParams(
          event = Some("push"), branch = Some("release/one"), status = Some("success"),
          actor = Some("alice"), headSha = Some("abc"), excludePullRequests = Some(true),
          scopedWorkflowSourceRepoId = Some(42L), page = Some(2), limit = Some(10)
        )
        val built = GiteaRequests.actionsListWorkflowRuns(config, "a b", "repo/one", "ci.yml", params)
        val request = built.request
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(
          """{"total_count":3,"workflow_runs":[{"id":17,"run_attempt":2,"previous_attempt_url":"/attempts/1","pull_requests":[{"number":4,"head":{"ref":"feature","repo":{"id":42}}}]}]}"""
        ))
        val result = built.decode(request.send(backend))

        assertTrue(
          request.method == Method.GET,
          request.uri.toString.contains("/repos/a%20b/repo%2Fone/actions/workflows/ci.yml/runs?"),
          request.uri.paramsSeq == Seq(
            "event" -> "push", "branch" -> "release/one", "status" -> "success", "actor" -> "alice",
            "head_sha" -> "abc", "exclude_pull_requests" -> "true", "scoped_workflow_source_repo_id" -> "42",
            "page" -> "2", "limit" -> "10"
          ),
          request.header("Authorization").contains("token secret"),
          built.retryable,
          result.map(_.totalCount) == Right(Some(3L)),
          result.toOption.flatMap(_.workflowRuns).flatMap(_.headOption).flatMap(_.pullRequests)
            .flatMap(_.headOption).flatMap(_.head).flatMap(_.repo).flatMap(_.id) == Some(42L)
        )
      },
      test("fetches a specific attempt and preserves run metadata") {
        val built = GiteaRequests.getWorkflowRunAttempt(config, "owner", "repo", 17, 2)
        val request = built.request
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"id":17,"run_attempt":2,"status":"completed","head_repository":{"name":"repo"}}""")
        )
        val result = built.decode(request.send(backend))

        assertTrue(
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/actions/runs/17/attempts/2",
          result.map(_.runAttempt) == Right(Some(2L)),
          result.toOption.flatMap(_.headRepository).flatMap(_.name) == Some("repo")
        )
      },
      test("lists attempt jobs with pagination filters and nested steps") {
        val built = GiteaRequests.listWorkflowRunAttemptJobs(
          config, "owner", "repo", 17, 2, WorkflowAttemptJobsParams(Some("completed"), Some(3), Some(5))
        )
        val request = built.request
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(
          """{"total_count":12,"jobs":[{"id":7,"run_attempt":2,"labels":["linux"],"steps":[{"name":"compile","number":1}]}]}"""
        ))
        val result = built.decode(request.send(backend))

        assertTrue(
          request.uri.toString.contains("/actions/runs/17/attempts/2/jobs?"),
          request.uri.paramsSeq == Seq("status" -> "completed", "page" -> "3", "limit" -> "5"),
          result.map(_.totalCount) == Right(Some(12L)),
          result.toOption.flatMap(_.jobs).flatMap(_.headOption).flatMap(_.steps)
            .flatMap(_.headOption).flatMap(_.name) == Some("compile")
        )
      },
      test("exposes the workflow attempt through the versioned facade and propagates 404") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        client.actions.runAttempt("owner", "repo", 17, 2).either.map { result =>
          assertTrue(result.isLeft)
        }
      }
    )
