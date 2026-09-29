package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TopicResponse(
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("repo_count") repoCount: Option[Long] = None,
    @jsonField("topic_name") topicName: Option[String] = None,
    @jsonField("updated") updated: Option[java.time.Instant] = None
)

object TopicResponse:
  given JsonCodec[TopicResponse] = DeriveJsonCodec.gen[TopicResponse]
