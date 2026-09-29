package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Issue
import zio.{Chunk, IO}

/** Additive issue operations introduced in Gitea v1.27.3. */
trait IssuesApiV1273 extends IssuesApi:
  def addAssignees(owner: String, repo: String, index: Long, assignees: Chunk[String]): IO[GiteaError, Issue]

  def removeAssignees(owner: String, repo: String, index: Long, assignees: Chunk[String]): IO[GiteaError, Issue]

  /** Returns false for a 404, which can also mean an inaccessible issue. */
  def isAssignee(owner: String, repo: String, index: Long, assignee: String): IO[GiteaError, Boolean]
