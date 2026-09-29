package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class MergeUpstreamRequest(
    @jsonField("branch") branch: Option[String] = None,
    @jsonField("ff_only") ffOnly: Option[Boolean] = None
)

object MergeUpstreamRequest:
  given JsonCodec[MergeUpstreamRequest] = DeriveJsonCodec.gen[MergeUpstreamRequest]
