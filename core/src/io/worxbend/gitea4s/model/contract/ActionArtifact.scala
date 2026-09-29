package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ActionArtifact(
    @jsonField("archive_download_url") archiveDownloadUrl: Option[String] = None,
    @jsonField("created_at") createdAt: Option[java.time.Instant] = None,
    @jsonField("expired") expired: Option[Boolean] = None,
    @jsonField("expires_at") expiresAt: Option[java.time.Instant] = None,
    @jsonField("id") id: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("size_in_bytes") sizeInBytes: Option[Long] = None,
    @jsonField("updated_at") updatedAt: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("workflow_run") workflowRun: Option[ActionWorkflowRun] = None
)

object ActionArtifact:
  given JsonCodec[ActionArtifact] = DeriveJsonCodec.gen[ActionArtifact]
