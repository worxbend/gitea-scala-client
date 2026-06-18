package io.worxbend.gitea4s.http

import java.time.Instant

final case class IssueTrackedTimeListParams(
    user: Option[String] = None,
    since: Option[Instant] = None,
    before: Option[Instant] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object IssueTrackedTimeListParams:
  val default: IssueTrackedTimeListParams = IssueTrackedTimeListParams()
