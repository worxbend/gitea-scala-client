package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Reference(
    @jsonField("object") `object`: Option[GitObject] = None,
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object Reference:
  given JsonCodec[Reference] = DeriveJsonCodec.gen[Reference]
