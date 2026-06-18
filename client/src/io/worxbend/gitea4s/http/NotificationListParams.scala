package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.model.NotificationSubjectType
import zio.Chunk

import java.time.Instant

enum NotificationStatusType(val queryValue: String):
  case Unread extends NotificationStatusType("unread")
  case Read extends NotificationStatusType("read")
  case Pinned extends NotificationStatusType("pinned")

final case class NotificationListParams(
    all: Option[Boolean] = None,
    statusTypes: Chunk[NotificationStatusType] = Chunk.empty,
    subjectTypes: Chunk[NotificationSubjectType] = Chunk.empty,
    since: Option[Instant] = None,
    before: Option[Instant] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object NotificationListParams:
  val default: NotificationListParams = NotificationListParams()
