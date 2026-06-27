package io.worxbend.gitea4s

import io.worxbend.gitea4s.api.{
  IssuesApi,
  NotificationsApi,
  OrgsApi,
  PullRequestsApi,
  ReleasesApi,
  ReposApi,
  UsersApi
}
import io.worxbend.gitea4s.internal.SttpGiteaClient
import sttp.client4.Backend
import zio.Task

/** Entry point to the Gitea API, organized into resource namespaces.
  *
  * Each namespace groups the operations for one resource family:
  *   - [[repos]] — repositories, Git data, contents, collaborators, statuses
  *   - [[issues]] — issues, comments, labels, reactions, tracked time
  *   - [[pulls]] — pull requests, reviews, merges, diffs
  *   - [[releases]] — releases and release assets
  *   - [[notifications]] — notification threads and counts
  *   - [[users]] — the current user, user lookup, search, followers
  *   - [[orgs]] — organizations and their members/repositories
  */
trait GiteaClient:
  def repos: ReposApi
  def issues: IssuesApi
  def pulls: PullRequestsApi
  def releases: ReleasesApi
  def notifications: NotificationsApi
  def users: UsersApi
  def orgs: OrgsApi

object GiteaClient:
  def fromBackend(config: GiteaConfig, backend: Backend[Task]): GiteaClient =
    new SttpGiteaClient(config, backend)
