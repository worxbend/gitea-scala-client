package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NodeInfo(
    @jsonField("metadata") metadata: Option[zio.json.ast.Json] = None,
    @jsonField("openRegistrations") openRegistrations: Option[Boolean] = None,
    @jsonField("protocols") protocols: Option[List[String]] = None,
    @jsonField("services") services: Option[NodeInfoServices] = None,
    @jsonField("software") software: Option[NodeInfoSoftware] = None,
    @jsonField("usage") usage: Option[NodeInfoUsage] = None,
    @jsonField("version") version: Option[String] = None
)

object NodeInfo:
  given JsonCodec[NodeInfo] = DeriveJsonCodec.gen[NodeInfo]
