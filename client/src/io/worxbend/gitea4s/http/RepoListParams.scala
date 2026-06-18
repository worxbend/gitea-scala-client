package io.worxbend.gitea4s.http

final case class RepoListParams(
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object RepoListParams:
  val default: RepoListParams = RepoListParams()
