package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Tag(
    @jsonField("commit") commit: Option[CommitMeta] = None,
    @jsonField("id") id: Option[String] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("tarball_url") tarballUrl: Option[String] = None,
    @jsonField("zipball_url") zipballUrl: Option[String] = None
)

object Tag:
  given JsonCodec[Tag] = DeriveJsonCodec.gen[Tag]
