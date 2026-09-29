package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class MergeUpstreamResponse(
    @jsonField("merge_type") mergeType: Option[String] = None
)

object MergeUpstreamResponse:
  given JsonCodec[MergeUpstreamResponse] = DeriveJsonCodec.gen[MergeUpstreamResponse]
