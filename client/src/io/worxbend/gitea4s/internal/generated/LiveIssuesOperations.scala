package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.IssuesOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedIssuesRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveIssuesOperations extends IssuesOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def issueSearchIssues(state: Option[contract.IssueSearchState] = None, labels: Option[String] = None, milestones: Option[String] = None, q: Option[String] = None, `type`: Option[contract.IssueSearchType] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, assigned: Option[Boolean] = None, created: Option[Boolean] = None, mentioned: Option[Boolean] = None, reviewRequested: Option[Boolean] = None, reviewed: Option[Boolean] = None, owner: Option[String] = None, createdBy: Option[String] = None, team: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Issue]] =
    executor.send(GeneratedIssuesRequests.issueSearchIssues(config, state, labels, milestones, q, `type`, since, before, assigned, created, mentioned, reviewRequested, reviewed, owner, createdBy, team, page, limit))

  override def issueListIssueCommentAttachments(owner: String, repo: String, id: Long): IO[GiteaError, zio.Chunk[contract.Attachment]] =
    executor.send(GeneratedIssuesRequests.issueListIssueCommentAttachments(config, owner, repo, id))

  override def issueGetIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, contract.Attachment] =
    executor.send(GeneratedIssuesRequests.issueGetIssueCommentAttachment(config, owner, repo, id, attachmentId))

  override def issueDeleteIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedIssuesRequests.issueDeleteIssueCommentAttachment(config, owner, repo, id, attachmentId))

  override def issueEditIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment] =
    executor.send(GeneratedIssuesRequests.issueEditIssueCommentAttachment(config, owner, repo, id, attachmentId, body))

  override def issueListIssueAttachments(owner: String, repo: String, index: Long): IO[GiteaError, zio.Chunk[contract.Attachment]] =
    executor.send(GeneratedIssuesRequests.issueListIssueAttachments(config, owner, repo, index))

  override def issueGetIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long): IO[GiteaError, contract.Attachment] =
    executor.send(GeneratedIssuesRequests.issueGetIssueAttachment(config, owner, repo, index, attachmentId))

  override def issueDeleteIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedIssuesRequests.issueDeleteIssueAttachment(config, owner, repo, index, attachmentId))

  override def issueEditIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment] =
    executor.send(GeneratedIssuesRequests.issueEditIssueAttachment(config, owner, repo, index, attachmentId, body))

  override def issueDeleteCommentDeprecated(owner: String, repo: String, index: Int, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedIssuesRequests.issueDeleteCommentDeprecated(config, owner, repo, index, id))

  override def issueEditCommentDeprecated(owner: String, repo: String, index: Int, id: Long, body: contract.EditIssueCommentOption): IO[GiteaError, Option[contract.Comment]] =
    executor.send(GeneratedIssuesRequests.issueEditCommentDeprecated(config, owner, repo, index, id, body))

  override def issueGetCommentsAndTimeline(owner: String, repo: String, index: Long, since: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None, before: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.TimelineComment]] =
    executor.send(GeneratedIssuesRequests.issueGetCommentsAndTimeline(config, owner, repo, index, since, page, limit, before))
