package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditPullRequestOption(
    @jsonField("allow_maintainer_edit") allowMaintainerEdit: Option[Boolean] = None,
    @jsonField("assignee") assignee: Option[String] = None,
    @jsonField("assignees") assignees: Option[List[String]] = None,
    @jsonField("base") base: Option[String] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("content_version") contentVersion: Option[Long] = None,
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None,
    @jsonField("labels") labels: Option[List[Long]] = None,
    @jsonField("milestone") milestone: Option[Long] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("unset_due_date") unsetDueDate: Option[Boolean] = None
)

object EditPullRequestOption:
  given JsonCodec[EditPullRequestOption] = DeriveJsonCodec.gen[EditPullRequestOption]
