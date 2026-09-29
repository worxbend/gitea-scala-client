package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflowJobsResponse(
    @jsonField("jobs") jobs: Option[List[ActionWorkflowJob]] = None,
    @jsonField("total_count") totalCount: Option[Long] = None
)

object ActionWorkflowJobsResponse:
  given JsonCodec[ActionWorkflowJobsResponse] = DeriveJsonCodec.gen[ActionWorkflowJobsResponse]
