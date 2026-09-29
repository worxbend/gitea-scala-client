package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflowJob(
    @jsonField("completed_at") completedAt: Option[java.time.Instant] = None,
    @jsonField("conclusion") conclusion: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("head_branch") headBranch: Option[String] = None,
    @jsonField("head_sha") headSha: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("labels") labels: Option[List[String]] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("run_attempt") runAttempt: Option[Long] = None,
    @jsonField("run_id") runId: Option[Long] = None,
    @jsonField("run_url") runUrl: Option[String] = None,
    @jsonField("runner_id") runnerId: Option[Long] = None,
    @jsonField("runner_name") runnerName: Option[String] = None,
    @jsonField("started_at") startedAt: Option[java.time.Instant] = None,
    @jsonField("status") status: Option[String] = None,
    @jsonField("steps") steps: Option[List[ActionWorkflowStep]] = None,
    @jsonField("url") url: Option[String] = None
)

object ActionWorkflowJob:
  given JsonCodec[ActionWorkflowJob] = DeriveJsonCodec.gen[ActionWorkflowJob]
