package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TeamSearchResult(data: Option[List[Team]] = None, ok: Option[Boolean] = None)

object TeamSearchResult:
  given JsonCodec[TeamSearchResult] = DeriveJsonCodec.gen[TeamSearchResult]
