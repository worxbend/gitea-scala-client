package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.api.{
  IssuesApi,
  NotificationsApi,
  OrgsApi,
  PullRequestsApi,
  ReleasesApi,
  ReposApi,
  UsersApi
}
import sttp.client4.Backend
import zio.Task

/** The sttp-backed [[GiteaClient]] implementation.
  *
  * This type is a thin coordinator: it owns the shared [[GiteaRequestExecutor]]
  * and exposes one impl per resource namespace, each implemented in its own
  * `Sttp*Api` class so the surface can grow without a single god-object.
  */
final class SttpGiteaClient(config: GiteaConfig, backend: Backend[Task]) extends GiteaClient:
  private val executor = GiteaRequestExecutor(backend, config.maxRetries)

  override val repos: ReposApi = SttpReposApi(config, executor)
  override val issues: IssuesApi = SttpIssuesApi(config, executor)
  override val pulls: PullRequestsApi = SttpPullsApi(config, executor)
  override val releases: ReleasesApi = SttpReleasesApi(config, executor)
  override val notifications: NotificationsApi = SttpNotificationsApi(config, executor)
  override val users: UsersApi = SttpUsersApi(config, executor)
  override val orgs: OrgsApi = SttpOrgsApi(config, executor)
