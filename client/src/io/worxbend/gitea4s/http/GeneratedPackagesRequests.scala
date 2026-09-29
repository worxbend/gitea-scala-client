package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedPackagesRequests:
  def listPackages(config: GiteaConfig, owner: String, page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.PackageType] = None, q: Option[String] = None): GiteaRequest[zio.Chunk[contract.Package]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listPackages, List("packages", owner.toString),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ `type`.toList.map(value => ("type", value.value)) ++ q.toList.map(value => ("q", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Package]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listPackageVersions(config: GiteaConfig, owner: String, `type`: String, name: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Package]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listPackageVersions, List("packages", owner.toString, `type`.toString, name.toString),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Package]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deletePackage(config: GiteaConfig, owner: String, `type`: String, name: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deletePackage, List("packages", owner.toString, `type`.toString, name.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getLatestPackageVersion(config: GiteaConfig, owner: String, `type`: String, name: String): GiteaRequest[contract.Package] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getLatestPackageVersion, List("packages", owner.toString, `type`.toString, name.toString, "-", "latest"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Package](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def linkPackage(config: GiteaConfig, owner: String, `type`: String, name: String, repoName: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.linkPackage, List("packages", owner.toString, `type`.toString, name.toString, "-", "link", repoName.toString),
      Nil, None, response => if response.code.code == 201 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def unlinkPackage(config: GiteaConfig, owner: String, `type`: String, name: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.unlinkPackage, List("packages", owner.toString, `type`.toString, name.toString, "-", "unlink"),
      Nil, None, response => if response.code.code == 201 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getPackage(config: GiteaConfig, owner: String, `type`: String, name: String, version: String): GiteaRequest[contract.Package] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getPackage, List("packages", owner.toString, `type`.toString, name.toString, version.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Package](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deletePackageVersion(config: GiteaConfig, owner: String, `type`: String, name: String, version: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deletePackageVersion, List("packages", owner.toString, `type`.toString, name.toString, version.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listPackageFiles(config: GiteaConfig, owner: String, `type`: String, name: String, version: String): GiteaRequest[zio.Chunk[contract.PackageFile]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listPackageFiles, List("packages", owner.toString, `type`.toString, name.toString, version.toString, "files"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.PackageFile]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)
