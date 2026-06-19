package io.worxbend.gitea4s.http

final case class GiteaEndpoint(
    method: String,
    path: String,
    operationId: String,
    parameters: List[GiteaParameter],
    response: String
)

final case class GiteaParameter(name: String, in: String, required: Boolean)

object GiteaEndpoints:
  val userGetCurrent: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/user",
      operationId = "userGetCurrent",
      parameters = Nil,
      response = "#/responses/User"
    )

  val userGet: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/users/{username}",
      operationId = "userGet",
      parameters = List(GiteaParameter("username", "path", required = true)),
      response = "#/responses/User"
    )

  val userSearch: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/users/search",
      operationId = "userSearch",
      parameters = List(
        GiteaParameter("q", "query", required = false),
        GiteaParameter("uid", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "SearchResults[User]"
    )

  val repoGet: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}",
      operationId = "repoGet",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true)
      ),
      response = "#/responses/Repository"
    )

  val orgGet: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/orgs/{org}",
      operationId = "orgGet",
      parameters = List(GiteaParameter("org", "path", required = true)),
      response = "#/responses/Organization"
    )

  val orgListMembers: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/orgs/{org}/members",
      operationId = "orgListMembers",
      parameters = List(
        GiteaParameter("org", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/UserList"
    )

  val orgListPublicMembers: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/orgs/{org}/public_members",
      operationId = "orgListPublicMembers",
      parameters = List(
        GiteaParameter("org", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/UserList"
    )

  val orgListRepos: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/orgs/{org}/repos",
      operationId = "orgListRepos",
      parameters = List(
        GiteaParameter("org", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/RepositoryList"
    )

  val userListRepos: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/users/{username}/repos",
      operationId = "userListRepos",
      parameters = List(
        GiteaParameter("username", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/RepositoryList"
    )

  val repoListTopics: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/topics",
      operationId = "repoListTopics",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/TopicNames"
    )

  val repoNewPinAllowed: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/new_pin_allowed",
      operationId = "repoNewPinAllowed",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true)
      ),
      response = "#/responses/RepoNewIssuePinsAllowed"
    )

  val repoListBranches: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/branches",
      operationId = "repoListBranches",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/BranchList"
    )

  val repoListTags: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/tags",
      operationId = "repoListTags",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/TagList"
    )

  val repoListReleases: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/releases",
      operationId = "repoListReleases",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("draft", "query", required = false),
        GiteaParameter("pre-release", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/ReleaseList"
    )

  val repoGetRelease: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/releases/{id}",
      operationId = "repoGetRelease",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/Release"
    )

  val repoGetCombinedStatusByRef: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/commits/{ref}/status",
      operationId = "repoGetCombinedStatusByRef",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("ref", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/CombinedStatus"
    )

  val repoListStatusesByRef: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/commits/{ref}/statuses",
      operationId = "repoListStatusesByRef",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("ref", "path", required = true),
        GiteaParameter("sort", "query", required = false),
        GiteaParameter("state", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/CommitStatusList"
    )

  val repoListStatuses: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/statuses/{sha}",
      operationId = "repoListStatuses",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("sha", "path", required = true),
        GiteaParameter("sort", "query", required = false),
        GiteaParameter("state", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/CommitStatusList"
    )

  val repoCreateStatus: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/statuses/{sha}",
      operationId = "repoCreateStatus",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("sha", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/CommitStatus"
    )

  val repoListPullRequests: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls",
      operationId = "repoListPullRequests",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("base_branch", "query", required = false),
        GiteaParameter("state", "query", required = false),
        GiteaParameter("sort", "query", required = false),
        GiteaParameter("milestone", "query", required = false),
        GiteaParameter("labels", "query", required = false),
        GiteaParameter("poster", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/PullRequestList"
    )

  val repoCreatePullRequest: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls",
      operationId = "repoCreatePullRequest",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/PullRequest"
    )

  val repoListPinnedPullRequests: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/pinned",
      operationId = "repoListPinnedPullRequests",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true)
      ),
      response = "#/responses/PullRequestList"
    )

  val repoGetPullRequestByBaseHead: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{base}/{head}",
      operationId = "repoGetPullRequestByBaseHead",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("base", "path", required = true),
        GiteaParameter("head", "path", required = true)
      ),
      response = "#/responses/PullRequest"
    )

  val repoGetPullRequest: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}",
      operationId = "repoGetPullRequest",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/PullRequest"
    )

  val repoEditPullRequest: GiteaEndpoint =
    GiteaEndpoint(
      method = "PATCH",
      path = "/repos/{owner}/{repo}/pulls/{index}",
      operationId = "repoEditPullRequest",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/PullRequest"
    )

  val repoPullRequestIsMerged: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}/merge",
      operationId = "repoPullRequestIsMerged",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "204 merged / 404 not merged"
    )

  val repoMergePullRequest: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/merge",
      operationId = "repoMergePullRequest",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/empty"
    )

  val repoCancelScheduledAutoMerge: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/pulls/{index}/merge",
      operationId = "repoCancelScheduledAutoMerge",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val repoUpdatePullRequest: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/update",
      operationId = "repoUpdatePullRequest",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("style", "query", required = false)
      ),
      response = "#/responses/empty"
    )

  val repoResolvePullReviewComment: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/comments/{id}/resolve",
      operationId = "repoResolvePullReviewComment",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val repoUnresolvePullReviewComment: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/comments/{id}/unresolve",
      operationId = "repoUnresolvePullReviewComment",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val repoCreatePullReviewRequests: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/requested_reviewers",
      operationId = "repoCreatePullReviewRequests",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = true)
      ),
      response = "#/responses/PullReviewList"
    )

  val repoDeletePullReviewRequests: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/pulls/{index}/requested_reviewers",
      operationId = "repoDeletePullReviewRequests",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = true)
      ),
      response = "#/responses/empty"
    )

  val repoListPullReviews: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews",
      operationId = "repoListPullReviews",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/PullReviewList"
    )

  val repoCreatePullReview: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews",
      operationId = "repoCreatePullReview",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = true)
      ),
      response = "#/responses/PullReview"
    )

  val repoGetPullReview: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}",
      operationId = "repoGetPullReview",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/PullReview"
    )

  val repoSubmitPullReview: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}",
      operationId = "repoSubmitPullReview",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true),
        GiteaParameter("body", "body", required = true)
      ),
      response = "#/responses/PullReview"
    )

  val repoDeletePullReview: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}",
      operationId = "repoDeletePullReview",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val repoGetPullReviewComments: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}/comments",
      operationId = "repoGetPullReviewComments",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/PullReviewCommentList"
    )

  val repoDismissPullReview: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}/dismissals",
      operationId = "repoDismissPullReview",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true),
        GiteaParameter("body", "body", required = true)
      ),
      response = "#/responses/PullReview"
    )

  val repoUnDismissPullReview: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/pulls/{index}/reviews/{id}/undismissals",
      operationId = "repoUnDismissPullReview",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/PullReview"
    )

  val repoDownloadPullDiffOrPatch: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}.{diffType}",
      operationId = "repoDownloadPullDiffOrPatch",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("diffType", "path", required = true),
        GiteaParameter("binary", "query", required = false)
      ),
      response = "#/responses/string"
    )

  val repoGetPullRequestFiles: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}/files",
      operationId = "repoGetPullRequestFiles",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("skip-to", "query", required = false),
        GiteaParameter("whitespace", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/ChangedFileList"
    )

  val repoGetPullRequestCommits: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/pulls/{index}/commits",
      operationId = "repoGetPullRequestCommits",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false),
        GiteaParameter("verification", "query", required = false),
        GiteaParameter("files", "query", required = false)
      ),
      response = "#/responses/CommitList"
    )

  val issueListIssues: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues",
      operationId = "issueListIssues",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("state", "query", required = false),
        GiteaParameter("labels", "query", required = false),
        GiteaParameter("q", "query", required = false),
        GiteaParameter("type", "query", required = false),
        GiteaParameter("milestones", "query", required = false),
        GiteaParameter("since", "query", required = false),
        GiteaParameter("before", "query", required = false),
        GiteaParameter("created_by", "query", required = false),
        GiteaParameter("assigned_by", "query", required = false),
        GiteaParameter("mentioned_by", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/IssueList"
    )

  val repoListPinnedIssues: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/pinned",
      operationId = "repoListPinnedIssues",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true)
      ),
      response = "#/responses/IssueList"
    )

  val issueGetIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}",
      operationId = "issueGetIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/Issue"
    )

  val issueDelete: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}",
      operationId = "issueDelete",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val pinIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/pin",
      operationId = "pinIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val unpinIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/pin",
      operationId = "unpinIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val moveIssuePin: GiteaEndpoint =
    GiteaEndpoint(
      method = "PATCH",
      path = "/repos/{owner}/{repo}/issues/{index}/pin/{position}",
      operationId = "moveIssuePin",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("position", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueCreateIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues",
      operationId = "issueCreateIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Issue"
    )

  val issueEditIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "PATCH",
      path = "/repos/{owner}/{repo}/issues/{index}",
      operationId = "issueEditIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Issue"
    )

  val issueCreateComment: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/comments",
      operationId = "issueCreateComment",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Comment"
    )

  val issueGetComments: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/comments",
      operationId = "issueGetComments",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("since", "query", required = false),
        GiteaParameter("before", "query", required = false)
      ),
      response = "#/responses/CommentList"
    )

  val issueGetRepoComments: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/comments",
      operationId = "issueGetRepoComments",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("since", "query", required = false),
        GiteaParameter("before", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/CommentList"
    )

  val issueGetComment: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/comments/{id}",
      operationId = "issueGetComment",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/Comment"
    )

  val issueEditComment: GiteaEndpoint =
    GiteaEndpoint(
      method = "PATCH",
      path = "/repos/{owner}/{repo}/issues/comments/{id}",
      operationId = "issueEditComment",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Comment"
    )

  val issueDeleteComment: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/comments/{id}",
      operationId = "issueDeleteComment",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueGetCommentReactions: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/comments/{id}/reactions",
      operationId = "issueGetCommentReactions",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/ReactionList"
    )

  val issuePostCommentReaction: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/comments/{id}/reactions",
      operationId = "issuePostCommentReaction",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true),
        GiteaParameter("content", "body", required = false)
      ),
      response = "#/responses/Reaction"
    )

  val issueDeleteCommentReaction: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/comments/{id}/reactions",
      operationId = "issueDeleteCommentReaction",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("id", "path", required = true),
        GiteaParameter("content", "body", required = false)
      ),
      response = "#/responses/empty"
    )

  val issueListBlocks: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/blocks",
      operationId = "issueListBlocks",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/IssueList"
    )

  val issueCreateIssueBlocking: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/blocks",
      operationId = "issueCreateIssueBlocking",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Issue"
    )

  val issueRemoveIssueBlocking: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/blocks",
      operationId = "issueRemoveIssueBlocking",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Issue"
    )

  val issueListIssueDependencies: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/dependencies",
      operationId = "issueListIssueDependencies",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/IssueList"
    )

  val issueCreateIssueDependencies: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/dependencies",
      operationId = "issueCreateIssueDependencies",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Issue"
    )

  val issueRemoveIssueDependencies: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/dependencies",
      operationId = "issueRemoveIssueDependencies",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/Issue"
    )

  val issueGetLabels: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/labels",
      operationId = "issueGetLabels",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/LabelList"
    )

  val issueReplaceLabels: GiteaEndpoint =
    GiteaEndpoint(
      method = "PUT",
      path = "/repos/{owner}/{repo}/issues/{index}/labels",
      operationId = "issueReplaceLabels",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/LabelList"
    )

  val issueAddLabel: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/labels",
      operationId = "issueAddLabel",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/LabelList"
    )

  val issueClearLabels: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/labels",
      operationId = "issueClearLabels",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueRemoveLabel: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/labels/{id}",
      operationId = "issueRemoveLabel",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueLockIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "PUT",
      path = "/repos/{owner}/{repo}/issues/{index}/lock",
      operationId = "issueLockIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/empty"
    )

  val issueUnlockIssue: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/lock",
      operationId = "issueUnlockIssue",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueEditIssueDeadline: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/deadline",
      operationId = "issueEditIssueDeadline",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/IssueDeadline"
    )

  val issueGetIssueReactions: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/reactions",
      operationId = "issueGetIssueReactions",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/ReactionList"
    )

  val issuePostIssueReaction: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/reactions",
      operationId = "issuePostIssueReaction",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("content", "body", required = false)
      ),
      response = "#/responses/Reaction"
    )

  val issueDeleteIssueReaction: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/reactions",
      operationId = "issueDeleteIssueReaction",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("content", "body", required = false)
      ),
      response = "#/responses/empty"
    )

  val issueDeleteStopWatch: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/stopwatch/delete",
      operationId = "issueDeleteStopWatch",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueStartStopWatch: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/stopwatch/start",
      operationId = "issueStartStopWatch",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueStopStopWatch: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/stopwatch/stop",
      operationId = "issueStopStopWatch",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueSubscriptions: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/subscriptions",
      operationId = "issueSubscriptions",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/UserList"
    )

  val issueCheckSubscription: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/subscriptions/check",
      operationId = "issueCheckSubscription",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/WatchInfo"
    )

  val issueAddSubscription: GiteaEndpoint =
    GiteaEndpoint(
      method = "PUT",
      path = "/repos/{owner}/{repo}/issues/{index}/subscriptions/{user}",
      operationId = "issueAddSubscription",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("user", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueDeleteSubscription: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/subscriptions/{user}",
      operationId = "issueDeleteSubscription",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("user", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueTrackedTimes: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/repos/{owner}/{repo}/issues/{index}/times",
      operationId = "issueTrackedTimes",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("user", "query", required = false),
        GiteaParameter("since", "query", required = false),
        GiteaParameter("before", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/TrackedTimeList"
    )

  val issueAddTime: GiteaEndpoint =
    GiteaEndpoint(
      method = "POST",
      path = "/repos/{owner}/{repo}/issues/{index}/times",
      operationId = "issueAddTime",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("body", "body", required = false)
      ),
      response = "#/responses/TrackedTime"
    )

  val issueResetTime: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/times",
      operationId = "issueResetTime",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val issueDeleteTime: GiteaEndpoint =
    GiteaEndpoint(
      method = "DELETE",
      path = "/repos/{owner}/{repo}/issues/{index}/times/{id}",
      operationId = "issueDeleteTime",
      parameters = List(
        GiteaParameter("owner", "path", required = true),
        GiteaParameter("repo", "path", required = true),
        GiteaParameter("index", "path", required = true),
        GiteaParameter("id", "path", required = true)
      ),
      response = "#/responses/empty"
    )

  val userGetStopWatches: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/user/stopwatches",
      operationId = "userGetStopWatches",
      parameters = List(
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/StopWatchList"
    )

  val notifyGetList: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/notifications",
      operationId = "notifyGetList",
      parameters = List(
        GiteaParameter("all", "query", required = false),
        GiteaParameter("status-types", "query", required = false),
        GiteaParameter("subject-type", "query", required = false),
        GiteaParameter("since", "query", required = false),
        GiteaParameter("before", "query", required = false),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/NotificationThreadList"
    )

  val notifyNewAvailable: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/notifications/new",
      operationId = "notifyNewAvailable",
      parameters = Nil,
      response = "#/responses/NotificationCount"
    )

  val notifyGetThread: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/notifications/threads/{id}",
      operationId = "notifyGetThread",
      parameters = List(GiteaParameter("id", "path", required = true)),
      response = "#/responses/NotificationThread"
    )

  val userListFollowers: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/users/{username}/followers",
      operationId = "userListFollowers",
      parameters = List(
        GiteaParameter("username", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/UserList"
    )

  val userListFollowing: GiteaEndpoint =
    GiteaEndpoint(
      method = "GET",
      path = "/users/{username}/following",
      operationId = "userListFollowing",
      parameters = List(
        GiteaParameter("username", "path", required = true),
        GiteaParameter("page", "query", required = false),
        GiteaParameter("limit", "query", required = false)
      ),
      response = "#/responses/UserList"
    )
