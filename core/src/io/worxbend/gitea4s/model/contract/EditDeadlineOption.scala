package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditDeadlineOption(
    @jsonField("due_date") dueDate: java.time.Instant
)

object EditDeadlineOption:
  given JsonCodec[EditDeadlineOption] = DeriveJsonCodec.gen[EditDeadlineOption]
