package io.worxbend.gitea4s.model

import zio.json.*

final case class Note(
    commit: Option[Commit] = None,
    message: Option[String] = None
)

object Note:
  given JsonCodec[Note] = DeriveJsonCodec.gen[Note]
