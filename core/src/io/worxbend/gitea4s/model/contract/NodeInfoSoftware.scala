package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NodeInfoSoftware(
    @jsonField("homepage") homepage: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("repository") repository: Option[String] = None,
    @jsonField("version") version: Option[String] = None
)

object NodeInfoSoftware:
  given JsonCodec[NodeInfoSoftware] = DeriveJsonCodec.gen[NodeInfoSoftware]
