package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.model.{
  Auth,
  Branch,
  Comment,
  CreateIssue,
  CreateIssueComment,
  EditIssue,
  Issue,
  IssueLabelsOption,
  IssueState,
  Label,
  LockIssueOption,
  NotificationCount,
  NotificationThread,
  Organization,
  Page,
  PullRequest,
  Release,
  Repository,
  Tag,
  User
}
import sttp.client4.*
import sttp.model.{MediaType, Uri}
import zio.json.*

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

  def organizationPublicMembers(config: GiteaConfig, org: String, page: Int = 1): GiteaRequest[Page[User]] =
    paginatedUsers(
      config = config,
      endpoint = GiteaEndpoints.orgListPublicMembers,
      path = List("orgs", org, "public_members"),
      page = page
    )

  def organizationRepos(config: GiteaConfig, org: String, params: RepoListParams = RepoListParams.default)
      : GiteaRequest[Page[Repository]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.orgListRepos,
      List("orgs", org, "repos"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Repository](response, page, pageSize)
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

  def repoBranches(config: GiteaConfig, owner: String, repo: String, page: Int = 1): GiteaRequest[Page[Branch]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.repoListBranches,
      List("repos", owner, repo, "branches"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Branch](response, page, pageSize)
    )

  def repoTags(config: GiteaConfig, owner: String, repo: String, page: Int = 1): GiteaRequest[Page[Tag]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.repoListTags,
      List("repos", owner, repo, "tags"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Tag](response, page, pageSize)
    )

  def repoReleases(config: GiteaConfig, owner: String, repo: String, page: Int = 1): GiteaRequest[Page[Release]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.repoListReleases,
      List("repos", owner, repo, "releases"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Release](response, page, pageSize)
    )

  def repoRelease(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Release] =
    get(
      config,
      GiteaEndpoints.repoGetRelease,
      List("repos", owner, repo, "releases", id.toString),
      Nil,
      GiteaResponseMapper.decodeJson[Release]
    )

  def repoPullRequests(
      config: GiteaConfig,
      owner: String,
      repo: String,
      params: PullRequestListParams = PullRequestListParams.default
  ): GiteaRequest[Page[PullRequest]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.repoListPullRequests,
      List("repos", owner, repo, "pulls"),
      pullRequestQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[PullRequest](response, page, pageSize)
    )

  def repoPullRequest(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[PullRequest] =
    get(
      config,
      GiteaEndpoints.repoGetPullRequest,
      List("repos", owner, repo, "pulls", index.toString),
      Nil,
      GiteaResponseMapper.decodeJson[PullRequest]
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

  def createIssue(config: GiteaConfig, owner: String, repo: String, body: CreateIssue): GiteaRequest[Issue] =
    postJson(
      config,
      GiteaEndpoints.issueCreateIssue,
      List("repos", owner, repo, "issues"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def editIssue(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: EditIssue
  ): GiteaRequest[Issue] =
    patchJson(
      config,
      GiteaEndpoints.issueEditIssue,
      List("repos", owner, repo, "issues", index.toString),
      body.toJson,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def createIssueComment(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: CreateIssueComment
  ): GiteaRequest[Comment] =
    postJson(
      config,
      GiteaEndpoints.issueCreateComment,
      List("repos", owner, repo, "issues", index.toString, "comments"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Comment]
    )

  def issueLabels(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[zio.Chunk[Label]] =
    get(
      config,
      GiteaEndpoints.issueGetLabels,
      List("repos", owner, repo, "issues", index.toString, "labels"),
      Nil,
      GiteaResponseMapper.decodeChunk[Label]
    )

  def replaceIssueLabels(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: IssueLabelsOption
  ): GiteaRequest[zio.Chunk[Label]] =
    putJson(
      config,
      GiteaEndpoints.issueReplaceLabels,
      List("repos", owner, repo, "issues", index.toString, "labels"),
      body.toJson,
      GiteaResponseMapper.decodeChunk[Label]
    )

  def addIssueLabels(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: IssueLabelsOption
  ): GiteaRequest[zio.Chunk[Label]] =
    postJson(
      config,
      GiteaEndpoints.issueAddLabel,
      List("repos", owner, repo, "issues", index.toString, "labels"),
      body.toJson,
      GiteaResponseMapper.decodeChunk[Label]
    )

  def clearIssueLabels(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueClearLabels,
      List("repos", owner, repo, "issues", index.toString, "labels"),
      GiteaResponseMapper.decodeUnit
    )

  def removeIssueLabel(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueRemoveLabel,
      List("repos", owner, repo, "issues", index.toString, "labels", id.toString),
      GiteaResponseMapper.decodeUnit
    )

  def lockIssue(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: LockIssueOption
  ): GiteaRequest[Unit] =
    putJson(
      config,
      GiteaEndpoints.issueLockIssue,
      List("repos", owner, repo, "issues", index.toString, "lock"),
      body.toJson,
      GiteaResponseMapper.decodeUnit
    )

  def unlockIssue(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueUnlockIssue,
      List("repos", owner, repo, "issues", index.toString, "lock"),
      GiteaResponseMapper.decodeUnit
    )

  def notifications(config: GiteaConfig, params: NotificationListParams = NotificationListParams.default)
      : GiteaRequest[Page[NotificationThread]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.notifyGetList,
      List("notifications"),
      notificationQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[NotificationThread](response, page, pageSize)
    )

  def notificationCount(config: GiteaConfig): GiteaRequest[NotificationCount] =
    get(
      config,
      GiteaEndpoints.notifyNewAvailable,
      List("notifications", "new"),
      Nil,
      GiteaResponseMapper.decodeJson[NotificationCount]
    )

  def notificationThread(config: GiteaConfig, id: String): GiteaRequest[NotificationThread] =
    get(
      config,
      GiteaEndpoints.notifyGetThread,
      List("notifications", "threads", id),
      Nil,
      GiteaResponseMapper.decodeJson[NotificationThread]
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
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def postJson[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      json: String,
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .post(apiUri(config.baseUrl, path, Nil))
        .body(json)
        .contentType(MediaType.ApplicationJson)
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def putJson[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      json: String,
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .put(apiUri(config.baseUrl, path, Nil))
        .body(json)
        .contentType(MediaType.ApplicationJson)
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def patchJson[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      json: String,
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .patch(apiUri(config.baseUrl, path, Nil))
        .body(json)
        .contentType(MediaType.ApplicationJson)
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def delete[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .delete(apiUri(config.baseUrl, path, Nil))
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
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

  private def pullRequestQuery(params: PullRequestListParams, page: Int, pageSize: Int): List[(String, String)] =
    List(
      params.baseBranch.map("base_branch" -> _),
      params.state.map(state => "state" -> state.queryValue),
      params.sort.map(sort => "sort" -> sort.queryValue),
      params.milestone.map(value => "milestone" -> value.toString),
      params.poster.map("poster" -> _),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten ++ params.labels.map(label => "labels" -> label.toString)

  private def notificationQuery(params: NotificationListParams, page: Int, pageSize: Int): List[(String, String)] =
    List(
      params.all.map(value => "all" -> value.toString),
      params.since.map(value => "since" -> value.toString),
      params.before.map(value => "before" -> value.toString),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten ++
      params.statusTypes.map(statusType => "status-types" -> statusType.queryValue) ++
      params.subjectTypes.map(subjectType => "subject-type" -> subjectType.queryValue)

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
