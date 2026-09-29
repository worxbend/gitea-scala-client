package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueConfig(
    @jsonField("blank_issues_enabled") blankIssuesEnabled: Option[Boolean] = None,
    @jsonField("contact_links") contactLinks: Option[List[IssueConfigContactLink]] = None
)

object IssueConfig:
  given JsonCodec[IssueConfig] = DeriveJsonCodec.gen[IssueConfig]
