package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait AdminOperations:
  def adminCronList(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Cron]]
  def adminCronRun(task: String): IO[GiteaError, Unit]
  def adminGetAllEmails(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Email]]
  def adminSearchEmails(q: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Email]]
  def adminListHooks(page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.AdminHookType] = None): IO[GiteaError, zio.Chunk[contract.Hook]]
  def adminCreateHook(body: contract.CreateHookOption): IO[GiteaError, contract.Hook]
  def adminGetHook(id: Long): IO[GiteaError, contract.Hook]
  def adminDeleteHook(id: Long): IO[GiteaError, Unit]
  def adminEditHook(id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook]
  def adminGetAllOrgs(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]]
  def adminUnadoptedList(page: Option[Int] = None, limit: Option[Int] = None, pattern: Option[String] = None): IO[GiteaError, zio.Chunk[String]]
  def adminAdoptRepository(owner: String, repo: String): IO[GiteaError, Unit]
  def adminDeleteUnadoptedRepository(owner: String, repo: String): IO[GiteaError, Unit]
  def adminSearchUsers(sourceId: Option[Long] = None, loginName: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None, q: Option[String] = None, visibility: Option[String] = None, isActive: Option[Boolean] = None, isAdmin: Option[Boolean] = None, isRestricted: Option[Boolean] = None, is2faEnabled: Option[Boolean] = None, isProhibitLogin: Option[Boolean] = None): IO[GiteaError, zio.Chunk[contract.User]]
  def adminCreateUser(body: contract.CreateUserOption): IO[GiteaError, contract.User]
  def adminDeleteUser(username: String, purge: Option[Boolean] = None): IO[GiteaError, Unit]
  def adminEditUser(username: String, body: contract.EditUserOption): IO[GiteaError, contract.User]
  def adminListUserBadges(username: String): IO[GiteaError, zio.Chunk[contract.Badge]]
  def adminAddUserBadges(username: String, body: contract.UserBadgeOption): IO[GiteaError, Unit]
  def adminDeleteUserBadges(username: String, body: contract.UserBadgeOption): IO[GiteaError, Unit]
  def adminCreatePublicKey(username: String, body: contract.CreateKeyOption): IO[GiteaError, contract.PublicKey]
  def adminDeleteUserPublicKey(username: String, id: Long): IO[GiteaError, Unit]
  def adminCreateOrg(username: String, body: contract.CreateOrgOption): IO[GiteaError, contract.Organization]
  def adminRenameUser(username: String, body: contract.RenameUserOption): IO[GiteaError, Unit]
  def adminCreateRepo(username: String, body: contract.CreateRepoOption): IO[GiteaError, contract.Repository]
