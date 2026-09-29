package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.ReleasesOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedReleasesRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveReleasesOperations extends ReleasesOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def repoCreateRelease(owner: String, repo: String, body: contract.CreateReleaseOption): IO[GiteaError, contract.Release] =
    executor.send(GeneratedReleasesRequests.repoCreateRelease(config, owner, repo, body))

  override def repoDeleteReleaseByTag(owner: String, repo: String, tag: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReleasesRequests.repoDeleteReleaseByTag(config, owner, repo, tag))

  override def repoDeleteRelease(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedReleasesRequests.repoDeleteRelease(config, owner, repo, id))

  override def repoEditRelease(owner: String, repo: String, id: Long, body: contract.EditReleaseOption): IO[GiteaError, contract.Release] =
    executor.send(GeneratedReleasesRequests.repoEditRelease(config, owner, repo, id, body))

  override def repoDeleteReleaseAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedReleasesRequests.repoDeleteReleaseAttachment(config, owner, repo, id, attachmentId))

  override def repoEditReleaseAttachment(owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment] =
    executor.send(GeneratedReleasesRequests.repoEditReleaseAttachment(config, owner, repo, id, attachmentId, body))
