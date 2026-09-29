package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait ReleasesOperations:
  def repoCreateRelease(owner: String, repo: String, body: contract.CreateReleaseOption): IO[GiteaError, contract.Release]
  def repoDeleteReleaseByTag(owner: String, repo: String, tag: String): IO[GiteaError, Unit]
  def repoDeleteRelease(owner: String, repo: String, id: Long): IO[GiteaError, Unit]
  def repoEditRelease(owner: String, repo: String, id: Long, body: contract.EditReleaseOption): IO[GiteaError, contract.Release]
  def repoDeleteReleaseAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, Unit]
  def repoEditReleaseAttachment(owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment]
