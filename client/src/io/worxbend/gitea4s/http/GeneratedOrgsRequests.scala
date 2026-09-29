package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedOrgsRequests:
  def orgGetAll(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Organization]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgGetAll, List("orgs"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Organization]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgCreate(config: GiteaConfig, body: contract.CreateOrgOption): GiteaRequest[contract.Organization] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgCreate, List("orgs"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Organization](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgDelete(config: GiteaConfig, org: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgDelete, List("orgs", org.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgEdit(config: GiteaConfig, org: String, body: contract.EditOrgOption): GiteaRequest[contract.Organization] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgEdit, List("orgs", org.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Organization](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListActivityFeeds(config: GiteaConfig, org: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Activity]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListActivityFeeds, List("orgs", org.toString, "activities", "feeds"),
      date.toList.map(value => ("date", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Activity]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgUpdateAvatar(config: GiteaConfig, org: String, body: contract.UpdateUserAvatarOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgUpdateAvatar, List("orgs", org.toString, "avatar"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgDeleteAvatar(config: GiteaConfig, org: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgDeleteAvatar, List("orgs", org.toString, "avatar"),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def organizationListBlocks(config: GiteaConfig, org: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.User]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.organizationListBlocks, List("orgs", org.toString, "blocks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.User]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def organizationCheckUserBlock(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.organizationCheckUserBlock, List("orgs", org.toString, "blocks", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def organizationBlockUser(config: GiteaConfig, org: String, username: String, note: Option[String] = None): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.organizationBlockUser, List("orgs", org.toString, "blocks", username.toString),
      note.toList.map(value => ("note", value.toString)), None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def organizationUnblockUser(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.organizationUnblockUser, List("orgs", org.toString, "blocks", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListHooks(config: GiteaConfig, org: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Hook]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListHooks, List("orgs", org.toString, "hooks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Hook]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgCreateHook(config: GiteaConfig, org: String, body: contract.CreateHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgCreateHook, List("orgs", org.toString, "hooks"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgGetHook(config: GiteaConfig, org: String, id: Long): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgGetHook, List("orgs", org.toString, "hooks", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgDeleteHook(config: GiteaConfig, org: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgDeleteHook, List("orgs", org.toString, "hooks", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgEditHook(config: GiteaConfig, org: String, id: Long, body: contract.EditHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgEditHook, List("orgs", org.toString, "hooks", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListLabels(config: GiteaConfig, org: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Label]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListLabels, List("orgs", org.toString, "labels"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Label]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgCreateLabel(config: GiteaConfig, org: String, body: contract.CreateLabelOption): GiteaRequest[contract.Label] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgCreateLabel, List("orgs", org.toString, "labels"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Label](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgGetLabel(config: GiteaConfig, org: String, id: Long): GiteaRequest[contract.Label] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgGetLabel, List("orgs", org.toString, "labels", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Label](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgDeleteLabel(config: GiteaConfig, org: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgDeleteLabel, List("orgs", org.toString, "labels", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgEditLabel(config: GiteaConfig, org: String, id: Long, body: contract.EditLabelOption): GiteaRequest[contract.Label] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgEditLabel, List("orgs", org.toString, "labels", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Label](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgIsMember(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgIsMember, List("orgs", org.toString, "members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgDeleteMember(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgDeleteMember, List("orgs", org.toString, "members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgIsPublicMember(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgIsPublicMember, List("orgs", org.toString, "public_members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgPublicizeMember(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgPublicizeMember, List("orgs", org.toString, "public_members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgConcealMember(config: GiteaConfig, org: String, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgConcealMember, List("orgs", org.toString, "public_members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def renameOrg(config: GiteaConfig, org: String, body: contract.RenameOrgOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.renameOrg, List("orgs", org.toString, "rename"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListTeams(config: GiteaConfig, org: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Team]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListTeams, List("orgs", org.toString, "teams"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Team]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgCreateTeam(config: GiteaConfig, org: String, body: contract.CreateTeamOption): GiteaRequest[contract.Team] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgCreateTeam, List("orgs", org.toString, "teams"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Team](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def teamSearch(config: GiteaConfig, org: String, q: Option[String] = None, includeDesc: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.TeamSearchResult] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.teamSearch, List("orgs", org.toString, "teams", "search"),
      q.toList.map(value => ("q", value.toString)) ++ includeDesc.toList.map(value => ("include_desc", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.TeamSearchResult](response, sttp.model.StatusCode.Ok), sttp.model.MediaType.ApplicationJson, Accept.Json)
