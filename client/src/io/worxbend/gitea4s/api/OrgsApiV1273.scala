package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{CreateRepoOption, Repository}
import zio.IO

/** Additive organization operations introduced in Gitea v1.27.3. */
trait OrgsApiV1273 extends OrgsApi:
  def createRepository(org: String, body: CreateRepoOption): IO[GiteaError, Repository]

  /** The legacy `/org/{org}/repos` route; prefer `createRepository`. */
  def createRepositoryLegacy(org: String, body: CreateRepoOption): IO[GiteaError, Repository]

  /** Deletes all repositories in the organization. This operation is irreversible. */
  def deleteAllRepositories(org: String): IO[GiteaError, Unit]
