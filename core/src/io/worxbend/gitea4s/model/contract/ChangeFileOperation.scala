package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ChangeFileOperation(
    @jsonField("content") content: Option[String] = None,
    @jsonField("from_path") fromPath: Option[String] = None,
    @jsonField("operation") operation: String,
    @jsonField("path") path: String,
    @jsonField("sha") sha: Option[String] = None
)

object ChangeFileOperation:
  given JsonCodec[ChangeFileOperation] = DeriveJsonCodec.gen[ChangeFileOperation]
