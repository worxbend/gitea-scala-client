package io.worxbend.gitea4s

import io.worxbend.gitea4s.api.{IssuesApi, ReposApi, UsersApi}
import io.worxbend.gitea4s.internal.SttpGiteaClient
import sttp.client4.Backend
import zio.Task

trait GiteaClient extends ReposApi with IssuesApi with UsersApi

object GiteaClient:
  def fromBackend(config: GiteaConfig, backend: Backend[Task]): GiteaClient =
    new SttpGiteaClient(config, backend)
