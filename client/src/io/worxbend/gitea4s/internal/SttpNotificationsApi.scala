package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.NotificationsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{GiteaRequests, NotificationListParams}
import io.worxbend.gitea4s.model.{NotificationCount, NotificationThread}
import zio.IO
import zio.stream.ZStream

private[gitea4s] final class SttpNotificationsApi(config: GiteaConfig, executor: GiteaRequestExecutor)
    extends NotificationsApi:
  override def list(
      params: NotificationListParams = NotificationListParams.default
  ): ZStream[Any, GiteaError, NotificationThread] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.notifications(config, params.copy(page = Some(page))))
    }

  override def unreadCount: IO[GiteaError, NotificationCount] =
    executor.send(GiteaRequests.notificationCount(config))

  override def thread(id: String): IO[GiteaError, NotificationThread] =
    executor.send(GiteaRequests.notificationThread(config, id))
