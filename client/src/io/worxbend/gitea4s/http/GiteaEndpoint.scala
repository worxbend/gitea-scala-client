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
