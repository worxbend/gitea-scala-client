package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, IssueListParams}
import io.worxbend.gitea4s.model.{Issue, Repository, User}
import sttp.client4.Backend
import zio.{IO, Task}
import zio.stream.ZStream

final class SttpGiteaClient(config: GiteaConfig, backend: Backend[Task]) extends GiteaClient:
  private val executor = GiteaRequestExecutor(backend)

  override def me: IO[GiteaError, User] =
    executor.send(GiteaRequests.currentUser(config))

  override def get(username: String): IO[GiteaError, User] =
    executor.send(GiteaRequests.user(config, username))

  override def get(owner: String, repo: String): IO[GiteaError, Repository] =
    executor.send(GiteaRequests.repository(config, owner, repo))

  override def list(
      owner: String,
      repo: String,
      params: IssueListParams = IssueListParams.default
  ): ZStream[Any, GiteaError, Issue] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issues(config, owner, repo, params.copy(page = Some(page))))
    }
