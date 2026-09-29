package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UserHeatmapData(
    @jsonField("contributions") contributions: Option[Long] = None,
    @jsonField("timestamp") timestamp: Option[TimeStamp] = None
)

object UserHeatmapData:
  given JsonCodec[UserHeatmapData] = DeriveJsonCodec.gen[UserHeatmapData]
