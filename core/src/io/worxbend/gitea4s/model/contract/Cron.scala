package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Cron(
    @jsonField("exec_times") execTimes: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("next") next: Option[java.time.Instant] = None,
    @jsonField("prev") prev: Option[java.time.Instant] = None,
    @jsonField("schedule") schedule: Option[String] = None
)

object Cron:
  given JsonCodec[Cron] = DeriveJsonCodec.gen[Cron]
