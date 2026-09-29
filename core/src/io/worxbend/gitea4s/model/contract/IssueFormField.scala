package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueFormField(
    @jsonField("attributes") attributes: Option[Map[String, zio.json.ast.Json]] = None,
    @jsonField("id") id: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("validations") validations: Option[Map[String, zio.json.ast.Json]] = None,
    @jsonField("visible") visible: Option[List[String]] = None
)

object IssueFormField:
  given JsonCodec[IssueFormField] = DeriveJsonCodec.gen[IssueFormField]
