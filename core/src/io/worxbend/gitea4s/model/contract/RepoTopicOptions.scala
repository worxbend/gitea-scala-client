package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RepoTopicOptions(
    @jsonField("topics") topics: Option[List[String]] = None
)

object RepoTopicOptions:
  given JsonCodec[RepoTopicOptions] = DeriveJsonCodec.gen[RepoTopicOptions]
