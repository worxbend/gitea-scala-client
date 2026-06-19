package io.worxbend.gitea4s.http

final case class CommitNoteParams(
    verification: Option[Boolean] = None,
    files: Option[Boolean] = None
)

object CommitNoteParams:
  val default: CommitNoteParams = CommitNoteParams()
