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
  override def notificationThreads(
      params: NotificationListParams = NotificationListParams.default
  ): ZStream[Any, GiteaError, NotificationThread] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.notifications(config, params.copy(page = Some(page))))
    }

  override def unreadNotificationCount: IO[GiteaError, NotificationCount] =
    executor.send(GiteaRequests.notificationCount(config))

  override def notificationThread(id: String): IO[GiteaError, NotificationThread] =
    executor.send(GiteaRequests.notificationThread(config, id))
