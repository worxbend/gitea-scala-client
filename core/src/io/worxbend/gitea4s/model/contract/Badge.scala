package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Badge(
    @jsonField("description") description: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("image_url") imageUrl: Option[String] = None,
    @jsonField("slug") slug: Option[String] = None
)

object Badge:
  given JsonCodec[Badge] = DeriveJsonCodec.gen[Badge]
