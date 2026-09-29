package io.worxbend.gitea4s.model

import zio.json.*

final case class CreatePullReviewCommentReplyOptions(body: String)

object CreatePullReviewCommentReplyOptions:
  given JsonCodec[CreatePullReviewCommentReplyOptions] = DeriveJsonCodec.gen[CreatePullReviewCommentReplyOptions]
