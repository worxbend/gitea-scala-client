package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class MarkupOption(
    @jsonField("Context") Context: Option[String] = None,
    @jsonField("FilePath") FilePath: Option[String] = None,
    @jsonField("Mode") Mode: Option[String] = None,
    @jsonField("Text") Text: Option[String] = None,
    @jsonField("Wiki") Wiki: Option[Boolean] = None
)

object MarkupOption:
  given JsonCodec[MarkupOption] = DeriveJsonCodec.gen[MarkupOption]
