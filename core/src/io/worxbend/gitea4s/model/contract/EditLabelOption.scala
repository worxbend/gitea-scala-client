package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditLabelOption(
    @jsonField("color") color: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("exclusive") exclusive: Option[Boolean] = None,
    @jsonField("is_archived") isArchived: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None
)

object EditLabelOption:
  given JsonCodec[EditLabelOption] = DeriveJsonCodec.gen[EditLabelOption]
