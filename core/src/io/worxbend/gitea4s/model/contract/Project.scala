package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Project(
    @jsonField("closed_at") closedAt: Option[java.time.Instant] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("creator_id") creatorId: Option[Long] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("is_closed") isClosed: Option[Boolean] = None,
    @jsonField("owner_id") ownerId: Option[Long] = None,
    @jsonField("repo_id") repoId: Option[Long] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None
)

object Project:
  given JsonCodec[Project] = DeriveJsonCodec.gen[Project]
