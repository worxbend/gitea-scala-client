package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class DismissPullReviewOptions(
    @jsonField("message") message: Option[String] = None,
    @jsonField("priors") priors: Option[Boolean] = None
)

object DismissPullReviewOptions:
  given JsonCodec[DismissPullReviewOptions] = DeriveJsonCodec.gen[DismissPullReviewOptions]
