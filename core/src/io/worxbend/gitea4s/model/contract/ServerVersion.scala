package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ServerVersion(
    @jsonField("version") version: Option[String] = None
)

object ServerVersion:
  given JsonCodec[ServerVersion] = DeriveJsonCodec.gen[ServerVersion]
