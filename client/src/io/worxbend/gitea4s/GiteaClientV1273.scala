package io.worxbend.gitea4s

import io.worxbend.gitea4s.api.{ActionsApi, IssuesApiV1273, OrgsApiV1273, PullRequestsApiV1273, ReposApiV1273, TokensApi}
import io.worxbend.gitea4s.internal.SttpGiteaClient
import sttp.client4.Backend
import zio.Task

/** Additive v1.27.3 surface; the published `GiteaClient` interface stays binary-compatible. */
trait GiteaClientV1273 extends GiteaClient:
  def actions: ActionsApi

  override def repos: ReposApiV1273

  override def issues: IssuesApiV1273

  override def orgs: OrgsApiV1273

  override def pulls: PullRequestsApiV1273

  def tokens: TokensApi

object GiteaClientV1273:
  def fromBackend(config: GiteaConfig, backend: Backend[Task]): GiteaClientV1273 =
    new SttpGiteaClient(config, backend)
