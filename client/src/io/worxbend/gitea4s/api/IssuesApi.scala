package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.IssueListParams
import io.worxbend.gitea4s.model.Issue
import zio.stream.ZStream

trait IssuesApi:
  def list(
      owner: String,
      repo: String,
      params: IssueListParams = IssueListParams.default
  ): ZStream[Any, GiteaError, Issue]
