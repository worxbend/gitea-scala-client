package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class StopWatch(
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("duration") duration: Option[String] = None,
    @jsonField("issue_index") issueIndex: Option[Long] = None,
    @jsonField("issue_title") issueTitle: Option[String] = None,
    @jsonField("repo_name") repoName: Option[String] = None,
    @jsonField("repo_owner_name") repoOwnerName: Option[String] = None,
    @jsonField("seconds") seconds: Option[Long] = None
)

object StopWatch:
  given JsonCodec[StopWatch] = DeriveJsonCodec.gen[StopWatch]
