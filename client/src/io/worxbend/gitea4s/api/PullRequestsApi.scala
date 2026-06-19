package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  PullRequestCommitsParams,
  PullRequestDiffType,
  PullRequestFilesParams,
  PullRequestListParams,
  PullRequestUpdateStyle
}
import io.worxbend.gitea4s.model.{
  ChangedFile,
  Commit,
  CreatePullRequestOption,
  CreatePullReviewOptions,
  DismissPullReviewOptions,
  EditPullRequestOption,
  MergePullRequestOption,
  PullRequest,
  PullReview,
  PullReviewComment,
  PullReviewRequestOptions,
  SubmitPullReviewOptions
}
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

  def createPullRequest(owner: String, repo: String, body: CreatePullRequestOption): IO[GiteaError, PullRequest]

  def editPullRequest(
      owner: String,
      repo: String,
      index: Long,
      body: EditPullRequestOption
  ): IO[GiteaError, PullRequest]

  def pullRequestIsMerged(owner: String, repo: String, index: Long): IO[GiteaError, Boolean]

  def mergePullRequest(
      owner: String,
      repo: String,
      index: Long,
      body: MergePullRequestOption
  ): IO[GiteaError, Unit]

  def cancelScheduledAutoMerge(owner: String, repo: String, index: Long): IO[GiteaError, Unit]

  def updatePullRequest(owner: String, repo: String, index: Long, style: PullRequestUpdateStyle): IO[GiteaError, Unit]

  def requestPullReviews(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Chunk[PullReview]]

  def cancelPullReviewRequests(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Unit]

  def pullRequestReviews(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, PullReview]

  def createPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      body: CreatePullReviewOptions
  ): IO[GiteaError, PullReview]

  def pullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview]

  def submitPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: SubmitPullReviewOptions
  ): IO[GiteaError, PullReview]

  def deletePullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit]

  def dismissPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: DismissPullReviewOptions
  ): IO[GiteaError, PullReview]

  def undismissPullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview]

  def pullRequestReviewComments(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, Chunk[PullReviewComment]]

  def resolvePullRequestReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit]

  def unresolvePullRequestReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit]

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
