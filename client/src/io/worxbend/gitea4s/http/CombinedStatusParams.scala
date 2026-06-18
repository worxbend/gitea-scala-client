package io.worxbend.gitea4s.http

final case class CombinedStatusParams(
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object CombinedStatusParams:
  val default: CombinedStatusParams = CombinedStatusParams()
