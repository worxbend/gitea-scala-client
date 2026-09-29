package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class AddCollaboratorOption(
    @jsonField("permission") permission: Option[String] = None
)

object AddCollaboratorOption:
  given JsonCodec[AddCollaboratorOption] = DeriveJsonCodec.gen[AddCollaboratorOption]
