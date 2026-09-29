package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueMeta(
    @jsonField("index") index: Option[Long] = None,
    @jsonField("owner") owner: Option[String] = None,
    @jsonField("repo") repo: Option[String] = None
)

object IssueMeta:
  given JsonCodec[IssueMeta] = DeriveJsonCodec.gen[IssueMeta]
