package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreatePullRequestOption(
    @jsonField("allow_maintainer_edit") allowMaintainerEdit: Option[Boolean] = None,
    @jsonField("assignee") assignee: Option[String] = None,
    @jsonField("assignees") assignees: Option[List[String]] = None,
    @jsonField("base") base: Option[String] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None,
    @jsonField("head") head: Option[String] = None,
    @jsonField("labels") labels: Option[List[Long]] = None,
    @jsonField("milestone") milestone: Option[Long] = None,
    @jsonField("reviewers") reviewers: Option[List[String]] = None,
    @jsonField("team_reviewers") teamReviewers: Option[List[String]] = None,
    @jsonField("title") title: Option[String] = None
)

object CreatePullRequestOption:
  given JsonCodec[CreatePullRequestOption] = DeriveJsonCodec.gen[CreatePullRequestOption]
