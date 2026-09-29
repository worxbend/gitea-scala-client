package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Compare(
    @jsonField("commits") commits: Option[List[Commit]] = None,
    @jsonField("total_commits") totalCommits: Option[Long] = None
)

object Compare:
  given JsonCodec[Compare] = DeriveJsonCodec.gen[Compare]
