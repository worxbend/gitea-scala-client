package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Package(
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("creator") creator: Option[User] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("owner") owner: Option[User] = None,
    @jsonField("repository") repository: Option[Repository] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("version") version: Option[String] = None
)

object Package:
  given JsonCodec[Package] = DeriveJsonCodec.gen[Package]
