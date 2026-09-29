package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreatePushMirrorOption(
    @jsonField("interval") interval: Option[String] = None,
    @jsonField("remote_address") remoteAddress: Option[String] = None,
    @jsonField("remote_password") remotePassword: Option[String] = None,
    @jsonField("remote_username") remoteUsername: Option[String] = None,
    @jsonField("sync_on_commit") syncOnCommit: Option[Boolean] = None
):
  override def toString: String = "CreatePushMirrorOption(<redacted>)"

object CreatePushMirrorOption:
  given JsonCodec[CreatePushMirrorOption] = DeriveJsonCodec.gen[CreatePushMirrorOption]
