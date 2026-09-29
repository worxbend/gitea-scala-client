package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PullRequest(
    @jsonField("additions") additions: Option[Long] = None,
    @jsonField("allow_maintainer_edit") allowMaintainerEdit: Option[Boolean] = None,
    @jsonField("assignee") assignee: Option[User] = None,
    @jsonField("assignees") assignees: Option[List[User]] = None,
    @jsonField("base") base: Option[PRBranchInfo] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("changed_files") changedFiles: Option[Long] = None,
    @jsonField("closed_at") closedAt: Option[java.time.Instant] = None,
    @jsonField("comments") comments: Option[Long] = None,
    @jsonField("content_version") contentVersion: Option[Long] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("deletions") deletions: Option[Long] = None,
    @jsonField("diff_url") diffUrl: Option[String] = None,
    @jsonField("draft") draft: Option[Boolean] = None,
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None,
    @jsonField("head") head: Option[PRBranchInfo] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("is_locked") isLocked: Option[Boolean] = None,
    @jsonField("labels") labels: Option[List[Label]] = None,
    @jsonField("merge_base") mergeBase: Option[String] = None,
    @jsonField("merge_commit_sha") mergeCommitSha: Option[String] = None,
    @jsonField("mergeable") mergeable: Option[Boolean] = None,
    @jsonField("merged") merged: Option[Boolean] = None,
    @jsonField("merged_at") mergedAt: Option[java.time.Instant] = None,
    @jsonField("merged_by") mergedBy: Option[User] = None,
    @jsonField("milestone") milestone: Option[Milestone] = None,
    @jsonField("number") number: Option[Long] = None,
    @jsonField("patch_url") patchUrl: Option[String] = None,
    @jsonField("pin_order") pinOrder: Option[Long] = None,
    @jsonField("requested_reviewers") requestedReviewers: Option[List[User]] = None,
    @jsonField("requested_reviewers_teams") requestedReviewersTeams: Option[List[Team]] = None,
    @jsonField("review_comments") reviewComments: Option[Long] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("user") user: Option[User] = None
)

object PullRequest:
  given JsonCodec[PullRequest] = DeriveJsonCodec.gen[PullRequest]
