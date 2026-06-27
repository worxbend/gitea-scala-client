package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.PullRequestsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  GiteaRequests,
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

private[gitea4s] final class SttpPullsApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends PullRequestsApi:
  override def list(
      owner: String,
      repo: String,
      params: PullRequestListParams = PullRequestListParams.default
  ): ZStream[Any, GiteaError, PullRequest] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequests(config, owner, repo, params.copy(page = Some(page))))
    }

  override def pinned(owner: String, repo: String): IO[GiteaError, Chunk[PullRequest]] =
    executor.send(GiteaRequests.pinnedPullRequests(config, owner, repo))

  override def byBaseHead(
      owner: String,
      repo: String,
      base: String,
      head: String
  ): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoPullRequestByBaseHead(config, owner, repo, base, head))

  override def forCommit(owner: String, repo: String, sha: String): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoCommitPullRequest(config, owner, repo, sha))

  override def get(owner: String, repo: String, index: Long): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoPullRequest(config, owner, repo, index))

  override def create(
      owner: String,
      repo: String,
      body: CreatePullRequestOption
  ): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.createPullRequest(config, owner, repo, body))

  override def edit(
      owner: String,
      repo: String,
      index: Long,
      body: EditPullRequestOption
  ): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.editPullRequest(config, owner, repo, index, body))

  override def isMerged(owner: String, repo: String, index: Long): IO[GiteaError, Boolean] =
    executor.send(GiteaRequests.repoPullRequestIsMerged(config, owner, repo, index))

  override def merge(
      owner: String,
      repo: String,
      index: Long,
      body: MergePullRequestOption
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.mergePullRequest(config, owner, repo, index, body))

  override def cancelAutoMerge(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.cancelScheduledAutoMerge(config, owner, repo, index))

  override def update(
      owner: String,
      repo: String,
      index: Long,
      style: PullRequestUpdateStyle
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.updatePullRequest(config, owner, repo, index, style))

  override def requestReviews(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Chunk[PullReview]] =
    executor.send(GiteaRequests.createPullReviewRequests(config, owner, repo, index, body))

  override def cancelReviewRequests(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deletePullReviewRequests(config, owner, repo, index, body))

  override def reviews(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, PullReview] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullReviews(config, owner, repo, index, page))
    }

  override def createReview(
      owner: String,
      repo: String,
      index: Long,
      body: CreatePullReviewOptions
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.createPullReview(config, owner, repo, index, body))

  override def review(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.repoPullReview(config, owner, repo, index, id))

  override def submitReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: SubmitPullReviewOptions
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.submitPullReview(config, owner, repo, index, id, body))

  override def deleteReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deletePullReview(config, owner, repo, index, id))

  override def dismissReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: DismissPullReviewOptions
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.dismissPullReview(config, owner, repo, index, id, body))

  override def undismissReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.undismissPullReview(config, owner, repo, index, id))

  override def reviewComments(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, Chunk[PullReviewComment]] =
    executor.send(GiteaRequests.repoPullReviewComments(config, owner, repo, index, id))

  override def resolveReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.resolvePullReviewComment(config, owner, repo, id))

  override def unresolveReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.unresolvePullReviewComment(config, owner, repo, id))

  override def diffOrPatch(
      owner: String,
      repo: String,
      index: Long,
      diffType: PullRequestDiffType,
      binary: Option[Boolean] = None
  ): IO[GiteaError, String] =
    executor.send(GiteaRequests.repoPullRequestDiffOrPatch(config, owner, repo, index, diffType, binary))

  override def files(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestFilesParams = PullRequestFilesParams.default
  ): ZStream[Any, GiteaError, ChangedFile] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequestFiles(config, owner, repo, index, params.copy(page = Some(page))))
    }

  override def commits(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestCommitsParams = PullRequestCommitsParams.default
  ): ZStream[Any, GiteaError, Commit] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequestCommits(config, owner, repo, index, params.copy(page = Some(page))))
    }
