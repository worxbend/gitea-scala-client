package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateLabelOption(
    @jsonField("color") color: String,
    @jsonField("description") description: Option[String] = None,
    @jsonField("exclusive") exclusive: Option[Boolean] = None,
    @jsonField("is_archived") isArchived: Option[Boolean] = None,
    @jsonField("name") name: String
)

object CreateLabelOption:
  given JsonCodec[CreateLabelOption] = DeriveJsonCodec.gen[CreateLabelOption]
