package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.OrgsOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedOrgsRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveOrgsOperations extends OrgsOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def orgGetAll(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]] =
    executor.send(GeneratedOrgsRequests.orgGetAll(config, page, limit))

  override def orgCreate(body: contract.CreateOrgOption): IO[GiteaError, contract.Organization] =
    executor.send(GeneratedOrgsRequests.orgCreate(config, body))

  override def orgDelete(org: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgDelete(config, org))

  override def orgEdit(org: String, body: contract.EditOrgOption): IO[GiteaError, contract.Organization] =
    executor.send(GeneratedOrgsRequests.orgEdit(config, org, body))

  override def orgListActivityFeeds(org: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]] =
    executor.send(GeneratedOrgsRequests.orgListActivityFeeds(config, org, date, page, limit))

  override def orgUpdateAvatar(org: String, body: contract.UpdateUserAvatarOption): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgUpdateAvatar(config, org, body))

  override def orgDeleteAvatar(org: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgDeleteAvatar(config, org))

  override def organizationListBlocks(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]] =
    executor.send(GeneratedOrgsRequests.organizationListBlocks(config, org, page, limit))

  override def organizationCheckUserBlock(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.organizationCheckUserBlock(config, org, username))

  override def organizationBlockUser(org: String, username: String, note: Option[String] = None): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.organizationBlockUser(config, org, username, note))

  override def organizationUnblockUser(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.organizationUnblockUser(config, org, username))

  override def orgListHooks(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]] =
    executor.send(GeneratedOrgsRequests.orgListHooks(config, org, page, limit))

  override def orgCreateHook(org: String, body: contract.CreateHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedOrgsRequests.orgCreateHook(config, org, body))

  override def orgGetHook(org: String, id: Long): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedOrgsRequests.orgGetHook(config, org, id))

  override def orgDeleteHook(org: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgDeleteHook(config, org, id))

  override def orgEditHook(org: String, id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedOrgsRequests.orgEditHook(config, org, id, body))

  override def orgListLabels(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Label]] =
    executor.send(GeneratedOrgsRequests.orgListLabels(config, org, page, limit))

  override def orgCreateLabel(org: String, body: contract.CreateLabelOption): IO[GiteaError, contract.Label] =
    executor.send(GeneratedOrgsRequests.orgCreateLabel(config, org, body))

  override def orgGetLabel(org: String, id: Long): IO[GiteaError, contract.Label] =
    executor.send(GeneratedOrgsRequests.orgGetLabel(config, org, id))

  override def orgDeleteLabel(org: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgDeleteLabel(config, org, id))

  override def orgEditLabel(org: String, id: Long, body: contract.EditLabelOption): IO[GiteaError, contract.Label] =
    executor.send(GeneratedOrgsRequests.orgEditLabel(config, org, id, body))

  override def orgIsMember(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgIsMember(config, org, username))

  override def orgDeleteMember(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgDeleteMember(config, org, username))

  override def orgIsPublicMember(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgIsPublicMember(config, org, username))

  override def orgPublicizeMember(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgPublicizeMember(config, org, username))

  override def orgConcealMember(org: String, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.orgConcealMember(config, org, username))

  override def renameOrg(org: String, body: contract.RenameOrgOption): IO[GiteaError, Unit] =
    executor.send(GeneratedOrgsRequests.renameOrg(config, org, body))

  override def orgListTeams(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Team]] =
    executor.send(GeneratedOrgsRequests.orgListTeams(config, org, page, limit))

  override def orgCreateTeam(org: String, body: contract.CreateTeamOption): IO[GiteaError, contract.Team] =
    executor.send(GeneratedOrgsRequests.orgCreateTeam(config, org, body))

  override def teamSearch(org: String, q: Option[String] = None, includeDesc: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.TeamSearchResult] =
    executor.send(GeneratedOrgsRequests.teamSearch(config, org, q, includeDesc, page, limit))
