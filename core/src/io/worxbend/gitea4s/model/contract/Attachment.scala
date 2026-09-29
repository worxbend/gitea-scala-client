package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Attachment(
    @jsonField("browser_download_url") browserDownloadUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("download_count") downloadCount: Option[Long] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("size") size: Option[Long] = None,
    @jsonField("uuid") uuid: Option[String] = None
)

object Attachment:
  given JsonCodec[Attachment] = DeriveJsonCodec.gen[Attachment]
