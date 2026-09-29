package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionRunnerLabel(
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None
)

object ActionRunnerLabel:
  given JsonCodec[ActionRunnerLabel] = DeriveJsonCodec.gen[ActionRunnerLabel]
