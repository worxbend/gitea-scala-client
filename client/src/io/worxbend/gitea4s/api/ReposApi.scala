package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  CombinedStatusParams,
  CommitNoteParams,
  CommitStatusListParams,
  RepoListParams,
  SingleCommitParams
}
import io.worxbend.gitea4s.model.{
  Branch,
  Commit,
  CommitDiffType,
  CombinedStatus,
  CommitStatus,
  CreateStatusOption,
  NewIssuePinsAllowed,
  Note,
  Repository,
  Tag
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

  def list(owner: String, params: RepoListParams): ZStream[Any, GiteaError, Repository]

  def newIssuePinsAllowed(owner: String, repo: String): IO[GiteaError, NewIssuePinsAllowed]

  def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]]

  def branches(owner: String, repo: String): ZStream[Any, GiteaError, Branch]

  def tags(owner: String, repo: String): ZStream[Any, GiteaError, Tag]

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
