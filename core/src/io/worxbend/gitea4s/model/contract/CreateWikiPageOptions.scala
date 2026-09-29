package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateWikiPageOptions(
    @jsonField("content_base64") contentBase64: Option[String] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("title") title: Option[String] = None
)

object CreateWikiPageOptions:
  given JsonCodec[CreateWikiPageOptions] = DeriveJsonCodec.gen[CreateWikiPageOptions]
