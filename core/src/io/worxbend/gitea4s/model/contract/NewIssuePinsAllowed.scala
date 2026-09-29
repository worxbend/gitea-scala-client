package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NewIssuePinsAllowed(
    @jsonField("issues") issues: Option[Boolean] = None,
    @jsonField("pull_requests") pullRequests: Option[Boolean] = None
)

object NewIssuePinsAllowed:
  given JsonCodec[NewIssuePinsAllowed] = DeriveJsonCodec.gen[NewIssuePinsAllowed]
