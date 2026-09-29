package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.ReleaseListParams
import io.worxbend.gitea4s.model.{Release, ReleaseAsset}
import zio.{Chunk, IO}
import zio.stream.ZStream

/** Release and release-asset operations, reached through `client.releases`. */
trait ReleasesApi extends io.worxbend.gitea4s.api.generated.ReleasesOperations:
  def createAttachment(owner: String, repo: String, id: Long, upload: io.worxbend.gitea4s.model.AttachmentUpload,
      name: Option[String] = None): IO[GiteaError, io.worxbend.gitea4s.model.contract.Attachment]
  def list(
      owner: String,
      repo: String,
      params: ReleaseListParams = ReleaseListParams.default
  ): ZStream[Any, GiteaError, Release]

  def get(owner: String, repo: String, id: Long): IO[GiteaError, Release]

  def latest(owner: String, repo: String): IO[GiteaError, Release]

  def byTag(owner: String, repo: String, tag: String): IO[GiteaError, Release]

  def assets(owner: String, repo: String, releaseId: Long): IO[GiteaError, Chunk[ReleaseAsset]]

  def asset(owner: String, repo: String, releaseId: Long, assetId: Long): IO[GiteaError, ReleaseAsset]
