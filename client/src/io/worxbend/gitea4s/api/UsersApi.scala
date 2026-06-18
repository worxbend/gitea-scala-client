package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.User
import zio.IO

trait UsersApi:
  def me: IO[GiteaError, User]

  def get(username: String): IO[GiteaError, User]
