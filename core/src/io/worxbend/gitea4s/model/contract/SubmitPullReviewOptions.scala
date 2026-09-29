package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class SubmitPullReviewOptions(
    @jsonField("body") body: Option[String] = None,
    @jsonField("event") event: Option[String] = None
)

object SubmitPullReviewOptions:
  given JsonCodec[SubmitPullReviewOptions] = DeriveJsonCodec.gen[SubmitPullReviewOptions]
