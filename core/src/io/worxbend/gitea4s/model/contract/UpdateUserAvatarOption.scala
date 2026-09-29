package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UpdateUserAvatarOption(
    @jsonField("image") image: Option[String] = None
)

object UpdateUserAvatarOption:
  given JsonCodec[UpdateUserAvatarOption] = DeriveJsonCodec.gen[UpdateUserAvatarOption]
