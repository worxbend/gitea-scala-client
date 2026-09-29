package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class User(
    @jsonField("active") active: Option[Boolean] = None,
    @jsonField("avatar_url") avatarUrl: Option[String] = None,
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("email") email: Option[String] = None,
    @jsonField("followers_count") followersCount: Option[Long] = None,
    @jsonField("following_count") followingCount: Option[Long] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("is_admin") isAdmin: Option[Boolean] = None,
    @jsonField("language") language: Option[String] = None,
    @jsonField("last_login") lastLogin: Option[java.time.Instant] = None,
    @jsonField("location") location: Option[String] = None,
    @jsonField("login") login: Option[String] = None,
    @jsonField("login_name") loginName: Option[String] = None,
    @jsonField("prohibit_login") prohibitLogin: Option[Boolean] = None,
    @jsonField("restricted") restricted: Option[Boolean] = None,
    @jsonField("source_id") sourceId: Option[Long] = None,
    @jsonField("starred_repos_count") starredReposCount: Option[Long] = None,
    @jsonField("visibility") visibility: Option[String] = None,
    @jsonField("website") website: Option[String] = None
)

object User:
  given JsonCodec[User] = DeriveJsonCodec.gen[User]
