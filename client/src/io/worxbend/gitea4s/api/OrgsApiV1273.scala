package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import zio.IO

/** Additive organization operations introduced in Gitea v1.27.3. */
trait OrgsApiV1273 extends OrgsApi:
  /** Deletes all repositories in the organization. This operation is irreversible. */
  def deleteAllRepositories(org: String): IO[GiteaError, Unit]
