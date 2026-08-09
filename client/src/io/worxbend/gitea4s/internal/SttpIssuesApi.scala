package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.IssuesApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  GiteaRequests,
  IssueCommentListParams,
  IssueListParams,
  IssueTrackedTimeListParams,
  RepositoryCommentListParams
}
import io.worxbend.gitea4s.model.{
  AddTimeOption,
  Comment,
  CreateIssue,
  CreateIssueComment,
  EditDeadlineOption,
  EditIssue,
  EditIssueComment,
  EditReactionOption,
  Issue,
  IssueDeadline,
  IssueLabelsOption,
  IssueMeta,
  IssueState,
  Label,
  LockIssueOption,
  Reaction,
  TrackedTime,
  User,
  WatchInfo
}
import zio.{Chunk, IO}
import zio.stream.ZStream

private[gitea4s] final class SttpIssuesApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends IssuesApi:
  override def get(owner: String, repo: String, index: Long): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.issue(config, owner, repo, index))

  override def list(
      owner: String,
      repo: String,
      params: IssueListParams = IssueListParams.default
  ): ZStream[Any, GiteaError, Issue] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.issues(config, owner, repo, params.copy(page = Some(page))))
    }

  override def pinned(owner: String, repo: String): IO[GiteaError, Chunk[Issue]] =
    executor.send(GiteaRequests.pinnedIssues(config, owner, repo))

  override def create(owner: String, repo: String, body: CreateIssue): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.createIssue(config, owner, repo, body))

  override def delete(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssue(config, owner, repo, index))

  override def pin(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.pinIssue(config, owner, repo, index))

  override def unpin(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.unpinIssue(config, owner, repo, index))

  override def movePin(owner: String, repo: String, index: Long, position: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.moveIssuePin(config, owner, repo, index, position))

  override def edit(owner: String, repo: String, index: Long, body: EditIssue): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.editIssue(config, owner, repo, index, body))

  override def close(owner: String, repo: String, index: Long): IO[GiteaError, Issue] =
    edit(owner, repo, index, EditIssue(state = Some(IssueState.Closed)))

  override def labels(owner: String, repo: String, index: Long): IO[GiteaError, Chunk[Label]] =
    executor.send(GiteaRequests.issueLabels(config, owner, repo, index))

  override def replaceLabels(
      owner: String,
      repo: String,
      index: Long,
      labels: Chunk[Long]
  ): IO[GiteaError, Chunk[Label]] =
    executor.send(GiteaRequests.replaceIssueLabels(config, owner, repo, index, IssueLabelsOption(labels.toList)))

  override def addLabels(
      owner: String,
      repo: String,
      index: Long,
      labels: Chunk[Long]
  ): IO[GiteaError, Chunk[Label]] =
    executor.send(GiteaRequests.addIssueLabels(config, owner, repo, index, IssueLabelsOption(labels.toList)))

  override def clearLabels(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.clearIssueLabels(config, owner, repo, index))

  override def removeLabel(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.removeIssueLabel(config, owner, repo, index, id))

  override def lock(owner: String, repo: String, index: Long, body: LockIssueOption): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.lockIssue(config, owner, repo, index, body))

  override def unlock(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.unlockIssue(config, owner, repo, index))

  override def editDeadline(
      owner: String,
      repo: String,
      index: Long,
      body: EditDeadlineOption
  ): IO[GiteaError, IssueDeadline] =
    executor.send(GiteaRequests.editIssueDeadline(config, owner, repo, index, body))

  override def comment(owner: String, repo: String, index: Long, body: String): IO[GiteaError, Comment] =
    executor.send(GiteaRequests.createIssueComment(config, owner, repo, index, CreateIssueComment(body)))

  override def comments(
      owner: String,
      repo: String,
      index: Long,
      params: IssueCommentListParams = IssueCommentListParams.default
  ): IO[GiteaError, Chunk[Comment]] =
    executor.send(GiteaRequests.issueComments(config, owner, repo, index, params))

  override def repositoryComments(
      owner: String,
      repo: String,
      params: RepositoryCommentListParams = RepositoryCommentListParams.default
  ): ZStream[Any, GiteaError, Comment] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.repoIssueComments(config, owner, repo, params.copy(page = Some(page))))
    }

  override def comment(owner: String, repo: String, id: Long): IO[GiteaError, Comment] =
    executor.send(GiteaRequests.issueComment(config, owner, repo, id))

  override def editComment(owner: String, repo: String, id: Long, body: EditIssueComment): IO[GiteaError, Comment] =
    executor.send(GiteaRequests.editIssueComment(config, owner, repo, id, body))

  override def deleteComment(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssueComment(config, owner, repo, id))

  override def commentReactions(owner: String, repo: String, id: Long): IO[GiteaError, Chunk[Reaction]] =
    executor.send(GiteaRequests.issueCommentReactions(config, owner, repo, id))

  override def reactToComment(
      owner: String,
      repo: String,
      id: Long,
      body: EditReactionOption
  ): IO[GiteaError, Reaction] =
    executor.send(GiteaRequests.postIssueCommentReaction(config, owner, repo, id, body))

  override def deleteCommentReaction(
      owner: String,
      repo: String,
      id: Long,
      body: EditReactionOption
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssueCommentReaction(config, owner, repo, id, body))

  override def blocks(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, Issue] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issueBlocks(config, owner, repo, index, page))
    }

  override def block(owner: String, repo: String, index: Long, blockedIssue: IssueMeta): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.createIssueBlocking(config, owner, repo, index, blockedIssue))

  override def unblock(owner: String, repo: String, index: Long, blockedIssue: IssueMeta): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.removeIssueBlocking(config, owner, repo, index, blockedIssue))

  override def dependencies(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, Issue] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issueDependencies(config, owner, repo, index, page))
    }

  override def addDependency(
      owner: String,
      repo: String,
      index: Long,
      dependency: IssueMeta
  ): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.createIssueDependency(config, owner, repo, index, dependency))

  override def removeDependency(
      owner: String,
      repo: String,
      index: Long,
      dependency: IssueMeta
  ): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.removeIssueDependency(config, owner, repo, index, dependency))

  override def reactions(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, Reaction] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issueReactions(config, owner, repo, index, page))
    }

  override def react(
      owner: String,
      repo: String,
      index: Long,
      body: EditReactionOption
  ): IO[GiteaError, Reaction] =
    executor.send(GiteaRequests.postIssueReaction(config, owner, repo, index, body))

  override def deleteReaction(
      owner: String,
      repo: String,
      index: Long,
      body: EditReactionOption
  ): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssueReaction(config, owner, repo, index, body))

  override def subscribers(owner: String, repo: String, index: Long): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issueSubscriptions(config, owner, repo, index, page))
    }

  override def subscription(owner: String, repo: String, index: Long): IO[GiteaError, WatchInfo] =
    executor.send(GiteaRequests.issueSubscription(config, owner, repo, index))

  override def subscribe(owner: String, repo: String, index: Long, user: String): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.addIssueSubscription(config, owner, repo, index, user))

  override def unsubscribe(owner: String, repo: String, index: Long, user: String): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssueSubscription(config, owner, repo, index, user))

  override def trackedTimes(
      owner: String,
      repo: String,
      index: Long,
      params: IssueTrackedTimeListParams = IssueTrackedTimeListParams.default
  ): ZStream[Any, GiteaError, TrackedTime] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.issueTrackedTimes(config, owner, repo, index, params.copy(page = Some(page))))
    }

  override def addTrackedTime(
      owner: String,
      repo: String,
      index: Long,
      body: AddTimeOption
  ): IO[GiteaError, TrackedTime] =
    executor.send(GiteaRequests.addIssueTrackedTime(config, owner, repo, index, body))

  override def resetTrackedTime(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.resetIssueTrackedTime(config, owner, repo, index))

  override def deleteTrackedTime(owner: String, repo: String, index: Long, id: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssueTrackedTime(config, owner, repo, index, id))

  override def startStopwatch(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.startIssueStopwatch(config, owner, repo, index))

  override def stopStopwatch(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.stopIssueStopwatch(config, owner, repo, index))

  override def deleteStopwatch(owner: String, repo: String, index: Long): IO[GiteaError, Unit] =
    executor.send(GiteaRequests.deleteIssueStopwatch(config, owner, repo, index))
