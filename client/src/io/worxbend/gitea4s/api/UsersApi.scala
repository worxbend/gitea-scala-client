package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.UserSearchParams
import io.worxbend.gitea4s.model.User
import zio.IO
import zio.stream.ZStream

trait UsersApi:
  def me: IO[GiteaError, User]

  def get(username: String): IO[GiteaError, User]

  def followers(username: String): ZStream[Any, GiteaError, User]

  def following(username: String): ZStream[Any, GiteaError, User]

  def search(params: UserSearchParams): ZStream[Any, GiteaError, User]
