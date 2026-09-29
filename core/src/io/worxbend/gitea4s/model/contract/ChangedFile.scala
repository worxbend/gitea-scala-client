package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ChangedFile(
    @jsonField("additions") additions: Option[Long] = None,
    @jsonField("changes") changes: Option[Long] = None,
    @jsonField("contents_url") contentsUrl: Option[String] = None,
    @jsonField("deletions") deletions: Option[Long] = None,
    @jsonField("filename") filename: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("previous_filename") previousFilename: Option[String] = None,
    @jsonField("raw_url") rawUrl: Option[String] = None,
    @jsonField("status") status: Option[String] = None
)

object ChangedFile:
  given JsonCodec[ChangedFile] = DeriveJsonCodec.gen[ChangedFile]
