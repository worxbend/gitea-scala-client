package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RunDetails(
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("run_url") runUrl: Option[String] = None,
    @jsonField("workflow_run_id") workflowRunId: Option[Long] = None
)

object RunDetails:
  given JsonCodec[RunDetails] = DeriveJsonCodec.gen[RunDetails]
