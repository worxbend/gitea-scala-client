package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Activity(
    @jsonField("act_user") actUser: Option[User] = None,
    @jsonField("act_user_id") actUserId: Option[Long] = None,
    @jsonField("comment") comment: Option[Comment] = None,
    @jsonField("comment_id") commentId: Option[Long] = None,
    @jsonField("content") content: Option[String] = None,
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("is_private") isPrivate: Option[Boolean] = None,
    @jsonField("op_type") opType: Option[String] = None,
    @jsonField("ref_name") refName: Option[String] = None,
    @jsonField("repo") repo: Option[Repository] = None,
    @jsonField("repo_id") repoId: Option[Long] = None,
    @jsonField("user_id") userId: Option[Long] = None
)

object Activity:
  given JsonCodec[Activity] = DeriveJsonCodec.gen[Activity]
