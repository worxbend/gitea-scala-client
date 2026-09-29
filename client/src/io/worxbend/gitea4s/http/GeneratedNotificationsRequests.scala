package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedNotificationsRequests:
  def notifyReadList(config: GiteaConfig, lastReadAt: Option[java.time.Instant] = None, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None): GiteaRequest[zio.Chunk[contract.NotificationThread]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.notifyReadList, List("notifications"),
      lastReadAt.toList.map(value => ("last_read_at", value.toString)) ++ all.toList.map(value => ("all", value.toString)) ++ statusTypes.toList.flatMap(_.map(value => ("status-types", value.toString))) ++ toStatus.toList.map(value => ("to-status", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.NotificationThread]](response, sttp.model.StatusCode(205)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def notifyReadThread(config: GiteaConfig, id: String, toStatus: Option[String] = None): GiteaRequest[contract.NotificationThread] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.notifyReadThread, List("notifications", "threads", id.toString),
      toStatus.toList.map(value => ("to-status", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.NotificationThread](response, sttp.model.StatusCode(205)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def notifyGetRepoList(config: GiteaConfig, owner: String, repo: String, all: Option[Boolean] = None, statusTypes: Option[List[String]] = None, subjectType: Option[List[contract.NotificationSubjectType]] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.NotificationThread]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.notifyGetRepoList, List("repos", owner.toString, repo.toString, "notifications"),
      all.toList.map(value => ("all", value.toString)) ++ statusTypes.toList.flatMap(_.map(value => ("status-types", value.toString))) ++ subjectType.toList.flatMap(_.map(value => ("subject-type", value.value))) ++ since.toList.map(value => ("since", value.toString)) ++ before.toList.map(value => ("before", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.NotificationThread]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def notifyReadRepoList(config: GiteaConfig, owner: String, repo: String, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None, lastReadAt: Option[java.time.Instant] = None): GiteaRequest[zio.Chunk[contract.NotificationThread]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.notifyReadRepoList, List("repos", owner.toString, repo.toString, "notifications"),
      all.toList.map(value => ("all", value.toString)) ++ statusTypes.toList.flatMap(_.map(value => ("status-types", value.toString))) ++ toStatus.toList.map(value => ("to-status", value.toString)) ++ lastReadAt.toList.map(value => ("last_read_at", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.NotificationThread]](response, sttp.model.StatusCode(205)), sttp.model.MediaType.ApplicationJson, Accept.Json)
