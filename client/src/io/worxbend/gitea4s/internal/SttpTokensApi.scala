package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.TokensApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaRequests
import io.worxbend.gitea4s.model.CurrentAccessToken
import zio.IO

private[gitea4s] final class SttpTokensApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends TokensApi:
  override def current: IO[GiteaError, CurrentAccessToken] =
    executor.send(GiteaRequests.currentToken(config))

  override def revokeCurrent: IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteCurrentToken(config))
