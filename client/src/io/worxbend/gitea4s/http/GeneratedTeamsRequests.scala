package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedTeamsRequests:
  def orgGetTeam(config: GiteaConfig, id: Long): GiteaRequest[contract.Team] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgGetTeam, List("teams", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Team](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgDeleteTeam(config: GiteaConfig, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgDeleteTeam, List("teams", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgEditTeam(config: GiteaConfig, id: Int, body: contract.EditTeamOption): GiteaRequest[contract.Team] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgEditTeam, List("teams", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Team](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListTeamActivityFeeds(config: GiteaConfig, id: Long, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Activity]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListTeamActivityFeeds, List("teams", id.toString, "activities", "feeds"),
      date.toList.map(value => ("date", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Activity]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListTeamMembers(config: GiteaConfig, id: Long, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.User]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListTeamMembers, List("teams", id.toString, "members"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.User]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListTeamMember(config: GiteaConfig, id: Long, username: String): GiteaRequest[contract.User] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListTeamMember, List("teams", id.toString, "members", username.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.User](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgAddTeamMember(config: GiteaConfig, id: Long, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgAddTeamMember, List("teams", id.toString, "members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgRemoveTeamMember(config: GiteaConfig, id: Long, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgRemoveTeamMember, List("teams", id.toString, "members", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListTeamRepos(config: GiteaConfig, id: Long, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListTeamRepos, List("teams", id.toString, "repos"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListTeamRepo(config: GiteaConfig, id: Long, org: String, repo: String): GiteaRequest[contract.Repository] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListTeamRepo, List("teams", id.toString, "repos", org.toString, repo.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Repository](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgAddTeamRepository(config: GiteaConfig, id: Long, org: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgAddTeamRepository, List("teams", id.toString, "repos", org.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgRemoveTeamRepository(config: GiteaConfig, id: Long, org: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgRemoveTeamRepository, List("teams", id.toString, "repos", org.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)
