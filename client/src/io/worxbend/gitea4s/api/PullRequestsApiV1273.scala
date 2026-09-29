package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.PullReviewComment
import zio.IO

/** Additive pull-request operations introduced in Gitea v1.27.3. */
trait PullRequestsApiV1273 extends PullRequestsApi:
  def replyToReviewComment(
      owner: String,
      repo: String,
      index: Long,
      commentId: Long,
      body: String
  ): IO[GiteaError, PullReviewComment]
