package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedIssuesRequests:
  def issueSearchIssues(config: GiteaConfig, state: Option[contract.IssueSearchState] = None, labels: Option[String] = None, milestones: Option[String] = None, q: Option[String] = None, `type`: Option[contract.IssueSearchType] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, assigned: Option[Boolean] = None, created: Option[Boolean] = None, mentioned: Option[Boolean] = None, reviewRequested: Option[Boolean] = None, reviewed: Option[Boolean] = None, owner: Option[String] = None, createdBy: Option[String] = None, team: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Issue]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueSearchIssues, List("repos", "issues", "search"),
      state.toList.map(value => ("state", value.value)) ++ labels.toList.map(value => ("labels", value.toString)) ++ milestones.toList.map(value => ("milestones", value.toString)) ++ q.toList.map(value => ("q", value.toString)) ++ `type`.toList.map(value => ("type", value.value)) ++ since.toList.map(value => ("since", value.toString)) ++ before.toList.map(value => ("before", value.toString)) ++ assigned.toList.map(value => ("assigned", value.toString)) ++ created.toList.map(value => ("created", value.toString)) ++ mentioned.toList.map(value => ("mentioned", value.toString)) ++ reviewRequested.toList.map(value => ("review_requested", value.toString)) ++ reviewed.toList.map(value => ("reviewed", value.toString)) ++ owner.toList.map(value => ("owner", value.toString)) ++ createdBy.toList.map(value => ("created_by", value.toString)) ++ team.toList.map(value => ("team", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Issue]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueListIssueCommentAttachments(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[zio.Chunk[contract.Attachment]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueListIssueCommentAttachments, List("repos", owner.toString, repo.toString, "issues", "comments", id.toString, "assets"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Attachment]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueGetIssueCommentAttachment(config: GiteaConfig, owner: String, repo: String, id: Long, attachmentId: Long): GiteaRequest[contract.Attachment] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueGetIssueCommentAttachment, List("repos", owner.toString, repo.toString, "issues", "comments", id.toString, "assets", attachmentId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Attachment](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueDeleteIssueCommentAttachment(config: GiteaConfig, owner: String, repo: String, id: Long, attachmentId: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueDeleteIssueCommentAttachment, List("repos", owner.toString, repo.toString, "issues", "comments", id.toString, "assets", attachmentId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueEditIssueCommentAttachment(config: GiteaConfig, owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): GiteaRequest[contract.Attachment] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueEditIssueCommentAttachment, List("repos", owner.toString, repo.toString, "issues", "comments", id.toString, "assets", attachmentId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Attachment](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueListIssueAttachments(config: GiteaConfig, owner: String, repo: String, index: Long): GiteaRequest[zio.Chunk[contract.Attachment]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueListIssueAttachments, List("repos", owner.toString, repo.toString, "issues", index.toString, "assets"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Attachment]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueGetIssueAttachment(config: GiteaConfig, owner: String, repo: String, index: Long, attachmentId: Long): GiteaRequest[contract.Attachment] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueGetIssueAttachment, List("repos", owner.toString, repo.toString, "issues", index.toString, "assets", attachmentId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Attachment](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueDeleteIssueAttachment(config: GiteaConfig, owner: String, repo: String, index: Long, attachmentId: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueDeleteIssueAttachment, List("repos", owner.toString, repo.toString, "issues", index.toString, "assets", attachmentId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueEditIssueAttachment(config: GiteaConfig, owner: String, repo: String, index: Long, attachmentId: Long, body: contract.EditAttachmentOptions): GiteaRequest[contract.Attachment] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueEditIssueAttachment, List("repos", owner.toString, repo.toString, "issues", index.toString, "assets", attachmentId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Attachment](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueDeleteCommentDeprecated(config: GiteaConfig, owner: String, repo: String, index: Int, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueDeleteCommentDeprecated, List("repos", owner.toString, repo.toString, "issues", index.toString, "comments", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueEditCommentDeprecated(config: GiteaConfig, owner: String, repo: String, index: Int, id: Long, body: contract.EditIssueCommentOption): GiteaRequest[Option[contract.Comment]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueEditCommentDeprecated, List("repos", owner.toString, repo.toString, "issues", index.toString, "comments", id.toString),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(None) else if response.code.code == 200 then GiteaResponseMapper.decodeJson[contract.Comment](response).map(Some(_)) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueGetCommentsAndTimeline(config: GiteaConfig, owner: String, repo: String, index: Long, since: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None, before: Option[java.time.Instant] = None): GiteaRequest[zio.Chunk[contract.TimelineComment]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueGetCommentsAndTimeline, List("repos", owner.toString, repo.toString, "issues", index.toString, "timeline"),
      since.toList.map(value => ("since", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ before.toList.map(value => ("before", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.TimelineComment]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)
