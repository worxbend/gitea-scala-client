package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Hook(
    @jsonField("active") active: Option[Boolean] = None,
    @jsonField("authorization_header") authorizationHeader: Option[String] = None,
    @jsonField("branch_filter") branchFilter: Option[String] = None,
    @jsonField("config") config: Option[Map[String, String]] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("events") events: Option[List[String]] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None
)

object Hook:
  given JsonCodec[Hook] = DeriveJsonCodec.gen[Hook]
