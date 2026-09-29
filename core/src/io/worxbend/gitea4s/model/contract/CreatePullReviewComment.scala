package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreatePullReviewComment(
    @jsonField("body") body: Option[String] = None,
    @jsonField("new_position") newPosition: Option[Long] = None,
    @jsonField("old_position") oldPosition: Option[Long] = None,
    @jsonField("path") path: Option[String] = None
)

object CreatePullReviewComment:
  given JsonCodec[CreatePullReviewComment] = DeriveJsonCodec.gen[CreatePullReviewComment]
