package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NodeInfoUsage(
    @jsonField("localComments") localComments: Option[Long] = None,
    @jsonField("localPosts") localPosts: Option[Long] = None,
    @jsonField("users") users: Option[NodeInfoUsageUsers] = None
)

object NodeInfoUsage:
  given JsonCodec[NodeInfoUsage] = DeriveJsonCodec.gen[NodeInfoUsage]
