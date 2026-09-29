package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreatePullReviewOptions(
    @jsonField("body") body: Option[String] = None,
    @jsonField("comments") comments: Option[List[CreatePullReviewComment]] = None,
    @jsonField("commit_id") commitId: Option[String] = None,
    @jsonField("event") event: Option[String] = None
)

object CreatePullReviewOptions:
  given JsonCodec[CreatePullReviewOptions] = DeriveJsonCodec.gen[CreatePullReviewOptions]
