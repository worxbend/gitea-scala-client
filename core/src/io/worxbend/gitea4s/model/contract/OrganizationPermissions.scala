package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class OrganizationPermissions(
    @jsonField("can_create_repository") canCreateRepository: Option[Boolean] = None,
    @jsonField("can_read") canRead: Option[Boolean] = None,
    @jsonField("can_write") canWrite: Option[Boolean] = None,
    @jsonField("is_admin") isAdmin: Option[Boolean] = None,
    @jsonField("is_owner") isOwner: Option[Boolean] = None
)

object OrganizationPermissions:
  given JsonCodec[OrganizationPermissions] = DeriveJsonCodec.gen[OrganizationPermissions]
