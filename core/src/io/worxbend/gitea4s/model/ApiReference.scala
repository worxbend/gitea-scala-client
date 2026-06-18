package io.worxbend.gitea4s.model

import zio.json.*

final case class ApiReference(version: String, document: String)

object ApiReference:
  val gitea1262: ApiReference =
    ApiReference(version = "1.26.2", document = "plugin-redoc-2.yaml")

  given JsonCodec[ApiReference] = DeriveJsonCodec.gen[ApiReference]
