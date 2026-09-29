package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.AdminOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedAdminRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveAdminOperations extends AdminOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def adminCronList(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Cron]] =
    executor.send(GeneratedAdminRequests.adminCronList(config, page, limit))

  override def adminCronRun(task: String): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminCronRun(config, task))

  override def adminGetAllEmails(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Email]] =
    executor.send(GeneratedAdminRequests.adminGetAllEmails(config, page, limit))

  override def adminSearchEmails(q: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Email]] =
    executor.send(GeneratedAdminRequests.adminSearchEmails(config, q, page, limit))

  override def adminListHooks(page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.AdminHookType] = None): IO[GiteaError, zio.Chunk[contract.Hook]] =
    executor.send(GeneratedAdminRequests.adminListHooks(config, page, limit, `type`))

  override def adminCreateHook(body: contract.CreateHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedAdminRequests.adminCreateHook(config, body))

  override def adminGetHook(id: Long): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedAdminRequests.adminGetHook(config, id))

  override def adminDeleteHook(id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminDeleteHook(config, id))

  override def adminEditHook(id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedAdminRequests.adminEditHook(config, id, body))

  override def adminGetAllOrgs(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]] =
    executor.send(GeneratedAdminRequests.adminGetAllOrgs(config, page, limit))

  override def adminUnadoptedList(page: Option[Int] = None, limit: Option[Int] = None, pattern: Option[String] = None): IO[GiteaError, zio.Chunk[String]] =
    executor.send(GeneratedAdminRequests.adminUnadoptedList(config, page, limit, pattern))

  override def adminAdoptRepository(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminAdoptRepository(config, owner, repo))

  override def adminDeleteUnadoptedRepository(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminDeleteUnadoptedRepository(config, owner, repo))

  override def adminSearchUsers(sourceId: Option[Long] = None, loginName: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None, q: Option[String] = None, visibility: Option[String] = None, isActive: Option[Boolean] = None, isAdmin: Option[Boolean] = None, isRestricted: Option[Boolean] = None, is2faEnabled: Option[Boolean] = None, isProhibitLogin: Option[Boolean] = None): IO[GiteaError, zio.Chunk[contract.User]] =
    executor.send(GeneratedAdminRequests.adminSearchUsers(config, sourceId, loginName, page, limit, sort, order, q, visibility, isActive, isAdmin, isRestricted, is2faEnabled, isProhibitLogin))

  override def adminCreateUser(body: contract.CreateUserOption): IO[GiteaError, contract.User] =
    executor.send(GeneratedAdminRequests.adminCreateUser(config, body))

  override def adminDeleteUser(username: String, purge: Option[Boolean] = None): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminDeleteUser(config, username, purge))

  override def adminEditUser(username: String, body: contract.EditUserOption): IO[GiteaError, contract.User] =
    executor.send(GeneratedAdminRequests.adminEditUser(config, username, body))

  override def adminListUserBadges(username: String): IO[GiteaError, zio.Chunk[contract.Badge]] =
    executor.send(GeneratedAdminRequests.adminListUserBadges(config, username))

  override def adminAddUserBadges(username: String, body: contract.UserBadgeOption): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminAddUserBadges(config, username, body))

  override def adminDeleteUserBadges(username: String, body: contract.UserBadgeOption): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminDeleteUserBadges(config, username, body))

  override def adminCreatePublicKey(username: String, body: contract.CreateKeyOption): IO[GiteaError, contract.PublicKey] =
    executor.send(GeneratedAdminRequests.adminCreatePublicKey(config, username, body))

  override def adminDeleteUserPublicKey(username: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminDeleteUserPublicKey(config, username, id))

  override def adminCreateOrg(username: String, body: contract.CreateOrgOption): IO[GiteaError, contract.Organization] =
    executor.send(GeneratedAdminRequests.adminCreateOrg(config, username, body))

  override def adminRenameUser(username: String, body: contract.RenameUserOption): IO[GiteaError, Unit] =
    executor.send(GeneratedAdminRequests.adminRenameUser(config, username, body))

  override def adminCreateRepo(username: String, body: contract.CreateRepoOption): IO[GiteaError, contract.Repository] =
    executor.send(GeneratedAdminRequests.adminCreateRepo(config, username, body))
