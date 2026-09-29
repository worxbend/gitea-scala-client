package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflowStep(
    @jsonField("completed_at") completedAt: Option[java.time.Instant] = None,
    @jsonField("conclusion") conclusion: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("number") number: Option[Long] = None,
    @jsonField("started_at") startedAt: Option[java.time.Instant] = None,
    @jsonField("status") status: Option[String] = None
)

object ActionWorkflowStep:
  given JsonCodec[ActionWorkflowStep] = DeriveJsonCodec.gen[ActionWorkflowStep]
