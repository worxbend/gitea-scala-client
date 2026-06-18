package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.api.OrgsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  GiteaRequests,
  IssueCommentListParams,
  IssueListParams,
  NotificationListParams,
  PullRequestListParams,
  RepoListParams,
  RepositoryCommentListParams,
  UserSearchParams
}
import io.worxbend.gitea4s.model.{
  Branch,
  Comment,
  CreateIssue,
  CreateIssueComment,
  EditDeadlineOption,
  EditIssueComment,
  EditIssue,
  EditReactionOption,
  Issue,
  IssueDeadline,
  IssueLabelsOption,
  IssueMeta,
  IssueState,
  Label,
  LockIssueOption,
  NotificationCount,
  NotificationThread,
  Organization,
  PullRequest,
  Reaction,
  Release,
  Repository,
  Tag,
  User
}
import sttp.client4.Backend
import zio.{Chunk, IO, Task}
import zio.stream.ZStream

final class SttpGiteaClient(config: GiteaConfig, backend: Backend[Task]) extends GiteaClient:
  private val executor = GiteaRequestExecutor(backend, config.maxRetries)

  override val orgs: OrgsApi =
    new OrgsApi:
      override def get(org: String): IO[GiteaError, Organization] =
        executor.send(GiteaRequests.organization(config, org))

      override def members(org: String): ZStream[Any, GiteaError, User] =
        Pagination.paginated { page =>
          executor.send(GiteaRequests.organizationMembers(config, org, page))
        }

      override def publicMembers(org: String): ZStream[Any, GiteaError, User] =
        Pagination.paginated { page =>
          executor.send(GiteaRequests.organizationPublicMembers(config, org, page))
        }

      override def repos(org: String, params: RepoListParams): ZStream[Any, GiteaError, Repository] =
        Pagination.paginated { page =>
          executor.send(GiteaRequests.organizationRepos(config, org, params.copy(page = Some(page))))
        }

  override def me: IO[GiteaError, User] =
    executor.send(GiteaRequests.currentUser(config))

  override def get(username: String): IO[GiteaError, User] =
    executor.send(GiteaRequests.user(config, username))

  override def followers(username: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userFollowers(config, username, page))
    }

  override def following(username: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userFollowing(config, username, page))
    }

  override def search(params: UserSearchParams): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userSearch(config, params.copy(page = Some(page))))
    }

  override def get(owner: String, repo: String): IO[GiteaError, Repository] =
    executor.send(GiteaRequests.repository(config, owner, repo))

  override def list(
      owner: String,
      params: RepoListParams
  ): ZStream[Any, GiteaError, Repository] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.userRepos(config, owner, params.copy(page = Some(page))))
    }

  override def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoTopics(config, owner, repo, page))
    }.runCollect

  override def branches(owner: String, repo: String): ZStream[Any, GiteaError, Branch] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoBranches(config, owner, repo, page))
    }

  override def tags(owner: String, repo: String): ZStream[Any, GiteaError, Tag] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoTags(config, owner, repo, page))
    }

  override def releases(owner: String, repo: String): ZStream[Any, GiteaError, Release] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoReleases(config, owner, repo, page))
    }

  override def release(owner: String, repo: String, id: Long): IO[GiteaError, Release] =
    executor.send(GiteaRequests.repoRelease(config, owner, repo, id))

  override def pullRequests(
      owner: String,
      repo: String,
      params: PullRequestListParams
  ): ZStream[Any, GiteaError, PullRequest] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoPullRequests(config, owner, repo, params.copy(page = Some(page))))
    }

  override def pullRequest(owner: String, repo: String, index: Long): IO[GiteaError, PullRequest] =
    executor.send(GiteaRequests.repoPullRequest(config, owner, repo, index))

  override def notificationThreads(
      params: NotificationListParams
  ): ZStream[Any, GiteaError, NotificationThread] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.notifications(config, params.copy(page = Some(page))))
    }

  override def unreadNotificationCount: IO[GiteaError, NotificationCount] =
    executor.send(GiteaRequests.notificationCount(config))

  override def notificationThread(id: String): IO[GiteaError, NotificationThread] =
    executor.send(GiteaRequests.notificationThread(config, id))

  override def get(owner: String, repo: String, index: Long): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.issue(config, owner, repo, index))

  override def list(
      owner: String,
      repo: String,
      params: IssueListParams
  ): ZStream[Any, GiteaError, Issue] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.issues(config, owner, repo, params.copy(page = Some(page))))
    }

  override def create(owner: String, repo: String, body: CreateIssue): IO[GiteaError, Issue] =
    executor.send(GiteaRequests.createIssue(config, owner, repo, body))

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
      params: IssueCommentListParams
  ): IO[GiteaError, Chunk[Comment]] =
    executor.send(GiteaRequests.issueComments(config, owner, repo, index, params))

  override def repositoryComments(
      owner: String,
      repo: String,
      params: RepositoryCommentListParams
  ): ZStream[Any, GiteaError, Comment] =
    Pagination.paginated { page =>
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
