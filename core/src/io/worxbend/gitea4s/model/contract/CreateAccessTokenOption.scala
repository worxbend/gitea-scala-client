package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateAccessTokenOption(
    @jsonField("name") name: String,
    @jsonField("scopes") scopes: Option[List[String]] = None
)

object CreateAccessTokenOption:
  given JsonCodec[CreateAccessTokenOption] = DeriveJsonCodec.gen[CreateAccessTokenOption]
