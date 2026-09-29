package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.ActionsOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedActionsRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveActionsOperations extends ActionsOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def listAdminWorkflowJobs(status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse] =
    executor.send(GeneratedActionsRequests.listAdminWorkflowJobs(config, status, page, limit, sort, order))

  override def getAdminRunners(disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse] =
    executor.send(GeneratedActionsRequests.getAdminRunners(config, disabled))

  override def adminCreateRunnerRegistrationToken(): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.adminCreateRunnerRegistrationToken(config))

  override def getAdminRunner(runnerId: String): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.getAdminRunner(config, runnerId))

  override def deleteAdminRunner(runnerId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteAdminRunner(config, runnerId))

  override def updateAdminRunner(runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.updateAdminRunner(config, runnerId, body))

  override def listAdminWorkflowRuns(event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse] =
    executor.send(GeneratedActionsRequests.listAdminWorkflowRuns(config, event, branch, status, actor, headSha, page, limit))

  override def getOrgWorkflowJobs(org: String, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse] =
    executor.send(GeneratedActionsRequests.getOrgWorkflowJobs(config, org, status, page, limit))

  override def getOrgRunners(org: String, disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse] =
    executor.send(GeneratedActionsRequests.getOrgRunners(config, org, disabled))

  override def orgCreateRunnerRegistrationToken(org: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.orgCreateRunnerRegistrationToken(config, org))

  override def getOrgRunner(org: String, runnerId: String): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.getOrgRunner(config, org, runnerId))

  override def deleteOrgRunner(org: String, runnerId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteOrgRunner(config, org, runnerId))

  override def updateOrgRunner(org: String, runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.updateOrgRunner(config, org, runnerId, body))

  override def getOrgWorkflowRuns(org: String, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse] =
    executor.send(GeneratedActionsRequests.getOrgWorkflowRuns(config, org, event, branch, status, actor, headSha, page, limit))

  override def orgListActionsSecrets(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Secret]] =
    executor.send(GeneratedActionsRequests.orgListActionsSecrets(config, org, page, limit))

  override def updateOrgSecret(org: String, secretname: String, body: contract.CreateOrUpdateSecretOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.updateOrgSecret(config, org, secretname, body))

  override def deleteOrgSecret(org: String, secretname: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteOrgSecret(config, org, secretname))

  override def getOrgVariablesList(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.ActionVariable]] =
    executor.send(GeneratedActionsRequests.getOrgVariablesList(config, org, page, limit))

  override def getOrgVariable(org: String, variablename: String): IO[GiteaError, contract.ActionVariable] =
    executor.send(GeneratedActionsRequests.getOrgVariable(config, org, variablename))

  override def updateOrgVariable(org: String, variablename: String, body: contract.UpdateVariableOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.updateOrgVariable(config, org, variablename, body))

  override def createOrgVariable(org: String, variablename: String, body: contract.CreateVariableOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.createOrgVariable(config, org, variablename, body))

  override def deleteOrgVariable(org: String, variablename: String): IO[GiteaError, Option[contract.ActionVariable]] =
    executor.send(GeneratedActionsRequests.deleteOrgVariable(config, org, variablename))

  override def getArtifacts(owner: String, repo: String, name: Option[String] = None): IO[GiteaError, contract.ActionArtifactsResponse] =
    executor.send(GeneratedActionsRequests.getArtifacts(config, owner, repo, name))

  override def getArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, contract.ActionArtifact] =
    executor.send(GeneratedActionsRequests.getArtifact(config, owner, repo, artifactId))

  override def deleteArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteArtifact(config, owner, repo, artifactId))

  override def listWorkflowJobs(owner: String, repo: String, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse] =
    executor.send(GeneratedActionsRequests.listWorkflowJobs(config, owner, repo, status, page, limit, sort, order))

  override def getWorkflowJob(owner: String, repo: String, jobId: String): IO[GiteaError, contract.ActionWorkflowJob] =
    executor.send(GeneratedActionsRequests.getWorkflowJob(config, owner, repo, jobId))

  override def downloadActionsRunJobLogs(owner: String, repo: String, jobId: Int): IO[GiteaError, zio.Chunk[Byte]] =
    executor.send(GeneratedActionsRequests.downloadActionsRunJobLogs(config, owner, repo, jobId))

  override def getRepoRunners(owner: String, repo: String, disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse] =
    executor.send(GeneratedActionsRequests.getRepoRunners(config, owner, repo, disabled))

  override def repoCreateRunnerRegistrationToken(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.repoCreateRunnerRegistrationToken(config, owner, repo))

  override def getRepoRunner(owner: String, repo: String, runnerId: String): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.getRepoRunner(config, owner, repo, runnerId))

  override def deleteRepoRunner(owner: String, repo: String, runnerId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteRepoRunner(config, owner, repo, runnerId))

  override def updateRepoRunner(owner: String, repo: String, runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.updateRepoRunner(config, owner, repo, runnerId, body))

  override def getWorkflowRuns(owner: String, repo: String, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, excludePullRequests: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse] =
    executor.send(GeneratedActionsRequests.getWorkflowRuns(config, owner, repo, event, branch, status, actor, headSha, excludePullRequests, page, limit))

  override def GetWorkflowRun(owner: String, repo: String, run: Int): IO[GiteaError, contract.ActionWorkflowRun] =
    executor.send(GeneratedActionsRequests.GetWorkflowRun(config, owner, repo, run))

  override def deleteActionRun(owner: String, repo: String, run: Int): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteActionRun(config, owner, repo, run))

  override def getArtifactsOfRun(owner: String, repo: String, run: Int, name: Option[String] = None): IO[GiteaError, contract.ActionArtifactsResponse] =
    executor.send(GeneratedActionsRequests.getArtifactsOfRun(config, owner, repo, run, name))

  override def listWorkflowRunJobs(owner: String, repo: String, run: Int, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse] =
    executor.send(GeneratedActionsRequests.listWorkflowRunJobs(config, owner, repo, run, status, page, limit, sort, order))

  override def rerunWorkflowJob(owner: String, repo: String, run: Int, jobId: Int): IO[GiteaError, contract.ActionWorkflowJob] =
    executor.send(GeneratedActionsRequests.rerunWorkflowJob(config, owner, repo, run, jobId))

  override def rerunWorkflowRun(owner: String, repo: String, run: Int): IO[GiteaError, contract.ActionWorkflowRun] =
    executor.send(GeneratedActionsRequests.rerunWorkflowRun(config, owner, repo, run))

  override def rerunFailedWorkflowRun(owner: String, repo: String, run: Int): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.rerunFailedWorkflowRun(config, owner, repo, run))

  override def repoListActionsSecrets(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Secret]] =
    executor.send(GeneratedActionsRequests.repoListActionsSecrets(config, owner, repo, page, limit))

  override def updateRepoSecret(owner: String, repo: String, secretname: String, body: contract.CreateOrUpdateSecretOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.updateRepoSecret(config, owner, repo, secretname, body))

  override def deleteRepoSecret(owner: String, repo: String, secretname: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteRepoSecret(config, owner, repo, secretname))

  override def ListActionTasks(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionTaskResponse] =
    executor.send(GeneratedActionsRequests.ListActionTasks(config, owner, repo, page, limit))

  override def getRepoVariablesList(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.ActionVariable]] =
    executor.send(GeneratedActionsRequests.getRepoVariablesList(config, owner, repo, page, limit))

  override def getRepoVariable(owner: String, repo: String, variablename: String): IO[GiteaError, contract.ActionVariable] =
    executor.send(GeneratedActionsRequests.getRepoVariable(config, owner, repo, variablename))

  override def updateRepoVariable(owner: String, repo: String, variablename: String, body: contract.UpdateVariableOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.updateRepoVariable(config, owner, repo, variablename, body))

  override def createRepoVariable(owner: String, repo: String, variablename: String, body: contract.CreateVariableOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.createRepoVariable(config, owner, repo, variablename, body))

  override def deleteRepoVariable(owner: String, repo: String, variablename: String): IO[GiteaError, Option[contract.ActionVariable]] =
    executor.send(GeneratedActionsRequests.deleteRepoVariable(config, owner, repo, variablename))

  override def ActionsListRepositoryWorkflows(owner: String, repo: String): IO[GiteaError, contract.ActionWorkflowResponse] =
    executor.send(GeneratedActionsRequests.ActionsListRepositoryWorkflows(config, owner, repo))

  override def ActionsGetWorkflow(owner: String, repo: String, workflowId: String): IO[GiteaError, contract.ActionWorkflow] =
    executor.send(GeneratedActionsRequests.ActionsGetWorkflow(config, owner, repo, workflowId))

  override def ActionsDisableWorkflow(owner: String, repo: String, workflowId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.ActionsDisableWorkflow(config, owner, repo, workflowId))

  override def ActionsDispatchWorkflow(owner: String, repo: String, workflowId: String, body: contract.CreateActionWorkflowDispatch, returnRunDetails: Option[Boolean] = None, scopedWorkflowSourceRepoId: Option[Long] = None): IO[GiteaError, Option[contract.RunDetails]] =
    executor.send(GeneratedActionsRequests.ActionsDispatchWorkflow(config, owner, repo, workflowId, body, returnRunDetails, scopedWorkflowSourceRepoId))

  override def ActionsEnableWorkflow(owner: String, repo: String, workflowId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.ActionsEnableWorkflow(config, owner, repo, workflowId))

  override def getUserWorkflowJobs(status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse] =
    executor.send(GeneratedActionsRequests.getUserWorkflowJobs(config, status, page, limit, sort, order))

  override def getUserRunners(disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse] =
    executor.send(GeneratedActionsRequests.getUserRunners(config, disabled))

  override def userCreateRunnerRegistrationToken(): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.userCreateRunnerRegistrationToken(config))

  override def getUserRunner(runnerId: String): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.getUserRunner(config, runnerId))

  override def deleteUserRunner(runnerId: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteUserRunner(config, runnerId))

  override def updateUserRunner(runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner] =
    executor.send(GeneratedActionsRequests.updateUserRunner(config, runnerId, body))

  override def getUserWorkflowRuns(event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse] =
    executor.send(GeneratedActionsRequests.getUserWorkflowRuns(config, event, branch, status, actor, headSha, page, limit))

  override def updateUserSecret(secretname: String, body: contract.CreateOrUpdateSecretOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.updateUserSecret(config, secretname, body))

  override def deleteUserSecret(secretname: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteUserSecret(config, secretname))

  override def getUserVariablesList(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.ActionVariable]] =
    executor.send(GeneratedActionsRequests.getUserVariablesList(config, page, limit))

  override def getUserVariable(variablename: String): IO[GiteaError, contract.ActionVariable] =
    executor.send(GeneratedActionsRequests.getUserVariable(config, variablename))

  override def updateUserVariable(variablename: String, body: contract.UpdateVariableOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.updateUserVariable(config, variablename, body))

  override def createUserVariable(variablename: String, body: contract.CreateVariableOption): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.createUserVariable(config, variablename, body))

  override def deleteUserVariable(variablename: String): IO[GiteaError, Unit] =
    executor.send(GeneratedActionsRequests.deleteUserVariable(config, variablename))
