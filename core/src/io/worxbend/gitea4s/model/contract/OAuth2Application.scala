package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class OAuth2Application(
    @jsonField("client_id") clientId: Option[String] = None,
    @jsonField("client_secret") clientSecret: Option[String] = None,
    @jsonField("confidential_client") confidentialClient: Option[Boolean] = None,
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("redirect_uris") redirectUris: Option[List[String]] = None,
    @jsonField("skip_secondary_authorization") skipSecondaryAuthorization: Option[Boolean] = None
):
  override def toString: String = "OAuth2Application(<redacted>)"

object OAuth2Application:
  given JsonCodec[OAuth2Application] = DeriveJsonCodec.gen[OAuth2Application]
