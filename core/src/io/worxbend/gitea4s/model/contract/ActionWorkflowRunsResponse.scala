package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflowRunsResponse(
    @jsonField("total_count") totalCount: Option[Long] = None,
    @jsonField("workflow_runs") workflowRuns: Option[List[ActionWorkflowRun]] = None
)

object ActionWorkflowRunsResponse:
  given JsonCodec[ActionWorkflowRunsResponse] = DeriveJsonCodec.gen[ActionWorkflowRunsResponse]
