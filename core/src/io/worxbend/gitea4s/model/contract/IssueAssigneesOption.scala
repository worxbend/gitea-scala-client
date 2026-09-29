package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueAssigneesOption(
    @jsonField("assignees") assignees: Option[List[String]] = None
)

object IssueAssigneesOption:
  given JsonCodec[IssueAssigneesOption] = DeriveJsonCodec.gen[IssueAssigneesOption]
