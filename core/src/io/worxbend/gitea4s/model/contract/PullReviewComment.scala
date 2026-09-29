package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullReviewComment(
    @jsonField("body") body: Option[String] = None,
    @jsonField("commit_id") commitId: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("diff_hunk") diffHunk: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("original_commit_id") originalCommitId: Option[String] = None,
    @jsonField("original_position") originalPosition: Option[Long] = None,
    @jsonField("path") path: Option[String] = None,
    @jsonField("position") position: Option[Long] = None,
    @jsonField("pull_request_review_id") pullRequestReviewId: Option[Long] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    @jsonField("resolver") resolver: Option[User] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("user") user: Option[User] = None
)

object PullReviewComment:
  given JsonCodec[PullReviewComment] = DeriveJsonCodec.gen[PullReviewComment]
