package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PayloadCommitVerification(
    @jsonField("payload") payload: Option[String] = None,
    @jsonField("reason") reason: Option[String] = None,
    @jsonField("signature") signature: Option[String] = None,
    @jsonField("signer") signer: Option[PayloadUser] = None,
    @jsonField("verified") verified: Option[Boolean] = None
)

object PayloadCommitVerification:
  given JsonCodec[PayloadCommitVerification] = DeriveJsonCodec.gen[PayloadCommitVerification]
