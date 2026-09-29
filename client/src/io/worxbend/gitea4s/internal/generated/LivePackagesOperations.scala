package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.PackagesOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedPackagesRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LivePackagesOperations extends PackagesOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def listPackages(owner: String, page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.PackageType] = None, q: Option[String] = None): IO[GiteaError, zio.Chunk[contract.Package]] =
    executor.send(GeneratedPackagesRequests.listPackages(config, owner, page, limit, `type`, q))

  override def listPackageVersions(owner: String, `type`: String, name: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Package]] =
    executor.send(GeneratedPackagesRequests.listPackageVersions(config, owner, `type`, name, page, limit))

  override def deletePackage(owner: String, `type`: String, name: String): IO[GiteaError, Unit] =
    executor.send(GeneratedPackagesRequests.deletePackage(config, owner, `type`, name))

  override def getLatestPackageVersion(owner: String, `type`: String, name: String): IO[GiteaError, contract.Package] =
    executor.send(GeneratedPackagesRequests.getLatestPackageVersion(config, owner, `type`, name))

  override def linkPackage(owner: String, `type`: String, name: String, repoName: String): IO[GiteaError, Unit] =
    executor.send(GeneratedPackagesRequests.linkPackage(config, owner, `type`, name, repoName))

  override def unlinkPackage(owner: String, `type`: String, name: String): IO[GiteaError, Unit] =
    executor.send(GeneratedPackagesRequests.unlinkPackage(config, owner, `type`, name))

  override def getPackage(owner: String, `type`: String, name: String, version: String): IO[GiteaError, contract.Package] =
    executor.send(GeneratedPackagesRequests.getPackage(config, owner, `type`, name, version))

  override def deletePackageVersion(owner: String, `type`: String, name: String, version: String): IO[GiteaError, Unit] =
    executor.send(GeneratedPackagesRequests.deletePackageVersion(config, owner, `type`, name, version))

  override def listPackageFiles(owner: String, `type`: String, name: String, version: String): IO[GiteaError, zio.Chunk[contract.PackageFile]] =
    executor.send(GeneratedPackagesRequests.listPackageFiles(config, owner, `type`, name, version))
