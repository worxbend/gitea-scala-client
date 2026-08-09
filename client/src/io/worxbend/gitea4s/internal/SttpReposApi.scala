package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.ReposApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  ArchiveParams,
  CombinedStatusParams,
  CommitNoteParams,
  CommitStatusListParams,
  ContentsParams,
  GiteaRequests,
  GitTreeParams,
  RepoListParams,
  SingleCommitParams
}
import io.worxbend.gitea4s.model.{
  AnnotatedTag,
  Branch,
  BranchProtection,
  CombinedStatus,
  Commit,
  CommitDiffType,
  CommitStatus,
  ContentsResponse,
  CreateStatusOption,
  GitBlobResponse,
  GitTreeResponse,
  LanguageStatistics,
  NewIssuePinsAllowed,
  Note,
  Reference,
  RepoCollaboratorPermission,
  Repository,
  Tag,
  TagProtection,
  Team,
  User
}
import zio.{Chunk, IO}
import zio.stream.ZStream

private[gitea4s] final class SttpReposApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends ReposApi:
  override def get(owner: String, repo: String): IO[GiteaError, Repository] =
    executor.send(GiteaRequests.repository(config, owner, repo))

  override def commit(
      owner: String,
      repo: String,
      sha: String,
      params: SingleCommitParams = SingleCommitParams.default
  ): IO[GiteaError, Commit] =
    executor.send(GiteaRequests.repoSingleCommit(config, owner, repo, sha, params))

  override def commitDiffOrPatch(
      owner: String,
      repo: String,
      sha: String,
      diffType: CommitDiffType
  ): IO[GiteaError, String] =
    executor.send(GiteaRequests.repoCommitDiffOrPatch(config, owner, repo, sha, diffType))

  override def commitNote(
      owner: String,
      repo: String,
      sha: String,
      params: CommitNoteParams = CommitNoteParams.default
  ): IO[GiteaError, Note] =
    executor.send(GiteaRequests.repoCommitNote(config, owner, repo, sha, params))

  override def gitTree(
      owner: String,
      repo: String,
      sha: String,
      params: GitTreeParams = GitTreeParams.default
  ): IO[GiteaError, GitTreeResponse] =
    executor.send(GiteaRequests.gitTree(config, owner, repo, sha, params))

  override def gitBlob(owner: String, repo: String, sha: String): IO[GiteaError, GitBlobResponse] =
    executor.send(GiteaRequests.gitBlob(config, owner, repo, sha))

  override def annotatedTag(owner: String, repo: String, sha: String): IO[GiteaError, AnnotatedTag] =
    executor.send(GiteaRequests.annotatedTag(config, owner, repo, sha))

  override def gitRefs(owner: String, repo: String): IO[GiteaError, Chunk[Reference]] =
    executor.send(GiteaRequests.repoListAllGitRefs(config, owner, repo))

  override def gitRefs(owner: String, repo: String, ref: String): IO[GiteaError, Chunk[Reference]] =
    executor.send(GiteaRequests.repoListGitRefs(config, owner, repo, ref))

  override def contents(
      owner: String,
      repo: String,
      params: ContentsParams
  ): IO[GiteaError, Chunk[ContentsResponse]] =
    executor.send(GiteaRequests.repoContentsList(config, owner, repo, params))

  override def contents(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams
  ): IO[GiteaError, ContentsResponse] =
    executor.send(GiteaRequests.repoContents(config, owner, repo, filepath, params))

  override def rawFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): IO[GiteaError, Chunk[Byte]] =
    executor.send(GiteaRequests.repoRawFile(config, owner, repo, filepath, params))

  override def mediaFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): IO[GiteaError, Chunk[Byte]] =
    executor.send(GiteaRequests.repoMediaFile(config, owner, repo, filepath, params))

  override def archive(
      owner: String,
      repo: String,
      archive: String,
      params: ArchiveParams = ArchiveParams.default
  ): IO[GiteaError, Chunk[Byte]] =
    executor.send(GiteaRequests.repoGetArchive(config, owner, repo, archive, params))

  override def assignees(owner: String, repo: String): IO[GiteaError, Chunk[User]] =
    executor.send(GiteaRequests.repoAssignees(config, owner, repo))

  override def reviewers(owner: String, repo: String): IO[GiteaError, Chunk[User]] =
    executor.send(GiteaRequests.repoReviewers(config, owner, repo))

  override def stargazers(owner: String, repo: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoStargazers(config, owner, repo, page))
    }

  override def watchers(owner: String, repo: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoSubscribers(config, owner, repo, page))
    }

  override def collaborators(owner: String, repo: String): ZStream[Any, GiteaError, User] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoCollaborators(config, owner, repo, page))
    }

  override def isCollaborator(owner: String, repo: String, collaborator: String): IO[GiteaError, Boolean] =
    executor.send(GiteaRequests.repoCheckCollaborator(config, owner, repo, collaborator))

  override def collaboratorPermission(
      owner: String,
      repo: String,
      collaborator: String
  ): IO[GiteaError, RepoCollaboratorPermission] =
    executor.send(GiteaRequests.repoCollaboratorPermission(config, owner, repo, collaborator))

  override def teams(owner: String, repo: String): ZStream[Any, GiteaError, Team] =
    Pagination.paginated { page =>
      executor.send(GiteaRequests.repoTeams(config, owner, repo, page))
    }

  override def team(owner: String, repo: String, team: String): IO[GiteaError, Team] =
    executor.send(GiteaRequests.repoTeam(config, owner, repo, team))

  override def list(
      owner: String,
      params: RepoListParams = RepoListParams.default
  ): ZStream[Any, GiteaError, Repository] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.userRepos(config, owner, params.copy(page = Some(page))))
    }

  override def newIssuePinsAllowed(owner: String, repo: String): IO[GiteaError, NewIssuePinsAllowed] =
    executor.send(GiteaRequests.repoNewPinAllowed(config, owner, repo))

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

  override def languages(owner: String, repo: String): IO[GiteaError, LanguageStatistics] =
    executor.send(GiteaRequests.repoLanguages(config, owner, repo))

  override def gpgSigningKey(owner: String, repo: String): IO[GiteaError, String] =
    executor.send(GiteaRequests.repoSigningKey(config, owner, repo))

  override def tag(owner: String, repo: String, tag: String): IO[GiteaError, Tag] =
    executor.send(GiteaRequests.repoTag(config, owner, repo, tag))

  override def tagProtections(owner: String, repo: String): IO[GiteaError, Chunk[TagProtection]] =
    executor.send(GiteaRequests.repoTagProtections(config, owner, repo))

  override def tagProtection(owner: String, repo: String, id: Long): IO[GiteaError, TagProtection] =
    executor.send(GiteaRequests.repoTagProtection(config, owner, repo, id))

  override def branchProtections(owner: String, repo: String): IO[GiteaError, Chunk[BranchProtection]] =
    executor.send(GiteaRequests.repoBranchProtections(config, owner, repo))

  override def branchProtection(owner: String, repo: String, name: String): IO[GiteaError, BranchProtection] =
    executor.send(GiteaRequests.repoBranchProtection(config, owner, repo, name))

  override def combinedStatusByRef(
      owner: String,
      repo: String,
      ref: String,
      params: CombinedStatusParams = CombinedStatusParams.default
  ): IO[GiteaError, CombinedStatus] =
    executor.send(GiteaRequests.repoCombinedStatusByRef(config, owner, repo, ref, params))

  override def statusesByRef(
      owner: String,
      repo: String,
      ref: String,
      params: CommitStatusListParams = CommitStatusListParams.default
  ): ZStream[Any, GiteaError, CommitStatus] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.repoStatusesByRef(config, owner, repo, ref, params.copy(page = Some(page))))
    }

  override def statuses(
      owner: String,
      repo: String,
      sha: String,
      params: CommitStatusListParams = CommitStatusListParams.default
  ): ZStream[Any, GiteaError, CommitStatus] =
    Pagination.paginatedFrom(params.page.getOrElse(1)) { page =>
      executor.send(GiteaRequests.repoStatuses(config, owner, repo, sha, params.copy(page = Some(page))))
    }

  override def createStatus(
      owner: String,
      repo: String,
      sha: String,
      body: CreateStatusOption
  ): IO[GiteaError, CommitStatus] =
    executor.send(GiteaRequests.createStatus(config, owner, repo, sha, body))
