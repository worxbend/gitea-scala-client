package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionArtifactsResponse(
    @jsonField("artifacts") artifacts: Option[List[ActionArtifact]] = None,
    @jsonField("total_count") totalCount: Option[Long] = None
)

object ActionArtifactsResponse:
  given JsonCodec[ActionArtifactsResponse] = DeriveJsonCodec.gen[ActionArtifactsResponse]
