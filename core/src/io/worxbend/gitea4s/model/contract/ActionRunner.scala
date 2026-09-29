package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionRunner(
    @jsonField("busy") busy: Option[Boolean] = None,
    @jsonField("disabled") disabled: Option[Boolean] = None,
    @jsonField("ephemeral") ephemeral: Option[Boolean] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("labels") labels: Option[List[ActionRunnerLabel]] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("status") status: Option[String] = None
)

object ActionRunner:
  given JsonCodec[ActionRunner] = DeriveJsonCodec.gen[ActionRunner]
