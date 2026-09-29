package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Label(
    @jsonField("color") color: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("exclusive") exclusive: Option[Boolean] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("is_archived") isArchived: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object Label:
  given JsonCodec[Label] = DeriveJsonCodec.gen[Label]
