package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueLabelsOption(
    @jsonField("labels") labels: Option[List[zio.json.ast.Json]] = None
)

object IssueLabelsOption:
  given JsonCodec[IssueLabelsOption] = DeriveJsonCodec.gen[IssueLabelsOption]
