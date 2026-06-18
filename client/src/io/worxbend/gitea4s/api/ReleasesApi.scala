package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Release
import zio.IO
import zio.stream.ZStream

trait ReleasesApi:
  def releases(owner: String, repo: String): ZStream[Any, GiteaError, Release]

  def release(owner: String, repo: String, id: Long): IO[GiteaError, Release]
