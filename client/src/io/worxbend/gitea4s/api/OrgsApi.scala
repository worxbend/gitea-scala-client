package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Organization, User}
import zio.IO
import zio.stream.ZStream

trait OrgsApi:
  def get(org: String): IO[GiteaError, Organization]

  def members(org: String): ZStream[Any, GiteaError, User]
