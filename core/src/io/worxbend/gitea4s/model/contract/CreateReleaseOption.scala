package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateReleaseOption(
    @jsonField("body") body: Option[String] = None,
    @jsonField("draft") draft: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("prerelease") prerelease: Option[Boolean] = None,
    @jsonField("tag_message") tagMessage: Option[String] = None,
    @jsonField("tag_name") tagName: String,
    @jsonField("target_commitish") targetCommitish: Option[String] = None
)

object CreateReleaseOption:
  given JsonCodec[CreateReleaseOption] = DeriveJsonCodec.gen[CreateReleaseOption]
