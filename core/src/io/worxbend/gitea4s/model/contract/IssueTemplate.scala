package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueTemplate(
    @jsonField("about") about: Option[String] = None,
    @jsonField("assignees") assignees: Option[IssueTemplateStringSlice] = None,
    @jsonField("body") body: Option[List[IssueFormField]] = None,
    @jsonField("content") content: Option[String] = None,
    @jsonField("file_name") fileName: Option[String] = None,
    @jsonField("labels") labels: Option[IssueTemplateStringSlice] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("title") title: Option[String] = None
)

object IssueTemplate:
  given JsonCodec[IssueTemplate] = DeriveJsonCodec.gen[IssueTemplate]
