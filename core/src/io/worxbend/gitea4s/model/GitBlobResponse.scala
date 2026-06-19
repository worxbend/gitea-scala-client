package io.worxbend.gitea4s.model

import zio.json.*

final case class GitBlobResponse(
    content: Option[String] = None,
    encoding: Option[String] = None,
    @jsonField("lfs_oid") lfsOid: Option[String] = None,
    @jsonField("lfs_size") lfsSize: Option[Long] = None,
    sha: Option[String] = None,
    size: Option[Long] = None,
    url: Option[String] = None
)

object GitBlobResponse:
  given JsonCodec[GitBlobResponse] = DeriveJsonCodec.gen[GitBlobResponse]
