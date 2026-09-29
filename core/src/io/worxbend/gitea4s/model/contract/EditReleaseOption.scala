package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditReleaseOption(
    @jsonField("body") body: Option[String] = None,
    @jsonField("draft") draft: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("prerelease") prerelease: Option[Boolean] = None,
    @jsonField("tag_name") tagName: Option[String] = None,
    @jsonField("target_commitish") targetCommitish: Option[String] = None
)

object EditReleaseOption:
  given JsonCodec[EditReleaseOption] = DeriveJsonCodec.gen[EditReleaseOption]
