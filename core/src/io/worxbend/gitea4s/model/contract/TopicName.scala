package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TopicName(
    @jsonField("topics") topics: Option[List[String]] = None
)

object TopicName:
  given JsonCodec[TopicName] = DeriveJsonCodec.gen[TopicName]
