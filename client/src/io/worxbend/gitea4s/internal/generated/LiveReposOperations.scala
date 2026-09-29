package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.ReposOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedReposRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveReposOperations extends ReposOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def repoMigrate(body: contract.MigrateRepoOptions): IO[GiteaError, contract.Repository] =
    executor.send(GeneratedReposRequests.repoMigrate(config, body))

  override def repoSearch(q: Option[String] = None, topic: Option[Boolean] = None, includeDesc: Option[Boolean] = None, uid: Option[Long] = None, priorityOwnerId: Option[Long] = None, teamId: Option[Long] = None, starredBy: Option[Long] = None, `private`: Option[Boolean] = None, isPrivate: Option[Boolean] = None, template: Option[Boolean] = None, archived: Option[Boolean] = None, mode: Option[String] = None, exclusive: Option[Boolean] = None, sort: Option[String] = None, order: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.SearchResults] =
    executor.send(GeneratedReposRequests.repoSearch(config, q, topic, includeDesc, uid, priorityOwnerId, teamId, starredBy, `private`, isPrivate, template, archived, mode, exclusive, sort, order, page, limit))

  override def repoListActivityFeeds(owner: String, repo: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]] =
    executor.send(GeneratedReposRequests.repoListActivityFeeds(config, owner, repo, date, page, limit))

  override def repoUpdateAvatar(owner: String, repo: String, body: contract.UpdateRepoAvatarOption): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoUpdateAvatar(config, owner, repo, body))

  override def repoDeleteAvatar(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteAvatar(config, owner, repo))

  override def repoAddCollaborator(owner: String, repo: String, collaborator: String, body: contract.AddCollaboratorOption): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoAddCollaborator(config, owner, repo, collaborator, body))

  override def repoDeleteCollaborator(owner: String, repo: String, collaborator: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteCollaborator(config, owner, repo, collaborator))

  override def repoGetAllCommits(owner: String, repo: String, sha: Option[String] = None, path: Option[String] = None, since: Option[java.time.Instant] = None, until: Option[java.time.Instant] = None, stat: Option[Boolean] = None, verification: Option[Boolean] = None, files: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None, not: Option[String] = None): IO[GiteaError, zio.Chunk[contract.Commit]] =
    executor.send(GeneratedReposRequests.repoGetAllCommits(config, owner, repo, sha, path, since, until, stat, verification, files, page, limit, not))

  override def repoCompareDiff(owner: String, repo: String, basehead: String, output: Option[contract.CompareOutput] = None): IO[GiteaError, Either[contract.Compare, String]] =
    executor.send(GeneratedReposRequests.repoCompareDiff(config, owner, repo, basehead, output))

  override def repoChangeFiles(owner: String, repo: String, body: contract.ChangeFilesOptions): IO[GiteaError, contract.FilesResponse] =
    executor.send(GeneratedReposRequests.repoChangeFiles(config, owner, repo, body))

  override def repoGetContentsExt(owner: String, repo: String, filepath: String, ref: Option[String] = None, includes: Option[String] = None): IO[GiteaError, contract.ContentsExtResponse] =
    executor.send(GeneratedReposRequests.repoGetContentsExt(config, owner, repo, filepath, ref, includes))

  override def repoUpdateFile(owner: String, repo: String, filepath: String, body: contract.UpdateFileOptions): IO[GiteaError, contract.FileResponse] =
    executor.send(GeneratedReposRequests.repoUpdateFile(config, owner, repo, filepath, body))

  override def repoCreateFile(owner: String, repo: String, filepath: String, body: contract.CreateFileOptions): IO[GiteaError, contract.FileResponse] =
    executor.send(GeneratedReposRequests.repoCreateFile(config, owner, repo, filepath, body))

  override def repoDeleteFile(owner: String, repo: String, filepath: String, body: contract.DeleteFileOptions): IO[GiteaError, contract.FileDeleteResponse] =
    executor.send(GeneratedReposRequests.repoDeleteFile(config, owner, repo, filepath, body))

  override def repoApplyDiffPatch(owner: String, repo: String, body: contract.ApplyDiffPatchFileOptions): IO[GiteaError, contract.FileResponse] =
    executor.send(GeneratedReposRequests.repoApplyDiffPatch(config, owner, repo, body))

  override def repoGetEditorConfig(owner: String, repo: String, filepath: String, ref: Option[String] = None): IO[GiteaError, String] =
    executor.send(GeneratedReposRequests.repoGetEditorConfig(config, owner, repo, filepath, ref))

  override def repoGetFileContents(owner: String, repo: String, ref: Option[String] = None, body: String): IO[GiteaError, zio.Chunk[contract.ContentsResponse]] =
    executor.send(GeneratedReposRequests.repoGetFileContents(config, owner, repo, ref, body))

  override def repoGetFileContentsPost(owner: String, repo: String, body: contract.GetFilesOptions, ref: Option[String] = None): IO[GiteaError, zio.Chunk[contract.ContentsResponse]] =
    executor.send(GeneratedReposRequests.repoGetFileContentsPost(config, owner, repo, body, ref))

  override def listForks(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedReposRequests.listForks(config, owner, repo, page, limit))

  override def repoListHooks(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]] =
    executor.send(GeneratedReposRequests.repoListHooks(config, owner, repo, page, limit))

  override def repoCreateHook(owner: String, repo: String, body: contract.CreateHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedReposRequests.repoCreateHook(config, owner, repo, body))

  override def repoGetHook(owner: String, repo: String, id: Long): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedReposRequests.repoGetHook(config, owner, repo, id))

  override def repoDeleteHook(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteHook(config, owner, repo, id))

  override def repoEditHook(owner: String, repo: String, id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedReposRequests.repoEditHook(config, owner, repo, id, body))

  override def repoTestHook(owner: String, repo: String, id: Long, ref: Option[String] = None): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoTestHook(config, owner, repo, id, ref))

  override def repoGetIssueConfig(owner: String, repo: String): IO[GiteaError, contract.IssueConfig] =
    executor.send(GeneratedReposRequests.repoGetIssueConfig(config, owner, repo))

  override def repoValidateIssueConfig(owner: String, repo: String): IO[GiteaError, contract.IssueConfigValidation] =
    executor.send(GeneratedReposRequests.repoValidateIssueConfig(config, owner, repo))

  override def repoGetIssueTemplates(owner: String, repo: String): IO[GiteaError, zio.Chunk[contract.IssueTemplate]] =
    executor.send(GeneratedReposRequests.repoGetIssueTemplates(config, owner, repo))

  override def repoListKeys(owner: String, repo: String, keyId: Option[Int] = None, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.DeployKey]] =
    executor.send(GeneratedReposRequests.repoListKeys(config, owner, repo, keyId, fingerprint, page, limit))

  override def repoCreateKey(owner: String, repo: String, body: contract.CreateKeyOption): IO[GiteaError, contract.DeployKey] =
    executor.send(GeneratedReposRequests.repoCreateKey(config, owner, repo, body))

  override def repoGetKey(owner: String, repo: String, id: Long): IO[GiteaError, contract.DeployKey] =
    executor.send(GeneratedReposRequests.repoGetKey(config, owner, repo, id))

  override def repoDeleteKey(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteKey(config, owner, repo, id))

  override def issueListLabels(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Label]] =
    executor.send(GeneratedReposRequests.issueListLabels(config, owner, repo, page, limit))

  override def issueCreateLabel(owner: String, repo: String, body: contract.CreateLabelOption): IO[GiteaError, contract.Label] =
    executor.send(GeneratedReposRequests.issueCreateLabel(config, owner, repo, body))

  override def issueGetLabel(owner: String, repo: String, id: Long): IO[GiteaError, contract.Label] =
    executor.send(GeneratedReposRequests.issueGetLabel(config, owner, repo, id))

  override def issueDeleteLabel(owner: String, repo: String, id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.issueDeleteLabel(config, owner, repo, id))

  override def issueEditLabel(owner: String, repo: String, id: Long, body: contract.EditLabelOption): IO[GiteaError, contract.Label] =
    executor.send(GeneratedReposRequests.issueEditLabel(config, owner, repo, id, body))

  override def repoGetLicenses(owner: String, repo: String): IO[GiteaError, zio.Chunk[String]] =
    executor.send(GeneratedReposRequests.repoGetLicenses(config, owner, repo))

  override def repoMergeUpstream(owner: String, repo: String, body: contract.MergeUpstreamRequest): IO[GiteaError, contract.MergeUpstreamResponse] =
    executor.send(GeneratedReposRequests.repoMergeUpstream(config, owner, repo, body))

  override def issueGetMilestonesList(owner: String, repo: String, state: Option[String] = None, name: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Milestone]] =
    executor.send(GeneratedReposRequests.issueGetMilestonesList(config, owner, repo, state, name, page, limit))

  override def issueCreateMilestone(owner: String, repo: String, body: contract.CreateMilestoneOption): IO[GiteaError, contract.Milestone] =
    executor.send(GeneratedReposRequests.issueCreateMilestone(config, owner, repo, body))

  override def issueGetMilestone(owner: String, repo: String, id: String): IO[GiteaError, contract.Milestone] =
    executor.send(GeneratedReposRequests.issueGetMilestone(config, owner, repo, id))

  override def issueDeleteMilestone(owner: String, repo: String, id: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.issueDeleteMilestone(config, owner, repo, id))

  override def issueEditMilestone(owner: String, repo: String, id: String, body: contract.EditMilestoneOption): IO[GiteaError, contract.Milestone] =
    executor.send(GeneratedReposRequests.issueEditMilestone(config, owner, repo, id, body))

  override def repoMirrorSync(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoMirrorSync(config, owner, repo))

  override def repoListPushMirrors(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.PushMirror]] =
    executor.send(GeneratedReposRequests.repoListPushMirrors(config, owner, repo, page, limit))

  override def repoAddPushMirror(owner: String, repo: String, body: contract.CreatePushMirrorOption): IO[GiteaError, contract.PushMirror] =
    executor.send(GeneratedReposRequests.repoAddPushMirror(config, owner, repo, body))

  override def repoPushMirrorSync(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoPushMirrorSync(config, owner, repo))

  override def repoGetPushMirrorByRemoteName(owner: String, repo: String, name: String): IO[GiteaError, contract.PushMirror] =
    executor.send(GeneratedReposRequests.repoGetPushMirrorByRemoteName(config, owner, repo, name))

  override def repoDeletePushMirror(owner: String, repo: String, name: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeletePushMirror(config, owner, repo, name))

  override def repoSigningKeySSH(owner: String, repo: String): IO[GiteaError, String] =
    executor.send(GeneratedReposRequests.repoSigningKeySSH(config, owner, repo))

  override def userCurrentCheckSubscription(owner: String, repo: String): IO[GiteaError, contract.WatchInfo] =
    executor.send(GeneratedReposRequests.userCurrentCheckSubscription(config, owner, repo))

  override def userCurrentPutSubscription(owner: String, repo: String): IO[GiteaError, contract.WatchInfo] =
    executor.send(GeneratedReposRequests.userCurrentPutSubscription(config, owner, repo))

  override def userCurrentDeleteSubscription(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.userCurrentDeleteSubscription(config, owner, repo))

  override def repoCreateTag(owner: String, repo: String, body: contract.CreateTagOption): IO[GiteaError, contract.Tag] =
    executor.send(GeneratedReposRequests.repoCreateTag(config, owner, repo, body))

  override def repoDeleteTag(owner: String, repo: String, tag: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteTag(config, owner, repo, tag))

  override def repoAddTeam(owner: String, repo: String, team: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoAddTeam(config, owner, repo, team))

  override def repoDeleteTeam(owner: String, repo: String, team: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteTeam(config, owner, repo, team))

  override def repoTrackedTimes(owner: String, repo: String, user: Option[String] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.TrackedTime]] =
    executor.send(GeneratedReposRequests.repoTrackedTimes(config, owner, repo, user, since, before, page, limit))

  override def userTrackedTimes(owner: String, repo: String, user: String): IO[GiteaError, zio.Chunk[contract.TrackedTime]] =
    executor.send(GeneratedReposRequests.userTrackedTimes(config, owner, repo, user))

  override def repoUpdateTopics(owner: String, repo: String, body: contract.RepoTopicOptions): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoUpdateTopics(config, owner, repo, body))

  override def repoAddTopic(owner: String, repo: String, topic: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoAddTopic(config, owner, repo, topic))

  override def repoDeleteTopic(owner: String, repo: String, topic: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteTopic(config, owner, repo, topic))

  override def repoCreateWikiPage(owner: String, repo: String, body: contract.CreateWikiPageOptions): IO[GiteaError, contract.WikiPage] =
    executor.send(GeneratedReposRequests.repoCreateWikiPage(config, owner, repo, body))

  override def repoGetWikiPage(owner: String, repo: String, pageName: String): IO[GiteaError, contract.WikiPage] =
    executor.send(GeneratedReposRequests.repoGetWikiPage(config, owner, repo, pageName))

  override def repoDeleteWikiPage(owner: String, repo: String, pageName: String): IO[GiteaError, Unit] =
    executor.send(GeneratedReposRequests.repoDeleteWikiPage(config, owner, repo, pageName))

  override def repoEditWikiPage(owner: String, repo: String, pageName: String, body: contract.CreateWikiPageOptions): IO[GiteaError, contract.WikiPage] =
    executor.send(GeneratedReposRequests.repoEditWikiPage(config, owner, repo, pageName, body))

  override def repoGetWikiPages(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.WikiPageMetaData]] =
    executor.send(GeneratedReposRequests.repoGetWikiPages(config, owner, repo, page, limit))

  override def repoGetWikiPageRevisions(owner: String, repo: String, pageName: String, page: Option[Int] = None): IO[GiteaError, contract.WikiCommitList] =
    executor.send(GeneratedReposRequests.repoGetWikiPageRevisions(config, owner, repo, pageName, page))

  override def generateRepo(templateOwner: String, templateRepo: String, body: contract.GenerateRepoOption): IO[GiteaError, contract.Repository] =
    executor.send(GeneratedReposRequests.generateRepo(config, templateOwner, templateRepo, body))
