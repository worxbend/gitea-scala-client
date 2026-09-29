package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullRequestMinimalHeadRepo(
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object PullRequestMinimalHeadRepo:
  given JsonCodec[PullRequestMinimalHeadRepo] = DeriveJsonCodec.gen[PullRequestMinimalHeadRepo]
