package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullReview(
    @jsonField("body") body: Option[String] = None,
    @jsonField("comments_count") commentsCount: Option[Long] = None,
    @jsonField("commit_id") commitId: Option[String] = None,
    @jsonField("dismissed") dismissed: Option[Boolean] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("official") official: Option[Boolean] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    @jsonField("stale") stale: Option[Boolean] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("submitted_at") submittedAt: Option[java.time.Instant] = None,
    @jsonField("team") team: Option[Team] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("user") user: Option[User] = None
)

object PullReview:
  given JsonCodec[PullReview] = DeriveJsonCodec.gen[PullReview]
