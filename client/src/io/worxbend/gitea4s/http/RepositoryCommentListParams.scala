package io.worxbend.gitea4s.http

import java.time.Instant

final case class RepositoryCommentListParams(
    since: Option[Instant] = None,
    before: Option[Instant] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object RepositoryCommentListParams:
  val default: RepositoryCommentListParams = RepositoryCommentListParams()
