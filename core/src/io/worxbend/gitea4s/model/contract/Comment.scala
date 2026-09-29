package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Comment(
    @jsonField("assets") assets: Option[List[Attachment]] = None,
    @jsonField("body") body: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("issue_url") issueUrl: Option[String] = None,
    @jsonField("original_author") originalAuthor: Option[String] = None,
    @jsonField("original_author_id") originalAuthorId: Option[Long] = None,
    @jsonField("pull_request_url") pullRequestUrl: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("user") user: Option[User] = None
)

object Comment:
  given JsonCodec[Comment] = DeriveJsonCodec.gen[Comment]
