package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Note(
    @jsonField("commit") commit: Option[Commit] = None,
    @jsonField("message") message: Option[String] = None
)

object Note:
  given JsonCodec[Note] = DeriveJsonCodec.gen[Note]
