package io.worxbend.gitea4s.http

final case class GitTreeParams(
    recursive: Option[Boolean] = None,
    page: Option[Int] = None,
    perPage: Option[Int] = None
)

object GitTreeParams:
  val default: GitTreeParams = GitTreeParams()
