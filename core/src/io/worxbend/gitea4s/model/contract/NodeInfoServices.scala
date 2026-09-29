package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NodeInfoServices(
    @jsonField("inbound") inbound: Option[List[String]] = None,
    @jsonField("outbound") outbound: Option[List[String]] = None
)

object NodeInfoServices:
  given JsonCodec[NodeInfoServices] = DeriveJsonCodec.gen[NodeInfoServices]
