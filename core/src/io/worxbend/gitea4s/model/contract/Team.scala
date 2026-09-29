package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Team(
    @jsonField("can_create_org_repo") canCreateOrgRepo: Option[Boolean] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("includes_all_repositories") includesAllRepositories: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("organization") organization: Option[Organization] = None,
    @jsonField("permission") permission: Option[String] = None,
    @jsonField("units") units: Option[List[String]] = None,
    @jsonField("units_map") unitsMap: Option[Map[String, String]] = None,
    @jsonField("visibility") visibility: Option[String] = None
)

object Team:
  given JsonCodec[Team] = DeriveJsonCodec.gen[Team]
