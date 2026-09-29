package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueConfigContactLink(
    @jsonField("about") about: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object IssueConfigContactLink:
  given JsonCodec[IssueConfigContactLink] = DeriveJsonCodec.gen[IssueConfigContactLink]
