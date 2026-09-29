package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateIssueCommentOption(
    @jsonField("body") body: String
)

object CreateIssueCommentOption:
  given JsonCodec[CreateIssueCommentOption] = DeriveJsonCodec.gen[CreateIssueCommentOption]
