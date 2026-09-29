package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait OrgsOperations:
  def orgGetAll(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]]
  def orgCreate(body: contract.CreateOrgOption): IO[GiteaError, contract.Organization]
  def orgDelete(org: String): IO[GiteaError, Unit]
  def orgEdit(org: String, body: contract.EditOrgOption): IO[GiteaError, contract.Organization]
  def orgListActivityFeeds(org: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]]
  def orgUpdateAvatar(org: String, body: contract.UpdateUserAvatarOption): IO[GiteaError, Unit]
  def orgDeleteAvatar(org: String): IO[GiteaError, Unit]
  def organizationListBlocks(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]
  def organizationCheckUserBlock(org: String, username: String): IO[GiteaError, Unit]
  def organizationBlockUser(org: String, username: String, note: Option[String] = None): IO[GiteaError, Unit]
  def organizationUnblockUser(org: String, username: String): IO[GiteaError, Unit]
  def orgListHooks(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]]
  def orgCreateHook(org: String, body: contract.CreateHookOption): IO[GiteaError, contract.Hook]
  def orgGetHook(org: String, id: Long): IO[GiteaError, contract.Hook]
  def orgDeleteHook(org: String, id: Long): IO[GiteaError, Unit]
  def orgEditHook(org: String, id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook]
  def orgListLabels(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Label]]
  def orgCreateLabel(org: String, body: contract.CreateLabelOption): IO[GiteaError, contract.Label]
  def orgGetLabel(org: String, id: Long): IO[GiteaError, contract.Label]
  def orgDeleteLabel(org: String, id: Long): IO[GiteaError, Unit]
  def orgEditLabel(org: String, id: Long, body: contract.EditLabelOption): IO[GiteaError, contract.Label]
  def orgIsMember(org: String, username: String): IO[GiteaError, Unit]
  def orgDeleteMember(org: String, username: String): IO[GiteaError, Unit]
  def orgIsPublicMember(org: String, username: String): IO[GiteaError, Unit]
  def orgPublicizeMember(org: String, username: String): IO[GiteaError, Unit]
  def orgConcealMember(org: String, username: String): IO[GiteaError, Unit]
  def renameOrg(org: String, body: contract.RenameOrgOption): IO[GiteaError, Unit]
  def orgListTeams(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Team]]
  def orgCreateTeam(org: String, body: contract.CreateTeamOption): IO[GiteaError, contract.Team]
  def teamSearch(org: String, q: Option[String] = None, includeDesc: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.TeamSearchResult]
