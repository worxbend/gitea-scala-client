package io.worxbend.gitea4s.model

import java.time.Instant
import zio.Chunk
import zio.json.*

final case class PullRequestMinimalHeadRepo(
    id: Option[Long] = None,
    name: Option[String] = None,
    url: Option[String] = None
)
object PullRequestMinimalHeadRepo:
  given JsonCodec[PullRequestMinimalHeadRepo] = DeriveJsonCodec.gen[PullRequestMinimalHeadRepo]

final case class PullRequestMinimalHead(
    ref: Option[String] = None,
    repo: Option[PullRequestMinimalHeadRepo] = None,
    sha: Option[String] = None
)
object PullRequestMinimalHead:
  given JsonCodec[PullRequestMinimalHead] = DeriveJsonCodec.gen[PullRequestMinimalHead]

final case class PullRequestMinimal(
    base: Option[PullRequestMinimalHead] = None,
    head: Option[PullRequestMinimalHead] = None,
    id: Option[Long] = None,
    number: Option[Long] = None,
    url: Option[String] = None
)
object PullRequestMinimal:
  given JsonCodec[PullRequestMinimal] = DeriveJsonCodec.gen[PullRequestMinimal]

final case class ActionWorkflowRun(
    actor: Option[User] = None,
    @jsonField("completed_at") completedAt: Option[Instant] = None,
    conclusion: Option[String] = None,
    @jsonField("display_title") displayTitle: Option[String] = None,
    event: Option[String] = None,
    @jsonField("head_branch") headBranch: Option[String] = None,
    @jsonField("head_repository") headRepository: Option[Repository] = None,
    @jsonField("head_sha") headSha: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    id: Option[Long] = None,
    path: Option[String] = None,
    @jsonField("previous_attempt_url") previousAttemptUrl: Option[String] = None,
    @jsonField("pull_requests") pullRequests: Option[Chunk[PullRequestMinimal]] = None,
    repository: Option[Repository] = None,
    @jsonField("repository_id") repositoryId: Option[Long] = None,
    @jsonField("run_attempt") runAttempt: Option[Long] = None,
    @jsonField("run_number") runNumber: Option[Long] = None,
    @jsonField("started_at") startedAt: Option[Instant] = None,
    status: Option[String] = None,
    @jsonField("trigger_actor") triggerActor: Option[User] = None,
    url: Option[String] = None
)
object ActionWorkflowRun:
  given JsonCodec[ActionWorkflowRun] = DeriveJsonCodec.gen[ActionWorkflowRun]

final case class ActionWorkflowStep(
    @jsonField("completed_at") completedAt: Option[Instant] = None,
    conclusion: Option[String] = None,
    name: Option[String] = None,
    number: Option[Long] = None,
    @jsonField("started_at") startedAt: Option[Instant] = None,
    status: Option[String] = None
)
object ActionWorkflowStep:
  given JsonCodec[ActionWorkflowStep] = DeriveJsonCodec.gen[ActionWorkflowStep]

final case class ActionWorkflowJob(
    @jsonField("completed_at") completedAt: Option[Instant] = None,
    conclusion: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("head_branch") headBranch: Option[String] = None,
    @jsonField("head_sha") headSha: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    id: Option[Long] = None,
    labels: Option[Chunk[String]] = None,
    name: Option[String] = None,
    @jsonField("run_attempt") runAttempt: Option[Long] = None,
    @jsonField("run_id") runId: Option[Long] = None,
    @jsonField("run_url") runUrl: Option[String] = None,
    @jsonField("runner_id") runnerId: Option[Long] = None,
    @jsonField("runner_name") runnerName: Option[String] = None,
    @jsonField("started_at") startedAt: Option[Instant] = None,
    status: Option[String] = None,
    steps: Option[Chunk[ActionWorkflowStep]] = None,
    url: Option[String] = None
)
object ActionWorkflowJob:
  given JsonCodec[ActionWorkflowJob] = DeriveJsonCodec.gen[ActionWorkflowJob]

final case class ActionWorkflowRunsResponse(
    @jsonField("total_count") totalCount: Option[Long] = None,
    @jsonField("workflow_runs") workflowRuns: Option[Chunk[ActionWorkflowRun]] = None
)
object ActionWorkflowRunsResponse:
  given JsonCodec[ActionWorkflowRunsResponse] = DeriveJsonCodec.gen[ActionWorkflowRunsResponse]

final case class ActionWorkflowJobsResponse(
    jobs: Option[Chunk[ActionWorkflowJob]] = None,
    @jsonField("total_count") totalCount: Option[Long] = None
)
object ActionWorkflowJobsResponse:
  given JsonCodec[ActionWorkflowJobsResponse] = DeriveJsonCodec.gen[ActionWorkflowJobsResponse]
