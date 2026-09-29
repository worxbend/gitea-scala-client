package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CombinedStatus(
    @jsonField("commit_url") commitUrl: Option[String] = None,
    @jsonField("repository") repository: Option[Repository] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("statuses") statuses: Option[List[CommitStatus]] = None,
    @jsonField("total_count") totalCount: Option[Long] = None,
    @jsonField("url") url: Option[String] = None
)

object CombinedStatus:
  given JsonCodec[CombinedStatus] = DeriveJsonCodec.gen[CombinedStatus]
