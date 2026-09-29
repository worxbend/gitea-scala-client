package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CommitAffectedFiles(
    @jsonField("filename") filename: Option[String] = None,
    @jsonField("status") status: Option[String] = None
)

object CommitAffectedFiles:
  given JsonCodec[CommitAffectedFiles] = DeriveJsonCodec.gen[CommitAffectedFiles]
