package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RepoCollaboratorPermission(
    @jsonField("permission") permission: Option[String] = None,
    @jsonField("role_name") roleName: Option[String] = None,
    @jsonField("user") user: Option[User] = None
)

object RepoCollaboratorPermission:
  given JsonCodec[RepoCollaboratorPermission] = DeriveJsonCodec.gen[RepoCollaboratorPermission]
