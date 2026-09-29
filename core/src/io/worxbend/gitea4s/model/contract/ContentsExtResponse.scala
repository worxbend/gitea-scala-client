package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ContentsExtResponse(
    @jsonField("dir_contents") dirContents: Option[List[ContentsResponse]] = None,
    @jsonField("file_contents") fileContents: Option[ContentsResponse] = None
)

object ContentsExtResponse:
  given JsonCodec[ContentsExtResponse] = DeriveJsonCodec.gen[ContentsExtResponse]
