package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait NotificationsOperations:
  def notifyReadList(lastReadAt: Option[java.time.Instant] = None, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]]
  def notifyReadThread(id: String, toStatus: Option[String] = None): IO[GiteaError, contract.NotificationThread]
  def notifyGetRepoList(owner: String, repo: String, all: Option[Boolean] = None, statusTypes: Option[List[String]] = None, subjectType: Option[List[contract.NotificationSubjectType]] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]]
  def notifyReadRepoList(owner: String, repo: String, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None, lastReadAt: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]]
