package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.NotificationsOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedNotificationsRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveNotificationsOperations extends NotificationsOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def notifyReadList(lastReadAt: Option[java.time.Instant] = None, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]] =
    executor.send(GeneratedNotificationsRequests.notifyReadList(config, lastReadAt, all, statusTypes, toStatus))

  override def notifyReadThread(id: String, toStatus: Option[String] = None): IO[GiteaError, contract.NotificationThread] =
    executor.send(GeneratedNotificationsRequests.notifyReadThread(config, id, toStatus))

  override def notifyGetRepoList(owner: String, repo: String, all: Option[Boolean] = None, statusTypes: Option[List[String]] = None, subjectType: Option[List[contract.NotificationSubjectType]] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]] =
    executor.send(GeneratedNotificationsRequests.notifyGetRepoList(config, owner, repo, all, statusTypes, subjectType, since, before, page, limit))

  override def notifyReadRepoList(owner: String, repo: String, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None, lastReadAt: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]] =
    executor.send(GeneratedNotificationsRequests.notifyReadRepoList(config, owner, repo, all, statusTypes, toStatus, lastReadAt))
