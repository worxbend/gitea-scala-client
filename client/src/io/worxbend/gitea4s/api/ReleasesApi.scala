package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Release, ReleaseAsset}
import zio.{Chunk, IO}
import zio.stream.ZStream

trait ReleasesApi:
  def releases(owner: String, repo: String): ZStream[Any, GiteaError, Release]

  def release(owner: String, repo: String, id: Long): IO[GiteaError, Release]

  def releaseByTag(owner: String, repo: String, tag: String): IO[GiteaError, Release]

  def releaseAssets(owner: String, repo: String, releaseId: Long): IO[GiteaError, Chunk[ReleaseAsset]]

  def releaseAsset(owner: String, repo: String, releaseId: Long, assetId: Long): IO[GiteaError, ReleaseAsset]
