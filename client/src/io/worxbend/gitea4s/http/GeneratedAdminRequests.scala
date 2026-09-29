package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedAdminRequests:
  def adminCronList(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Cron]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCronList, List("admin", "cron"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Cron]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCronRun(config: GiteaConfig, task: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCronRun, List("admin", "cron", task.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminGetAllEmails(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Email]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminGetAllEmails, List("admin", "emails"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Email]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminSearchEmails(config: GiteaConfig, q: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Email]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminSearchEmails, List("admin", "emails", "search"),
      q.toList.map(value => ("q", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Email]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminListHooks(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.AdminHookType] = None): GiteaRequest[zio.Chunk[contract.Hook]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminListHooks, List("admin", "hooks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ `type`.toList.map(value => ("type", value.value)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Hook]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCreateHook(config: GiteaConfig, body: contract.CreateHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCreateHook, List("admin", "hooks"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminGetHook(config: GiteaConfig, id: Long): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminGetHook, List("admin", "hooks", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminDeleteHook(config: GiteaConfig, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminDeleteHook, List("admin", "hooks", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminEditHook(config: GiteaConfig, id: Long, body: contract.EditHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminEditHook, List("admin", "hooks", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminGetAllOrgs(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Organization]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminGetAllOrgs, List("admin", "orgs"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Organization]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminUnadoptedList(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None, pattern: Option[String] = None): GiteaRequest[zio.Chunk[String]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminUnadoptedList, List("admin", "unadopted"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ pattern.toList.map(value => ("pattern", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[String]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminAdoptRepository(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminAdoptRepository, List("admin", "unadopted", owner.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminDeleteUnadoptedRepository(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminDeleteUnadoptedRepository, List("admin", "unadopted", owner.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminSearchUsers(config: GiteaConfig, sourceId: Option[Long] = None, loginName: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None, q: Option[String] = None, visibility: Option[String] = None, isActive: Option[Boolean] = None, isAdmin: Option[Boolean] = None, isRestricted: Option[Boolean] = None, is2faEnabled: Option[Boolean] = None, isProhibitLogin: Option[Boolean] = None): GiteaRequest[zio.Chunk[contract.User]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminSearchUsers, List("admin", "users"),
      sourceId.toList.map(value => ("source_id", value.toString)) ++ loginName.toList.map(value => ("login_name", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ sort.toList.map(value => ("sort", value.toString)) ++ order.toList.map(value => ("order", value.toString)) ++ q.toList.map(value => ("q", value.toString)) ++ visibility.toList.map(value => ("visibility", value.toString)) ++ isActive.toList.map(value => ("is_active", value.toString)) ++ isAdmin.toList.map(value => ("is_admin", value.toString)) ++ isRestricted.toList.map(value => ("is_restricted", value.toString)) ++ is2faEnabled.toList.map(value => ("is_2fa_enabled", value.toString)) ++ isProhibitLogin.toList.map(value => ("is_prohibit_login", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.User]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCreateUser(config: GiteaConfig, body: contract.CreateUserOption): GiteaRequest[contract.User] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCreateUser, List("admin", "users"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.User](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminDeleteUser(config: GiteaConfig, username: String, purge: Option[Boolean] = None): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminDeleteUser, List("admin", "users", username.toString),
      purge.toList.map(value => ("purge", value.toString)), None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminEditUser(config: GiteaConfig, username: String, body: contract.EditUserOption): GiteaRequest[contract.User] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminEditUser, List("admin", "users", username.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.User](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminListUserBadges(config: GiteaConfig, username: String): GiteaRequest[zio.Chunk[contract.Badge]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminListUserBadges, List("admin", "users", username.toString, "badges"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Badge]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminAddUserBadges(config: GiteaConfig, username: String, body: contract.UserBadgeOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminAddUserBadges, List("admin", "users", username.toString, "badges"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminDeleteUserBadges(config: GiteaConfig, username: String, body: contract.UserBadgeOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminDeleteUserBadges, List("admin", "users", username.toString, "badges"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCreatePublicKey(config: GiteaConfig, username: String, body: contract.CreateKeyOption): GiteaRequest[contract.PublicKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCreatePublicKey, List("admin", "users", username.toString, "keys"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.PublicKey](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminDeleteUserPublicKey(config: GiteaConfig, username: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminDeleteUserPublicKey, List("admin", "users", username.toString, "keys", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCreateOrg(config: GiteaConfig, username: String, body: contract.CreateOrgOption): GiteaRequest[contract.Organization] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCreateOrg, List("admin", "users", username.toString, "orgs"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Organization](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminRenameUser(config: GiteaConfig, username: String, body: contract.RenameUserOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminRenameUser, List("admin", "users", username.toString, "rename"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCreateRepo(config: GiteaConfig, username: String, body: contract.CreateRepoOption): GiteaRequest[contract.Repository] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCreateRepo, List("admin", "users", username.toString, "repos"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Repository](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)
