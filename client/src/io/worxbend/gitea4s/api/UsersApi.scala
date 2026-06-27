package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.UserSearchParams
import io.worxbend.gitea4s.model.{StopWatch, User}
import zio.IO
import zio.stream.ZStream

/** User operations, reached through `client.users`: the authenticated user
  * ([[me]]), user lookup and search, followers/following, and active stopwatches.
  */
trait UsersApi:
  def me: IO[GiteaError, User]

  def get(username: String): IO[GiteaError, User]

  def followers(username: String): ZStream[Any, GiteaError, User]

  def following(username: String): ZStream[Any, GiteaError, User]

  def search(params: UserSearchParams): ZStream[Any, GiteaError, User]

  def stopwatches: ZStream[Any, GiteaError, StopWatch]
