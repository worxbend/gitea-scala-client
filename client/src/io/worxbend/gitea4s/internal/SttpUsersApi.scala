package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.UsersApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, UserSearchParams}
import io.worxbend.gitea4s.model.{StopWatch, User}
import zio.IO
import zio.stream.ZStream

private[gitea4s] final class SttpUsersApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends UsersApi:
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

  override def stopwatches: ZStream[Any, GiteaError, StopWatch] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userStopwatches(config, page))
    }
