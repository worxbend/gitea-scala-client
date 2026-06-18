package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.PullRequestListParams
import io.worxbend.gitea4s.model.PullRequest
import zio.{Chunk, IO}
import zio.stream.ZStream

trait PullRequestsApi:
  def pullRequests(
      owner: String,
      repo: String,
      params: PullRequestListParams = PullRequestListParams.default
  ): ZStream[Any, GiteaError, PullRequest]

  def pinnedPullRequests(owner: String, repo: String): IO[GiteaError, Chunk[PullRequest]]

  def pullRequestByBaseHead(owner: String, repo: String, base: String, head: String): IO[GiteaError, PullRequest]

  def pullRequest(owner: String, repo: String, index: Long): IO[GiteaError, PullRequest]
