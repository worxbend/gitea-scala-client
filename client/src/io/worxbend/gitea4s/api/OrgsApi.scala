package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.RepoListParams
import io.worxbend.gitea4s.model.{CreateRepoOption, Organization, Repository, User}
import zio.IO
import zio.stream.ZStream

/** Organization operations, reached through `client.orgs`: organization
  * metadata, members and public members, and organization repositories.
  */
trait OrgsApi extends io.worxbend.gitea4s.api.generated.OrgsOperations:
  def createRepository(org: String, body: CreateRepoOption): IO[GiteaError, Repository]
  /** Legacy `/org/{org}/repos` route; prefer `createRepository`. */
  def createRepositoryLegacy(org: String, body: CreateRepoOption): IO[GiteaError, Repository]
  /** Deletes all repositories in the organization. This operation is irreversible. */
  def deleteAllRepositories(org: String): IO[GiteaError, Unit]

  def get(org: String): IO[GiteaError, Organization]

  def members(org: String): ZStream[Any, GiteaError, User]

  def publicMembers(org: String): ZStream[Any, GiteaError, User]

  def repos(org: String, params: RepoListParams = RepoListParams.default): ZStream[Any, GiteaError, Repository]
