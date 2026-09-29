package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateIssueOption(
    @jsonField("assignee") assignee: Option[String] = None,
    @jsonField("assignees") assignees: Option[List[String]] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("closed") closed: Option[Boolean] = None,
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None,
    @jsonField("labels") labels: Option[List[Long]] = None,
    @jsonField("milestone") milestone: Option[Long] = None,
    @jsonField("projects") projects: Option[List[Long]] = None,
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("title") title: String
)

object CreateIssueOption:
  given JsonCodec[CreateIssueOption] = DeriveJsonCodec.gen[CreateIssueOption]
