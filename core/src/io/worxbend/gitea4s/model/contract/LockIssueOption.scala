package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class LockIssueOption(
    @jsonField("lock_reason") lockReason: Option[String] = None
)

object LockIssueOption:
  given JsonCodec[LockIssueOption] = DeriveJsonCodec.gen[LockIssueOption]
