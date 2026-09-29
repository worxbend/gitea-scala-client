package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class WikiCommitList(
    @jsonField("commits") commits: Option[List[WikiCommit]] = None,
    @jsonField("count") count: Option[Long] = None
)

object WikiCommitList:
  given JsonCodec[WikiCommitList] = DeriveJsonCodec.gen[WikiCommitList]
