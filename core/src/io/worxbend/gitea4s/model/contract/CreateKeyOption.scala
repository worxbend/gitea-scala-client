package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateKeyOption(
    @jsonField("key") key: String,
    @jsonField("read_only") readOnly: Option[Boolean] = None,
    @jsonField("title") title: String
)

object CreateKeyOption:
  given JsonCodec[CreateKeyOption] = DeriveJsonCodec.gen[CreateKeyOption]
