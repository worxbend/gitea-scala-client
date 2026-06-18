package io.worxbend.gitea4s.http

final case class PullRequestCommitsParams(
    verification: Option[Boolean] = None,
    files: Option[Boolean] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object PullRequestCommitsParams:
  val default: PullRequestCommitsParams = PullRequestCommitsParams()
