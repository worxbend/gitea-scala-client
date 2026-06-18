package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.IssueListParams
import io.worxbend.gitea4s.model.{Comment, CreateIssue, EditIssue, Issue}
import zio.IO
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

  def comment(owner: String, repo: String, index: Long, body: String): IO[GiteaError, Comment]
