package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  ArchiveParams,
  CombinedStatusParams,
  CommitNoteParams,
  CommitStatusListParams,
  ContentsParams,
  GitTreeParams,
  RepoListParams,
  SingleCommitParams
}
import io.worxbend.gitea4s.model.{
  AnnotatedTag,
  Branch,
  BranchProtection,
  CreateBranchProtectionOption,
  CreateBranchRepoOption,
  CreateForkOption,
  CreateRepoOption,
  CreateTagProtectionOption,
  Commit,
  CommitDiffType,
  CombinedStatus,
  CommitStatus,
  ContentsResponse,
  CreateStatusOption,
  EditBranchProtectionOption,
  EditGitHookOption,
  EditRepoOption,
  EditTagProtectionOption,
  GitBlobResponse,
  GitHook,
  GitTreeResponse,
  LanguageStatistics,
  NewIssuePinsAllowed,
  Note,
  Reference,
  RenameBranchRepoOption,
  RepoCollaboratorPermission,
  Repository,
  User,
  Tag,
  TagProtection,
  Team,
  TransferRepoOption,
  UpdateBranchProtectionPriorities,
  UpdateBranchRepoOption
}
import zio.{Chunk, IO}
import zio.stream.ZStream

/** Repository operations, reached through `client.repos`: repository metadata,
  * Git data (commits, trees, blobs, refs, tags), file contents and downloads,
  * collaborators and teams, branch/tag protections, and commit statuses.
  */
trait ReposApi extends io.worxbend.gitea4s.api.generated.ReposOperations:
  def createForCurrentUser(body: CreateRepoOption): IO[GiteaError, Repository]
  def edit(owner: String, repo: String, body: EditRepoOption): IO[GiteaError, Repository]
  /** Permanently deletes the named repository. */
  def delete(owner: String, repo: String): IO[GiteaError, Unit]
  def fork(owner: String, repo: String, body: CreateForkOption): IO[GiteaError, Repository]
  def createBranch(owner: String, repo: String, body: CreateBranchRepoOption): IO[GiteaError, Branch]
  def deleteBranch(owner: String, repo: String, branch: String): IO[GiteaError, Unit]
  def getBranch(owner: String, repo: String, branch: String): IO[GiteaError, Branch]
  def updateBranch(owner: String, repo: String, branch: String, body: UpdateBranchRepoOption): IO[GiteaError, Unit]
  def renameBranch(owner: String, repo: String, branch: String, body: RenameBranchRepoOption): IO[GiteaError, Unit]
  def gitHooks(owner: String, repo: String): IO[GiteaError, Chunk[GitHook]]
  def gitHook(owner: String, repo: String, id: String): IO[GiteaError, GitHook]
  def editGitHook(owner: String, repo: String, id: String, body: EditGitHookOption): IO[GiteaError, GitHook]
  def deleteGitHook(owner: String, repo: String, id: String): IO[GiteaError, Unit]
  def createTagProtection(owner: String, repo: String, body: CreateTagProtectionOption): IO[GiteaError, TagProtection]
  def editTagProtection(owner: String, repo: String, id: Long, body: EditTagProtectionOption): IO[GiteaError, TagProtection]
  def deleteTagProtection(owner: String, repo: String, id: Long): IO[GiteaError, Unit]
  def createBranchProtection(owner: String, repo: String, body: CreateBranchProtectionOption): IO[GiteaError, BranchProtection]
  def editBranchProtection(owner: String, repo: String, name: String, body: EditBranchProtectionOption): IO[GiteaError, BranchProtection]
  def deleteBranchProtection(owner: String, repo: String, name: String): IO[GiteaError, Unit]
  def updateBranchProtectionPriorities(owner: String, repo: String, body: UpdateBranchProtectionPriorities): IO[GiteaError, Unit]
  /** Requests transfer of repository ownership to `newOwner`. */
  def transfer(owner: String, repo: String, body: TransferRepoOption): IO[GiteaError, Repository]
  def acceptTransfer(owner: String, repo: String): IO[GiteaError, Repository]
  def rejectTransfer(owner: String, repo: String): IO[GiteaError, Repository]
  /** A 404 also occurs when a repository is inaccessible. */
  def isAssignee(owner: String, repo: String, assignee: String): IO[GiteaError, Boolean]

  def get(owner: String, repo: String): IO[GiteaError, Repository]

  def commit(
      owner: String,
      repo: String,
      sha: String,
      params: SingleCommitParams = SingleCommitParams.default
  ): IO[GiteaError, Commit]

  def commitDiffOrPatch(owner: String, repo: String, sha: String, diffType: CommitDiffType): IO[GiteaError, String]

  def commitNote(
      owner: String,
      repo: String,
      sha: String,
      params: CommitNoteParams = CommitNoteParams.default
  ): IO[GiteaError, Note]

  def gitTree(
      owner: String,
      repo: String,
      sha: String,
      params: GitTreeParams = GitTreeParams.default
  ): IO[GiteaError, GitTreeResponse]

  def gitBlob(owner: String, repo: String, sha: String): IO[GiteaError, GitBlobResponse]

  def annotatedTag(owner: String, repo: String, sha: String): IO[GiteaError, AnnotatedTag]

  def gitRefs(owner: String, repo: String): IO[GiteaError, Chunk[Reference]]

  def gitRefs(owner: String, repo: String, ref: String): IO[GiteaError, Chunk[Reference]]

  def contents(
      owner: String,
      repo: String,
      params: ContentsParams
  ): IO[GiteaError, Chunk[ContentsResponse]]

  def contents(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams
  ): IO[GiteaError, ContentsResponse]

  def rawFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): IO[GiteaError, Chunk[Byte]]

  def mediaFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): IO[GiteaError, Chunk[Byte]]

  def archive(
      owner: String,
      repo: String,
      archive: String,
      params: ArchiveParams = ArchiveParams.default
  ): IO[GiteaError, Chunk[Byte]]

  def assignees(owner: String, repo: String): IO[GiteaError, Chunk[User]]

  def reviewers(owner: String, repo: String): IO[GiteaError, Chunk[User]]

  def stargazers(owner: String, repo: String): ZStream[Any, GiteaError, User]

  def watchers(owner: String, repo: String): ZStream[Any, GiteaError, User]

  def collaborators(owner: String, repo: String): ZStream[Any, GiteaError, User]

  def isCollaborator(owner: String, repo: String, collaborator: String): IO[GiteaError, Boolean]

  def collaboratorPermission(
      owner: String,
      repo: String,
      collaborator: String
  ): IO[GiteaError, RepoCollaboratorPermission]

  def teams(owner: String, repo: String): ZStream[Any, GiteaError, Team]

  def team(owner: String, repo: String, team: String): IO[GiteaError, Team]

  def list(owner: String, params: RepoListParams = RepoListParams.default): ZStream[Any, GiteaError, Repository]

  def newIssuePinsAllowed(owner: String, repo: String): IO[GiteaError, NewIssuePinsAllowed]

  /** Every topic on the repository.
    *
    * The endpoint is paginated and this crawls it to exhaustion rather than
    * returning a stream, because Gitea caps a repository at 25 topics — below
    * the default page size, so in practice this is a single request. The other
    * paginated reads on this trait return a `ZStream` precisely because their
    * collections have no such bound.
    */
  def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]]

  def branches(owner: String, repo: String): ZStream[Any, GiteaError, Branch]

  /** Filters branches by name. The built-in client sends `q` to Gitea; other
    * implementations retain a working default without implementing the new method.
    */
  def branches(owner: String, repo: String, query: String): ZStream[Any, GiteaError, Branch] =
    branches(owner, repo).filter(_.name.exists(_.contains(query)))

  def tags(owner: String, repo: String): ZStream[Any, GiteaError, Tag]

  def languages(owner: String, repo: String): IO[GiteaError, LanguageStatistics]

  def gpgSigningKey(owner: String, repo: String): IO[GiteaError, String]

  def tag(owner: String, repo: String, tag: String): IO[GiteaError, Tag]

  def tagProtections(owner: String, repo: String): IO[GiteaError, Chunk[TagProtection]]

  def tagProtection(owner: String, repo: String, id: Long): IO[GiteaError, TagProtection]

  def branchProtections(owner: String, repo: String): IO[GiteaError, Chunk[BranchProtection]]

  def branchProtection(owner: String, repo: String, name: String): IO[GiteaError, BranchProtection]

  def combinedStatusByRef(
      owner: String,
      repo: String,
      ref: String,
      params: CombinedStatusParams = CombinedStatusParams.default
  ): IO[GiteaError, CombinedStatus]

  def statusesByRef(
      owner: String,
      repo: String,
      ref: String,
      params: CommitStatusListParams = CommitStatusListParams.default
  ): ZStream[Any, GiteaError, CommitStatus]

  def statuses(
      owner: String,
      repo: String,
      sha: String,
      params: CommitStatusListParams = CommitStatusListParams.default
  ): ZStream[Any, GiteaError, CommitStatus]

  def createStatus(
      owner: String,
      repo: String,
      sha: String,
      body: CreateStatusOption
  ): IO[GiteaError, CommitStatus]
