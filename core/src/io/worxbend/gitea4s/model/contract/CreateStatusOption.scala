package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateStatusOption(
    @jsonField("context") context: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("target_url") targetUrl: Option[String] = None
)

object CreateStatusOption:
  given JsonCodec[CreateStatusOption] = DeriveJsonCodec.gen[CreateStatusOption]
