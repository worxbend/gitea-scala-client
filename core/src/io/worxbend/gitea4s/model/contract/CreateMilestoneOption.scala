package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateMilestoneOption(
    @jsonField("description") description: Option[String] = None,
    @jsonField("due_on") dueOn: Option[java.time.Instant] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("title") title: Option[String] = None
)

object CreateMilestoneOption:
  given JsonCodec[CreateMilestoneOption] = DeriveJsonCodec.gen[CreateMilestoneOption]
