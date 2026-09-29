package io.worxbend.gitea4s.http

final case class WorkflowRunsParams(
    event: Option[String] = None,
    branch: Option[String] = None,
    status: Option[String] = None,
    actor: Option[String] = None,
    headSha: Option[String] = None,
    excludePullRequests: Option[Boolean] = None,
    scopedWorkflowSourceRepoId: Option[Long] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

final case class WorkflowAttemptJobsParams(
    status: Option[String] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)
