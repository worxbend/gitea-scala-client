package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Issue(
    @jsonField("assets") assets: Option[List[Attachment]] = None,
    @jsonField("assignee") assignee: Option[User] = None,
    @jsonField("assignees") assignees: Option[List[User]] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("closed_at") closedAt: Option[java.time.Instant] = None,
    @jsonField("comments") comments: Option[Long] = None,
    @jsonField("content_version") contentVersion: Option[Long] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("is_locked") isLocked: Option[Boolean] = None,
    @jsonField("labels") labels: Option[List[Label]] = None,
    @jsonField("milestone") milestone: Option[Milestone] = None,
    @jsonField("number") number: Option[Long] = None,
    @jsonField("original_author") originalAuthor: Option[String] = None,
    @jsonField("original_author_id") originalAuthorId: Option[Long] = None,
    @jsonField("pin_order") pinOrder: Option[Long] = None,
    @jsonField("projects") projects: Option[List[Project]] = None,
    @jsonField("pull_request") pullRequest: Option[PullRequestMeta] = None,
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("repository") repository: Option[RepositoryMeta] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("time_estimate") timeEstimate: Option[Long] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("user") user: Option[User] = None
)

object Issue:
  given JsonCodec[Issue] = DeriveJsonCodec.gen[Issue]
