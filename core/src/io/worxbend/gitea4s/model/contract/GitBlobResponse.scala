package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GitBlobResponse(
    @jsonField("content") content: Option[String] = None,
    @jsonField("encoding") encoding: Option[String] = None,
    @jsonField("lfs_oid") lfsOid: Option[String] = None,
    @jsonField("lfs_size") lfsSize: Option[Long] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("size") size: Option[Long] = None,
    @jsonField("url") url: Option[String] = None
)

object GitBlobResponse:
  given JsonCodec[GitBlobResponse] = DeriveJsonCodec.gen[GitBlobResponse]
