package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.api.{
  ActionsApi,
  IssuesApi,
  NotificationsApi,
  OrgsApi,
  PullRequestsApi,
  ReleasesApi,
  ReposApi,
  SettingsApi,
  UsersApi,
  TokensApi
}
import io.worxbend.gitea4s.api.generated.{AdminOperations, CatalogOperations, PackagesOperations, TeamsOperations}
import sttp.client4.Backend
import zio.Task

/** The sttp-backed [[GiteaClient]] implementation.
  *
  * This type is a thin coordinator: it owns the shared [[GiteaRequestExecutor]]
  * and exposes one impl per resource namespace, each implemented in its own
  * `Sttp*Api` class so the surface can grow without a single god-object.
  */
final class SttpGiteaClient(config: GiteaConfig, backend: Backend[Task]) extends GiteaClient:
  private val executor = GiteaRequestExecutor(backend, config.maxRetries, config.observer)

  override val repos: ReposApi = SttpReposApi(config, executor)
  override val issues: IssuesApi = SttpIssuesApi(config, executor)
  override val pulls: PullRequestsApi = SttpPullsApi(config, executor)
  override val releases: ReleasesApi = SttpReleasesApi(config, executor)
  override val notifications: NotificationsApi = SttpNotificationsApi(config, executor)
  override val users: UsersApi = SttpUsersApi(config, executor)
  override val orgs: OrgsApi = SttpOrgsApi(config, executor)
  override val tokens: TokensApi = SttpTokensApi(config, executor)
  override val actions: ActionsApi = SttpActionsApi(config, executor)
  override val settings: SettingsApi = SttpSettingsApi(config, executor)
  override val admin: AdminOperations = SttpAdminApi(config, executor)
  override val teams: TeamsOperations = SttpTeamsApi(config, executor)
  override val packages: PackagesOperations = SttpPackagesApi(config, executor)
  override val catalog: CatalogOperations = SttpCatalogApi(config, executor)
