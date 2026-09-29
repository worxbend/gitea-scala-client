package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateActionWorkflowDispatch(
    @jsonField("inputs") inputs: Option[Map[String, String]] = None,
    @jsonField("ref") ref: String
)

object CreateActionWorkflowDispatch:
  given JsonCodec[CreateActionWorkflowDispatch] = DeriveJsonCodec.gen[CreateActionWorkflowDispatch]
