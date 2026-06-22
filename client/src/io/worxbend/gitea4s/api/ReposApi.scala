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
  Commit,
  CommitDiffType,
  CombinedStatus,
  CommitStatus,
  ContentsResponse,
  CreateStatusOption,
  GitBlobResponse,
  GitTreeResponse,
  NewIssuePinsAllowed,
  Note,
  Reference,
  RepoCollaboratorPermission,
  Repository,
  User,
  Tag,
  TagProtection,
  Team
}
import zio.{Chunk, IO}
import zio.stream.ZStream

trait ReposApi:
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

  def collaborators(owner: String, repo: String): ZStream[Any, GiteaError, User]

  def isCollaborator(owner: String, repo: String, collaborator: String): IO[GiteaError, Boolean]

  def collaboratorPermission(
      owner: String,
      repo: String,
      collaborator: String
  ): IO[GiteaError, RepoCollaboratorPermission]

  def teams(owner: String, repo: String): ZStream[Any, GiteaError, Team]

  def team(owner: String, repo: String, team: String): IO[GiteaError, Team]

  def list(owner: String, params: RepoListParams): ZStream[Any, GiteaError, Repository]

  def newIssuePinsAllowed(owner: String, repo: String): IO[GiteaError, NewIssuePinsAllowed]

  def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]]

  def branches(owner: String, repo: String): ZStream[Any, GiteaError, Branch]

  def tags(owner: String, repo: String): ZStream[Any, GiteaError, Tag]

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
