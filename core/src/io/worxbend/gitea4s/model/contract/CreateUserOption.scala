package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateUserOption(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("email") email: String,
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("login_name") loginName: Option[String] = None,
    @jsonField("must_change_password") mustChangePassword: Option[Boolean] = None,
    @jsonField("password") password: Option[String] = None,
    @jsonField("restricted") restricted: Option[Boolean] = None,
    @jsonField("send_notify") sendNotify: Option[Boolean] = None,
    @jsonField("source_id") sourceId: Option[Long] = None,
    @jsonField("username") username: String,
    @jsonField("visibility") visibility: Option[String] = None
):
  override def toString: String = "CreateUserOption(<redacted>)"

object CreateUserOption:
  given JsonCodec[CreateUserOption] = DeriveJsonCodec.gen[CreateUserOption]
