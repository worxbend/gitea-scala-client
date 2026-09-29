package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateGPGKeyOption(
    @jsonField("armored_public_key") armoredPublicKey: String,
    @jsonField("armored_signature") armoredSignature: Option[String] = None
)

object CreateGPGKeyOption:
  given JsonCodec[CreateGPGKeyOption] = DeriveJsonCodec.gen[CreateGPGKeyOption]
