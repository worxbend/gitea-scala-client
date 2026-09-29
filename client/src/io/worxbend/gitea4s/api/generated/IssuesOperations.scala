package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait IssuesOperations:
  def issueSearchIssues(state: Option[contract.IssueSearchState] = None, labels: Option[String] = None, milestones: Option[String] = None, q: Option[String] = None, `type`: Option[contract.IssueSearchType] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, assigned: Option[Boolean] = None, created: Option[Boolean] = None, mentioned: Option[Boolean] = None, reviewRequested: Option[Boolean] = None, reviewed: Option[Boolean] = None, owner: Option[String] = None, createdBy: Option[String] = None, team: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Issue]]
  def issueListIssueCommentAttachments(owner: String, repo: String, id: Long): IO[GiteaError, zio.Chunk[contract.Attachment]]
  def issueGetIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, contract.Attachment]
  def issueDeleteIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, Unit]
  def issueEditIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment]
  def issueListIssueAttachments(owner: String, repo: String, index: Long): IO[GiteaError, zio.Chunk[contract.Attachment]]
  def issueGetIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long): IO[GiteaError, contract.Attachment]
  def issueDeleteIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long): IO[GiteaError, Unit]
  def issueEditIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment]
  def issueDeleteCommentDeprecated(owner: String, repo: String, index: Int, id: Long): IO[GiteaError, Unit]
  def issueEditCommentDeprecated(owner: String, repo: String, index: Int, id: Long, body: contract.EditIssueCommentOption): IO[GiteaError, Option[contract.Comment]]
  def issueGetCommentsAndTimeline(owner: String, repo: String, index: Long, since: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None, before: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.TimelineComment]]
