package io.worxbend.gitea4s.model

import java.time.Instant

import zio.json.*
import zio.json.internal.Write

final case class GiteaErrorPayload(
    message: Option[String] = None,
    url: Option[String] = None
)

object GiteaErrorPayload:
  given JsonCodec[GiteaErrorPayload] = DeriveJsonCodec.gen[GiteaErrorPayload]

final case class User(
    id: Option[Long] = None,
    login: Option[String] = None,
    @jsonField("login_name") loginName: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    email: Option[String] = None,
    @jsonField("avatar_url") avatarUrl: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("is_admin") isAdmin: Option[Boolean] = None,
    active: Option[Boolean] = None,
    restricted: Option[Boolean] = None,
    visibility: Option[String] = None,
    website: Option[String] = None,
    location: Option[String] = None,
    description: Option[String] = None,
    language: Option[String] = None,
    @jsonField("followers_count") followersCount: Option[Long] = None,
    @jsonField("following_count") followingCount: Option[Long] = None,
    @jsonField("starred_repos_count") starredReposCount: Option[Long] = None,
    created: Option[Instant] = None,
    @jsonField("last_login") lastLogin: Option[Instant] = None
)

object User:
  given JsonCodec[User] = DeriveJsonCodec.gen[User]

final case class Organization(
    id: Option[Long] = None,
    name: Option[String] = None,
    username: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    email: Option[String] = None,
    @jsonField("avatar_url") avatarUrl: Option[String] = None,
    description: Option[String] = None,
    location: Option[String] = None,
    visibility: Option[String] = None,
    website: Option[String] = None,
    @jsonField("repo_admin_change_team_access") repoAdminChangeTeamAccess: Option[Boolean] = None
)

object Organization:
  given JsonCodec[Organization] = DeriveJsonCodec.gen[Organization]

final case class Permission(
    admin: Option[Boolean] = None,
    pull: Option[Boolean] = None,
    push: Option[Boolean] = None
)

object Permission:
  given JsonCodec[Permission] = DeriveJsonCodec.gen[Permission]

final case class Repository(
    id: Option[Long] = None,
    owner: Option[User] = None,
    name: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    description: Option[String] = None,
    @jsonField("private") isPrivate: Option[Boolean] = None,
    fork: Option[Boolean] = None,
    empty: Option[Boolean] = None,
    archived: Option[Boolean] = None,
    mirror: Option[Boolean] = None,
    template: Option[Boolean] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("clone_url") cloneUrl: Option[String] = None,
    @jsonField("ssh_url") sshUrl: Option[String] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    language: Option[String] = None,
    size: Option[Long] = None,
    @jsonField("stars_count") starsCount: Option[Long] = None,
    @jsonField("forks_count") forksCount: Option[Long] = None,
    @jsonField("watchers_count") watchersCount: Option[Long] = None,
    @jsonField("open_issues_count") openIssuesCount: Option[Long] = None,
    permissions: Option[Permission] = None,
    topics: Option[List[String]] = None,
    @jsonField("object_format_name") objectFormatName: Option[ObjectFormatName] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    url: Option[String] = None
)

object Repository:
  given JsonCodec[Repository] = DeriveJsonCodec.gen[Repository]

final case class TopicNames(
    topics: Option[List[String]] = None
)

object TopicNames:
  given JsonCodec[TopicNames] = DeriveJsonCodec.gen[TopicNames]

final case class NewIssuePinsAllowed(
    issues: Option[Boolean] = None,
    @jsonField("pull_requests") pullRequests: Option[Boolean] = None
)

object NewIssuePinsAllowed:
  given JsonCodec[NewIssuePinsAllowed] = DeriveJsonCodec.gen[NewIssuePinsAllowed]

final case class NotificationCount(
    @jsonField("new") unread: Option[Long] = None
)

object NotificationCount:
  given JsonCodec[NotificationCount] = DeriveJsonCodec.gen[NotificationCount]

final case class NotificationSubject(
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("latest_comment_html_url") latestCommentHtmlUrl: Option[String] = None,
    @jsonField("latest_comment_url") latestCommentUrl: Option[String] = None,
    state: Option[NotificationSubjectState] = None,
    title: Option[String] = None,
    @jsonField("type") subjectType: Option[NotificationSubjectType] = None,
    url: Option[String] = None
)

object NotificationSubject:
  given JsonCodec[NotificationSubject] = DeriveJsonCodec.gen[NotificationSubject]

final case class NotificationThread(
    id: Option[Long] = None,
    pinned: Option[Boolean] = None,
    repository: Option[Repository] = None,
    subject: Option[NotificationSubject] = None,
    unread: Option[Boolean] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    url: Option[String] = None
)

object NotificationThread:
  given JsonCodec[NotificationThread] = DeriveJsonCodec.gen[NotificationThread]

final case class Label(
    id: Option[Long] = None,
    name: Option[String] = None,
    color: Option[String] = None,
    description: Option[String] = None,
    exclusive: Option[Boolean] = None,
    @jsonField("is_archived") isArchived: Option[Boolean] = None,
    url: Option[String] = None
)

object Label:
  given JsonCodec[Label] = DeriveJsonCodec.gen[Label]

final case class Milestone(
    id: Option[Long] = None,
    title: Option[String] = None,
    description: Option[String] = None,
    state: Option[IssueState] = None,
    @jsonField("open_issues") openIssues: Option[Long] = None,
    @jsonField("closed_issues") closedIssues: Option[Long] = None,
    @jsonField("due_on") dueOn: Option[Instant] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    @jsonField("closed_at") closedAt: Option[Instant] = None
)

object Milestone:
  given JsonCodec[Milestone] = DeriveJsonCodec.gen[Milestone]

final case class Issue(
    id: Option[Long] = None,
    number: Option[Long] = None,
    title: Option[String] = None,
    body: Option[String] = None,
    state: Option[IssueState] = None,
    user: Option[User] = None,
    assignee: Option[User] = None,
    assignees: Option[List[User]] = None,
    labels: Option[List[Label]] = None,
    milestone: Option[Milestone] = None,
    comments: Option[Long] = None,
    @jsonField("is_locked") isLocked: Option[Boolean] = None,
    @jsonField("pull_request") pullRequest: Option[PullRequestMeta] = None,
    repository: Option[RepositoryMeta] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    @jsonField("closed_at") closedAt: Option[Instant] = None,
    @jsonField("due_date") dueDate: Option[Instant] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    url: Option[String] = None
)

object Issue:
  given JsonCodec[Issue] = DeriveJsonCodec.gen[Issue]

final case class CreateIssue(
    title: String,
    assignee: Option[String] = None,
    assignees: Option[List[String]] = None,
    body: Option[String] = None,
    closed: Option[Boolean] = None,
    @jsonField("due_date") dueDate: Option[Instant] = None,
    labels: Option[List[Long]] = None,
    milestone: Option[Long] = None,
    ref: Option[String] = None
)

object CreateIssue:
  given JsonCodec[CreateIssue] = DeriveJsonCodec.gen[CreateIssue]

final case class EditIssue(
    assignee: Option[String] = None,
    assignees: Option[List[String]] = None,
    body: Option[String] = None,
    @jsonField("content_version") contentVersion: Option[Long] = None,
    @jsonField("due_date") dueDate: Option[Instant] = None,
    milestone: Option[Long] = None,
    ref: Option[String] = None,
    state: Option[IssueState] = None,
    title: Option[String] = None,
    @jsonField("unset_due_date") unsetDueDate: Option[Boolean] = None
)

object EditIssue:
  given JsonCodec[EditIssue] = DeriveJsonCodec.gen[EditIssue]

final case class CreateIssueComment(
    body: String
)

object CreateIssueComment:
  given JsonCodec[CreateIssueComment] = DeriveJsonCodec.gen[CreateIssueComment]

final case class EditIssueComment(
    body: String
)

object EditIssueComment:
  given JsonCodec[EditIssueComment] = DeriveJsonCodec.gen[EditIssueComment]

final case class EditReactionOption(
    content: String
)

object EditReactionOption:
  given JsonCodec[EditReactionOption] = DeriveJsonCodec.gen[EditReactionOption]

final case class Reaction(
    content: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    user: Option[User] = None
)

object Reaction:
  given JsonCodec[Reaction] = DeriveJsonCodec.gen[Reaction]

final case class WatchInfo(
    @jsonField("created_at") createdAt: Option[Instant] = None,
    ignored: Option[Boolean] = None,
    reason: Option[String] = None,
    @jsonField("repository_url") repositoryUrl: Option[String] = None,
    subscribed: Option[Boolean] = None,
    url: Option[String] = None
)

object WatchInfo:
  given JsonCodec[WatchInfo] = DeriveJsonCodec.gen[WatchInfo]

final case class AddTimeOption(
    time: Long,
    created: Option[Instant] = None,
    @jsonField("user_name") userName: Option[String] = None
)

object AddTimeOption:
  given JsonCodec[AddTimeOption] = DeriveJsonCodec.gen[AddTimeOption]

final case class TrackedTime(
    created: Option[Instant] = None,
    id: Option[Long] = None,
    issue: Option[Issue] = None,
    @jsonField("issue_id") issueId: Option[Long] = None,
    time: Option[Long] = None,
    @jsonField("user_id") userId: Option[Long] = None,
    @jsonField("user_name") userName: Option[String] = None
)

object TrackedTime:
  given JsonCodec[TrackedTime] = DeriveJsonCodec.gen[TrackedTime]

final case class StopWatch(
    created: Option[Instant] = None,
    duration: Option[String] = None,
    @jsonField("issue_index") issueIndex: Option[Long] = None,
    @jsonField("issue_title") issueTitle: Option[String] = None,
    @jsonField("repo_name") repoName: Option[String] = None,
    @jsonField("repo_owner_name") repoOwnerName: Option[String] = None,
    seconds: Option[Long] = None
)

object StopWatch:
  given JsonCodec[StopWatch] = DeriveJsonCodec.gen[StopWatch]

final case class IssueMeta(
    index: Long,
    owner: Option[String] = None,
    repo: Option[String] = None
)

object IssueMeta:
  given JsonCodec[IssueMeta] = DeriveJsonCodec.gen[IssueMeta]

final case class IssueLabelsOption(
    labels: List[Long]
)

object IssueLabelsOption:
  given JsonCodec[IssueLabelsOption] = DeriveJsonCodec.gen[IssueLabelsOption]

final case class LockIssueOption(
    @jsonField("lock_reason") lockReason: Option[String] = None
)

object LockIssueOption:
  given JsonCodec[LockIssueOption] = DeriveJsonCodec.gen[LockIssueOption]

final case class EditDeadlineOption(
    @jsonField("due_date") dueDate: Option[Instant]
)

object EditDeadlineOption:
  given JsonCodec[EditDeadlineOption] =
    val decoder = DeriveJsonDecoder.gen[EditDeadlineOption]
    val encoder =
      new JsonEncoder[EditDeadlineOption]:
        private val instantEncoder = summon[JsonEncoder[Instant]]

        override def unsafeEncode(value: EditDeadlineOption, indent: Option[Int], out: Write): Unit =
          out.write("""{"due_date":""")
          value.dueDate match
            case Some(dueDate) => instantEncoder.unsafeEncode(dueDate, indent, out)
            case None => out.write("null")
          out.write("}")

    JsonCodec(encoder, decoder)

final case class IssueDeadline(
    @jsonField("due_date") dueDate: Option[Instant] = None
)

object IssueDeadline:
  given JsonCodec[IssueDeadline] = DeriveJsonCodec.gen[IssueDeadline]

final case class Comment(
    id: Option[Long] = None,
    body: Option[String] = None,
    user: Option[User] = None,
    @jsonField("issue_url") issueUrl: Option[String] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None
)

object Comment:
  given JsonCodec[Comment] = DeriveJsonCodec.gen[Comment]

final case class PullRequest(
    id: Option[Long] = None,
    number: Option[Long] = None,
    title: Option[String] = None,
    body: Option[String] = None,
    state: Option[IssueState] = None,
    draft: Option[Boolean] = None,
    user: Option[User] = None,
    assignee: Option[User] = None,
    assignees: Option[List[User]] = None,
    labels: Option[List[Label]] = None,
    milestone: Option[Milestone] = None,
    base: Option[PRBranchInfo] = None,
    head: Option[PRBranchInfo] = None,
    mergeable: Option[Boolean] = None,
    merged: Option[Boolean] = None,
    @jsonField("merged_at") mergedAt: Option[Instant] = None,
    @jsonField("merged_by") mergedBy: Option[User] = None,
    additions: Option[Long] = None,
    deletions: Option[Long] = None,
    @jsonField("changed_files") changedFiles: Option[Long] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("diff_url") diffUrl: Option[String] = None,
    @jsonField("patch_url") patchUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    @jsonField("closed_at") closedAt: Option[Instant] = None,
    url: Option[String] = None
)

object PullRequest:
  given JsonCodec[PullRequest] = DeriveJsonCodec.gen[PullRequest]

final case class PullReview(
    body: Option[String] = None,
    @jsonField("comments_count") commentsCount: Option[Long] = None,
    @jsonField("commit_id") commitId: Option[String] = None,
    dismissed: Option[Boolean] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    id: Option[Long] = None,
    official: Option[Boolean] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    stale: Option[Boolean] = None,
    state: Option[PullReviewState] = None,
    @jsonField("submitted_at") submittedAt: Option[Instant] = None,
    team: Option[Team] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    user: Option[User] = None
)

object PullReview:
  given JsonCodec[PullReview] = DeriveJsonCodec.gen[PullReview]

final case class PullReviewComment(
    body: Option[String] = None,
    @jsonField("commit_id") commitId: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("diff_hunk") diffHunk: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    id: Option[Long] = None,
    @jsonField("original_commit_id") originalCommitId: Option[String] = None,
    @jsonField("original_position") originalPosition: Option[Long] = None,
    path: Option[String] = None,
    position: Option[Long] = None,
    @jsonField("pull_request_review_id") pullRequestReviewId: Option[Long] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    resolver: Option[User] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    user: Option[User] = None
)

object PullReviewComment:
  given JsonCodec[PullReviewComment] = DeriveJsonCodec.gen[PullReviewComment]

final case class CreatePullReviewComment(
    body: Option[String] = None,
    @jsonField("new_position") newPosition: Option[Long] = None,
    @jsonField("old_position") oldPosition: Option[Long] = None,
    path: Option[String] = None
)

object CreatePullReviewComment:
  given JsonCodec[CreatePullReviewComment] = DeriveJsonCodec.gen[CreatePullReviewComment]

final case class CreatePullReviewOptions(
    body: Option[String] = None,
    comments: Option[List[CreatePullReviewComment]] = None,
    @jsonField("commit_id") commitId: Option[String] = None,
    event: Option[PullReviewState] = None
)

object CreatePullReviewOptions:
  given JsonCodec[CreatePullReviewOptions] = DeriveJsonCodec.gen[CreatePullReviewOptions]

final case class SubmitPullReviewOptions(
    body: Option[String] = None,
    event: Option[PullReviewState] = None
)

object SubmitPullReviewOptions:
  given JsonCodec[SubmitPullReviewOptions] = DeriveJsonCodec.gen[SubmitPullReviewOptions]

final case class DismissPullReviewOptions(
    message: Option[String] = None,
    priors: Option[Boolean] = None
)

object DismissPullReviewOptions:
  given JsonCodec[DismissPullReviewOptions] = DeriveJsonCodec.gen[DismissPullReviewOptions]

final case class PullReviewRequestOptions(
    reviewers: Option[List[String]] = None,
    @jsonField("team_reviewers") teamReviewers: Option[List[String]] = None
)

object PullReviewRequestOptions:
  given JsonCodec[PullReviewRequestOptions] = DeriveJsonCodec.gen[PullReviewRequestOptions]

final case class CreatePullRequestOption(
    @jsonField("allow_maintainer_edit") allowMaintainerEdit: Option[Boolean] = None,
    assignee: Option[String] = None,
    assignees: Option[List[String]] = None,
    base: Option[String] = None,
    body: Option[String] = None,
    @jsonField("due_date") dueDate: Option[Instant] = None,
    head: Option[String] = None,
    labels: Option[List[Long]] = None,
    milestone: Option[Long] = None,
    reviewers: Option[List[String]] = None,
    @jsonField("team_reviewers") teamReviewers: Option[List[String]] = None,
    title: Option[String] = None
)

object CreatePullRequestOption:
  given JsonCodec[CreatePullRequestOption] = DeriveJsonCodec.gen[CreatePullRequestOption]

final case class EditPullRequestOption(
    @jsonField("allow_maintainer_edit") allowMaintainerEdit: Option[Boolean] = None,
    assignee: Option[String] = None,
    assignees: Option[List[String]] = None,
    base: Option[String] = None,
    body: Option[String] = None,
    @jsonField("content_version") contentVersion: Option[Long] = None,
    @jsonField("due_date") dueDate: Option[Instant] = None,
    labels: Option[List[Long]] = None,
    milestone: Option[Long] = None,
    state: Option[IssueState] = None,
    title: Option[String] = None,
    @jsonField("unset_due_date") unsetDueDate: Option[Boolean] = None
)

object EditPullRequestOption:
  given JsonCodec[EditPullRequestOption] = DeriveJsonCodec.gen[EditPullRequestOption]

final case class MergePullRequestOption(
    @jsonField("Do") mergeMethod: MergePullRequestMethod,
    @jsonField("MergeCommitID") mergeCommitId: Option[String] = None,
    @jsonField("MergeMessageField") mergeMessageField: Option[String] = None,
    @jsonField("MergeTitleField") mergeTitleField: Option[String] = None,
    @jsonField("delete_branch_after_merge") deleteBranchAfterMerge: Option[Boolean] = None,
    @jsonField("force_merge") forceMerge: Option[Boolean] = None,
    @jsonField("head_commit_id") headCommitId: Option[String] = None,
    @jsonField("merge_when_checks_succeed") mergeWhenChecksSucceed: Option[Boolean] = None
)

object MergePullRequestOption:
  given JsonCodec[MergePullRequestOption] = DeriveJsonCodec.gen[MergePullRequestOption]

final case class ChangedFile(
    additions: Option[Long] = None,
    changes: Option[Long] = None,
    @jsonField("contents_url") contentsUrl: Option[String] = None,
    deletions: Option[Long] = None,
    filename: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("previous_filename") previousFilename: Option[String] = None,
    @jsonField("raw_url") rawUrl: Option[String] = None,
    status: Option[String] = None
)

object ChangedFile:
  given JsonCodec[ChangedFile] = DeriveJsonCodec.gen[ChangedFile]

final case class Commit(
    author: Option[User] = None,
    commit: Option[RepoCommit] = None,
    committer: Option[User] = None,
    created: Option[Instant] = None,
    files: Option[List[CommitAffectedFile]] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    parents: Option[List[CommitMeta]] = None,
    sha: Option[String] = None,
    stats: Option[CommitStats] = None,
    url: Option[String] = None
)

object Commit:
  given JsonCodec[Commit] = DeriveJsonCodec.gen[Commit]

final case class RepoCommit(
    author: Option[CommitUser] = None,
    committer: Option[CommitUser] = None,
    message: Option[String] = None,
    tree: Option[CommitMeta] = None,
    url: Option[String] = None,
    verification: Option[PayloadCommitVerification] = None
)

object RepoCommit:
  given JsonCodec[RepoCommit] = DeriveJsonCodec.gen[RepoCommit]

final case class CommitAffectedFile(
    filename: Option[String] = None,
    status: Option[String] = None
)

object CommitAffectedFile:
  given JsonCodec[CommitAffectedFile] = DeriveJsonCodec.gen[CommitAffectedFile]

final case class CommitStats(
    additions: Option[Long] = None,
    deletions: Option[Long] = None,
    total: Option[Long] = None
)

object CommitStats:
  given JsonCodec[CommitStats] = DeriveJsonCodec.gen[CommitStats]

final case class Release(
    id: Option[Long] = None,
    author: Option[User] = None,
    name: Option[String] = None,
    @jsonField("tag_name") tagName: Option[String] = None,
    body: Option[String] = None,
    draft: Option[Boolean] = None,
    prerelease: Option[Boolean] = None,
    @jsonField("target_commitish") targetCommitish: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("tarball_url") tarballUrl: Option[String] = None,
    @jsonField("zipball_url") zipballUrl: Option[String] = None,
    @jsonField("upload_url") uploadUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("published_at") publishedAt: Option[Instant] = None,
    url: Option[String] = None
)

object Release:
  given JsonCodec[Release] = DeriveJsonCodec.gen[Release]

final case class Branch(
    name: Option[String] = None,
    commit: Option[PayloadCommit] = None,
    @jsonField("protected") isProtected: Option[Boolean] = None,
    @jsonField("user_can_merge") userCanMerge: Option[Boolean] = None,
    @jsonField("user_can_push") userCanPush: Option[Boolean] = None,
    @jsonField("enable_status_check") enableStatusCheck: Option[Boolean] = None,
    @jsonField("required_approvals") requiredApprovals: Option[Long] = None,
    @jsonField("status_check_contexts") statusCheckContexts: Option[List[String]] = None,
    @jsonField("effective_branch_protection_name") effectiveBranchProtectionName: Option[String] = None
)

object Branch:
  given JsonCodec[Branch] = DeriveJsonCodec.gen[Branch]

final case class Tag(
    id: Option[String] = None,
    name: Option[String] = None,
    message: Option[String] = None,
    commit: Option[CommitMeta] = None,
    @jsonField("tarball_url") tarballUrl: Option[String] = None,
    @jsonField("zipball_url") zipballUrl: Option[String] = None
)

object Tag:
  given JsonCodec[Tag] = DeriveJsonCodec.gen[Tag]

final case class PullRequestMeta(
    draft: Option[Boolean] = None,
    merged: Option[Boolean] = None,
    @jsonField("merged_at") mergedAt: Option[Instant] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None
)

object PullRequestMeta:
  given JsonCodec[PullRequestMeta] = DeriveJsonCodec.gen[PullRequestMeta]

final case class RepositoryMeta(
    id: Option[Long] = None,
    owner: Option[String] = None,
    name: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None
)

object RepositoryMeta:
  given JsonCodec[RepositoryMeta] = DeriveJsonCodec.gen[RepositoryMeta]

final case class PRBranchInfo(
    label: Option[String] = None,
    ref: Option[String] = None,
    sha: Option[String] = None,
    @jsonField("repo_id") repoId: Option[Long] = None
)

object PRBranchInfo:
  given JsonCodec[PRBranchInfo] = DeriveJsonCodec.gen[PRBranchInfo]

final case class Team(
    id: Option[Long] = None,
    name: Option[String] = None,
    description: Option[String] = None,
    organization: Option[Organization] = None,
    permission: Option[TeamPermission] = None,
    @jsonField("includes_all_repositories") includesAllRepositories: Option[Boolean] = None,
    @jsonField("can_create_org_repo") canCreateOrgRepo: Option[Boolean] = None,
    units: Option[List[String]] = None
)

object Team:
  given JsonCodec[Team] = DeriveJsonCodec.gen[Team]

final case class PayloadCommit(
    id: Option[String] = None,
    message: Option[String] = None,
    url: Option[String] = None,
    timestamp: Option[Instant] = None,
    added: Option[List[String]] = None,
    removed: Option[List[String]] = None,
    modified: Option[List[String]] = None,
    author: Option[PayloadUser] = None,
    committer: Option[PayloadUser] = None,
    verification: Option[PayloadCommitVerification] = None
)

object PayloadCommit:
  given JsonCodec[PayloadCommit] = DeriveJsonCodec.gen[PayloadCommit]

final case class CommitMeta(
    sha: Option[String] = None,
    created: Option[Instant] = None,
    url: Option[String] = None
)

object CommitMeta:
  given JsonCodec[CommitMeta] = DeriveJsonCodec.gen[CommitMeta]

final case class CommitUser(
    date: Option[String] = None,
    email: Option[String] = None,
    name: Option[String] = None
)

object CommitUser:
  given JsonCodec[CommitUser] = DeriveJsonCodec.gen[CommitUser]

final case class PayloadUser(
    name: Option[String] = None,
    email: Option[String] = None,
    username: Option[String] = None
)

object PayloadUser:
  given JsonCodec[PayloadUser] = DeriveJsonCodec.gen[PayloadUser]

final case class PayloadCommitVerification(
    verified: Option[Boolean] = None,
    reason: Option[String] = None,
    signature: Option[String] = None,
    payload: Option[String] = None,
    signer: Option[PayloadUser] = None
)

object PayloadCommitVerification:
  given JsonCodec[PayloadCommitVerification] = DeriveJsonCodec.gen[PayloadCommitVerification]
