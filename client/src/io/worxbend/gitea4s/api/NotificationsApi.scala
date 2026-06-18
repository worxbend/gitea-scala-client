package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.NotificationListParams
import io.worxbend.gitea4s.model.{NotificationCount, NotificationThread}
import zio.IO
import zio.stream.ZStream

trait NotificationsApi:
  def notificationThreads(
      params: NotificationListParams = NotificationListParams.default
  ): ZStream[Any, GiteaError, NotificationThread]

  def unreadNotificationCount: IO[GiteaError, NotificationCount]

  def notificationThread(id: String): IO[GiteaError, NotificationThread]
