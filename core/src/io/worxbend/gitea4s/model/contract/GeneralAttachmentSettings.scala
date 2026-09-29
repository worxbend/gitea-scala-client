package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GeneralAttachmentSettings(
    @jsonField("allowed_types") allowedTypes: Option[String] = None,
    @jsonField("enabled") enabled: Option[Boolean] = None,
    @jsonField("max_files") maxFiles: Option[Long] = None,
    @jsonField("max_size") maxSize: Option[Long] = None
)

object GeneralAttachmentSettings:
  given JsonCodec[GeneralAttachmentSettings] = DeriveJsonCodec.gen[GeneralAttachmentSettings]
