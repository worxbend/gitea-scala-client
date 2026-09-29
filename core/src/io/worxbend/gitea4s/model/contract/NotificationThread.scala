package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NotificationThread(
    @jsonField("id") id: Option[Long] = None,
    @jsonField("pinned") pinned: Option[Boolean] = None,
    @jsonField("repository") repository: Option[Repository] = None,
    @jsonField("subject") subject: Option[NotificationSubject] = None,
    @jsonField("unread") unread: Option[Boolean] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None
)

object NotificationThread:
  given JsonCodec[NotificationThread] = DeriveJsonCodec.gen[NotificationThread]
