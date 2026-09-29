package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GeneralUISettings(
    @jsonField("allowed_reactions") allowedReactions: Option[List[String]] = None,
    @jsonField("custom_emojis") customEmojis: Option[List[String]] = None,
    @jsonField("default_theme") defaultTheme: Option[String] = None
)

object GeneralUISettings:
  given JsonCodec[GeneralUISettings] = DeriveJsonCodec.gen[GeneralUISettings]
