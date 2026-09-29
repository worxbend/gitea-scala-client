package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PackageFile(
    @jsonField("id") id: Option[Long] = None,
    @jsonField("md5") md5: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("sha1") sha1: Option[String] = None,
    @jsonField("sha256") sha256: Option[String] = None,
    @jsonField("sha512") sha512: Option[String] = None,
    @jsonField("size") size: Option[Long] = None
)

object PackageFile:
  given JsonCodec[PackageFile] = DeriveJsonCodec.gen[PackageFile]
