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

/** Pull-request operations, reached through `client.pulls`. */
trait PullRequestsApi:
  def list(
      owner: String,
      repo: String,
      params: PullRequestListParams = PullRequestListParams.default
  ): ZStream[Any, GiteaError, PullRequest]

  def pinned(owner: String, repo: String): IO[GiteaError, Chunk[PullRequest]]

  def byBaseHead(owner: String, repo: String, base: String, head: String): IO[GiteaError, PullRequest]

  def forCommit(owner: String, repo: String, sha: String): IO[GiteaError, PullRequest]

  def get(owner: String, repo: String, index: Long): IO[GiteaError, PullRequest]

  def create(owner: String, repo: String, body: CreatePullRequestOption): IO[GiteaError, PullRequest]

  def edit(owner: String, repo: String, index: Long, body: EditPullRequestOption): IO[GiteaError, PullRequest]

  def isMerged(owner: String, repo: String, index: Long): IO[GiteaError, Boolean]

  def merge(owner: String, repo: String, index: Long, body: MergePullRequestOption): IO[GiteaError, Unit]

  def cancelAutoMerge(owner: String, repo: String, index: Long): IO[GiteaError, Unit]

  def update(owner: String, repo: String, index: Long, style: PullRequestUpdateStyle): IO[GiteaError, Unit]

  def requestReviews(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Chunk[PullReview]]

  def cancelReviewRequests(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Unit]

  def reviews(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, PullReview]

  def createReview(
      owner: String,
      repo: String,
      index: Long,
      body: CreatePullReviewOptions
  ): IO[GiteaError, PullReview]

  def review(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview]

  def submitReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: SubmitPullReviewOptions
  ): IO[GiteaError, PullReview]

  def deleteReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit]

  def dismissReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: DismissPullReviewOptions
  ): IO[GiteaError, PullReview]

  def undismissReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview]

  def reviewComments(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, Chunk[PullReviewComment]]

  def resolveReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit]

  def unresolveReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit]

  def diffOrPatch(
      owner: String,
      repo: String,
      index: Long,
      diffType: PullRequestDiffType,
      binary: Option[Boolean] = None
  ): IO[GiteaError, String]

  def files(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestFilesParams = PullRequestFilesParams.default
  ): ZStream[Any, GiteaError, ChangedFile]

  def commits(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestCommitsParams = PullRequestCommitsParams.default
  ): ZStream[Any, GiteaError, Commit]
