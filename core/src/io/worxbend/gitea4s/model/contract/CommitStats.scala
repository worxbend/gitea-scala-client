package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CommitStats(
    @jsonField("additions") additions: Option[Long] = None,
    @jsonField("deletions") deletions: Option[Long] = None,
    @jsonField("total") total: Option[Long] = None
)

object CommitStats:
  given JsonCodec[CommitStats] = DeriveJsonCodec.gen[CommitStats]
