package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait PackagesOperations:
  def listPackages(owner: String, page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.PackageType] = None, q: Option[String] = None): IO[GiteaError, zio.Chunk[contract.Package]]
  def listPackageVersions(owner: String, `type`: String, name: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Package]]
  def deletePackage(owner: String, `type`: String, name: String): IO[GiteaError, Unit]
  def getLatestPackageVersion(owner: String, `type`: String, name: String): IO[GiteaError, contract.Package]
  def linkPackage(owner: String, `type`: String, name: String, repoName: String): IO[GiteaError, Unit]
  def unlinkPackage(owner: String, `type`: String, name: String): IO[GiteaError, Unit]
  def getPackage(owner: String, `type`: String, name: String, version: String): IO[GiteaError, contract.Package]
  def deletePackageVersion(owner: String, `type`: String, name: String, version: String): IO[GiteaError, Unit]
  def listPackageFiles(owner: String, `type`: String, name: String, version: String): IO[GiteaError, zio.Chunk[contract.PackageFile]]
