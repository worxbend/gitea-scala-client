package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{IssueCommentListParams, IssueListParams, RepositoryCommentListParams}
import io.worxbend.gitea4s.model.{
  Comment,
  CreateIssue,
  EditDeadlineOption,
  EditIssueComment,
  EditIssue,
  Issue,
  IssueDeadline,
  Label,
  LockIssueOption,
  IssueMeta
}
import zio.{Chunk, IO}
import zio.stream.ZStream

trait IssuesApi:
  def get(owner: String, repo: String, index: Long): IO[GiteaError, Issue]

  def list(
      owner: String,
      repo: String,
      params: IssueListParams = IssueListParams.default
  ): ZStream[Any, GiteaError, Issue]

  def create(owner: String, repo: String, body: CreateIssue): IO[GiteaError, Issue]

  def edit(owner: String, repo: String, index: Long, body: EditIssue): IO[GiteaError, Issue]

  def close(owner: String, repo: String, index: Long): IO[GiteaError, Issue]

  def labels(owner: String, repo: String, index: Long): IO[GiteaError, Chunk[Label]]

  def replaceLabels(owner: String, repo: String, index: Long, labels: Chunk[Long]): IO[GiteaError, Chunk[Label]]

  def addLabels(owner: String, repo: String, index: Long, labels: Chunk[Long]): IO[GiteaError, Chunk[Label]]

  def clearLabels(owner: String, repo: String, index: Long): IO[GiteaError, Unit]

  def removeLabel(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit]

  def lock(owner: String, repo: String, index: Long, body: LockIssueOption): IO[GiteaError, Unit]

  def unlock(owner: String, repo: String, index: Long): IO[GiteaError, Unit]

  def editDeadline(owner: String, repo: String, index: Long, body: EditDeadlineOption): IO[GiteaError, IssueDeadline]

  def comment(owner: String, repo: String, index: Long, body: String): IO[GiteaError, Comment]

  def comments(
      owner: String,
      repo: String,
      index: Long,
      params: IssueCommentListParams = IssueCommentListParams.default
  ): IO[GiteaError, Chunk[Comment]]

  def repositoryComments(
      owner: String,
      repo: String,
      params: RepositoryCommentListParams = RepositoryCommentListParams.default
  ): ZStream[Any, GiteaError, Comment]

  def comment(owner: String, repo: String, id: Long): IO[GiteaError, Comment]

  def editComment(owner: String, repo: String, id: Long, body: EditIssueComment): IO[GiteaError, Comment]

  def deleteComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit]

  def blocks(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, Issue]

  def block(owner: String, repo: String, index: Long, blockedIssue: IssueMeta): IO[GiteaError, Issue]

  def unblock(owner: String, repo: String, index: Long, blockedIssue: IssueMeta): IO[GiteaError, Issue]

  def dependencies(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, Issue]

  def addDependency(owner: String, repo: String, index: Long, dependency: IssueMeta): IO[GiteaError, Issue]

  def removeDependency(owner: String, repo: String, index: Long, dependency: IssueMeta): IO[GiteaError, Issue]
