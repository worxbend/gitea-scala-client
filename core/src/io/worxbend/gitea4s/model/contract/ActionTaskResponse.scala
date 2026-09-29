package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionTaskResponse(
    @jsonField("total_count") totalCount: Option[Long] = None,
    @jsonField("workflow_runs") workflowRuns: Option[List[ActionTask]] = None
)

object ActionTaskResponse:
  given JsonCodec[ActionTaskResponse] = DeriveJsonCodec.gen[ActionTaskResponse]
