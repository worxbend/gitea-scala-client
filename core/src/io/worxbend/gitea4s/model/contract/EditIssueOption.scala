package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditIssueOption(
    @jsonField("assignee") assignee: Option[String] = None,
    @jsonField("assignees") assignees: Option[List[String]] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("content_version") contentVersion: Option[Long] = None,
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None,
    @jsonField("milestone") milestone: Option[Long] = None,
    @jsonField("projects") projects: Option[List[Long]] = None,
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("unset_due_date") unsetDueDate: Option[Boolean] = None
)

object EditIssueOption:
  given JsonCodec[EditIssueOption] = DeriveJsonCodec.gen[EditIssueOption]
