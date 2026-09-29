package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.ActionsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, WorkflowAttemptJobsParams, WorkflowRunsParams}
import io.worxbend.gitea4s.model.{ActionWorkflowJobsResponse, ActionWorkflowRun, ActionWorkflowRunsResponse}
import zio.IO

private[gitea4s] final class SttpActionsApi(protected val config: GiteaConfig, protected val executor: GiteaRequestExecutor)
    extends ActionsApi with generated.LiveActionsOperations:
  override def downloadArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, zio.Chunk[Byte]] =
    executor.send(GiteaRequests.downloadArtifact(config, owner, repo, artifactId))
  override def workflowRuns(
      owner: String,
      repo: String,
      workflowId: String,
      params: WorkflowRunsParams
  ): IO[GiteaError, ActionWorkflowRunsResponse] =
    executor.send(GiteaRequests.actionsListWorkflowRuns(config, owner, repo, workflowId, params))

  override def runAttempt(owner: String, repo: String, run: Long, attempt: Long): IO[GiteaError, ActionWorkflowRun] =
    executor.send(GiteaRequests.getWorkflowRunAttempt(config, owner, repo, run, attempt))

  override def runAttemptJobs(
      owner: String,
      repo: String,
      run: Long,
      attempt: Long,
      params: WorkflowAttemptJobsParams
  ): IO[GiteaError, ActionWorkflowJobsResponse] =
    executor.send(GiteaRequests.listWorkflowRunAttemptJobs(config, owner, repo, run, attempt, params))
