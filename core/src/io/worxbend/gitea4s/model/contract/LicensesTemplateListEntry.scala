package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class LicensesTemplateListEntry(
    @jsonField("key") key: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object LicensesTemplateListEntry:
  given JsonCodec[LicensesTemplateListEntry] = DeriveJsonCodec.gen[LicensesTemplateListEntry]
