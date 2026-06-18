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
