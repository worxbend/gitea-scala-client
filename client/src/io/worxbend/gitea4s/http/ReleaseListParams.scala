package io.worxbend.gitea4s.http

final case class ReleaseListParams(
    draft: Option[Boolean] = None,
    preRelease: Option[Boolean] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object ReleaseListParams:
  val default: ReleaseListParams = ReleaseListParams()
