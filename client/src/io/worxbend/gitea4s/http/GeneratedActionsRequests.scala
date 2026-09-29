package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedActionsRequests:
  def listAdminWorkflowJobs(config: GiteaConfig, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): GiteaRequest[contract.ActionWorkflowJobsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listAdminWorkflowJobs, List("admin", "actions", "jobs"),
      status.toList.map(value => ("status", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ sort.toList.map(value => ("sort", value.toString)) ++ order.toList.map(value => ("order", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJobsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getAdminRunners(config: GiteaConfig, disabled: Option[Boolean] = None): GiteaRequest[contract.ActionRunnersResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getAdminRunners, List("admin", "actions", "runners"),
      disabled.toList.map(value => ("disabled", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunnersResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def adminCreateRunnerRegistrationToken(config: GiteaConfig): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.adminCreateRunnerRegistrationToken, List("admin", "actions", "runners", "registration-token"),
      Nil, None, response => if response.code.code == 200 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getAdminRunner(config: GiteaConfig, runnerId: String): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getAdminRunner, List("admin", "actions", "runners", runnerId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteAdminRunner(config: GiteaConfig, runnerId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteAdminRunner, List("admin", "actions", "runners", runnerId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateAdminRunner(config: GiteaConfig, runnerId: String, body: contract.EditActionRunnerOption): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateAdminRunner, List("admin", "actions", "runners", runnerId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listAdminWorkflowRuns(config: GiteaConfig, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.ActionWorkflowRunsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listAdminWorkflowRuns, List("admin", "actions", "runs"),
      event.toList.map(value => ("event", value.toString)) ++ branch.toList.map(value => ("branch", value.toString)) ++ status.toList.map(value => ("status", value.toString)) ++ actor.toList.map(value => ("actor", value.toString)) ++ headSha.toList.map(value => ("head_sha", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowRunsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getOrgWorkflowJobs(config: GiteaConfig, org: String, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.ActionWorkflowJobsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getOrgWorkflowJobs, List("orgs", org.toString, "actions", "jobs"),
      status.toList.map(value => ("status", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJobsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getOrgRunners(config: GiteaConfig, org: String, disabled: Option[Boolean] = None): GiteaRequest[contract.ActionRunnersResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getOrgRunners, List("orgs", org.toString, "actions", "runners"),
      disabled.toList.map(value => ("disabled", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunnersResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgCreateRunnerRegistrationToken(config: GiteaConfig, org: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgCreateRunnerRegistrationToken, List("orgs", org.toString, "actions", "runners", "registration-token"),
      Nil, None, response => if response.code.code == 200 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getOrgRunner(config: GiteaConfig, org: String, runnerId: String): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getOrgRunner, List("orgs", org.toString, "actions", "runners", runnerId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteOrgRunner(config: GiteaConfig, org: String, runnerId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteOrgRunner, List("orgs", org.toString, "actions", "runners", runnerId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateOrgRunner(config: GiteaConfig, org: String, runnerId: String, body: contract.EditActionRunnerOption): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateOrgRunner, List("orgs", org.toString, "actions", "runners", runnerId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getOrgWorkflowRuns(config: GiteaConfig, org: String, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.ActionWorkflowRunsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getOrgWorkflowRuns, List("orgs", org.toString, "actions", "runs"),
      event.toList.map(value => ("event", value.toString)) ++ branch.toList.map(value => ("branch", value.toString)) ++ status.toList.map(value => ("status", value.toString)) ++ actor.toList.map(value => ("actor", value.toString)) ++ headSha.toList.map(value => ("head_sha", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowRunsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListActionsSecrets(config: GiteaConfig, org: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Secret]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListActionsSecrets, List("orgs", org.toString, "actions", "secrets"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Secret]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateOrgSecret(config: GiteaConfig, org: String, secretname: String, body: contract.CreateOrUpdateSecretOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateOrgSecret, List("orgs", org.toString, "actions", "secrets", secretname.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteOrgSecret(config: GiteaConfig, org: String, secretname: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteOrgSecret, List("orgs", org.toString, "actions", "secrets", secretname.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getOrgVariablesList(config: GiteaConfig, org: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.ActionVariable]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getOrgVariablesList, List("orgs", org.toString, "actions", "variables"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.ActionVariable]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getOrgVariable(config: GiteaConfig, org: String, variablename: String): GiteaRequest[contract.ActionVariable] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getOrgVariable, List("orgs", org.toString, "actions", "variables", variablename.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionVariable](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateOrgVariable(config: GiteaConfig, org: String, variablename: String, body: contract.UpdateVariableOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateOrgVariable, List("orgs", org.toString, "actions", "variables", variablename.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def createOrgVariable(config: GiteaConfig, org: String, variablename: String, body: contract.CreateVariableOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.createOrgVariable, List("orgs", org.toString, "actions", "variables", variablename.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteOrgVariable(config: GiteaConfig, org: String, variablename: String): GiteaRequest[Option[contract.ActionVariable]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteOrgVariable, List("orgs", org.toString, "actions", "variables", variablename.toString),
      Nil, None, response => if response.code.code == 201 || response.code.code == 204 then Right(None) else if response.code.code == 200 then GiteaResponseMapper.decodeJson[contract.ActionVariable](response).map(Some(_)) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getArtifacts(config: GiteaConfig, owner: String, repo: String, name: Option[String] = None): GiteaRequest[contract.ActionArtifactsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getArtifacts, List("repos", owner.toString, repo.toString, "actions", "artifacts"),
      name.toList.map(value => ("name", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionArtifactsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getArtifact(config: GiteaConfig, owner: String, repo: String, artifactId: String): GiteaRequest[contract.ActionArtifact] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getArtifact, List("repos", owner.toString, repo.toString, "actions", "artifacts", artifactId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionArtifact](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteArtifact(config: GiteaConfig, owner: String, repo: String, artifactId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteArtifact, List("repos", owner.toString, repo.toString, "actions", "artifacts", artifactId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listWorkflowJobs(config: GiteaConfig, owner: String, repo: String, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): GiteaRequest[contract.ActionWorkflowJobsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listWorkflowJobs, List("repos", owner.toString, repo.toString, "actions", "jobs"),
      status.toList.map(value => ("status", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ sort.toList.map(value => ("sort", value.toString)) ++ order.toList.map(value => ("order", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJobsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getWorkflowJob(config: GiteaConfig, owner: String, repo: String, jobId: String): GiteaRequest[contract.ActionWorkflowJob] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getWorkflowJob, List("repos", owner.toString, repo.toString, "actions", "jobs", jobId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJob](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def downloadActionsRunJobLogs(config: GiteaConfig, owner: String, repo: String, jobId: Int): GiteaRequest[zio.Chunk[Byte]] =
    GiteaRequests.binaryFromContract(config, GeneratedEndpoints.downloadActionsRunJobLogs, List("repos", owner.toString, repo.toString, "actions", "jobs", jobId.toString, "logs"), Nil)

  def getRepoRunners(config: GiteaConfig, owner: String, repo: String, disabled: Option[Boolean] = None): GiteaRequest[contract.ActionRunnersResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getRepoRunners, List("repos", owner.toString, repo.toString, "actions", "runners"),
      disabled.toList.map(value => ("disabled", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunnersResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoCreateRunnerRegistrationToken(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoCreateRunnerRegistrationToken, List("repos", owner.toString, repo.toString, "actions", "runners", "registration-token"),
      Nil, None, response => if response.code.code == 200 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getRepoRunner(config: GiteaConfig, owner: String, repo: String, runnerId: String): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getRepoRunner, List("repos", owner.toString, repo.toString, "actions", "runners", runnerId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteRepoRunner(config: GiteaConfig, owner: String, repo: String, runnerId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteRepoRunner, List("repos", owner.toString, repo.toString, "actions", "runners", runnerId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateRepoRunner(config: GiteaConfig, owner: String, repo: String, runnerId: String, body: contract.EditActionRunnerOption): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateRepoRunner, List("repos", owner.toString, repo.toString, "actions", "runners", runnerId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getWorkflowRuns(config: GiteaConfig, owner: String, repo: String, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, excludePullRequests: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.ActionWorkflowRunsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getWorkflowRuns, List("repos", owner.toString, repo.toString, "actions", "runs"),
      event.toList.map(value => ("event", value.toString)) ++ branch.toList.map(value => ("branch", value.toString)) ++ status.toList.map(value => ("status", value.toString)) ++ actor.toList.map(value => ("actor", value.toString)) ++ headSha.toList.map(value => ("head_sha", value.toString)) ++ excludePullRequests.toList.map(value => ("exclude_pull_requests", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowRunsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def GetWorkflowRun(config: GiteaConfig, owner: String, repo: String, run: Int): GiteaRequest[contract.ActionWorkflowRun] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.GetWorkflowRun, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowRun](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteActionRun(config: GiteaConfig, owner: String, repo: String, run: Int): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteActionRun, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getArtifactsOfRun(config: GiteaConfig, owner: String, repo: String, run: Int, name: Option[String] = None): GiteaRequest[contract.ActionArtifactsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getArtifactsOfRun, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString, "artifacts"),
      name.toList.map(value => ("name", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionArtifactsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listWorkflowRunJobs(config: GiteaConfig, owner: String, repo: String, run: Int, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): GiteaRequest[contract.ActionWorkflowJobsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listWorkflowRunJobs, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString, "jobs"),
      status.toList.map(value => ("status", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ sort.toList.map(value => ("sort", value.toString)) ++ order.toList.map(value => ("order", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJobsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def rerunWorkflowJob(config: GiteaConfig, owner: String, repo: String, run: Int, jobId: Int): GiteaRequest[contract.ActionWorkflowJob] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.rerunWorkflowJob, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString, "jobs", jobId.toString, "rerun"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJob](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def rerunWorkflowRun(config: GiteaConfig, owner: String, repo: String, run: Int): GiteaRequest[contract.ActionWorkflowRun] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.rerunWorkflowRun, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString, "rerun"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowRun](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def rerunFailedWorkflowRun(config: GiteaConfig, owner: String, repo: String, run: Int): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.rerunFailedWorkflowRun, List("repos", owner.toString, repo.toString, "actions", "runs", run.toString, "rerun-failed-jobs"),
      Nil, None, response => if response.code.code == 201 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def repoListActionsSecrets(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Secret]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoListActionsSecrets, List("repos", owner.toString, repo.toString, "actions", "secrets"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Secret]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateRepoSecret(config: GiteaConfig, owner: String, repo: String, secretname: String, body: contract.CreateOrUpdateSecretOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateRepoSecret, List("repos", owner.toString, repo.toString, "actions", "secrets", secretname.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteRepoSecret(config: GiteaConfig, owner: String, repo: String, secretname: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteRepoSecret, List("repos", owner.toString, repo.toString, "actions", "secrets", secretname.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def ListActionTasks(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.ActionTaskResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.ListActionTasks, List("repos", owner.toString, repo.toString, "actions", "tasks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionTaskResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getRepoVariablesList(config: GiteaConfig, owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.ActionVariable]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getRepoVariablesList, List("repos", owner.toString, repo.toString, "actions", "variables"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.ActionVariable]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getRepoVariable(config: GiteaConfig, owner: String, repo: String, variablename: String): GiteaRequest[contract.ActionVariable] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getRepoVariable, List("repos", owner.toString, repo.toString, "actions", "variables", variablename.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionVariable](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateRepoVariable(config: GiteaConfig, owner: String, repo: String, variablename: String, body: contract.UpdateVariableOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateRepoVariable, List("repos", owner.toString, repo.toString, "actions", "variables", variablename.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def createRepoVariable(config: GiteaConfig, owner: String, repo: String, variablename: String, body: contract.CreateVariableOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.createRepoVariable, List("repos", owner.toString, repo.toString, "actions", "variables", variablename.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteRepoVariable(config: GiteaConfig, owner: String, repo: String, variablename: String): GiteaRequest[Option[contract.ActionVariable]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteRepoVariable, List("repos", owner.toString, repo.toString, "actions", "variables", variablename.toString),
      Nil, None, response => if response.code.code == 201 || response.code.code == 204 then Right(None) else if response.code.code == 200 then GiteaResponseMapper.decodeJson[contract.ActionVariable](response).map(Some(_)) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def ActionsListRepositoryWorkflows(config: GiteaConfig, owner: String, repo: String): GiteaRequest[contract.ActionWorkflowResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.ActionsListRepositoryWorkflows, List("repos", owner.toString, repo.toString, "actions", "workflows"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def ActionsGetWorkflow(config: GiteaConfig, owner: String, repo: String, workflowId: String): GiteaRequest[contract.ActionWorkflow] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.ActionsGetWorkflow, List("repos", owner.toString, repo.toString, "actions", "workflows", workflowId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflow](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def ActionsDisableWorkflow(config: GiteaConfig, owner: String, repo: String, workflowId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.ActionsDisableWorkflow, List("repos", owner.toString, repo.toString, "actions", "workflows", workflowId.toString, "disable"),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def ActionsDispatchWorkflow(config: GiteaConfig, owner: String, repo: String, workflowId: String, body: contract.CreateActionWorkflowDispatch, returnRunDetails: Option[Boolean] = None, scopedWorkflowSourceRepoId: Option[Long] = None): GiteaRequest[Option[contract.RunDetails]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.ActionsDispatchWorkflow, List("repos", owner.toString, repo.toString, "actions", "workflows", workflowId.toString, "dispatches"),
      returnRunDetails.toList.map(value => ("return_run_details", value.toString)) ++ scopedWorkflowSourceRepoId.toList.map(value => ("scoped_workflow_source_repo_id", value.toString)), Some(body.toJson), response => if response.code.code == 204 then Right(None) else if response.code.code == 200 then GiteaResponseMapper.decodeJson[contract.RunDetails](response).map(Some(_)) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def ActionsEnableWorkflow(config: GiteaConfig, owner: String, repo: String, workflowId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.ActionsEnableWorkflow, List("repos", owner.toString, repo.toString, "actions", "workflows", workflowId.toString, "enable"),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserWorkflowJobs(config: GiteaConfig, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): GiteaRequest[contract.ActionWorkflowJobsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserWorkflowJobs, List("user", "actions", "jobs"),
      status.toList.map(value => ("status", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ sort.toList.map(value => ("sort", value.toString)) ++ order.toList.map(value => ("order", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowJobsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserRunners(config: GiteaConfig, disabled: Option[Boolean] = None): GiteaRequest[contract.ActionRunnersResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserRunners, List("user", "actions", "runners"),
      disabled.toList.map(value => ("disabled", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunnersResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCreateRunnerRegistrationToken(config: GiteaConfig): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCreateRunnerRegistrationToken, List("user", "actions", "runners", "registration-token"),
      Nil, None, response => if response.code.code == 200 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserRunner(config: GiteaConfig, runnerId: String): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserRunner, List("user", "actions", "runners", runnerId.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteUserRunner(config: GiteaConfig, runnerId: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteUserRunner, List("user", "actions", "runners", runnerId.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateUserRunner(config: GiteaConfig, runnerId: String, body: contract.EditActionRunnerOption): GiteaRequest[contract.ActionRunner] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateUserRunner, List("user", "actions", "runners", runnerId.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.ActionRunner](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserWorkflowRuns(config: GiteaConfig, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.ActionWorkflowRunsResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserWorkflowRuns, List("user", "actions", "runs"),
      event.toList.map(value => ("event", value.toString)) ++ branch.toList.map(value => ("branch", value.toString)) ++ status.toList.map(value => ("status", value.toString)) ++ actor.toList.map(value => ("actor", value.toString)) ++ headSha.toList.map(value => ("head_sha", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionWorkflowRunsResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateUserSecret(config: GiteaConfig, secretname: String, body: contract.CreateOrUpdateSecretOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateUserSecret, List("user", "actions", "secrets", secretname.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteUserSecret(config: GiteaConfig, secretname: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteUserSecret, List("user", "actions", "secrets", secretname.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserVariablesList(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.ActionVariable]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserVariablesList, List("user", "actions", "variables"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.ActionVariable]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserVariable(config: GiteaConfig, variablename: String): GiteaRequest[contract.ActionVariable] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserVariable, List("user", "actions", "variables", variablename.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ActionVariable](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateUserVariable(config: GiteaConfig, variablename: String, body: contract.UpdateVariableOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateUserVariable, List("user", "actions", "variables", variablename.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def createUserVariable(config: GiteaConfig, variablename: String, body: contract.CreateVariableOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.createUserVariable, List("user", "actions", "variables", variablename.toString),
      Nil, Some(body.toJson), response => if response.code.code == 201 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def deleteUserVariable(config: GiteaConfig, variablename: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.deleteUserVariable, List("user", "actions", "variables", variablename.toString),
      Nil, None, response => if response.code.code == 201 || response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)
