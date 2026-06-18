package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.api.OrgsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, IssueListParams, RepoListParams, UserSearchParams}
import io.worxbend.gitea4s.model.{Issue, Organization, Repository, User}
import sttp.client4.Backend
import zio.{Chunk, IO, Task}
import zio.stream.ZStream

final class SttpGiteaClient(config: GiteaConfig, backend: Backend[Task]) extends GiteaClient:
  private val executor = GiteaRequestExecutor(backend)

  override val orgs: OrgsApi =
    new OrgsApi:
      override def get(org: String): IO[GiteaError, Organization] =
        executor.send(GiteaRequests.organization(config, org))

      override def members(org: String): ZStream[Any, GiteaError, User] =
        Pagination.paginated { page =>
          executor.send(GiteaRequests.organizationMembers(config, org, page))
        }

  override def me: IO[GiteaError, User] =
    executor.send(GiteaRequests.currentUser(config))

  override def get(username: String): IO[GiteaError, User] =
    executor.send(GiteaRequests.user(config, username))

  override def followers(username: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userFollowers(config, username, page))
    }

  override def following(username: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userFollowing(config, username, page))
    }

  override def search(params: UserSearchParams): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userSearch(config, params.copy(page = Some(page))))
    }

  override def get(owner: String, repo: String): IO[GiteaError, Repository] =
    executor.send(GiteaRequests.repository(config, owner, repo))

  override def list(
      owner: String,
      params: RepoListParams
  ): ZStream[Any, GiteaError, Repository] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userRepos(config, owner, params.copy(page = Some(page))))
    }

  override def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoTopics(config, owner, repo, page))
    }.runCollect

  override def get(owner: String, repo: String, index: Long): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.issue(config, owner, repo, index))

  override def list(
      owner: String,
      repo: String,
      params: IssueListParams
  ): ZStream[Any, GiteaError, Issue] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issues(config, owner, repo, params.copy(page = Some(page))))
    }
