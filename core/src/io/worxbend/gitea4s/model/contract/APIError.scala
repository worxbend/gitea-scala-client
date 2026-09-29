package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class APIError(
    @jsonField("message") message: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object APIError:
  given JsonCodec[APIError] = DeriveJsonCodec.gen[APIError]
