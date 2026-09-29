package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CommitDateOptions(
    @jsonField("author") author: Option[java.time.Instant] = None,
    @jsonField("committer") committer: Option[java.time.Instant] = None
)

object CommitDateOptions:
  given JsonCodec[CommitDateOptions] = DeriveJsonCodec.gen[CommitDateOptions]
