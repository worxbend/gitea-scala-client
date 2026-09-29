package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{WorkflowAttemptJobsParams, WorkflowRunsParams}
import io.worxbend.gitea4s.model.{ActionWorkflowJobsResponse, ActionWorkflowRun, ActionWorkflowRunsResponse}
import zio.IO

/** Action workflow operations; list responses expose their total count and one requested page. */
trait ActionsApi extends io.worxbend.gitea4s.api.generated.ActionsOperations:
  def downloadArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, zio.Chunk[Byte]]
  def workflowRuns(
      owner: String,
      repo: String,
      workflowId: String,
      params: WorkflowRunsParams = WorkflowRunsParams()
  ): IO[GiteaError, ActionWorkflowRunsResponse]

  def runAttempt(owner: String, repo: String, run: Long, attempt: Long): IO[GiteaError, ActionWorkflowRun]

  def runAttemptJobs(
      owner: String,
      repo: String,
      run: Long,
      attempt: Long,
      params: WorkflowAttemptJobsParams = WorkflowAttemptJobsParams()
  ): IO[GiteaError, ActionWorkflowJobsResponse]
