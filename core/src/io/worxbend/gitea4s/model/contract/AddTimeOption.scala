package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class AddTimeOption(
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("time") time: Long,
    @jsonField("user_name") userName: Option[String] = None
)

object AddTimeOption:
  given JsonCodec[AddTimeOption] = DeriveJsonCodec.gen[AddTimeOption]
