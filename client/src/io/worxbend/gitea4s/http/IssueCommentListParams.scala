package io.worxbend.gitea4s.http

import java.time.Instant

final case class IssueCommentListParams(
    since: Option[Instant] = None,
    before: Option[Instant] = None
)

object IssueCommentListParams:
  val default: IssueCommentListParams = IssueCommentListParams()
