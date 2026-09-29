package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class LicenseTemplateInfo(
    @jsonField("body") body: Option[String] = None,
    @jsonField("implementation") implementation: Option[String] = None,
    @jsonField("key") key: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object LicenseTemplateInfo:
  given JsonCodec[LicenseTemplateInfo] = DeriveJsonCodec.gen[LicenseTemplateInfo]
