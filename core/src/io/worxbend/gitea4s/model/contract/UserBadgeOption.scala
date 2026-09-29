package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UserBadgeOption(
    @jsonField("badge_slugs") badgeSlugs: Option[List[String]] = None
)

object UserBadgeOption:
  given JsonCodec[UserBadgeOption] = DeriveJsonCodec.gen[UserBadgeOption]
