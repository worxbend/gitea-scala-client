package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.model.{
  AddTimeOption,
  AnnotatedTag,
  Auth,
  Branch,
  ChangedFile,
  Comment,
  Commit,
  CommitDiffType,
  CommitStatus,
  CombinedStatus,
  ContentsResponse,
  CreateIssue,
  CreateIssueComment,
  CreatePullRequestOption,
  CreatePullReviewOptions,
  CreateStatusOption,
  DismissPullReviewOptions,
  EditDeadlineOption,
  EditIssueComment,
  EditIssue,
  EditPullRequestOption,
  EditReactionOption,
  GitBlobResponse,
  GitTreeResponse,
  Issue,
  IssueDeadline,
  IssueLabelsOption,
  IssueMeta,
  IssueState,
  Label,
  LockIssueOption,
  MergePullRequestOption,
  NewIssuePinsAllowed,
  Note,
  NotificationCount,
  NotificationThread,
  Organization,
  Page,
  PullRequest,
  PullReview,
  PullReviewComment,
  PullReviewRequestOptions,
  Reaction,
  Reference,
  Release,
  Repository,
  StopWatch,
  SubmitPullReviewOptions,
  Tag,
  TrackedTime,
  User,
  WatchInfo
}
import sttp.client4.*
import sttp.model.{MediaType, Method, Uri}
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

  def userStopwatches(config: GiteaConfig, page: Int = 1): GiteaRequest[Page[StopWatch]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.userGetStopWatches,
      List("user", "stopwatches"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[StopWatch](response, page, pageSize)
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

  def repoNewPinAllowed(config: GiteaConfig, owner: String, repo: String): GiteaRequest[NewIssuePinsAllowed] =
    get(
      config,
      GiteaEndpoints.repoNewPinAllowed,
      List("repos", owner, repo, "new_pin_allowed"),
      Nil,
      GiteaResponseMapper.decodeJson[NewIssuePinsAllowed]
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

  def repoCombinedStatusByRef(
      config: GiteaConfig,
      owner: String,
      repo: String,
      ref: String,
      params: CombinedStatusParams = CombinedStatusParams.default
  ): GiteaRequest[CombinedStatus] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.repoGetCombinedStatusByRef,
      List("repos", owner, repo, "commits", ref, "status"),
      pageQuery(page, pageSize),
      GiteaResponseMapper.decodeJson[CombinedStatus]
    )

  def repoStatusesByRef(
      config: GiteaConfig,
      owner: String,
      repo: String,
      ref: String,
      params: CommitStatusListParams = CommitStatusListParams.default
  ): GiteaRequest[Page[CommitStatus]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.repoListStatusesByRef,
      List("repos", owner, repo, "commits", ref, "statuses"),
      commitStatusQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[CommitStatus](response, page, pageSize)
    )

  def repoStatuses(
      config: GiteaConfig,
      owner: String,
      repo: String,
      sha: String,
      params: CommitStatusListParams = CommitStatusListParams.default
  ): GiteaRequest[Page[CommitStatus]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.repoListStatuses,
      List("repos", owner, repo, "statuses", sha),
      commitStatusQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[CommitStatus](response, page, pageSize)
    )

  def createStatus(
      config: GiteaConfig,
      owner: String,
      repo: String,
      sha: String,
      body: CreateStatusOption
  ): GiteaRequest[CommitStatus] =
    postJson(
      config,
      GiteaEndpoints.repoCreateStatus,
      List("repos", owner, repo, "statuses", sha),
      body.toJson,
      GiteaResponseMapper.decodeJson[CommitStatus]
    )

  def repoCommitPullRequest(config: GiteaConfig, owner: String, repo: String, sha: String): GiteaRequest[PullRequest] =
    get(
      config,
      GiteaEndpoints.repoGetCommitPullRequest,
      List("repos", owner, repo, "commits", sha, "pull"),
      Nil,
      GiteaResponseMapper.decodeJson[PullRequest]
    )

  def repoSingleCommit(
      config: GiteaConfig,
      owner: String,
      repo: String,
      sha: String,
      params: SingleCommitParams = SingleCommitParams.default
  ): GiteaRequest[Commit] =
    get(
      config,
      GiteaEndpoints.repoGetSingleCommit,
      List("repos", owner, repo, "git", "commits", sha),
      singleCommitQuery(params),
      GiteaResponseMapper.decodeJson[Commit]
    )

  def repoCommitDiffOrPatch(
      config: GiteaConfig,
      owner: String,
      repo: String,
      sha: String,
      diffType: CommitDiffType
  ): GiteaRequest[String] =
    get(
      config,
      GiteaEndpoints.repoDownloadCommitDiffOrPatch,
      List("repos", owner, repo, "git", "commits", s"$sha.${diffType.pathValue}"),
      Nil,
      GiteaResponseMapper.decodeString,
      accept = MediaType.TextPlain.toString
    )

  def repoCommitNote(
      config: GiteaConfig,
      owner: String,
      repo: String,
      sha: String,
      params: CommitNoteParams = CommitNoteParams.default
  ): GiteaRequest[Note] =
    get(
      config,
      GiteaEndpoints.repoGetNote,
      List("repos", owner, repo, "git", "notes", sha),
      commitNoteQuery(params),
      GiteaResponseMapper.decodeJson[Note]
    )

  def gitTree(
      config: GiteaConfig,
      owner: String,
      repo: String,
      sha: String,
      params: GitTreeParams = GitTreeParams.default
  ): GiteaRequest[GitTreeResponse] =
    get(
      config,
      GiteaEndpoints.getTree,
      List("repos", owner, repo, "git", "trees", sha),
      gitTreeQuery(params),
      GiteaResponseMapper.decodeJson[GitTreeResponse]
    )

  def gitBlob(config: GiteaConfig, owner: String, repo: String, sha: String): GiteaRequest[GitBlobResponse] =
    get(
      config,
      GiteaEndpoints.getBlob,
      List("repos", owner, repo, "git", "blobs", sha),
      Nil,
      GiteaResponseMapper.decodeJson[GitBlobResponse]
    )

  def annotatedTag(config: GiteaConfig, owner: String, repo: String, sha: String): GiteaRequest[AnnotatedTag] =
    get(
      config,
      GiteaEndpoints.getAnnotatedTag,
      List("repos", owner, repo, "git", "tags", sha),
      Nil,
      GiteaResponseMapper.decodeJson[AnnotatedTag]
    )

  def repoListAllGitRefs(config: GiteaConfig, owner: String, repo: String): GiteaRequest[zio.Chunk[Reference]] =
    get(
      config,
      GiteaEndpoints.repoListAllGitRefs,
      List("repos", owner, repo, "git", "refs"),
      Nil,
      GiteaResponseMapper.decodeChunk[Reference]
    )

  def repoListGitRefs(
      config: GiteaConfig,
      owner: String,
      repo: String,
      ref: String
  ): GiteaRequest[zio.Chunk[Reference]] =
    get(
      config,
      GiteaEndpoints.repoListGitRefs,
      List("repos", owner, repo, "git", "refs", ref),
      Nil,
      GiteaResponseMapper.decodeChunk[Reference]
    )

  def repoContentsList(
      config: GiteaConfig,
      owner: String,
      repo: String,
      params: ContentsParams = ContentsParams.default
  ): GiteaRequest[zio.Chunk[ContentsResponse]] =
    get(
      config,
      GiteaEndpoints.repoGetContentsList,
      List("repos", owner, repo, "contents"),
      contentsQuery(params),
      GiteaResponseMapper.decodeChunk[ContentsResponse]
    )

  def repoContents(
      config: GiteaConfig,
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): GiteaRequest[ContentsResponse] =
    get(
      config,
      GiteaEndpoints.repoGetContents,
      List("repos", owner, repo, "contents", filepath),
      contentsQuery(params),
      GiteaResponseMapper.decodeJson[ContentsResponse]
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

  def createPullRequest(
      config: GiteaConfig,
      owner: String,
      repo: String,
      body: CreatePullRequestOption
  ): GiteaRequest[PullRequest] =
    postJson(
      config,
      GiteaEndpoints.repoCreatePullRequest,
      List("repos", owner, repo, "pulls"),
      body.toJson,
      GiteaResponseMapper.decodeJson[PullRequest]
    )

  def pinnedPullRequests(config: GiteaConfig, owner: String, repo: String): GiteaRequest[zio.Chunk[PullRequest]] =
    get(
      config,
      GiteaEndpoints.repoListPinnedPullRequests,
      List("repos", owner, repo, "pulls", "pinned"),
      Nil,
      GiteaResponseMapper.decodeChunk[PullRequest]
    )

  def repoPullRequestByBaseHead(
      config: GiteaConfig,
      owner: String,
      repo: String,
      base: String,
      head: String
  ): GiteaRequest[PullRequest] =
    get(
      config,
      GiteaEndpoints.repoGetPullRequestByBaseHead,
      List("repos", owner, repo, "pulls", base, head),
      Nil,
      GiteaResponseMapper.decodeJson[PullRequest]
    )

  def repoPullRequest(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[PullRequest] =
    get(
      config,
      GiteaEndpoints.repoGetPullRequest,
      List("repos", owner, repo, "pulls", index.toString),
      Nil,
      GiteaResponseMapper.decodeJson[PullRequest]
    )

  def editPullRequest(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: EditPullRequestOption
  ): GiteaRequest[PullRequest] =
    patchJson(
      config,
      GiteaEndpoints.repoEditPullRequest,
      List("repos", owner, repo, "pulls", index.toString),
      body.toJson,
      GiteaResponseMapper.decodeJson[PullRequest]
    )

  def repoPullRequestIsMerged(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Boolean] =
    get(
      config,
      GiteaEndpoints.repoPullRequestIsMerged,
      List("repos", owner, repo, "pulls", index.toString, "merge"),
      Nil,
      GiteaResponseMapper.decodeNoContentOrNotFoundBoolean
    )

  def mergePullRequest(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: MergePullRequestOption
  ): GiteaRequest[Unit] =
    postJson(
      config,
      GiteaEndpoints.repoMergePullRequest,
      List("repos", owner, repo, "pulls", index.toString, "merge"),
      body.toJson,
      GiteaResponseMapper.decodeUnit
    )

  def cancelScheduledAutoMerge(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.repoCancelScheduledAutoMerge,
      List("repos", owner, repo, "pulls", index.toString, "merge"),
      GiteaResponseMapper.decodeUnit
    )

  def updatePullRequest(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      style: PullRequestUpdateStyle
  ): GiteaRequest[Unit] =
    post(
      config,
      GiteaEndpoints.repoUpdatePullRequest,
      List("repos", owner, repo, "pulls", index.toString, "update"),
      List("style" -> style.queryValue),
      GiteaResponseMapper.decodeUnit
    )

  def resolvePullReviewComment(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    post(
      config,
      GiteaEndpoints.repoResolvePullReviewComment,
      List("repos", owner, repo, "pulls", "comments", id.toString, "resolve"),
      GiteaResponseMapper.decodeUnit
    )

  def unresolvePullReviewComment(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    post(
      config,
      GiteaEndpoints.repoUnresolvePullReviewComment,
      List("repos", owner, repo, "pulls", "comments", id.toString, "unresolve"),
      GiteaResponseMapper.decodeUnit
    )

  def createPullReviewRequests(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): GiteaRequest[zio.Chunk[PullReview]] =
    postJson(
      config,
      GiteaEndpoints.repoCreatePullReviewRequests,
      List("repos", owner, repo, "pulls", index.toString, "requested_reviewers"),
      body.toJson,
      GiteaResponseMapper.decodeChunk[PullReview]
    )

  def deletePullReviewRequests(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): GiteaRequest[Unit] =
    deleteJson(
      config,
      GiteaEndpoints.repoDeletePullReviewRequests,
      List("repos", owner, repo, "pulls", index.toString, "requested_reviewers"),
      body.toJson,
      GiteaResponseMapper.decodeUnit
    )

  def repoPullReviews(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      page: Int = 1
  ): GiteaRequest[Page[PullReview]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.repoListPullReviews,
      List("repos", owner, repo, "pulls", index.toString, "reviews"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[PullReview](response, page, pageSize)
    )

  def createPullReview(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: CreatePullReviewOptions
  ): GiteaRequest[PullReview] =
    postJson(
      config,
      GiteaEndpoints.repoCreatePullReview,
      List("repos", owner, repo, "pulls", index.toString, "reviews"),
      body.toJson,
      GiteaResponseMapper.decodeJson[PullReview]
    )

  def repoPullReview(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): GiteaRequest[PullReview] =
    get(
      config,
      GiteaEndpoints.repoGetPullReview,
      List("repos", owner, repo, "pulls", index.toString, "reviews", id.toString),
      Nil,
      GiteaResponseMapper.decodeJson[PullReview]
    )

  def submitPullReview(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: SubmitPullReviewOptions
  ): GiteaRequest[PullReview] =
    postJson(
      config,
      GiteaEndpoints.repoSubmitPullReview,
      List("repos", owner, repo, "pulls", index.toString, "reviews", id.toString),
      body.toJson,
      GiteaResponseMapper.decodeJson[PullReview]
    )

  def deletePullReview(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.repoDeletePullReview,
      List("repos", owner, repo, "pulls", index.toString, "reviews", id.toString),
      GiteaResponseMapper.decodeUnit
    )

  def dismissPullReview(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: DismissPullReviewOptions
  ): GiteaRequest[PullReview] =
    postJson(
      config,
      GiteaEndpoints.repoDismissPullReview,
      List("repos", owner, repo, "pulls", index.toString, "reviews", id.toString, "dismissals"),
      body.toJson,
      GiteaResponseMapper.decodeJson[PullReview]
    )

  def undismissPullReview(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): GiteaRequest[PullReview] =
    post(
      config,
      GiteaEndpoints.repoUnDismissPullReview,
      List("repos", owner, repo, "pulls", index.toString, "reviews", id.toString, "undismissals"),
      GiteaResponseMapper.decodeJson[PullReview]
    )

  def repoPullReviewComments(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): GiteaRequest[zio.Chunk[PullReviewComment]] =
    get(
      config,
      GiteaEndpoints.repoGetPullReviewComments,
      List("repos", owner, repo, "pulls", index.toString, "reviews", id.toString, "comments"),
      Nil,
      GiteaResponseMapper.decodeChunk[PullReviewComment]
    )

  def repoPullRequestDiffOrPatch(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      diffType: PullRequestDiffType,
      binary: Option[Boolean] = None
  ): GiteaRequest[String] =
    get(
      config,
      GiteaEndpoints.repoDownloadPullDiffOrPatch,
      List("repos", owner, repo, "pulls", s"$index.${diffType.pathValue}"),
      binary.map(value => List("binary" -> value.toString)).getOrElse(Nil),
      GiteaResponseMapper.decodeString,
      accept = MediaType.TextPlain.toString
    )

  def repoPullRequestFiles(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestFilesParams = PullRequestFilesParams.default
  ): GiteaRequest[Page[ChangedFile]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.repoGetPullRequestFiles,
      List("repos", owner, repo, "pulls", index.toString, "files"),
      pullRequestFilesQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[ChangedFile](response, page, pageSize)
    )

  def repoPullRequestCommits(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestCommitsParams = PullRequestCommitsParams.default
  ): GiteaRequest[Page[Commit]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.repoGetPullRequestCommits,
      List("repos", owner, repo, "pulls", index.toString, "commits"),
      pullRequestCommitsQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[Commit](response, page, pageSize)
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

  def pinnedIssues(config: GiteaConfig, owner: String, repo: String): GiteaRequest[zio.Chunk[Issue]] =
    get(
      config,
      GiteaEndpoints.repoListPinnedIssues,
      List("repos", owner, repo, "issues", "pinned"),
      Nil,
      GiteaResponseMapper.decodeChunk[Issue]
    )

  def issue(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Issue] =
    get(
      config,
      GiteaEndpoints.issueGetIssue,
      List("repos", owner, repo, "issues", index.toString),
      Nil,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def deleteIssue(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueDelete,
      List("repos", owner, repo, "issues", index.toString),
      GiteaResponseMapper.decodeUnit
    )

  def pinIssue(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    post(
      config,
      GiteaEndpoints.pinIssue,
      List("repos", owner, repo, "issues", index.toString, "pin"),
      GiteaResponseMapper.decodeUnit
    )

  def unpinIssue(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.unpinIssue,
      List("repos", owner, repo, "issues", index.toString, "pin"),
      GiteaResponseMapper.decodeUnit
    )

  def moveIssuePin(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      position: Long
  ): GiteaRequest[Unit] =
    patch(
      config,
      GiteaEndpoints.moveIssuePin,
      List("repos", owner, repo, "issues", index.toString, "pin", position.toString),
      GiteaResponseMapper.decodeUnit
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

  def issueComments(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      params: IssueCommentListParams = IssueCommentListParams.default
  ): GiteaRequest[zio.Chunk[Comment]] =
    get(
      config,
      GiteaEndpoints.issueGetComments,
      List("repos", owner, repo, "issues", index.toString, "comments"),
      issueCommentQuery(params),
      GiteaResponseMapper.decodeChunk[Comment]
    )

  def repoIssueComments(
      config: GiteaConfig,
      owner: String,
      repo: String,
      params: RepositoryCommentListParams = RepositoryCommentListParams.default
  ): GiteaRequest[Page[Comment]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.issueGetRepoComments,
      List("repos", owner, repo, "issues", "comments"),
      repositoryCommentQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[Comment](response, page, pageSize)
    )

  def issueComment(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Comment] =
    get(
      config,
      GiteaEndpoints.issueGetComment,
      List("repos", owner, repo, "issues", "comments", id.toString),
      Nil,
      GiteaResponseMapper.decodeJson[Comment]
    )

  def editIssueComment(
      config: GiteaConfig,
      owner: String,
      repo: String,
      id: Long,
      body: EditIssueComment
  ): GiteaRequest[Comment] =
    patchJson(
      config,
      GiteaEndpoints.issueEditComment,
      List("repos", owner, repo, "issues", "comments", id.toString),
      body.toJson,
      GiteaResponseMapper.decodeJson[Comment]
    )

  def deleteIssueComment(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueDeleteComment,
      List("repos", owner, repo, "issues", "comments", id.toString),
      GiteaResponseMapper.decodeUnit
    )

  def issueCommentReactions(
      config: GiteaConfig,
      owner: String,
      repo: String,
      id: Long
  ): GiteaRequest[zio.Chunk[Reaction]] =
    get(
      config,
      GiteaEndpoints.issueGetCommentReactions,
      List("repos", owner, repo, "issues", "comments", id.toString, "reactions"),
      Nil,
      GiteaResponseMapper.decodeChunk[Reaction]
    )

  def postIssueCommentReaction(
      config: GiteaConfig,
      owner: String,
      repo: String,
      id: Long,
      body: EditReactionOption
  ): GiteaRequest[Reaction] =
    postJson(
      config,
      GiteaEndpoints.issuePostCommentReaction,
      List("repos", owner, repo, "issues", "comments", id.toString, "reactions"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Reaction]
    )

  def deleteIssueCommentReaction(
      config: GiteaConfig,
      owner: String,
      repo: String,
      id: Long,
      body: EditReactionOption
  ): GiteaRequest[Unit] =
    deleteJson(
      config,
      GiteaEndpoints.issueDeleteCommentReaction,
      List("repos", owner, repo, "issues", "comments", id.toString, "reactions"),
      body.toJson,
      GiteaResponseMapper.decodeUnit
    )

  def issueBlocks(config: GiteaConfig, owner: String, repo: String, index: Long, page: Int = 1)
      : GiteaRequest[Page[Issue]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.issueListBlocks,
      List("repos", owner, repo, "issues", index.toString, "blocks"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Issue](response, page, pageSize)
    )

  def createIssueBlocking(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: IssueMeta
  ): GiteaRequest[Issue] =
    postJson(
      config,
      GiteaEndpoints.issueCreateIssueBlocking,
      List("repos", owner, repo, "issues", index.toString, "blocks"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def removeIssueBlocking(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: IssueMeta
  ): GiteaRequest[Issue] =
    deleteJson(
      config,
      GiteaEndpoints.issueRemoveIssueBlocking,
      List("repos", owner, repo, "issues", index.toString, "blocks"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def issueDependencies(config: GiteaConfig, owner: String, repo: String, index: Long, page: Int = 1)
      : GiteaRequest[Page[Issue]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.issueListIssueDependencies,
      List("repos", owner, repo, "issues", index.toString, "dependencies"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Issue](response, page, pageSize)
    )

  def createIssueDependency(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: IssueMeta
  ): GiteaRequest[Issue] =
    postJson(
      config,
      GiteaEndpoints.issueCreateIssueDependencies,
      List("repos", owner, repo, "issues", index.toString, "dependencies"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Issue]
    )

  def removeIssueDependency(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: IssueMeta
  ): GiteaRequest[Issue] =
    deleteJson(
      config,
      GiteaEndpoints.issueRemoveIssueDependencies,
      List("repos", owner, repo, "issues", index.toString, "dependencies"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Issue]
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

  def editIssueDeadline(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: EditDeadlineOption
  ): GiteaRequest[IssueDeadline] =
    postJson(
      config,
      GiteaEndpoints.issueEditIssueDeadline,
      List("repos", owner, repo, "issues", index.toString, "deadline"),
      body.toJson,
      GiteaResponseMapper.decodeJson[IssueDeadline]
    )

  def issueReactions(config: GiteaConfig, owner: String, repo: String, index: Long, page: Int = 1)
      : GiteaRequest[Page[Reaction]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.issueGetIssueReactions,
      List("repos", owner, repo, "issues", index.toString, "reactions"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[Reaction](response, page, pageSize)
    )

  def postIssueReaction(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: EditReactionOption
  ): GiteaRequest[Reaction] =
    postJson(
      config,
      GiteaEndpoints.issuePostIssueReaction,
      List("repos", owner, repo, "issues", index.toString, "reactions"),
      body.toJson,
      GiteaResponseMapper.decodeJson[Reaction]
    )

  def deleteIssueReaction(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: EditReactionOption
  ): GiteaRequest[Unit] =
    deleteJson(
      config,
      GiteaEndpoints.issueDeleteIssueReaction,
      List("repos", owner, repo, "issues", index.toString, "reactions"),
      body.toJson,
      GiteaResponseMapper.decodeUnit
    )

  def issueSubscriptions(config: GiteaConfig, owner: String, repo: String, index: Long, page: Int = 1)
      : GiteaRequest[Page[User]] =
    val pageSize = config.pageSize

    get(
      config,
      GiteaEndpoints.issueSubscriptions,
      List("repos", owner, repo, "issues", index.toString, "subscriptions"),
      pageQuery(page, pageSize),
      response => GiteaResponseMapper.decodePage[User](response, page, pageSize)
    )

  def issueSubscription(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[WatchInfo] =
    get(
      config,
      GiteaEndpoints.issueCheckSubscription,
      List("repos", owner, repo, "issues", index.toString, "subscriptions", "check"),
      Nil,
      GiteaResponseMapper.decodeJson[WatchInfo]
    )

  def addIssueSubscription(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      user: String
  ): GiteaRequest[Unit] =
    put(
      config,
      GiteaEndpoints.issueAddSubscription,
      List("repos", owner, repo, "issues", index.toString, "subscriptions", user),
      GiteaResponseMapper.decodeUnit
    )

  def deleteIssueSubscription(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      user: String
  ): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueDeleteSubscription,
      List("repos", owner, repo, "issues", index.toString, "subscriptions", user),
      GiteaResponseMapper.decodeUnit
    )

  def issueTrackedTimes(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      params: IssueTrackedTimeListParams = IssueTrackedTimeListParams.default
  ): GiteaRequest[Page[TrackedTime]] =
    val page = params.page.getOrElse(1)
    val pageSize = params.limit.getOrElse(config.pageSize)

    get(
      config,
      GiteaEndpoints.issueTrackedTimes,
      List("repos", owner, repo, "issues", index.toString, "times"),
      trackedTimeQuery(params, page, pageSize),
      response => GiteaResponseMapper.decodePage[TrackedTime](response, page, pageSize)
    )

  def addIssueTrackedTime(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      body: AddTimeOption
  ): GiteaRequest[TrackedTime] =
    postJson(
      config,
      GiteaEndpoints.issueAddTime,
      List("repos", owner, repo, "issues", index.toString, "times"),
      body.toJson,
      GiteaResponseMapper.decodeJson[TrackedTime]
    )

  def resetIssueTrackedTime(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueResetTime,
      List("repos", owner, repo, "issues", index.toString, "times"),
      GiteaResponseMapper.decodeUnit
    )

  def deleteIssueTrackedTime(
      config: GiteaConfig,
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueDeleteTime,
      List("repos", owner, repo, "issues", index.toString, "times", id.toString),
      GiteaResponseMapper.decodeUnit
    )

  def startIssueStopwatch(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    post(
      config,
      GiteaEndpoints.issueStartStopWatch,
      List("repos", owner, repo, "issues", index.toString, "stopwatch", "start"),
      GiteaResponseMapper.decodeUnit
    )

  def stopIssueStopwatch(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    post(
      config,
      GiteaEndpoints.issueStopStopWatch,
      List("repos", owner, repo, "issues", index.toString, "stopwatch", "stop"),
      GiteaResponseMapper.decodeUnit
    )

  def deleteIssueStopwatch(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[Unit] =
    delete(
      config,
      GiteaEndpoints.issueDeleteStopWatch,
      List("repos", owner, repo, "issues", index.toString, "stopwatch", "delete"),
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
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A],
      accept: String = MediaType.ApplicationJson.toString
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .get(apiUri(config.baseUrl, path, query))
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config, accept)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def post[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      query: List[(String, String)],
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .post(apiUri(config.baseUrl, path, query))
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def post[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    post(config, endpoint, path, Nil, decode)

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

  private def put[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .put(apiUri(config.baseUrl, path, Nil))
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

  private def patch[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .patch(apiUri(config.baseUrl, path, Nil))
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

  private def deleteJson[A](
      config: GiteaConfig,
      endpoint: GiteaEndpoint,
      path: List[String],
      json: String,
      decode: Response[String] => Either[io.worxbend.gitea4s.error.GiteaError, A]
  ): GiteaRequest[A] =
    GiteaRequest(
      endpoint = endpoint,
      request = basicRequest
        .method(Method.DELETE, apiUri(config.baseUrl, path, Nil))
        .body(json)
        .contentType(MediaType.ApplicationJson)
        .response(asStringAlways)
        .readTimeout(config.timeout)
        .headers(commonHeaders(config)),
      decode = decode,
      retryable = GiteaRequest.isReadOnly(endpoint)
    )

  private def apiUri(baseUrl: Uri, path: List[String], query: List[(String, String)]): Uri =
    baseUrl.addPath(List("api", "v1") ++ path).addParams(query*)

  private def commonHeaders(config: GiteaConfig, accept: String = MediaType.ApplicationJson.toString): Map[String, String] =
    List(
      Some("Accept" -> accept),
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

  private def pullRequestFilesQuery(
      params: PullRequestFilesParams,
      page: Int,
      pageSize: Int
  ): List[(String, String)] =
    List(
      params.skipTo.map("skip-to" -> _),
      params.whitespace.map(value => "whitespace" -> value.queryValue),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten

  private def pullRequestCommitsQuery(
      params: PullRequestCommitsParams,
      page: Int,
      pageSize: Int
  ): List[(String, String)] =
    List(
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString),
      params.verification.map(value => "verification" -> value.toString),
      params.files.map(value => "files" -> value.toString)
    ).flatten

  private def commitStatusQuery(
      params: CommitStatusListParams,
      page: Int,
      pageSize: Int
  ): List[(String, String)] =
    List(
      params.sort.map(sort => "sort" -> sort.queryValue),
      params.state.map(state => "state" -> state.queryValue),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten

  private def singleCommitQuery(params: SingleCommitParams): List[(String, String)] =
    List(
      params.stat.map(value => "stat" -> value.toString),
      params.verification.map(value => "verification" -> value.toString),
      params.files.map(value => "files" -> value.toString)
    ).flatten

  private def commitNoteQuery(params: CommitNoteParams): List[(String, String)] =
    List(
      params.verification.map(value => "verification" -> value.toString),
      params.files.map(value => "files" -> value.toString)
    ).flatten

  private def gitTreeQuery(params: GitTreeParams): List[(String, String)] =
    List(
      params.recursive.map(value => "recursive" -> value.toString),
      params.page.map(value => "page" -> value.toString),
      params.perPage.map(value => "per_page" -> value.toString)
    ).flatten

  private def contentsQuery(params: ContentsParams): List[(String, String)] =
    params.ref.map("ref" -> _).toList

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

  private def issueCommentQuery(params: IssueCommentListParams): List[(String, String)] =
    List(
      params.since.map(value => "since" -> value.toString),
      params.before.map(value => "before" -> value.toString)
    ).flatten

  private def repositoryCommentQuery(
      params: RepositoryCommentListParams,
      page: Int,
      pageSize: Int
  ): List[(String, String)] =
    List(
      params.since.map(value => "since" -> value.toString),
      params.before.map(value => "before" -> value.toString),
      Some("page" -> page.toString),
      Some("limit" -> pageSize.toString)
    ).flatten

  private def trackedTimeQuery(
      params: IssueTrackedTimeListParams,
      page: Int,
      pageSize: Int
  ): List[(String, String)] =
    List(
      params.user.map("user" -> _),
      params.since.map(value => "since" -> value.toString),
      params.before.map(value => "before" -> value.toString),
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
