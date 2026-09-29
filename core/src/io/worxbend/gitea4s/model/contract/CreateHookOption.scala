package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateHookOption(
    @jsonField("active") active: Option[Boolean] = None,
    @jsonField("authorization_header") authorizationHeader: Option[String] = None,
    @jsonField("branch_filter") branchFilter: Option[String] = None,
    @jsonField("config") config: CreateHookOptionConfig,
    @jsonField("events") events: Option[List[String]] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("type") `type`: String
)

object CreateHookOption:
  given JsonCodec[CreateHookOption] = DeriveJsonCodec.gen[CreateHookOption]
