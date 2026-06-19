package io.worxbend.gitea4s.http

final case class ContentsParams(
    ref: Option[String] = None
)

object ContentsParams:
  val default: ContentsParams = ContentsParams()
