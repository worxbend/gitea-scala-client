package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionVariable(
    @jsonField("data") data: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("owner_id") ownerId: Option[Long] = None,
    @jsonField("repo_id") repoId: Option[Long] = None
)

object ActionVariable:
  given JsonCodec[ActionVariable] = DeriveJsonCodec.gen[ActionVariable]
