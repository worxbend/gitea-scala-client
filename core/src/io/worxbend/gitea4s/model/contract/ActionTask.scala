package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionTask(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("display_title") displayTitle: Option[String] = None,
    @jsonField("event") event: Option[String] = None,
    @jsonField("head_branch") headBranch: Option[String] = None,
    @jsonField("head_sha") headSha: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("run_number") runNumber: Option[Long] = None,
    @jsonField("run_started_at") runStartedAt: Option[java.time.Instant] = None,
    @jsonField("status") status: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("workflow_id") workflowId: Option[String] = None
)

object ActionTask:
  given JsonCodec[ActionTask] = DeriveJsonCodec.gen[ActionTask]
