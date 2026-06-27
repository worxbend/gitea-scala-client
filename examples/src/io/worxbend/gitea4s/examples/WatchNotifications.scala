package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.{NotificationListParams, NotificationStatusType}
import zio.{Chunk, Console, ZIO, ZIOAppDefault}

object WatchNotifications extends ZIOAppDefault:
  def run =
    ExampleSupport.liveConfigFromEnv match
      case Right(None) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLine(ExampleSupport.credentialsHint)
      case Left(error) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)
      case Right(Some(config)) =>
        val params =
          NotificationListParams(
            statusTypes = Chunk(NotificationStatusType.Unread),
            limit = Some(20)
          )

        val program =
          ZIO.serviceWithZIO[GiteaClient] { client =>
            for
              count <- client.notifications.unreadNotificationCount
              threads <- client.notifications.notificationThreads(params).take(20).runCollect
            yield (count, threads)
          }

        Console.printLine(ExampleSupport.referenceLine) *>
          program
            .provideLayer(ZioGiteaBackend.configured(config))
            .foldZIO(
              error => Console.printLineError(s"Reading notifications failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
              (count, threads) =>
                Console.printLine(s"Unread notifications: ${count.unread.getOrElse(0L)}") *>
                  ZIO.foreachDiscard(threads)(thread =>
                    Console.printLine(s"- ${ExampleSupport.notificationSummary(thread)}")
                  )
            )
