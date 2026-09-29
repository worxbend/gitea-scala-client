package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionWorkflow(
    @jsonField("badge_url") badgeUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("deleted_at") deletedAt: Option[java.time.Instant] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[String] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("path") path: Option[String] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None
)

object ActionWorkflow:
  given JsonCodec[ActionWorkflow] = DeriveJsonCodec.gen[ActionWorkflow]
