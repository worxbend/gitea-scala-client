package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateTagOption(
    @jsonField("message") message: Option[String] = None,
    @jsonField("tag_name") tagName: String,
    @jsonField("target") target: Option[String] = None
)

object CreateTagOption:
  given JsonCodec[CreateTagOption] = DeriveJsonCodec.gen[CreateTagOption]
