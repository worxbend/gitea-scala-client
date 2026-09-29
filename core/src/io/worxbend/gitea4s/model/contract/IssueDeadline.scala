package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueDeadline(
    @jsonField("due_date") dueDate: Option[java.time.Instant] = None
)

object IssueDeadline:
  given JsonCodec[IssueDeadline] = DeriveJsonCodec.gen[IssueDeadline]
