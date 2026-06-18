package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Organization
import zio.IO

trait OrgsApi:
  def get(org: String): IO[GiteaError, Organization]
