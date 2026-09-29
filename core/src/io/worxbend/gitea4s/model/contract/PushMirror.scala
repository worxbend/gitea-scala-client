package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PushMirror(
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("interval") interval: Option[String] = None,
    @jsonField("last_error") lastError: Option[String] = None,
    @jsonField("last_update") lastUpdate: Option[java.time.Instant] = None,
    @jsonField("remote_address") remoteAddress: Option[String] = None,
    @jsonField("remote_name") remoteName: Option[String] = None,
    @jsonField("repo_name") repoName: Option[String] = None,
    @jsonField("sync_on_commit") syncOnCommit: Option[Boolean] = None
)

object PushMirror:
  given JsonCodec[PushMirror] = DeriveJsonCodec.gen[PushMirror]
