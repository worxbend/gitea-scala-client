package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class FileResponse(
    @jsonField("commit") commit: Option[FileCommitResponse] = None,
    @jsonField("content") content: Option[ContentsResponse] = None,
    @jsonField("verification") verification: Option[PayloadCommitVerification] = None
)

object FileResponse:
  given JsonCodec[FileResponse] = DeriveJsonCodec.gen[FileResponse]
