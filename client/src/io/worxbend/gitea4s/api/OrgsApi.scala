package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.RepoListParams
import io.worxbend.gitea4s.model.{Organization, Repository, User}
import zio.IO
import zio.stream.ZStream

/** Organization operations, reached through `client.orgs`: organization
  * metadata, members and public members, and organization repositories.
  */
trait OrgsApi:
  def get(org: String): IO[GiteaError, Organization]

  def members(org: String): ZStream[Any, GiteaError, User]

  def publicMembers(org: String): ZStream[Any, GiteaError, User]

  def repos(org: String, params: RepoListParams = RepoListParams.default): ZStream[Any, GiteaError, Repository]
