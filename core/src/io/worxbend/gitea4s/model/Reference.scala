package io.worxbend.gitea4s.model

import zio.json.*

final case class Reference(
    @jsonField("object") gitObject: Option[GitObject] = None,
    ref: Option[String] = None,
    url: Option[String] = None
)

object Reference:
  given JsonCodec[Reference] = DeriveJsonCodec.gen[Reference]

final case class GitObject(
    sha: Option[String] = None,
    `type`: Option[String] = None,
    url: Option[String] = None
)

object GitObject:
  given JsonCodec[GitObject] = DeriveJsonCodec.gen[GitObject]
