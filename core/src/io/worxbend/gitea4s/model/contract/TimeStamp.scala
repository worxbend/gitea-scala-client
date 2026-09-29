package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TimeStamp(value: Long)

object TimeStamp:
  given JsonCodec[TimeStamp] = JsonCodec(
    summon[JsonEncoder[Long]].contramap(_.value),
    summon[JsonDecoder[Long]].map(TimeStamp.apply)
  )
