package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditTagProtectionOption(
    @jsonField("name_pattern") namePattern: Option[String] = None,
    @jsonField("whitelist_teams") whitelistTeams: Option[List[String]] = None,
    @jsonField("whitelist_usernames") whitelistUsernames: Option[List[String]] = None
)

object EditTagProtectionOption:
  given JsonCodec[EditTagProtectionOption] = DeriveJsonCodec.gen[EditTagProtectionOption]
