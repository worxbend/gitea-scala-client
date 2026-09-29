package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditHookOption(
    @jsonField("active") active: Option[Boolean] = None,
    @jsonField("authorization_header") authorizationHeader: Option[String] = None,
    @jsonField("branch_filter") branchFilter: Option[String] = None,
    @jsonField("config") config: Option[Map[String, String]] = None,
    @jsonField("events") events: Option[List[String]] = None,
    @jsonField("name") name: Option[String] = None
)

object EditHookOption:
  given JsonCodec[EditHookOption] = DeriveJsonCodec.gen[EditHookOption]
