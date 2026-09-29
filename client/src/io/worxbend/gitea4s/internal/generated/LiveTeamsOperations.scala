package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.TeamsOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedTeamsRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveTeamsOperations extends TeamsOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def orgGetTeam(id: Long): IO[GiteaError, contract.Team] =
    executor.send(GeneratedTeamsRequests.orgGetTeam(config, id))

  override def orgDeleteTeam(id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedTeamsRequests.orgDeleteTeam(config, id))

  override def orgEditTeam(id: Int, body: contract.EditTeamOption): IO[GiteaError, contract.Team] =
    executor.send(GeneratedTeamsRequests.orgEditTeam(config, id, body))

  override def orgListTeamActivityFeeds(id: Long, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]] =
    executor.send(GeneratedTeamsRequests.orgListTeamActivityFeeds(config, id, date, page, limit))

  override def orgListTeamMembers(id: Long, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]] =
    executor.send(GeneratedTeamsRequests.orgListTeamMembers(config, id, page, limit))

  override def orgListTeamMember(id: Long, username: String): IO[GiteaError, contract.User] =
    executor.send(GeneratedTeamsRequests.orgListTeamMember(config, id, username))

  override def orgAddTeamMember(id: Long, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedTeamsRequests.orgAddTeamMember(config, id, username))

  override def orgRemoveTeamMember(id: Long, username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedTeamsRequests.orgRemoveTeamMember(config, id, username))

  override def orgListTeamRepos(id: Long, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedTeamsRequests.orgListTeamRepos(config, id, page, limit))

  override def orgListTeamRepo(id: Long, org: String, repo: String): IO[GiteaError, contract.Repository] =
    executor.send(GeneratedTeamsRequests.orgListTeamRepo(config, id, org, repo))

  override def orgAddTeamRepository(id: Long, org: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedTeamsRequests.orgAddTeamRepository(config, id, org, repo))

  override def orgRemoveTeamRepository(id: Long, org: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedTeamsRequests.orgRemoveTeamRepository(config, id, org, repo))
