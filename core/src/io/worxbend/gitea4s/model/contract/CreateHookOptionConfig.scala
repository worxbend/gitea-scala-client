package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateHookOptionConfig(value: Map[String, String])

object CreateHookOptionConfig:
  given JsonCodec[CreateHookOptionConfig] = JsonCodec(
    summon[JsonEncoder[Map[String, String]]].contramap(_.value),
    summon[JsonDecoder[Map[String, String]]].map(CreateHookOptionConfig.apply)
  )
