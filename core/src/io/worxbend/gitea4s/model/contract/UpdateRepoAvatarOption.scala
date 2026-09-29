package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UpdateRepoAvatarOption(
    @jsonField("image") image: Option[String] = None
)

object UpdateRepoAvatarOption:
  given JsonCodec[UpdateRepoAvatarOption] = DeriveJsonCodec.gen[UpdateRepoAvatarOption]
