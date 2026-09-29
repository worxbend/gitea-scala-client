package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class NotificationSubject(
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("latest_comment_html_url") latestCommentHtmlUrl: Option[String] = None,
    @jsonField("latest_comment_url") latestCommentUrl: Option[String] = None,
    @jsonField("state") state: Option[String] = None,
    @jsonField("title") title: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object NotificationSubject:
  given JsonCodec[NotificationSubject] = DeriveJsonCodec.gen[NotificationSubject]
