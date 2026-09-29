package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class IssueTemplateStringSlice(value: List[String])

object IssueTemplateStringSlice:
  given JsonCodec[IssueTemplateStringSlice] = JsonCodec(
    summon[JsonEncoder[List[String]]].contramap(_.value),
    summon[JsonDecoder[List[String]]].map(IssueTemplateStringSlice.apply)
  )
