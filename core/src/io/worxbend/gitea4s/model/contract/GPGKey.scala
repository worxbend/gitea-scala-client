package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GPGKey(
    @jsonField("can_certify") canCertify: Option[Boolean] = None,
    @jsonField("can_encrypt_comms") canEncryptComms: Option[Boolean] = None,
    @jsonField("can_encrypt_storage") canEncryptStorage: Option[Boolean] = None,
    @jsonField("can_sign") canSign: Option[Boolean] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("emails") emails: Option[List[GPGKeyEmail]] = None,
    @jsonField("expires_at") expiresAt: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("key_id") keyId: Option[String] = None,
    @jsonField("primary_key_id") primaryKeyId: Option[String] = None,
    @jsonField("public_key") publicKey: Option[String] = None,
    @jsonField("subkeys") subkeys: Option[List[GPGKey]] = None,
    @jsonField("verified") verified: Option[Boolean] = None
)

object GPGKey:
  given JsonCodec[GPGKey] = DeriveJsonCodec.gen[GPGKey]
