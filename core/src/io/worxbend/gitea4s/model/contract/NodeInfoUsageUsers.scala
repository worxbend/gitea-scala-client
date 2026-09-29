package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NodeInfoUsageUsers(
    @jsonField("activeHalfyear") activeHalfyear: Option[Long] = None,
    @jsonField("activeMonth") activeMonth: Option[Long] = None,
    @jsonField("total") total: Option[Long] = None
)

object NodeInfoUsageUsers:
  given JsonCodec[NodeInfoUsageUsers] = DeriveJsonCodec.gen[NodeInfoUsageUsers]
