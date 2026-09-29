package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GitObject(
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object GitObject:
  given JsonCodec[GitObject] = DeriveJsonCodec.gen[GitObject]
