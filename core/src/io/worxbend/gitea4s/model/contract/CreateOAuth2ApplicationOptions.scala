package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateOAuth2ApplicationOptions(
    @jsonField("confidential_client") confidentialClient: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("redirect_uris") redirectUris: Option[List[String]] = None,
    @jsonField("skip_secondary_authorization") skipSecondaryAuthorization: Option[Boolean] = None
)

object CreateOAuth2ApplicationOptions:
  given JsonCodec[CreateOAuth2ApplicationOptions] = DeriveJsonCodec.gen[CreateOAuth2ApplicationOptions]
