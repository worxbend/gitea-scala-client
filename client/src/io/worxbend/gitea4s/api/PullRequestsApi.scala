package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  PullRequestCommitsParams,
  PullRequestDiffType,
  PullRequestFilesParams,
  PullRequestListParams
}
import io.worxbend.gitea4s.model.{ChangedFile, Commit, PullRequest, PullReview, PullReviewComment}
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

  def pullRequestIsMerged(owner: String, repo: String, index: Long): IO[GiteaError, Boolean]

  def pullRequestReviews(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, PullReview]

  def pullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview]

  def deletePullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit]

  def pullRequestReviewComments(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, Chunk[PullReviewComment]]

  def pullRequestDiffOrPatch(
      owner: String,
      repo: String,
      index: Long,
      diffType: PullRequestDiffType,
      binary: Option[Boolean] = None
  ): IO[GiteaError, String]

  def pullRequestFiles(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestFilesParams = PullRequestFilesParams.default
  ): ZStream[Any, GiteaError, ChangedFile]

  def pullRequestCommits(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestCommitsParams = PullRequestCommitsParams.default
  ): ZStream[Any, GiteaError, Commit]
