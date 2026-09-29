package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateOrgOption(
    @jsonField("description") description: Option[String] = None,
    @jsonField("email") email: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("location") location: Option[String] = None,
    @jsonField("repo_admin_change_team_access") repoAdminChangeTeamAccess: Option[Boolean] = None,
    @jsonField("username") username: String,
    @jsonField("visibility") visibility: Option[String] = None,
    @jsonField("website") website: Option[String] = None
)

object CreateOrgOption:
  given JsonCodec[CreateOrgOption] = DeriveJsonCodec.gen[CreateOrgOption]
