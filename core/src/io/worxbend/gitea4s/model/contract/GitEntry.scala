package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GitEntry(
    @jsonField("mode") mode: Option[String] = None,
    @jsonField("path") path: Option[String] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("size") size: Option[Long] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object GitEntry:
  given JsonCodec[GitEntry] = DeriveJsonCodec.gen[GitEntry]
