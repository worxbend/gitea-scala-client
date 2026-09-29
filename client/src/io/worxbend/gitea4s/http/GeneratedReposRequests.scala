package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedReposRequests:
  def repoMigrate(config: GiteaConfig, body: contract.MigrateRepoOptions): GiteaRequest[contract.Repository] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoMigrate, List("repos", "migrate"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Repository](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoSearch(config: GiteaConfig, q: Option[String] = None, topic: Option[Boolean] = None, includeDesc: Option[Boolean] = None, uid: Option[Long] = None, priorityOwnerId: Option[Long] = None, teamId: Option[Long] = None, starredBy: Option[Long] = None, `private`: Option[Boolean] = None, isPrivate: Option[Boolean] = None, template: Option[Boolean] = None, archived: Option[Boolean] = None, mode: Option[String] = None, exclusive: Option[Boolean] = None, sort: Option[String] = None, order: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.SearchResults] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoSearch, List("repos", "search"),
      q.toList.map(value => ("q", value.toString)) ++ topic.toList.map(value => ("topic", value.toString)) ++ includeDesc.toList.map(value => ("includeDesc", value.toString)) ++ uid.toList.map(value => ("uid", value.toString)) ++ priorityOwnerId.toList.map(value => ("priority_owner_id", value.toString)) ++ teamId.toList.map(value => ("team_id", value.toString)) ++ starredBy.toList.map(value => ("starredBy", value.toString)) ++ `private`.toList.map(value => ("private", value.toString)) ++ isPrivate.toList.map(value => ("is_private", value.toString)) ++ template.toList.map(value => ("template", value.toString)) ++ archived.toList.map(value => ("archived", value.toString)) ++ mode.toList.map(value => ("mode", value.toString)) ++ exclusive.toList.map(value => ("exclusive", value.toString)) ++ sort.toList.map(value => ("sort", value.toString)) ++ order.toList.map(value => ("order", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.SearchResults](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoListActivityFeeds(config: GiteaConfig, owner: String, repo: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Activity]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoListActivityFeeds, List("repos", owner.toString, repo.toString, "activities", "feeds"),
      date.toList.map(value => ("date", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Activity]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoUpdateAvatar(config: GiteaConfig, owner: String, repo: String, body: contract.UpdateRepoAvatarOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoUpdateAvatar, List("repos", owner.toString, repo.toString, "avatar"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteAvatar(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteAvatar, List("repos", owner.toString, repo.toString, "avatar"),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoAddCollaborator(config: GiteaConfig, owner: String, repo: String, collaborator: String, body: contract.AddCollaboratorOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoAddCollaborator, List("repos", owner.toString, repo.toString, "collaborators", collaborator.toString),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteCollaborator(config: GiteaConfig, owner: String, repo: String, collaborator: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteCollaborator, List("repos", owner.toString, repo.toString, "collaborators", collaborator.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetAllCommits(config: GiteaConfig, owner: String, repo: String, sha: Option[String] = None, path: Option[String] = None, since: Option[java.time.Instant] = None, until: Option[java.time.Instant] = None, stat: Option[Boolean] = None, verification: Option[Boolean] = None, files: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None, not: Option[String] = None): GiteaRequest[zio.Chunk[contract.Commit]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetAllCommits, List("repos", owner.toString, repo.toString, "commits"),
      sha.toList.map(value => ("sha", value.toString)) ++ path.toList.map(value => ("path", value.toString)) ++ since.toList.map(value => ("since", value.toString)) ++ until.toList.map(value => ("until", value.toString)) ++ stat.toList.map(value => ("stat", value.toString)) ++ verification.toList.map(value => ("verification", value.toString)) ++ files.toList.map(value => ("files", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ not.toList.map(value => ("not", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Commit]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCompareDiff(config: GiteaConfig, owner: String, repo: String, basehead: String, output: Option[contract.CompareOutput] = None): GiteaRequest[Either[contract.Compare, String]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCompareDiff, List("repos", owner.toString, repo.toString, "compare", basehead.toString),
      output.toList.map(value => ("output", value.value)), None, response => if output.isDefined then (if response.code.code == 200 then GiteaResponseMapper.decodeString(response).map(Right(_)) else Left(GiteaResponseMapper.toError(response))) else GiteaResponseMapper.decodeJsonAt[contract.Compare](response, sttp.model.StatusCode.Ok).map(Left(_)), sttp.model.MediaType.ApplicationJson, if output.isDefined then Accept.TextPlain else Accept.Json)

  def repoChangeFiles(config: GiteaConfig, owner: String, repo: String, body: contract.ChangeFilesOptions): GiteaRequest[contract.FilesResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoChangeFiles, List("repos", owner.toString, repo.toString, "contents"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.FilesResponse](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetContentsExt(config: GiteaConfig, owner: String, repo: String, filepath: String, ref: Option[String] = None, includes: Option[String] = None): GiteaRequest[contract.ContentsExtResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetContentsExt, List("repos", owner.toString, repo.toString, "contents-ext", filepath.toString),
      ref.toList.map(value => ("ref", value.toString)) ++ includes.toList.map(value => ("includes", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ContentsExtResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoUpdateFile(config: GiteaConfig, owner: String, repo: String, filepath: String, body: contract.UpdateFileOptions): GiteaRequest[contract.FileResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoUpdateFile, List("repos", owner.toString, repo.toString, "contents", filepath.toString),
      Nil, Some(body.toJson), response => if response.code.code == 200 || response.code.code == 201 then GiteaResponseMapper.decodeJson[contract.FileResponse](response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCreateFile(config: GiteaConfig, owner: String, repo: String, filepath: String, body: contract.CreateFileOptions): GiteaRequest[contract.FileResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateFile, List("repos", owner.toString, repo.toString, "contents", filepath.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.FileResponse](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteFile(config: GiteaConfig, owner: String, repo: String, filepath: String, body: contract.DeleteFileOptions): GiteaRequest[contract.FileDeleteResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteFile, List("repos", owner.toString, repo.toString, "contents", filepath.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.FileDeleteResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoApplyDiffPatch(config: GiteaConfig, owner: String, repo: String, body: contract.ApplyDiffPatchFileOptions): GiteaRequest[contract.FileResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoApplyDiffPatch, List("repos", owner.toString, repo.toString, "diffpatch"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.FileResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetEditorConfig(config: GiteaConfig, owner: String, repo: String, filepath: String, ref: Option[String] = None): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetEditorConfig, List("repos", owner.toString, repo.toString, "editorconfig", filepath.toString),
      ref.toList.map(value => ("ref", value.toString)), None, response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetFileContents(config: GiteaConfig, owner: String, repo: String, ref: Option[String] = None, body: String): GiteaRequest[zio.Chunk[contract.ContentsResponse]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetFileContents, List("repos", owner.toString, repo.toString, "file-contents"),
      ref.toList.map(value => ("ref", value.toString)) ++ List(("body", body.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.ContentsResponse]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetFileContentsPost(config: GiteaConfig, owner: String, repo: String, body: contract.GetFilesOptions, ref: Option[String] = None): GiteaRequest[zio.Chunk[contract.ContentsResponse]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetFileContentsPost, List("repos", owner.toString, repo.toString, "file-contents"),
      ref.toList.map(value => ("ref", value.toString)), Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.ContentsResponse]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listForks(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listForks, List("repos", owner.toString, repo.toString, "forks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoListHooks(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Hook]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoListHooks, List("repos", owner.toString, repo.toString, "hooks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Hook]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCreateHook(config: GiteaConfig, owner: String, repo: String, body: contract.CreateHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateHook, List("repos", owner.toString, repo.toString, "hooks"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetHook(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetHook, List("repos", owner.toString, repo.toString, "hooks", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteHook(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteHook, List("repos", owner.toString, repo.toString, "hooks", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoEditHook(config: GiteaConfig, owner: String, repo: String, id: Long, body: contract.EditHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoEditHook, List("repos", owner.toString, repo.toString, "hooks", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoTestHook(config: GiteaConfig, owner: String, repo: String, id: Long, ref: Option[String] = None): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoTestHook, List("repos", owner.toString, repo.toString, "hooks", id.toString, "tests"),
      ref.toList.map(value => ("ref", value.toString)), None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetIssueConfig(config: GiteaConfig, owner: String, repo: String): GiteaRequest[contract.IssueConfig] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetIssueConfig, List("repos", owner.toString, repo.toString, "issue_config"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.IssueConfig](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoValidateIssueConfig(config: GiteaConfig, owner: String, repo: String): GiteaRequest[contract.IssueConfigValidation] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoValidateIssueConfig, List("repos", owner.toString, repo.toString, "issue_config", "validate"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.IssueConfigValidation](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetIssueTemplates(config: GiteaConfig, owner: String, repo: String): GiteaRequest[zio.Chunk[contract.IssueTemplate]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetIssueTemplates, List("repos", owner.toString, repo.toString, "issue_templates"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.IssueTemplate]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoListKeys(config: GiteaConfig, owner: String, repo: String, keyId: Option[Int] = None, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.DeployKey]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoListKeys, List("repos", owner.toString, repo.toString, "keys"),
      keyId.toList.map(value => ("key_id", value.toString)) ++ fingerprint.toList.map(value => ("fingerprint", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.DeployKey]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCreateKey(config: GiteaConfig, owner: String, repo: String, body: contract.CreateKeyOption): GiteaRequest[contract.DeployKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateKey, List("repos", owner.toString, repo.toString, "keys"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.DeployKey](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetKey(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[contract.DeployKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetKey, List("repos", owner.toString, repo.toString, "keys", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.DeployKey](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteKey(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteKey, List("repos", owner.toString, repo.toString, "keys", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueListLabels(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Label]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueListLabels, List("repos", owner.toString, repo.toString, "labels"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Label]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueCreateLabel(config: GiteaConfig, owner: String, repo: String, body: contract.CreateLabelOption): GiteaRequest[contract.Label] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueCreateLabel, List("repos", owner.toString, repo.toString, "labels"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Label](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueGetLabel(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[contract.Label] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueGetLabel, List("repos", owner.toString, repo.toString, "labels", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Label](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueDeleteLabel(config: GiteaConfig, owner: String, repo: String, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueDeleteLabel, List("repos", owner.toString, repo.toString, "labels", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueEditLabel(config: GiteaConfig, owner: String, repo: String, id: Long, body: contract.EditLabelOption): GiteaRequest[contract.Label] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueEditLabel, List("repos", owner.toString, repo.toString, "labels", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Label](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetLicenses(config: GiteaConfig, owner: String, repo: String): GiteaRequest[zio.Chunk[String]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetLicenses, List("repos", owner.toString, repo.toString, "licenses"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[String]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoMergeUpstream(config: GiteaConfig, owner: String, repo: String, body: contract.MergeUpstreamRequest): GiteaRequest[contract.MergeUpstreamResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoMergeUpstream, List("repos", owner.toString, repo.toString, "merge-upstream"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.MergeUpstreamResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueGetMilestonesList(config: GiteaConfig, owner: String, repo: String, state: Option[String] = None, name: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Milestone]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueGetMilestonesList, List("repos", owner.toString, repo.toString, "milestones"),
      state.toList.map(value => ("state", value.toString)) ++ name.toList.map(value => ("name", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Milestone]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueCreateMilestone(config: GiteaConfig, owner: String, repo: String, body: contract.CreateMilestoneOption): GiteaRequest[contract.Milestone] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueCreateMilestone, List("repos", owner.toString, repo.toString, "milestones"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Milestone](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueGetMilestone(config: GiteaConfig, owner: String, repo: String, id: String): GiteaRequest[contract.Milestone] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueGetMilestone, List("repos", owner.toString, repo.toString, "milestones", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Milestone](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueDeleteMilestone(config: GiteaConfig, owner: String, repo: String, id: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueDeleteMilestone, List("repos", owner.toString, repo.toString, "milestones", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def issueEditMilestone(config: GiteaConfig, owner: String, repo: String, id: String, body: contract.EditMilestoneOption): GiteaRequest[contract.Milestone] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.issueEditMilestone, List("repos", owner.toString, repo.toString, "milestones", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Milestone](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoMirrorSync(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoMirrorSync, List("repos", owner.toString, repo.toString, "mirror-sync"),
      Nil, None, response => if response.code.code == 200 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoListPushMirrors(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.PushMirror]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoListPushMirrors, List("repos", owner.toString, repo.toString, "push_mirrors"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.PushMirror]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoAddPushMirror(config: GiteaConfig, owner: String, repo: String, body: contract.CreatePushMirrorOption): GiteaRequest[contract.PushMirror] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoAddPushMirror, List("repos", owner.toString, repo.toString, "push_mirrors"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.PushMirror](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoPushMirrorSync(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoPushMirrorSync, List("repos", owner.toString, repo.toString, "push_mirrors-sync"),
      Nil, None, response => if response.code.code == 200 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetPushMirrorByRemoteName(config: GiteaConfig, owner: String, repo: String, name: String): GiteaRequest[contract.PushMirror] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetPushMirrorByRemoteName, List("repos", owner.toString, repo.toString, "push_mirrors", name.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.PushMirror](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeletePushMirror(config: GiteaConfig, owner: String, repo: String, name: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeletePushMirror, List("repos", owner.toString, repo.toString, "push_mirrors", name.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoSigningKeySSH(config: GiteaConfig, owner: String, repo: String): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoSigningKeySSH, List("repos", owner.toString, repo.toString, "signing-key.pub"),
      Nil, None, response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.TextPlain)

  def userCurrentCheckSubscription(config: GiteaConfig, owner: String, repo: String): GiteaRequest[contract.WatchInfo] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentCheckSubscription, List("repos", owner.toString, repo.toString, "subscription"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.WatchInfo](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentPutSubscription(config: GiteaConfig, owner: String, repo: String): GiteaRequest[contract.WatchInfo] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentPutSubscription, List("repos", owner.toString, repo.toString, "subscription"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.WatchInfo](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentDeleteSubscription(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentDeleteSubscription, List("repos", owner.toString, repo.toString, "subscription"),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCreateTag(config: GiteaConfig, owner: String, repo: String, body: contract.CreateTagOption): GiteaRequest[contract.Tag] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateTag, List("repos", owner.toString, repo.toString, "tags"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Tag](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteTag(config: GiteaConfig, owner: String, repo: String, tag: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteTag, List("repos", owner.toString, repo.toString, "tags", tag.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoAddTeam(config: GiteaConfig, owner: String, repo: String, team: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoAddTeam, List("repos", owner.toString, repo.toString, "teams", team.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteTeam(config: GiteaConfig, owner: String, repo: String, team: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteTeam, List("repos", owner.toString, repo.toString, "teams", team.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoTrackedTimes(config: GiteaConfig, owner: String, repo: String, user: Option[String] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.TrackedTime]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoTrackedTimes, List("repos", owner.toString, repo.toString, "times"),
      user.toList.map(value => ("user", value.toString)) ++ since.toList.map(value => ("since", value.toString)) ++ before.toList.map(value => ("before", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.TrackedTime]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userTrackedTimes(config: GiteaConfig, owner: String, repo: String, user: String): GiteaRequest[zio.Chunk[contract.TrackedTime]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userTrackedTimes, List("repos", owner.toString, repo.toString, "times", user.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.TrackedTime]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoUpdateTopics(config: GiteaConfig, owner: String, repo: String, body: contract.RepoTopicOptions): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoUpdateTopics, List("repos", owner.toString, repo.toString, "topics"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoAddTopic(config: GiteaConfig, owner: String, repo: String, topic: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoAddTopic, List("repos", owner.toString, repo.toString, "topics", topic.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteTopic(config: GiteaConfig, owner: String, repo: String, topic: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteTopic, List("repos", owner.toString, repo.toString, "topics", topic.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCreateWikiPage(config: GiteaConfig, owner: String, repo: String, body: contract.CreateWikiPageOptions): GiteaRequest[contract.WikiPage] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateWikiPage, List("repos", owner.toString, repo.toString, "wiki", "new"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.WikiPage](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetWikiPage(config: GiteaConfig, owner: String, repo: String, pageName: String): GiteaRequest[contract.WikiPage] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetWikiPage, List("repos", owner.toString, repo.toString, "wiki", "page", pageName.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.WikiPage](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoDeleteWikiPage(config: GiteaConfig, owner: String, repo: String, pageName: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoDeleteWikiPage, List("repos", owner.toString, repo.toString, "wiki", "page", pageName.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoEditWikiPage(config: GiteaConfig, owner: String, repo: String, pageName: String, body: contract.CreateWikiPageOptions): GiteaRequest[contract.WikiPage] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoEditWikiPage, List("repos", owner.toString, repo.toString, "wiki", "page", pageName.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.WikiPage](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetWikiPages(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.WikiPageMetaData]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetWikiPages, List("repos", owner.toString, repo.toString, "wiki", "pages"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.WikiPageMetaData]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoGetWikiPageRevisions(config: GiteaConfig, owner: String, repo: String, pageName: String, page: Option[Int] = None): GiteaRequest[contract.WikiCommitList] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetWikiPageRevisions, List("repos", owner.toString, repo.toString, "wiki", "revisions", pageName.toString),
      page.toList.map(value => ("page", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.WikiCommitList](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def generateRepo(config: GiteaConfig, templateOwner: String, templateRepo: String, body: contract.GenerateRepoOption): GiteaRequest[contract.Repository] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.generateRepo, List("repos", templateOwner.toString, templateRepo.toString, "generate"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Repository](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)
