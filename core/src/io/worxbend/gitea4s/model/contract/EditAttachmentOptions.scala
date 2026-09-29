package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditAttachmentOptions(
    @jsonField("name") name: Option[String] = None
)

object EditAttachmentOptions:
  given JsonCodec[EditAttachmentOptions] = DeriveJsonCodec.gen[EditAttachmentOptions]
