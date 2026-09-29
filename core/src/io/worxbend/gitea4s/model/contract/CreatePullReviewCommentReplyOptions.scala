package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreatePullReviewCommentReplyOptions(
    @jsonField("body") body: Option[String] = None
)

object CreatePullReviewCommentReplyOptions:
  given JsonCodec[CreatePullReviewCommentReplyOptions] = DeriveJsonCodec.gen[CreatePullReviewCommentReplyOptions]
