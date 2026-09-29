package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TrackedTime(
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("issue") issue: Option[Issue] = None,
    @jsonField("issue_id") issueId: Option[Long] = None,
    @jsonField("time") time: Option[Long] = None,
    @jsonField("user_id") userId: Option[Long] = None,
    @jsonField("user_name") userName: Option[String] = None
)

object TrackedTime:
  given JsonCodec[TrackedTime] = DeriveJsonCodec.gen[TrackedTime]
