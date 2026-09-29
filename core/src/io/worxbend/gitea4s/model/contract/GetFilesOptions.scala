package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GetFilesOptions(
    @jsonField("files") files: Option[List[String]] = None
)

object GetFilesOptions:
  given JsonCodec[GetFilesOptions] = DeriveJsonCodec.gen[GetFilesOptions]
