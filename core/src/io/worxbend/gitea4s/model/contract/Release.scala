package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Release(
    @jsonField("assets") assets: Option[List[Attachment]] = None,
    @jsonField("author") author: Option[User] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("draft") draft: Option[Boolean] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("prerelease") prerelease: Option[Boolean] = None,
    @jsonField("published_at") publishedAt: Option[java.time.Instant] = None,
    @jsonField("tag_name") tagName: Option[String] = None,
    @jsonField("tarball_url") tarballUrl: Option[String] = None,
    @jsonField("target_commitish") targetCommitish: Option[String] = None,
    @jsonField("upload_url") uploadUrl: Option[String] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("zipball_url") zipballUrl: Option[String] = None
)

object Release:
  given JsonCodec[Release] = DeriveJsonCodec.gen[Release]
