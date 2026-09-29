package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class FilesResponse(
    @jsonField("commit") commit: Option[FileCommitResponse] = None,
    @jsonField("files") files: Option[List[ContentsResponse]] = None,
    @jsonField("verification") verification: Option[PayloadCommitVerification] = None
)

object FilesResponse:
  given JsonCodec[FilesResponse] = DeriveJsonCodec.gen[FilesResponse]
