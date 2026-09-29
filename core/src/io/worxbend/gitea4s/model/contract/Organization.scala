package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Organization(
    @jsonField("avatar_url") avatarUrl: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("email") email: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("location") location: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("repo_admin_change_team_access") repoAdminChangeTeamAccess: Option[Boolean] = None,
    @jsonField("username") username: Option[String] = None,
    @jsonField("visibility") visibility: Option[String] = None,
    @jsonField("website") website: Option[String] = None
)

object Organization:
  given JsonCodec[Organization] = DeriveJsonCodec.gen[Organization]
