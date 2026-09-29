package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedReleasesRequests:
  def repoCreateRelease(config: GiteaConfig, owner: String, repo: String, body: contract.CreateReleaseOption): GiteaRequest[contract.Release] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateRelease, List("repos", owner.toString, repo.toString, "releases"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Release](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteReleaseByTag(config: GiteaConfig, owner: String, repo: String, tag: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteReleaseByTag, List("repos", owner.toString, repo.toString, "releases", "tags", tag.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteRelease(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteRelease, List("repos", owner.toString, repo.toString, "releases", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoEditRelease(config: GiteaConfig, owner: String, repo: String, id: Long, body: contract.EditReleaseOption): GiteaRequest[contract.Release] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoEditRelease, List("repos", owner.toString, repo.toString, "releases", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Release](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteReleaseAttachment(config: GiteaConfig, owner: String, repo: String, id: Long, attachmentId: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteReleaseAttachment, List("repos", owner.toString, repo.toString, "releases", id.toString, "assets", attachmentId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoEditReleaseAttachment(config: GiteaConfig, owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): GiteaRequest[contract.Attachment] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoEditReleaseAttachment, List("repos", owner.toString, repo.toString, "releases", id.toString, "assets", attachmentId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Attachment](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)
