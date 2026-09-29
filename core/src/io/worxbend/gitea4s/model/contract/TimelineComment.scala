package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TimelineComment(
    @jsonField("assignee") assignee: Option[User] = None,
    @jsonField("assignee_team") assigneeTeam: Option[Team] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("dependent_issue") dependentIssue: Option[Issue] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("issue_url") issueUrl: Option[String] = None,
    @jsonField("label") label: Option[Label] = None,
    @jsonField("milestone") milestone: Option[Milestone] = None,
    @jsonField("new_ref") newRef: Option[String] = None,
    @jsonField("new_title") newTitle: Option[String] = None,
    @jsonField("old_milestone") oldMilestone: Option[Milestone] = None,
    @jsonField("old_project_id") oldProjectId: Option[Long] = None,
    @jsonField("old_ref") oldRef: Option[String] = None,
    @jsonField("old_title") oldTitle: Option[String] = None,
    @jsonField("project_id") projectId: Option[Long] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    @jsonField("ref_action") refAction: Option[String] = None,
    @jsonField("ref_comment") refComment: Option[Comment] = None,
    @jsonField("ref_commit_sha") refCommitSha: Option[String] = None,
    @jsonField("ref_issue") refIssue: Option[Issue] = None,
    @jsonField("removed_assignee") removedAssignee: Option[Boolean] = None,
    @jsonField("resolve_doer") resolveDoer: Option[User] = None,
    @jsonField("review_id") reviewId: Option[Long] = None,
    @jsonField("tracked_time") trackedTime: Option[TrackedTime] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("user") user: Option[User] = None
)

object TimelineComment:
  given JsonCodec[TimelineComment] = DeriveJsonCodec.gen[TimelineComment]
