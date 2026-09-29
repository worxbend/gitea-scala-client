package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditIssueCommentOption(
    @jsonField("body") body: String
)

object EditIssueCommentOption:
  given JsonCodec[EditIssueCommentOption] = DeriveJsonCodec.gen[EditIssueCommentOption]
