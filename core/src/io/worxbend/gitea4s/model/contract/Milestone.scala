package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Milestone(
    @jsonField("closed_at") closedAt: Option[java.time.Instant] = None,
    @jsonField("closed_issues") closedIssues: Option[Long] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("due_on") dueOn: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("open_issues") openIssues: Option[Long] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None
)

object Milestone:
  given JsonCodec[Milestone] = DeriveJsonCodec.gen[Milestone]
