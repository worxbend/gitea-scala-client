package io.worxbend.gitea4s.http

final case class SingleCommitParams(
    stat: Option[Boolean] = None,
    verification: Option[Boolean] = None,
    files: Option[Boolean] = None
)

object SingleCommitParams:
  val default: SingleCommitParams = SingleCommitParams()
