package io.worxbend.gitea4s.model

import zio.json.*

final case class GitTreeResponse(
    page: Option[Long] = None,
    sha: Option[String] = None,
    @jsonField("total_count") totalCount: Option[Long] = None,
    tree: Option[List[GitEntry]] = None,
    truncated: Option[Boolean] = None,
    url: Option[String] = None
)

object GitTreeResponse:
  given JsonCodec[GitTreeResponse] = DeriveJsonCodec.gen[GitTreeResponse]

final case class GitEntry(
    mode: Option[String] = None,
    path: Option[String] = None,
    sha: Option[String] = None,
    size: Option[Long] = None,
    `type`: Option[String] = None,
    url: Option[String] = None
)

object GitEntry:
  given JsonCodec[GitEntry] = DeriveJsonCodec.gen[GitEntry]
