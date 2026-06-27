package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.NotificationListParams
import io.worxbend.gitea4s.model.{NotificationCount, NotificationThread}
import zio.IO
import zio.stream.ZStream

/** Notification operations, reached through `client.notifications`. */
trait NotificationsApi:
  def list(
      params: NotificationListParams = NotificationListParams.default
  ): ZStream[Any, GiteaError, NotificationThread]

  def unreadCount: IO[GiteaError, NotificationCount]

  def thread(id: String): IO[GiteaError, NotificationThread]
