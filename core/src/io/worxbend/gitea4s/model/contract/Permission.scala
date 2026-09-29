package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Permission(
    @jsonField("admin") admin: Option[Boolean] = None,
    @jsonField("pull") pull: Option[Boolean] = None,
    @jsonField("push") push: Option[Boolean] = None
)

object Permission:
  given JsonCodec[Permission] = DeriveJsonCodec.gen[Permission]
