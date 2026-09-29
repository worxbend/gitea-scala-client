package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditTeamOption(
    @jsonField("can_create_org_repo") canCreateOrgRepo: Option[Boolean] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("includes_all_repositories") includesAllRepositories: Option[Boolean] = None,
    @jsonField("name") name: String,
    @jsonField("permission") permission: Option[String] = None,
    @jsonField("units") units: Option[List[String]] = None,
    @jsonField("units_map") unitsMap: Option[Map[String, String]] = None,
    @jsonField("visibility") visibility: Option[String] = None
)

object EditTeamOption:
  given JsonCodec[EditTeamOption] = DeriveJsonCodec.gen[EditTeamOption]
