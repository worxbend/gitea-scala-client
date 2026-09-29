package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GitignoreTemplateInfo(
    @jsonField("name") name: Option[String] = None,
    @jsonField("source") source: Option[String] = None
)

object GitignoreTemplateInfo:
  given JsonCodec[GitignoreTemplateInfo] = DeriveJsonCodec.gen[GitignoreTemplateInfo]
