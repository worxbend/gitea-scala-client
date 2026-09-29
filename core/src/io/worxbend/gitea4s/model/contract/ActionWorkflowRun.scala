package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflowRun(
    @jsonField("actor") actor: Option[User] = None,
    @jsonField("completed_at") completedAt: Option[java.time.Instant] = None,
    @jsonField("conclusion") conclusion: Option[String] = None,
    @jsonField("display_title") displayTitle: Option[String] = None,
    @jsonField("event") event: Option[String] = None,
    @jsonField("head_branch") headBranch: Option[String] = None,
    @jsonField("head_repository") headRepository: Option[Repository] = None,
    @jsonField("head_sha") headSha: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("path") path: Option[String] = None,
    @jsonField("previous_attempt_url") previousAttemptUrl: Option[String] = None,
    @jsonField("pull_requests") pullRequests: Option[List[PullRequestMinimal]] = None,
    @jsonField("repository") repository: Option[Repository] = None,
    @jsonField("repository_id") repositoryId: Option[Long] = None,
    @jsonField("run_attempt") runAttempt: Option[Long] = None,
    @jsonField("run_number") runNumber: Option[Long] = None,
    @jsonField("started_at") startedAt: Option[java.time.Instant] = None,
    @jsonField("status") status: Option[String] = None,
    @jsonField("trigger_actor") triggerActor: Option[User] = None,
    @jsonField("url") url: Option[String] = None
)

object ActionWorkflowRun:
  given JsonCodec[ActionWorkflowRun] = DeriveJsonCodec.gen[ActionWorkflowRun]
