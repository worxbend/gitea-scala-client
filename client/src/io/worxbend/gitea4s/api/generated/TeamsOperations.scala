package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait TeamsOperations:
  def orgGetTeam(id: Long): IO[GiteaError, contract.Team]
  def orgDeleteTeam(id: Long): IO[GiteaError, Unit]
  def orgEditTeam(id: Int, body: contract.EditTeamOption): IO[GiteaError, contract.Team]
  def orgListTeamActivityFeeds(id: Long, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]]
  def orgListTeamMembers(id: Long, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]
  def orgListTeamMember(id: Long, username: String): IO[GiteaError, contract.User]
  def orgAddTeamMember(id: Long, username: String): IO[GiteaError, Unit]
  def orgRemoveTeamMember(id: Long, username: String): IO[GiteaError, Unit]
  def orgListTeamRepos(id: Long, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]
  def orgListTeamRepo(id: Long, org: String, repo: String): IO[GiteaError, contract.Repository]
  def orgAddTeamRepository(id: Long, org: String, repo: String): IO[GiteaError, Unit]
  def orgRemoveTeamRepository(id: Long, org: String, repo: String): IO[GiteaError, Unit]
