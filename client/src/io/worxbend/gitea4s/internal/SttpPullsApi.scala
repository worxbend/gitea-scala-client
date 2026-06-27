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
  override def pullRequests(
      owner: String,
      repo: String,
      params: PullRequestListParams = PullRequestListParams.default
  ): ZStream[Any, GiteaError, PullRequest] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequests(config, owner, repo, params.copy(page = Some(page))))
    }

  override def pinnedPullRequests(owner: String, repo: String): IO[GiteaError, Chunk[PullRequest]] =
    executor.send(GiteaRequests.pinnedPullRequests(config, owner, repo))

  override def pullRequestByBaseHead(
      owner: String,
      repo: String,
      base: String,
      head: String
  ): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoPullRequestByBaseHead(config, owner, repo, base, head))

  override def commitPullRequest(owner: String, repo: String, sha: String): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoCommitPullRequest(config, owner, repo, sha))

  override def pullRequest(owner: String, repo: String, index: Long): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoPullRequest(config, owner, repo, index))

  override def createPullRequest(
      owner: String,
      repo: String,
      body: CreatePullRequestOption
  ): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.createPullRequest(config, owner, repo, body))

  override def editPullRequest(
      owner: String,
      repo: String,
      index: Long,
      body: EditPullRequestOption
  ): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.editPullRequest(config, owner, repo, index, body))

  override def pullRequestIsMerged(owner: String, repo: String, index: Long): IO[GiteaError, Boolean] =
    executor.send(GiteaRequests.repoPullRequestIsMerged(config, owner, repo, index))

  override def mergePullRequest(
      owner: String,
      repo: String,
      index: Long,
      body: MergePullRequestOption
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.mergePullRequest(config, owner, repo, index, body))

  override def cancelScheduledAutoMerge(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.cancelScheduledAutoMerge(config, owner, repo, index))

  override def updatePullRequest(
      owner: String,
      repo: String,
      index: Long,
      style: PullRequestUpdateStyle
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.updatePullRequest(config, owner, repo, index, style))

  override def requestPullReviews(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Chunk[PullReview]] =
    executor.send(GiteaRequests.createPullReviewRequests(config, owner, repo, index, body))

  override def cancelPullReviewRequests(
      owner: String,
      repo: String,
      index: Long,
      body: PullReviewRequestOptions
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deletePullReviewRequests(config, owner, repo, index, body))

  override def pullRequestReviews(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, PullReview] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullReviews(config, owner, repo, index, page))
    }

  override def createPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      body: CreatePullReviewOptions
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.createPullReview(config, owner, repo, index, body))

  override def pullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.repoPullReview(config, owner, repo, index, id))

  override def submitPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: SubmitPullReviewOptions
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.submitPullReview(config, owner, repo, index, id, body))

  override def deletePullRequestReview(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deletePullReview(config, owner, repo, index, id))

  override def dismissPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long,
      body: DismissPullReviewOptions
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.dismissPullReview(config, owner, repo, index, id, body))

  override def undismissPullRequestReview(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, PullReview] =
    executor.send(GiteaRequests.undismissPullReview(config, owner, repo, index, id))

  override def pullRequestReviewComments(
      owner: String,
      repo: String,
      index: Long,
      id: Long
  ): IO[GiteaError, Chunk[PullReviewComment]] =
    executor.send(GiteaRequests.repoPullReviewComments(config, owner, repo, index, id))

  override def resolvePullRequestReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.resolvePullReviewComment(config, owner, repo, id))

  override def unresolvePullRequestReviewComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.unresolvePullReviewComment(config, owner, repo, id))

  override def pullRequestDiffOrPatch(
      owner: String,
      repo: String,
      index: Long,
      diffType: PullRequestDiffType,
      binary: Option[Boolean] = None
  ): IO[GiteaError, String] =
    executor.send(GiteaRequests.repoPullRequestDiffOrPatch(config, owner, repo, index, diffType, binary))

  override def pullRequestFiles(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestFilesParams = PullRequestFilesParams.default
  ): ZStream[Any, GiteaError, ChangedFile] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequestFiles(config, owner, repo, index, params.copy(page = Some(page))))
    }

  override def pullRequestCommits(
      owner: String,
      repo: String,
      index: Long,
      params: PullRequestCommitsParams = PullRequestCommitsParams.default
  ): ZStream[Any, GiteaError, Commit] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequestCommits(config, owner, repo, index, params.copy(page = Some(page))))
    }
