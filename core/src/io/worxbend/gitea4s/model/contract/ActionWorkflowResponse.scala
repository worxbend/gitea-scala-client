package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflowResponse(
    @jsonField("total_count") totalCount: Option[Long] = None,
    @jsonField("workflows") workflows: Option[List[ActionWorkflow]] = None
)

object ActionWorkflowResponse:
  given JsonCodec[ActionWorkflowResponse] = DeriveJsonCodec.gen[ActionWorkflowResponse]
