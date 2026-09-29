package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GitTreeResponse(
    @jsonField("page") page: Option[Long] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("total_count") totalCount: Option[Long] = None,
    @jsonField("tree") tree: Option[List[GitEntry]] = None,
    @jsonField("truncated") truncated: Option[Boolean] = None,
    @jsonField("url") url: Option[String] = None
)

object GitTreeResponse:
  given JsonCodec[GitTreeResponse] = DeriveJsonCodec.gen[GitTreeResponse]
