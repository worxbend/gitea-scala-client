package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CommitStatus(
    @jsonField("context") context: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("creator") creator: Option[User] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("status") status: Option[String] = None,
    @jsonField("target_url") targetUrl: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None
)

object CommitStatus:
  given JsonCodec[CommitStatus] = DeriveJsonCodec.gen[CommitStatus]
