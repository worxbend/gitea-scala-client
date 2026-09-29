package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.{GiteaClientV1273, GiteaConfig}
import io.worxbend.gitea4s.api.{
  ActionsApi,
  IssuesApiV1273,
  NotificationsApi,
  OrgsApiV1273,
  PullRequestsApiV1273,
  ReleasesApi,
  ReposApiV1273,
  UsersApi,
  TokensApi
}
import sttp.client4.Backend
import zio.Task

/** The sttp-backed [[GiteaClientV1273]] implementation.
  *
  * This type is a thin coordinator: it owns the shared [[GiteaRequestExecutor]]
  * and exposes one impl per resource namespace, each implemented in its own
  * `Sttp*Api` class so the surface can grow without a single god-object.
  */
final class SttpGiteaClient(config: GiteaConfig, backend: Backend[Task]) extends GiteaClientV1273:
  private val executor = GiteaRequestExecutor(backend, config.maxRetries, config.observer)

  override val repos: ReposApiV1273 = SttpReposApi(config, executor)
  override val issues: IssuesApiV1273 = SttpIssuesApi(config, executor)
  override val pulls: PullRequestsApiV1273 = SttpPullsApi(config, executor)
  override val releases: ReleasesApi = SttpReleasesApi(config, executor)
  override val notifications: NotificationsApi = SttpNotificationsApi(config, executor)
  override val users: UsersApi = SttpUsersApi(config, executor)
  override val orgs: OrgsApiV1273 = SttpOrgsApi(config, executor)
  override val tokens: TokensApi = SttpTokensApi(config, executor)
  override val actions: ActionsApi = SttpActionsApi(config, executor)
