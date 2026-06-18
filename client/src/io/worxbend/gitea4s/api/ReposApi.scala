package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Repository
import zio.IO

trait ReposApi:
  def get(owner: String, repo: String): IO[GiteaError, Repository]
