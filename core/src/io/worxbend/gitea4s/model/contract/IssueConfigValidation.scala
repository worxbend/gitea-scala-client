package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueConfigValidation(
    @jsonField("message") message: Option[String] = None,
    @jsonField("valid") valid: Option[Boolean] = None
)

object IssueConfigValidation:
  given JsonCodec[IssueConfigValidation] = DeriveJsonCodec.gen[IssueConfigValidation]
