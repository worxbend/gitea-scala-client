package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.CurrentAccessToken
import zio.IO

/** Operations on the token currently authenticating the client. */
trait TokensApi:
  def current: IO[GiteaError, CurrentAccessToken]

  /** Revokes the current token. Subsequent requests using it cannot authenticate. */
  def revokeCurrent: IO[GiteaError, Unit]
