package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CommitMeta(
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object CommitMeta:
  given JsonCodec[CommitMeta] = DeriveJsonCodec.gen[CommitMeta]
