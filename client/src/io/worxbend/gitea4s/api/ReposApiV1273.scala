package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import zio.IO

/** Additive repository operations introduced in Gitea v1.27.3. */
trait ReposApiV1273 extends ReposApi:
  /** Returns false for a 404, which can also mean an inaccessible repository. */
  def isAssignee(owner: String, repo: String, assignee: String): IO[GiteaError, Boolean]
