package io.worxbend.gitea4s.model

import java.time.Instant

import zio.json.*

final case class ReleaseAsset(
    @jsonField("browser_download_url") browserDownloadUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    @jsonField("download_count") downloadCount: Option[Long] = None,
    id: Option[Long] = None,
    name: Option[String] = None,
    size: Option[Long] = None,
    uuid: Option[String] = None
)

object ReleaseAsset:
  given JsonCodec[ReleaseAsset] = DeriveJsonCodec.gen[ReleaseAsset]
