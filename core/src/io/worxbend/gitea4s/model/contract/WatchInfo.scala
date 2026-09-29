package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class WatchInfo(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("ignored") ignored: Option[Boolean] = None,
    @jsonField("reason") reason: Option[zio.json.ast.Json] = None,
    @jsonField("repository_url") repositoryUrl: Option[String] = None,
    @jsonField("subscribed") subscribed: Option[Boolean] = None,
    @jsonField("url") url: Option[String] = None
)

object WatchInfo:
  given JsonCodec[WatchInfo] = DeriveJsonCodec.gen[WatchInfo]
