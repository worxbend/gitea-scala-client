package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class FileDeleteResponse(
    @jsonField("commit") commit: Option[FileCommitResponse] = None,
    @jsonField("content") content: Option[zio.json.ast.Json] = None,
    @jsonField("verification") verification: Option[PayloadCommitVerification] = None
)

object FileDeleteResponse:
  given JsonCodec[FileDeleteResponse] = DeriveJsonCodec.gen[FileDeleteResponse]
