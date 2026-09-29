package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullRequestMinimalHead(
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("repo") repo: Option[PullRequestMinimalHeadRepo] = None,
    @jsonField("sha") sha: Option[String] = None
)

object PullRequestMinimalHead:
  given JsonCodec[PullRequestMinimalHead] = DeriveJsonCodec.gen[PullRequestMinimalHead]
