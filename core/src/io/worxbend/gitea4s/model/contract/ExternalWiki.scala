package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ExternalWiki(
    @jsonField("external_wiki_url") externalWikiUrl: Option[String] = None
)

object ExternalWiki:
  given JsonCodec[ExternalWiki] = DeriveJsonCodec.gen[ExternalWiki]
