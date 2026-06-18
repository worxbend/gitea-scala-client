package io.worxbend.gitea4s.model

import zio.Chunk
import zio.json.*

final case class Page[A](
    data: Chunk[A],
    totalCount: Option[Long],
    page: Int,
    pageSize: Int,
    hasNext: Boolean
)

object Page:
  given [A: JsonCodec]: JsonCodec[Page[A]] = DeriveJsonCodec.gen[Page[A]]
