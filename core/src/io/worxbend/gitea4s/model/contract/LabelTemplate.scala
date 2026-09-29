package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class LabelTemplate(
    @jsonField("color") color: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("exclusive") exclusive: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None
)

object LabelTemplate:
  given JsonCodec[LabelTemplate] = DeriveJsonCodec.gen[LabelTemplate]
