package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UserSettings(
    @jsonField("description") description: Option[String] = None,
    @jsonField("diff_view_style") diffViewStyle: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("hide_activity") hideActivity: Option[Boolean] = None,
    @jsonField("hide_email") hideEmail: Option[Boolean] = None,
    @jsonField("language") language: Option[String] = None,
    @jsonField("location") location: Option[String] = None,
    @jsonField("theme") theme: Option[String] = None,
    @jsonField("website") website: Option[String] = None
)

object UserSettings:
  given JsonCodec[UserSettings] = DeriveJsonCodec.gen[UserSettings]
