package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.model.IssueState
import zio.Chunk

import java.time.Instant

enum IssueListType(val queryValue: String):
  case Issues extends IssueListType("issues")
  case Pulls extends IssueListType("pulls")

final case class IssueListParams(
    state: Option[IssueState] = None,
    labels: Chunk[String] = Chunk.empty,
    q: Option[String] = None,
    issueType: Option[IssueListType] = None,
    milestones: Chunk[String] = Chunk.empty,
    since: Option[Instant] = None,
    before: Option[Instant] = None,
    createdBy: Option[String] = None,
    assignedBy: Option[String] = None,
    mentionedBy: Option[String] = None,
    page: Option[Int] = Some(1),
    limit: Option[Int] = None
)

object IssueListParams:
  val default: IssueListParams = IssueListParams()
