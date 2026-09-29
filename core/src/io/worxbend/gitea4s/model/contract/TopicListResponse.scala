package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TopicListResponse(
    @jsonField("topics") topics: Option[List[TopicResponse]] = None
)

object TopicListResponse:
  given JsonCodec[TopicListResponse] = DeriveJsonCodec.gen[TopicListResponse]
