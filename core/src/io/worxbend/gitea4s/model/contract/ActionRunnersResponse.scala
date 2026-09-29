package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionRunnersResponse(
    @jsonField("runners") runners: Option[List[ActionRunner]] = None,
    @jsonField("total_count") totalCount: Option[Long] = None
)

object ActionRunnersResponse:
  given JsonCodec[ActionRunnersResponse] = DeriveJsonCodec.gen[ActionRunnersResponse]
