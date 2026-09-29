package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class SearchResults(
    @jsonField("data") data: Option[List[Repository]] = None,
    @jsonField("ok") ok: Option[Boolean] = None
)

object SearchResults:
  given JsonCodec[SearchResults] = DeriveJsonCodec.gen[SearchResults]
