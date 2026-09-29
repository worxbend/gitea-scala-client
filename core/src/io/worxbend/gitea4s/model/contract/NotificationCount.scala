package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NotificationCount(
    @jsonField("new") `new`: Option[Long] = None
)

object NotificationCount:
  given JsonCodec[NotificationCount] = DeriveJsonCodec.gen[NotificationCount]
