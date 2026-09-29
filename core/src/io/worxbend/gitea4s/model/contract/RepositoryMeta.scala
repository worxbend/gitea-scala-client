package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RepositoryMeta(
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("owner") owner: Option[String] = None
)

object RepositoryMeta:
  given JsonCodec[RepositoryMeta] = DeriveJsonCodec.gen[RepositoryMeta]
