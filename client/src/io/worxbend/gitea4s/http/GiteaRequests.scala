package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.model.{Auth, Issue, IssueState, Organization, Page, Repository, User}
import sttp.client4.*
import sttp.model.{MediaType, Uri}

import java.nio.charset.StandardCharsets
import java.util.Base64

object GiteaRequests:
  def currentUser(config: GiteaConfig): GiteaRequest[User] =
    get(config, GiteaEndpoints.userGetCurrent, List("user"), Nil, GiteaResponseMapper.decodeJson[User])

  def user(config: GiteaConfig, username: String): GiteaRequest[User] =
    get(config, GiteaEndpoints.userGet, List("users", username), Nil, GiteaResponseMapper.decodeJson[User])

  def userSearch(config: GiteaConfig, params: UserSearchParams = UserSearchParams.default): GiteaRequest[Page[User]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.userSearch,
      List("users", "search"),
      userSearchQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodeUserSearchPage(response, page, pageSize)
    )

  def repository(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Repository] =
    get(
      config,
      GiteaEndpoints.repoGet,
      List("repos", owner, repo),
      Nil,
      GiteaResponseMapper.decodeJson[Repository]
    )

  def organization(config: GiteaConfig, org: String): GiteaRequest[Organization] =
    get(
      config,
      GiteaEndpoints.orgGet,
      List("orgs", org),
      Nil,
      GiteaResponseMapper.decodeJson[Organization]
    )

  def organizationMembers(config: GiteaConfig, org: String, page: Int = 1): GiteaRequest[Page[User]] =
    paginatedUsers(
      config = config,
      endpoint = GiteaEndpoints.orgListMembers,
      path = List("orgs", org, "members"),
      page = page
    )

  def userRepos(config: GiteaConfig, username: String, params: RepoListParams = RepoListParams.default)
      : GiteaRequest[Page[Repository]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.userListRepos,
      List("users", username, "repos"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Repository](response, page, pageSize)
    )

  def repoTopics(config: GiteaConfig, owner: String, repo: String, page: Int = 1): GiteaRequest[Page[String]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.repoListTopics,
      List("repos", owner, repo, "topics"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodeTopicNamesPage(response, page, pageSize)
    )

  def issues(config: GiteaConfig, owner: String, repo: String, params: IssueListParams = IssueListParams.default)
      : GiteaRequest[Page[Issue]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.issueListIssues,
      List("repos", owner, repo, "issues"),
      issueQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[Issue](response, page, pageSize)
    )

  def issue(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Issue] =
    get(
      config,
      GiteaEndpoints.issueGetIssue,
      List("repos", owner, repo, "issues", index.toString),
      Nil,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def userFollowers(config: GiteaConfig, username: String, page: Int = 1): GiteaRequest[Page[User]] =
    paginatedUsers(
      config = config,
      endpoint = GiteaEndpoints.userListFollowers,
      path = List("users", username, "followers"),
      page = page
    )

  def userFollowing(config: GiteaConfig, username: String, page: Int = 1): GiteaRequest[Page[User]] =
    paginatedUsers(
      config = config,
      endpoint = GiteaEndpoints.userListFollowing,
      path = List("users", username, "following"),
      page = page
    )

  def withJsonBody(config: GiteaConfig, request: Request[String], json: String): Request[String] =
    request
      .body(json)
      .contentType(MediaType.ApplicationJson)
      .headers(commonHeaders(config))

  private def get[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      query: List[(String, String)],
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .get(apiUri(config.baseUrl, path, query))
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode
    )

  private def apiUri(baseUrl: Uri, path: List[String], query: List[(String, String)]): Uri =
    baseUrl.addPath(List("api", "v1") ++ path).addParams(query*)

  private def commonHeaders(config: GiteaConfig): Map[String, String] =
    List(
      Some("Accept" -> MediaType.ApplicationJson.toString),
      authorizationHeader(config.auth),
      config.userAgent.map("User-Agent" -> _),
      config.otp.map("X-Gitea-OTP" -> _)
    ).flatten.toMap

  private def authorizationHeader(auth: Auth): Option[(String, String)] =
    auth match
      case Auth.Token(value) => Some("Authorization" -> s"token $value")
      case Auth.OAuth2(token) => Some("Authorization" -> s"Bearer $token")
      case Auth.Basic(username, password) =>
        val raw = s"$username:$password".getBytes(StandardCharsets.UTF_8)
        Some("Authorization" -> s"Basic ${Base64.getEncoder.encodeToString(raw)}")
      case Auth.Anonymous => None

  private def issueQuery(params: IssueListParams, page: Int, pageSize: Int): List[(String, String)] =
    List(
      params.state.map(state => "state" -> state.jsonValue),
      nonEmptyCsv("labels", params.labels),
      params.q.map("q" -> _),
      params.issueType.map(issueType => "type" -> issueType.queryValue),
      nonEmptyCsv("milestones", params.milestones),
      params.since.map(value => "since" -> value.toString),
      params.before.map(value => "before" -> value.toString),
      params.createdBy.map("created_by" -> _),
      params.assignedBy.map("assigned_by" -> _),
      params.mentionedBy.map("mentioned_by" -> _),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten

  private def userSearchQuery(params: UserSearchParams, page: Int, pageSize: Int): List[(String, String)] =
    List(
      params.q.map("q" -> _),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten

  private def nonEmptyCsv(name: String, values: zio.Chunk[String]): Option[(String, String)] =
    Option.when(values.nonEmpty)(name -> values.mkString(","))

  private def paginatedUsers(
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      page: Int
  ): GiteaRequest[Page[User]] =
    val pageSize = config.pageSize

    get(
      config,
      endpoint,
      path,
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[User](response, page, pageSize)
    )

  private def pageQuery(page: Int, pageSize: Int): List[(String, String)] =
    List(
      "page" -> page.toString,
      "limit" -> pageSize.toString
    )
