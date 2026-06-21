package io.worxbend.gitea4s.http

import zio.Chunk

final case class ArchiveParams(
    path: Chunk[String] = Chunk.empty
)

object ArchiveParams:
  val default: ArchiveParams = ArchiveParams()
