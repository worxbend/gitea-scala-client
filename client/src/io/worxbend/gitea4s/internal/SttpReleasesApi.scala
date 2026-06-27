package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.ReleasesApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, ReleaseListParams}
import io.worxbend.gitea4s.model.{Release, ReleaseAsset}
import zio.{Chunk, IO}
import zio.stream.ZStream

private[gitea4s] final class SttpReleasesApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends ReleasesApi:
  override def list(
      owner: String,
      repo: String,
      params: ReleaseListParams = ReleaseListParams.default
  ): ZStream[Any, GiteaError, Release] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoReleases(config, owner, repo, params.copy(page = Some(page))))
    }

  override def get(owner: String, repo: String, id: Long): IO[GiteaError, Release] =
    executor.send(GiteaRequests.repoRelease(config, owner, repo, id))

  override def latest(owner: String, repo: String): IO[GiteaError, Release] =
    executor.send(GiteaRequests.repoLatestRelease(config, owner, repo))

  override def byTag(owner: String, repo: String, tag: String): IO[GiteaError, Release] =
    executor.send(GiteaRequests.repoReleaseByTag(config, owner, repo, tag))

  override def assets(owner: String, repo: String, releaseId: Long): IO[GiteaError, Chunk[ReleaseAsset]] =
    executor.send(GiteaRequests.repoReleaseAssets(config, owner, repo, releaseId))

  override def asset(
      owner: String,
      repo: String,
      releaseId: Long,
      assetId: Long
  ): IO[GiteaError, ReleaseAsset] =
    executor.send(GiteaRequests.repoReleaseAsset(config, owner, repo, releaseId, assetId))
