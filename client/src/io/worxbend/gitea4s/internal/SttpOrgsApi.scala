package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.OrgsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, RepoListParams}
import io.worxbend.gitea4s.model.{Organization, Repository, User}
import zio.IO
import zio.stream.ZStream

private[gitea4s] final class SttpOrgsApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends OrgsApi:
  override def get(org: String): IO[GiteaError, Organization] =
    executor.send(GiteaRequests.organization(config, org))

  override def members(org: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.organizationMembers(config, org, page))
    }

  override def publicMembers(org: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.organizationPublicMembers(config, org, page))
    }

  override def repos(org: String, params: RepoListParams): ZStream[Any, GiteaError, Repository] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.organizationRepos(config, org, params.copy(page = Some(page))))
    }
