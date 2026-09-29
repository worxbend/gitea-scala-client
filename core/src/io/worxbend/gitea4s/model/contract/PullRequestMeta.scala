package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullRequestMeta(
    @jsonField("draft") draft: Option[Boolean] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("merged") merged: Option[Boolean] = None,
    @jsonField("merged_at") mergedAt: Option[java.time.Instant] = None
)

object PullRequestMeta:
  given JsonCodec[PullRequestMeta] = DeriveJsonCodec.gen[PullRequestMeta]
