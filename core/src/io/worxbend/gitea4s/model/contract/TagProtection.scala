package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TagProtection(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name_pattern") namePattern: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("whitelist_teams") whitelistTeams: Option[List[String]] = None,
    @jsonField("whitelist_usernames") whitelistUsernames: Option[List[String]] = None
)

object TagProtection:
  given JsonCodec[TagProtection] = DeriveJsonCodec.gen[TagProtection]
