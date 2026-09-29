package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class WikiPageMetaData(
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("last_commit") lastCommit: Option[WikiCommit] = None,
    @jsonField("sub_url") subUrl: Option[String] = None,
    @jsonField("title") title: Option[String] = None
)

object WikiPageMetaData:
  given JsonCodec[WikiPageMetaData] = DeriveJsonCodec.gen[WikiPageMetaData]
