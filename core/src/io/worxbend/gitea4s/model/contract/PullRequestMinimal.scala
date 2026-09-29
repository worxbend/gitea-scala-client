package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullRequestMinimal(
    @jsonField("base") base: Option[PullRequestMinimalHead] = None,
    @jsonField("head") head: Option[PullRequestMinimalHead] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("number") number: Option[Long] = None,
    @jsonField("url") url: Option[String] = None
)

object PullRequestMinimal:
  given JsonCodec[PullRequestMinimal] = DeriveJsonCodec.gen[PullRequestMinimal]
