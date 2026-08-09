package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.http.{NotificationListParams, NotificationStatusType}
import zio.Chunk

object ListNotifications extends ExampleApp:
  private val params =
    NotificationListParams(
      statusTypes = Chunk(NotificationStatusType.Unread),
      limit = Some(20)
    )

  def run =
    ExampleSupport.runExample("Reading notifications") { client =>
      for
        count <- client.notifications.unreadCount
        threads <- client.notifications.list(params).take(20).runCollect
      yield s"Unread notifications: ${count.unread.getOrElse(0L)}" +:
        threads.map(thread => s"- ${ExampleSupport.notificationSummary(thread)}")
    }
