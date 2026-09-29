package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UpdateBranchProtectionPriories(
    @jsonField("ids") ids: Option[List[Long]] = None
)

object UpdateBranchProtectionPriories:
  given JsonCodec[UpdateBranchProtectionPriories] = DeriveJsonCodec.gen[UpdateBranchProtectionPriories]
