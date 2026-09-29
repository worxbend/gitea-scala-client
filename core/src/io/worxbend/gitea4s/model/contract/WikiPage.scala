package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class WikiPage(
    @jsonField("commit_count") commitCount: Option[Long] = None,
    @jsonField("content_base64") contentBase64: Option[String] = None,
    @jsonField("footer") footer: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("last_commit") lastCommit: Option[WikiCommit] = None,
    @jsonField("sidebar") sidebar: Option[String] = None,
    @jsonField("sub_url") subUrl: Option[String] = None,
    @jsonField("title") title: Option[String] = None
)

object WikiPage:
  given JsonCodec[WikiPage] = DeriveJsonCodec.gen[WikiPage]
