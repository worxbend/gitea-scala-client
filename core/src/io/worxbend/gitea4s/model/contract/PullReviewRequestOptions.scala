package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullReviewRequestOptions(
    @jsonField("reviewers") reviewers: Option[List[String]] = None,
    @jsonField("team_reviewers") teamReviewers: Option[List[String]] = None
)

object PullReviewRequestOptions:
  given JsonCodec[PullReviewRequestOptions] = DeriveJsonCodec.gen[PullReviewRequestOptions]
