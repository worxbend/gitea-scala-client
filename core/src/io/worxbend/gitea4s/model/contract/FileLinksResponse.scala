package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class FileLinksResponse(
    @jsonField("git") git: Option[String] = None,
    @jsonField("html") html: Option[String] = None,
    @jsonField("self") self: Option[String] = None
)

object FileLinksResponse:
  given JsonCodec[FileLinksResponse] = DeriveJsonCodec.gen[FileLinksResponse]
