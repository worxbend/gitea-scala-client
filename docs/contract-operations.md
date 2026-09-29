# Gitea 1.27.3 operation reference

This page covers the 312 operations added after the initial handwritten client surface.
Every method is available on the ordinary `GiteaClient` namespace named below.
The wire models live in `io.worxbend.gitea4s.model.contract`; see [contract models](contract-models.md).
Path arguments are escaped as individual segments. Optional query arguments default to `None`.
Mutating requests are not automatically retried. Multipart uploads require `AttachmentUpload`.

## Actions

### `listAdminWorkflowJobs`

**GET `/admin/actions/jobs`** — ``client.actions.listAdminWorkflowJobs(status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse]``

- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `sort` (query, optional, string): sort jobs by attribute. Supported values are "id". Default is "id"
- `order` (query, optional, string): sort order, either "asc" (ascending) or "desc" (descending). Default is "asc"
- HTTP 200: `#/responses/WorkflowJobsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `getAdminRunners`

**GET `/admin/actions/runners`** — ``client.actions.getAdminRunners(disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse]``

- `disabled` (query, optional, boolean): filter by disabled status (true or false)
- HTTP 200: `#/responses/RunnerList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `adminCreateRunnerRegistrationToken`

**POST `/admin/actions/runners/registration-token`** — ``client.actions.adminCreateRunnerRegistrationToken(): IO[GiteaError, Unit]``

- HTTP 200: `#/responses/RegistrationToken`

### `getAdminRunner`

**GET `/admin/actions/runners/{runner_id}`** — ``client.actions.getAdminRunner(runnerId: String): IO[GiteaError, contract.ActionRunner]``

- `runner_id` (path, required, string): id of the runner
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteAdminRunner`

**DELETE `/admin/actions/runners/{runner_id}`** — ``client.actions.deleteAdminRunner(runnerId: String): IO[GiteaError, Unit]``

- `runner_id` (path, required, string): id of the runner
- HTTP 204: `description: runner has been deleted`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateAdminRunner`

**PATCH `/admin/actions/runners/{runner_id}`** — ``client.actions.updateAdminRunner(runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner]``

- `runner_id` (path, required, string): id of the runner
- `body` (body, optional, `contract.EditActionRunnerOption`)
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `listAdminWorkflowRuns`

**GET `/admin/actions/runs`** — ``client.actions.listAdminWorkflowRuns(event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse]``

- `event` (query, optional, string): workflow event name
- `branch` (query, optional, string): workflow branch
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `actor` (query, optional, string): triggered by user
- `head_sha` (query, optional, string): triggering sha of the workflow run
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/WorkflowRunsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getOrgWorkflowJobs`

**GET `/orgs/{org}/actions/jobs`** — ``client.actions.getOrgWorkflowJobs(org: String, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse]``

- `org` (path, required, string): name of the organization
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/WorkflowJobsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getOrgRunners`

**GET `/orgs/{org}/actions/runners`** — ``client.actions.getOrgRunners(org: String, disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse]``

- `org` (path, required, string): name of the organization
- `disabled` (query, optional, boolean): filter by disabled status (true or false)
- HTTP 200: `#/responses/RunnerList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `orgCreateRunnerRegistrationToken`

**POST `/orgs/{org}/actions/runners/registration-token`** — ``client.actions.orgCreateRunnerRegistrationToken(org: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- HTTP 200: `#/responses/RegistrationToken`

### `getOrgRunner`

**GET `/orgs/{org}/actions/runners/{runner_id}`** — ``client.actions.getOrgRunner(org: String, runnerId: String): IO[GiteaError, contract.ActionRunner]``

- `org` (path, required, string): name of the organization
- `runner_id` (path, required, string): id of the runner
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteOrgRunner`

**DELETE `/orgs/{org}/actions/runners/{runner_id}`** — ``client.actions.deleteOrgRunner(org: String, runnerId: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `runner_id` (path, required, string): id of the runner
- HTTP 204: `description: runner has been deleted`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateOrgRunner`

**PATCH `/orgs/{org}/actions/runners/{runner_id}`** — ``client.actions.updateOrgRunner(org: String, runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner]``

- `org` (path, required, string): name of the organization
- `runner_id` (path, required, string): id of the runner
- `body` (body, optional, `contract.EditActionRunnerOption`)
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `getOrgWorkflowRuns`

**GET `/orgs/{org}/actions/runs`** — ``client.actions.getOrgWorkflowRuns(org: String, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse]``

- `org` (path, required, string): name of the organization
- `event` (query, optional, string): workflow event name
- `branch` (query, optional, string): workflow branch
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `actor` (query, optional, string): triggered by user
- `head_sha` (query, optional, string): triggering sha of the workflow run
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/WorkflowRunsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `orgListActionsSecrets`

**GET `/orgs/{org}/actions/secrets`** — ``client.actions.orgListActionsSecrets(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Secret]]``

- `org` (path, required, string): name of the organization
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/SecretList`
- HTTP 404: `#/responses/notFound`

### `updateOrgSecret`

**PUT `/orgs/{org}/actions/secrets/{secretname}`** — ``client.actions.updateOrgSecret(org: String, secretname: String, body: contract.CreateOrUpdateSecretOption): IO[GiteaError, Unit]``

- `org` (path, required, string): name of organization
- `secretname` (path, required, string): name of the secret
- `body` (body, optional, `contract.CreateOrUpdateSecretOption`)
- HTTP 201: `description: response when creating a secret`
- HTTP 204: `description: response when updating a secret`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteOrgSecret`

**DELETE `/orgs/{org}/actions/secrets/{secretname}`** — ``client.actions.deleteOrgSecret(org: String, secretname: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of organization
- `secretname` (path, required, string): name of the secret
- HTTP 204: `description: delete one secret of the organization`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getOrgVariablesList`

**GET `/orgs/{org}/actions/variables`** — ``client.actions.getOrgVariablesList(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.ActionVariable]]``

- `org` (path, required, string): name of the organization
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/VariableList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getOrgVariable`

**GET `/orgs/{org}/actions/variables/{variablename}`** — ``client.actions.getOrgVariable(org: String, variablename: String): IO[GiteaError, contract.ActionVariable]``

- `org` (path, required, string): name of the organization
- `variablename` (path, required, string): name of the variable
- HTTP 200: `#/responses/ActionVariable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateOrgVariable`

**PUT `/orgs/{org}/actions/variables/{variablename}`** — ``client.actions.updateOrgVariable(org: String, variablename: String, body: contract.UpdateVariableOption): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `variablename` (path, required, string): name of the variable
- `body` (body, optional, `contract.UpdateVariableOption`)
- HTTP 201: `description: response when updating an org-level variable`
- HTTP 204: `description: response when updating an org-level variable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `createOrgVariable`

**POST `/orgs/{org}/actions/variables/{variablename}`** — ``client.actions.createOrgVariable(org: String, variablename: String, body: contract.CreateVariableOption): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `variablename` (path, required, string): name of the variable
- `body` (body, optional, `contract.CreateVariableOption`)
- HTTP 201: `description: successfully created the org-level variable`
- HTTP 400: `#/responses/error`
- HTTP 409: `description: variable name already exists.`
- HTTP 500: `#/responses/error`

### `deleteOrgVariable`

**DELETE `/orgs/{org}/actions/variables/{variablename}`** — ``client.actions.deleteOrgVariable(org: String, variablename: String): IO[GiteaError, Option[contract.ActionVariable]]``

- `org` (path, required, string): name of the organization
- `variablename` (path, required, string): name of the variable
- HTTP 200: `#/responses/ActionVariable`
- HTTP 201: `description: response when deleting a variable`
- HTTP 204: `description: response when deleting a variable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getArtifacts`

**GET `/repos/{owner}/{repo}/actions/artifacts`** — ``client.actions.getArtifacts(owner: String, repo: String, name: Option[String] = None): IO[GiteaError, contract.ActionArtifactsResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `name` (query, optional, string): name of the artifact
- HTTP 200: `#/responses/ArtifactsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getArtifact`

**GET `/repos/{owner}/{repo}/actions/artifacts/{artifact_id}`** — ``client.actions.getArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, contract.ActionArtifact]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `artifact_id` (path, required, string): id of the artifact
- HTTP 200: `#/responses/Artifact`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteArtifact`

**DELETE `/repos/{owner}/{repo}/actions/artifacts/{artifact_id}`** — ``client.actions.deleteArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `artifact_id` (path, required, string): id of the artifact
- HTTP 204: `description: No Content`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `listWorkflowJobs`

**GET `/repos/{owner}/{repo}/actions/jobs`** — ``client.actions.listWorkflowJobs(owner: String, repo: String, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `sort` (query, optional, string): sort jobs by attribute. Supported values are "id". Default is "id"
- `order` (query, optional, string): sort order, either "asc" (ascending) or "desc" (descending). Default is "asc"
- HTTP 200: `#/responses/WorkflowJobsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `getWorkflowJob`

**GET `/repos/{owner}/{repo}/actions/jobs/{job_id}`** — ``client.actions.getWorkflowJob(owner: String, repo: String, jobId: String): IO[GiteaError, contract.ActionWorkflowJob]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `job_id` (path, required, string): id of the job
- HTTP 200: `#/responses/WorkflowJob`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `downloadActionsRunJobLogs`

**GET `/repos/{owner}/{repo}/actions/jobs/{job_id}/logs`** — ``client.actions.downloadActionsRunJobLogs(owner: String, repo: String, jobId: Int): IO[GiteaError, zio.Chunk[Byte]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `job_id` (path, required, integer): id of the job
- HTTP 200: `description: output blob content`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getRepoRunners`

**GET `/repos/{owner}/{repo}/actions/runners`** — ``client.actions.getRepoRunners(owner: String, repo: String, disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `disabled` (query, optional, boolean): filter by disabled status (true or false)
- HTTP 200: `#/responses/RunnerList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `repoCreateRunnerRegistrationToken`

**POST `/repos/{owner}/{repo}/actions/runners/registration-token`** — ``client.actions.repoCreateRunnerRegistrationToken(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/RegistrationToken`

### `getRepoRunner`

**GET `/repos/{owner}/{repo}/actions/runners/{runner_id}`** — ``client.actions.getRepoRunner(owner: String, repo: String, runnerId: String): IO[GiteaError, contract.ActionRunner]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `runner_id` (path, required, string): id of the runner
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteRepoRunner`

**DELETE `/repos/{owner}/{repo}/actions/runners/{runner_id}`** — ``client.actions.deleteRepoRunner(owner: String, repo: String, runnerId: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `runner_id` (path, required, string): id of the runner
- HTTP 204: `description: runner has been deleted`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateRepoRunner`

**PATCH `/repos/{owner}/{repo}/actions/runners/{runner_id}`** — ``client.actions.updateRepoRunner(owner: String, repo: String, runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `runner_id` (path, required, string): id of the runner
- `body` (body, optional, `contract.EditActionRunnerOption`)
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `getWorkflowRuns`

**GET `/repos/{owner}/{repo}/actions/runs`** — ``client.actions.getWorkflowRuns(owner: String, repo: String, event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, excludePullRequests: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `event` (query, optional, string): workflow event name
- `branch` (query, optional, string): workflow branch
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `actor` (query, optional, string): triggered by user
- `head_sha` (query, optional, string): triggering sha of the workflow run
- `exclude_pull_requests` (query, optional, boolean): if true, the `pull_requests` field on each returned run is emptied
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/WorkflowRunsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `GetWorkflowRun`

**GET `/repos/{owner}/{repo}/actions/runs/{run}`** — ``client.actions.GetWorkflowRun(owner: String, repo: String, run: Int): IO[GiteaError, contract.ActionWorkflowRun]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): id of the run
- HTTP 200: `#/responses/WorkflowRun`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteActionRun`

**DELETE `/repos/{owner}/{repo}/actions/runs/{run}`** — ``client.actions.deleteActionRun(owner: String, repo: String, run: Int): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): runid of the workflow run
- HTTP 204: `description: No Content`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getArtifactsOfRun`

**GET `/repos/{owner}/{repo}/actions/runs/{run}/artifacts`** — ``client.actions.getArtifactsOfRun(owner: String, repo: String, run: Int, name: Option[String] = None): IO[GiteaError, contract.ActionArtifactsResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): runid of the workflow run
- `name` (query, optional, string): name of the artifact
- HTTP 200: `#/responses/ArtifactsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `listWorkflowRunJobs`

**GET `/repos/{owner}/{repo}/actions/runs/{run}/jobs`** — ``client.actions.listWorkflowRunJobs(owner: String, repo: String, run: Int, status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): runid of the workflow run
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `sort` (query, optional, string): sort jobs by attribute. Supported values are "id". Default is "id"
- `order` (query, optional, string): sort order, either "asc" (ascending) or "desc" (descending). Default is "asc"
- HTTP 200: `#/responses/WorkflowJobsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `rerunWorkflowJob`

**POST `/repos/{owner}/{repo}/actions/runs/{run}/jobs/{job_id}/rerun`** — ``client.actions.rerunWorkflowJob(owner: String, repo: String, run: Int, jobId: Int): IO[GiteaError, contract.ActionWorkflowJob]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): id of the run
- `job_id` (path, required, integer): id of the job
- HTTP 201: `#/responses/WorkflowJob`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `rerunWorkflowRun`

**POST `/repos/{owner}/{repo}/actions/runs/{run}/rerun`** — ``client.actions.rerunWorkflowRun(owner: String, repo: String, run: Int): IO[GiteaError, contract.ActionWorkflowRun]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): id of the run
- HTTP 201: `#/responses/WorkflowRun`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `rerunFailedWorkflowRun`

**POST `/repos/{owner}/{repo}/actions/runs/{run}/rerun-failed-jobs`** — ``client.actions.rerunFailedWorkflowRun(owner: String, repo: String, run: Int): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `run` (path, required, integer): id of the run
- HTTP 201: `#/responses/empty`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `repoListActionsSecrets`

**GET `/repos/{owner}/{repo}/actions/secrets`** — ``client.actions.repoListActionsSecrets(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Secret]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/SecretList`
- HTTP 404: `#/responses/notFound`

### `updateRepoSecret`

**PUT `/repos/{owner}/{repo}/actions/secrets/{secretname}`** — ``client.actions.updateRepoSecret(owner: String, repo: String, secretname: String, body: contract.CreateOrUpdateSecretOption): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repository
- `repo` (path, required, string): name of the repository
- `secretname` (path, required, string): name of the secret
- `body` (body, optional, `contract.CreateOrUpdateSecretOption`)
- HTTP 201: `description: response when creating a secret`
- HTTP 204: `description: response when updating a secret`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteRepoSecret`

**DELETE `/repos/{owner}/{repo}/actions/secrets/{secretname}`** — ``client.actions.deleteRepoSecret(owner: String, repo: String, secretname: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repository
- `repo` (path, required, string): name of the repository
- `secretname` (path, required, string): name of the secret
- HTTP 204: `description: delete one secret of the repository`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `ListActionTasks`

**GET `/repos/{owner}/{repo}/actions/tasks`** — ``client.actions.ListActionTasks(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionTaskResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results, default maximum page size is 50
- HTTP 200: `#/responses/TasksList`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/conflict`
- HTTP 422: `#/responses/validationError`

### `getRepoVariablesList`

**GET `/repos/{owner}/{repo}/actions/variables`** — ``client.actions.getRepoVariablesList(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.ActionVariable]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/VariableList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getRepoVariable`

**GET `/repos/{owner}/{repo}/actions/variables/{variablename}`** — ``client.actions.getRepoVariable(owner: String, repo: String, variablename: String): IO[GiteaError, contract.ActionVariable]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `variablename` (path, required, string): name of the variable
- HTTP 200: `#/responses/ActionVariable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateRepoVariable`

**PUT `/repos/{owner}/{repo}/actions/variables/{variablename}`** — ``client.actions.updateRepoVariable(owner: String, repo: String, variablename: String, body: contract.UpdateVariableOption): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `variablename` (path, required, string): name of the variable
- `body` (body, optional, `contract.UpdateVariableOption`)
- HTTP 201: `description: response when updating a repo-level variable`
- HTTP 204: `description: response when updating a repo-level variable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `createRepoVariable`

**POST `/repos/{owner}/{repo}/actions/variables/{variablename}`** — ``client.actions.createRepoVariable(owner: String, repo: String, variablename: String, body: contract.CreateVariableOption): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `variablename` (path, required, string): name of the variable
- `body` (body, optional, `contract.CreateVariableOption`)
- HTTP 201: `description: response when creating a repo-level variable`
- HTTP 400: `#/responses/error`
- HTTP 409: `description: variable name already exists.`
- HTTP 500: `#/responses/error`

### `deleteRepoVariable`

**DELETE `/repos/{owner}/{repo}/actions/variables/{variablename}`** — ``client.actions.deleteRepoVariable(owner: String, repo: String, variablename: String): IO[GiteaError, Option[contract.ActionVariable]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `variablename` (path, required, string): name of the variable
- HTTP 200: `#/responses/ActionVariable`
- HTTP 201: `description: response when deleting a variable`
- HTTP 204: `description: response when deleting a variable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `ActionsListRepositoryWorkflows`

**GET `/repos/{owner}/{repo}/actions/workflows`** — ``client.actions.ActionsListRepositoryWorkflows(owner: String, repo: String): IO[GiteaError, contract.ActionWorkflowResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/ActionWorkflowList`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`
- HTTP 500: `#/responses/error`

### `ActionsGetWorkflow`

**GET `/repos/{owner}/{repo}/actions/workflows/{workflow_id}`** — ``client.actions.ActionsGetWorkflow(owner: String, repo: String, workflowId: String): IO[GiteaError, contract.ActionWorkflow]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `workflow_id` (path, required, string): id of the workflow
- HTTP 200: `#/responses/ActionWorkflow`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`
- HTTP 500: `#/responses/error`

### `ActionsDisableWorkflow`

**PUT `/repos/{owner}/{repo}/actions/workflows/{workflow_id}/disable`** — ``client.actions.ActionsDisableWorkflow(owner: String, repo: String, workflowId: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `workflow_id` (path, required, string): id of the workflow
- HTTP 204: `description: No Content`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `ActionsDispatchWorkflow`

**POST `/repos/{owner}/{repo}/actions/workflows/{workflow_id}/dispatches`** — ``client.actions.ActionsDispatchWorkflow(owner: String, repo: String, workflowId: String, body: contract.CreateActionWorkflowDispatch, returnRunDetails: Option[Boolean] = None, scopedWorkflowSourceRepoId: Option[Long] = None): IO[GiteaError, Option[contract.RunDetails]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `workflow_id` (path, required, string): id of the workflow
- `body` (body, optional, `contract.CreateActionWorkflowDispatch`)
- `return_run_details` (query, optional, boolean): Whether the response should include the workflow run ID and URLs.
- `scoped_workflow_source_repo_id` (query, optional, integer): For a scoped workflow, the ID of the source repository providing it; omit or 0 for a repo-level workflow.
- HTTP 200: `#/responses/RunDetails`
- HTTP 204: `description: No Content, if return_run_details is missing or false`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `ActionsEnableWorkflow`

**PUT `/repos/{owner}/{repo}/actions/workflows/{workflow_id}/enable`** — ``client.actions.ActionsEnableWorkflow(owner: String, repo: String, workflowId: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `workflow_id` (path, required, string): id of the workflow
- HTTP 204: `description: No Content`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/conflict`
- HTTP 422: `#/responses/validationError`

### `getUserWorkflowJobs`

**GET `/user/actions/jobs`** — ``client.actions.getUserWorkflowJobs(status: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None): IO[GiteaError, contract.ActionWorkflowJobsResponse]``

- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `sort` (query, optional, string): sort jobs by attribute. Supported values are "id". Default is "id"
- `order` (query, optional, string): sort order, either "asc" (ascending) or "desc" (descending). Default is "asc"
- HTTP 200: `#/responses/WorkflowJobsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `getUserRunners`

**GET `/user/actions/runners`** — ``client.actions.getUserRunners(disabled: Option[Boolean] = None): IO[GiteaError, contract.ActionRunnersResponse]``

- `disabled` (query, optional, boolean): filter by disabled status (true or false)
- HTTP 200: `#/responses/RunnerList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `userCreateRunnerRegistrationToken`

**POST `/user/actions/runners/registration-token`** — ``client.actions.userCreateRunnerRegistrationToken(): IO[GiteaError, Unit]``

- HTTP 200: `#/responses/RegistrationToken`

### `getUserRunner`

**GET `/user/actions/runners/{runner_id}`** — ``client.actions.getUserRunner(runnerId: String): IO[GiteaError, contract.ActionRunner]``

- `runner_id` (path, required, string): id of the runner
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteUserRunner`

**DELETE `/user/actions/runners/{runner_id}`** — ``client.actions.deleteUserRunner(runnerId: String): IO[GiteaError, Unit]``

- `runner_id` (path, required, string): id of the runner
- HTTP 204: `description: runner has been deleted`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateUserRunner`

**PATCH `/user/actions/runners/{runner_id}`** — ``client.actions.updateUserRunner(runnerId: String, body: contract.EditActionRunnerOption): IO[GiteaError, contract.ActionRunner]``

- `runner_id` (path, required, string): id of the runner
- `body` (body, optional, `contract.EditActionRunnerOption`)
- HTTP 200: `#/responses/Runner`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `getUserWorkflowRuns`

**GET `/user/actions/runs`** — ``client.actions.getUserWorkflowRuns(event: Option[String] = None, branch: Option[String] = None, status: Option[String] = None, actor: Option[String] = None, headSha: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.ActionWorkflowRunsResponse]``

- `event` (query, optional, string): workflow event name
- `branch` (query, optional, string): workflow branch
- `status` (query, optional, string): workflow status (pending, queued, in_progress, failure, success, skipped)
- `actor` (query, optional, string): triggered by user
- `head_sha` (query, optional, string): triggering sha of the workflow run
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/WorkflowRunsList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateUserSecret`

**PUT `/user/actions/secrets/{secretname}`** — ``client.actions.updateUserSecret(secretname: String, body: contract.CreateOrUpdateSecretOption): IO[GiteaError, Unit]``

- `secretname` (path, required, string): name of the secret
- `body` (body, optional, `contract.CreateOrUpdateSecretOption`)
- HTTP 201: `description: response when creating a secret`
- HTTP 204: `description: response when updating a secret`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `deleteUserSecret`

**DELETE `/user/actions/secrets/{secretname}`** — ``client.actions.deleteUserSecret(secretname: String): IO[GiteaError, Unit]``

- `secretname` (path, required, string): name of the secret
- HTTP 204: `description: delete one secret of the user`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getUserVariablesList`

**GET `/user/actions/variables`** — ``client.actions.getUserVariablesList(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.ActionVariable]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/VariableList`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `getUserVariable`

**GET `/user/actions/variables/{variablename}`** — ``client.actions.getUserVariable(variablename: String): IO[GiteaError, contract.ActionVariable]``

- `variablename` (path, required, string): name of the variable
- HTTP 200: `#/responses/ActionVariable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `updateUserVariable`

**PUT `/user/actions/variables/{variablename}`** — ``client.actions.updateUserVariable(variablename: String, body: contract.UpdateVariableOption): IO[GiteaError, Unit]``

- `variablename` (path, required, string): name of the variable
- `body` (body, optional, `contract.UpdateVariableOption`)
- HTTP 201: `description: response when updating a variable`
- HTTP 204: `description: response when updating a variable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `createUserVariable`

**POST `/user/actions/variables/{variablename}`** — ``client.actions.createUserVariable(variablename: String, body: contract.CreateVariableOption): IO[GiteaError, Unit]``

- `variablename` (path, required, string): name of the variable
- `body` (body, optional, `contract.CreateVariableOption`)
- HTTP 201: `description: successfully created the user-level variable`
- HTTP 400: `#/responses/error`
- HTTP 409: `description: variable name already exists.`

### `deleteUserVariable`

**DELETE `/user/actions/variables/{variablename}`** — ``client.actions.deleteUserVariable(variablename: String): IO[GiteaError, Unit]``

- `variablename` (path, required, string): name of the variable
- HTTP 201: `description: response when deleting a variable`
- HTTP 204: `description: response when deleting a variable`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `downloadArtifact`

**GET `/repos/{owner}/{repo}/actions/artifacts/{artifact_id}/zip`** — ``client.actions.downloadArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, Chunk[Byte]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repository
- `artifact_id` (path, required, string): id of the artifact
- HTTP 302: `description: redirect to the blob download`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

## Admin

### `adminCronList`

**GET `/admin/cron`** — ``client.admin.adminCronList(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Cron]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/CronList`
- HTTP 403: `#/responses/forbidden`

### `adminCronRun`

**POST `/admin/cron/{task}`** — ``client.admin.adminCronRun(task: String): IO[GiteaError, Unit]``

- `task` (path, required, string): task to run
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `adminGetAllEmails`

**GET `/admin/emails`** — ``client.admin.adminGetAllEmails(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Email]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/EmailList`
- HTTP 403: `#/responses/forbidden`

### `adminSearchEmails`

**GET `/admin/emails/search`** — ``client.admin.adminSearchEmails(q: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Email]]``

- `q` (query, optional, string): keyword
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/EmailList`
- HTTP 403: `#/responses/forbidden`

### `adminListHooks`

**GET `/admin/hooks`** — ``client.admin.adminListHooks(page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.AdminHookType] = None): IO[GiteaError, zio.Chunk[contract.Hook]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `type` (query, optional, string): system, default or both kinds of webhooks
- HTTP 200: `#/responses/HookList`

### `adminCreateHook`

**POST `/admin/hooks`** — ``client.admin.adminCreateHook(body: contract.CreateHookOption): IO[GiteaError, contract.Hook]``

- `body` (body, required, `contract.CreateHookOption`)
- HTTP 201: `#/responses/Hook`

### `adminGetHook`

**GET `/admin/hooks/{id}`** — ``client.admin.adminGetHook(id: Long): IO[GiteaError, contract.Hook]``

- `id` (path, required, integer): id of the hook to get
- HTTP 200: `#/responses/Hook`

### `adminDeleteHook`

**DELETE `/admin/hooks/{id}`** — ``client.admin.adminDeleteHook(id: Long): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the hook to delete
- HTTP 204: `#/responses/empty`

### `adminEditHook`

**PATCH `/admin/hooks/{id}`** — ``client.admin.adminEditHook(id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook]``

- `id` (path, required, integer): id of the hook to update
- `body` (body, optional, `contract.EditHookOption`)
- HTTP 200: `#/responses/Hook`

### `adminGetAllOrgs`

**GET `/admin/orgs`** — ``client.admin.adminGetAllOrgs(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/OrganizationList`
- HTTP 403: `#/responses/forbidden`

### `adminUnadoptedList`

**GET `/admin/unadopted`** — ``client.admin.adminUnadoptedList(page: Option[Int] = None, limit: Option[Int] = None, pattern: Option[String] = None): IO[GiteaError, zio.Chunk[String]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `pattern` (query, optional, string): pattern of repositories to search for
- HTTP 200: `#/responses/StringSlice`
- HTTP 403: `#/responses/forbidden`

### `adminAdoptRepository`

**POST `/admin/unadopted/{owner}/{repo}`** — ``client.admin.adminAdoptRepository(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `adminDeleteUnadoptedRepository`

**DELETE `/admin/unadopted/{owner}/{repo}`** — ``client.admin.adminDeleteUnadoptedRepository(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`

### `adminSearchUsers`

**GET `/admin/users`** — ``client.admin.adminSearchUsers(sourceId: Option[Long] = None, loginName: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None, sort: Option[String] = None, order: Option[String] = None, q: Option[String] = None, visibility: Option[String] = None, isActive: Option[Boolean] = None, isAdmin: Option[Boolean] = None, isRestricted: Option[Boolean] = None, is2faEnabled: Option[Boolean] = None, isProhibitLogin: Option[Boolean] = None): IO[GiteaError, zio.Chunk[contract.User]]``

- `source_id` (query, optional, integer): ID of the user's login source to search for
- `login_name` (query, optional, string): identifier of the user, provided by the external authenticator
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `sort` (query, optional, string): sort users by attribute. Supported values are "name", "created", "updated" and "id". Default is "name"
- `order` (query, optional, string): sort order, either "asc" (ascending) or "desc" (descending). Default is "asc", ignored if "sort" is not specified.
- `q` (query, optional, string): search term (username, full name, email)
- `visibility` (query, optional, string): visibility filter. Supported values are "public", "limited" and "private".
- `is_active` (query, optional, boolean): filter active users
- `is_admin` (query, optional, boolean): filter admin users
- `is_restricted` (query, optional, boolean): filter restricted users
- `is_2fa_enabled` (query, optional, boolean): filter 2FA enabled users
- `is_prohibit_login` (query, optional, boolean): filter login prohibited users
- HTTP 200: `#/responses/UserList`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminCreateUser`

**POST `/admin/users`** — ``client.admin.adminCreateUser(body: contract.CreateUserOption): IO[GiteaError, contract.User]``

- `body` (body, optional, `contract.CreateUserOption`)
- HTTP 201: `#/responses/User`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminDeleteUser`

**DELETE `/admin/users/{username}`** — ``client.admin.adminDeleteUser(username: String, purge: Option[Boolean] = None): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to delete
- `purge` (query, optional, boolean): purge the user from the system completely
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `adminEditUser`

**PATCH `/admin/users/{username}`** — ``client.admin.adminEditUser(username: String, body: contract.EditUserOption): IO[GiteaError, contract.User]``

- `username` (path, required, string): username of the user whose data is to be edited
- `body` (body, optional, `contract.EditUserOption`)
- HTTP 200: `#/responses/User`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminListUserBadges`

**GET `/admin/users/{username}/badges`** — ``client.admin.adminListUserBadges(username: String): IO[GiteaError, zio.Chunk[contract.Badge]]``

- `username` (path, required, string): username of the user whose badges are to be listed
- HTTP 200: `#/responses/BadgeList`
- HTTP 404: `#/responses/notFound`

### `adminAddUserBadges`

**POST `/admin/users/{username}/badges`** — ``client.admin.adminAddUserBadges(username: String, body: contract.UserBadgeOption): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to whom a badge is to be added
- `body` (body, optional, `contract.UserBadgeOption`)
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`

### `adminDeleteUserBadges`

**DELETE `/admin/users/{username}/badges`** — ``client.admin.adminDeleteUserBadges(username: String, body: contract.UserBadgeOption): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user whose badge is to be deleted
- `body` (body, optional, `contract.UserBadgeOption`)
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminCreatePublicKey`

**POST `/admin/users/{username}/keys`** — ``client.admin.adminCreatePublicKey(username: String, body: contract.CreateKeyOption): IO[GiteaError, contract.PublicKey]``

- `username` (path, required, string): username of the user who is to receive a public key
- `key` (body, optional, `contract.CreateKeyOption`)
- HTTP 201: `#/responses/PublicKey`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminDeleteUserPublicKey`

**DELETE `/admin/users/{username}/keys/{id}`** — ``client.admin.adminDeleteUserPublicKey(username: String, id: Long): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user whose public key is to be deleted
- `id` (path, required, integer): id of the key to delete
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `adminCreateOrg`

**POST `/admin/users/{username}/orgs`** — ``client.admin.adminCreateOrg(username: String, body: contract.CreateOrgOption): IO[GiteaError, contract.Organization]``

- `username` (path, required, string): username of the user who will own the created organization
- `organization` (body, required, `contract.CreateOrgOption`)
- HTTP 201: `#/responses/Organization`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminRenameUser`

**POST `/admin/users/{username}/rename`** — ``client.admin.adminRenameUser(username: String, body: contract.RenameUserOption): IO[GiteaError, Unit]``

- `username` (path, required, string): current username of the user
- `body` (body, required, `contract.RenameUserOption`)
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `adminCreateRepo`

**POST `/admin/users/{username}/repos`** — ``client.admin.adminCreateRepo(username: String, body: contract.CreateRepoOption): IO[GiteaError, contract.Repository]``

- `username` (path, required, string): username of the user who will own the created repository
- `repository` (body, required, `contract.CreateRepoOption`)
- HTTP 201: `#/responses/Repository`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/error`
- HTTP 422: `#/responses/validationError`

## Catalog

### `listGitignoresTemplates`

**GET `/gitignore/templates`** — ``client.catalog.listGitignoresTemplates(): IO[GiteaError, zio.Chunk[String]]``

- HTTP 200: `#/responses/GitignoreTemplateList`

### `getGitignoreTemplateInfo`

**GET `/gitignore/templates/{name}`** — ``client.catalog.getGitignoreTemplateInfo(name: String): IO[GiteaError, contract.GitignoreTemplateInfo]``

- `name` (path, required, string): name of the template
- HTTP 200: `#/responses/GitignoreTemplateInfo`
- HTTP 404: `#/responses/notFound`

### `listLabelTemplates`

**GET `/label/templates`** — ``client.catalog.listLabelTemplates(): IO[GiteaError, zio.Chunk[String]]``

- HTTP 200: `#/responses/LabelTemplateList`

### `getLabelTemplateInfo`

**GET `/label/templates/{name}`** — ``client.catalog.getLabelTemplateInfo(name: String): IO[GiteaError, zio.Chunk[contract.LabelTemplate]]``

- `name` (path, required, string): name of the template
- HTTP 200: `#/responses/LabelTemplateInfo`
- HTTP 404: `#/responses/notFound`

### `listLicenseTemplates`

**GET `/licenses`** — ``client.catalog.listLicenseTemplates(): IO[GiteaError, zio.Chunk[contract.LicensesTemplateListEntry]]``

- HTTP 200: `#/responses/LicenseTemplateList`

### `getLicenseTemplateInfo`

**GET `/licenses/{name}`** — ``client.catalog.getLicenseTemplateInfo(name: String): IO[GiteaError, contract.LicenseTemplateInfo]``

- `name` (path, required, string): name of the license
- HTTP 200: `#/responses/LicenseTemplateInfo`
- HTTP 404: `#/responses/notFound`

### `renderMarkdown`

**POST `/markdown`** — ``client.catalog.renderMarkdown(body: contract.MarkdownOption): IO[GiteaError, String]``

- `body` (body, optional, `contract.MarkdownOption`)
- HTTP 200: `#/responses/MarkdownRender`
- HTTP 422: `#/responses/validationError`

### `renderMarkdownRaw`

**POST `/markdown/raw`** — ``client.catalog.renderMarkdownRaw(body: String): IO[GiteaError, String]``

- `body` (body, required, string): Request body to render
- HTTP 200: `#/responses/MarkdownRender`
- HTTP 422: `#/responses/validationError`

### `renderMarkup`

**POST `/markup`** — ``client.catalog.renderMarkup(body: contract.MarkupOption): IO[GiteaError, String]``

- `body` (body, optional, `contract.MarkupOption`)
- HTTP 200: `#/responses/MarkupRender`
- HTTP 422: `#/responses/validationError`

### `repoGetByID`

**GET `/repositories/{id}`** — ``client.catalog.repoGetByID(id: Long): IO[GiteaError, contract.Repository]``

- `id` (path, required, integer): id of the repo to get
- HTTP 200: `#/responses/Repository`
- HTTP 404: `#/responses/notFound`

### `getSigningKey`

**GET `/signing-key.gpg`** — ``client.catalog.getSigningKey(): IO[GiteaError, String]``

- HTTP 200: `type:string`

### `getSigningKeySSH`

**GET `/signing-key.pub`** — ``client.catalog.getSigningKeySSH(): IO[GiteaError, String]``

- HTTP 200: `type:string`

### `topicSearch`

**GET `/topics/search`** — ``client.catalog.topicSearch(q: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.TopicListResponse]``

- `q` (query, required, string): keywords to search
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/TopicListResponse`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `getVersion`

**GET `/version`** — ``client.catalog.getVersion(): IO[GiteaError, contract.ServerVersion]``

- HTTP 200: `#/responses/ServerVersion`

## Notifications

### `notifyReadList`

**PUT `/notifications`** — ``client.notifications.notifyReadList(lastReadAt: Option[java.time.Instant] = None, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]]``

- `last_read_at` (query, optional, timestamp): Describes the last point that notifications were checked. Anything updated since this time will not be updated.
- `all` (query, optional, string): If true, mark all notifications on this repo. Default value is false
- `status-types` (query, optional, list of string): Mark notifications with the provided status types. Options are: unread, read and/or pinned. Defaults to unread.
- `to-status` (query, optional, string): Status to mark notifications as, Defaults to read.
- HTTP 205: `#/responses/NotificationThreadList`

### `notifyReadThread`

**PATCH `/notifications/threads/{id}`** — ``client.notifications.notifyReadThread(id: String, toStatus: Option[String] = None): IO[GiteaError, contract.NotificationThread]``

- `id` (path, required, string): id of notification thread
- `to-status` (query, optional, string): Status to mark notifications as
- HTTP 205: `#/responses/NotificationThread`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `notifyGetRepoList`

**GET `/repos/{owner}/{repo}/notifications`** — ``client.notifications.notifyGetRepoList(owner: String, repo: String, all: Option[Boolean] = None, statusTypes: Option[List[String]] = None, subjectType: Option[List[contract.NotificationSubjectType]] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `all` (query, optional, boolean): If true, show notifications marked as read. Default value is false
- `status-types` (query, optional, list of string): Show notifications with the provided status types. Options are: unread, read and/or pinned. Defaults to unread & pinned
- `subject-type` (query, optional, list of string): filter notifications by subject type
- `since` (query, optional, timestamp): Only show notifications updated after the given time. This is a timestamp in RFC 3339 format
- `before` (query, optional, timestamp): Only show notifications updated before the given time. This is a timestamp in RFC 3339 format
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/NotificationThreadList`

### `notifyReadRepoList`

**PUT `/repos/{owner}/{repo}/notifications`** — ``client.notifications.notifyReadRepoList(owner: String, repo: String, all: Option[String] = None, statusTypes: Option[List[String]] = None, toStatus: Option[String] = None, lastReadAt: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.NotificationThread]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `all` (query, optional, string): If true, mark all notifications on this repo. Default value is false
- `status-types` (query, optional, list of string): Mark notifications with the provided status types. Options are: unread, read and/or pinned. Defaults to unread.
- `to-status` (query, optional, string): Status to mark notifications as. Defaults to read.
- `last_read_at` (query, optional, timestamp): Describes the last point that notifications were checked. Anything updated since this time will not be updated.
- HTTP 205: `#/responses/NotificationThreadList`

## Orgs

### `orgGetAll`

**GET `/orgs`** — ``client.orgs.orgGetAll(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/OrganizationList`

### `orgCreate`

**POST `/orgs`** — ``client.orgs.orgCreate(body: contract.CreateOrgOption): IO[GiteaError, contract.Organization]``

- `organization` (body, required, `contract.CreateOrgOption`)
- HTTP 201: `#/responses/Organization`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `orgDelete`

**DELETE `/orgs/{org}`** — ``client.orgs.orgDelete(org: String): IO[GiteaError, Unit]``

- `org` (path, required, string): organization that is to be deleted
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `orgEdit`

**PATCH `/orgs/{org}`** — ``client.orgs.orgEdit(org: String, body: contract.EditOrgOption): IO[GiteaError, contract.Organization]``

- `org` (path, required, string): name of the organization to edit
- `body` (body, required, `contract.EditOrgOption`)
- HTTP 200: `#/responses/Organization`
- HTTP 404: `#/responses/notFound`

### `orgListActivityFeeds`

**GET `/orgs/{org}/activities/feeds`** — ``client.orgs.orgListActivityFeeds(org: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]]``

- `org` (path, required, string): name of the org
- `date` (query, optional, string): the date of the activities to be found
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/ActivityFeedsList`
- HTTP 404: `#/responses/notFound`

### `orgUpdateAvatar`

**POST `/orgs/{org}/avatar`** — ``client.orgs.orgUpdateAvatar(org: String, body: contract.UpdateUserAvatarOption): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `body` (body, optional, `contract.UpdateUserAvatarOption`)
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `orgDeleteAvatar`

**DELETE `/orgs/{org}/avatar`** — ``client.orgs.orgDeleteAvatar(org: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `organizationListBlocks`

**GET `/orgs/{org}/blocks`** — ``client.orgs.organizationListBlocks(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]``

- `org` (path, required, string): name of the organization
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/UserList`

### `organizationCheckUserBlock`

**GET `/orgs/{org}/blocks/{username}`** — ``client.orgs.organizationCheckUserBlock(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user to check
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `organizationBlockUser`

**PUT `/orgs/{org}/blocks/{username}`** — ``client.orgs.organizationBlockUser(org: String, username: String, note: Option[String] = None): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user to block
- `note` (query, optional, string): optional note for the block
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `organizationUnblockUser`

**DELETE `/orgs/{org}/blocks/{username}`** — ``client.orgs.organizationUnblockUser(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user to unblock
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `orgListHooks`

**GET `/orgs/{org}/hooks`** — ``client.orgs.orgListHooks(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]]``

- `org` (path, required, string): name of the organization
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/HookList`
- HTTP 404: `#/responses/notFound`

### `orgCreateHook`

**POST `/orgs/{org}/hooks`** — ``client.orgs.orgCreateHook(org: String, body: contract.CreateHookOption): IO[GiteaError, contract.Hook]``

- `org` (path, required, string): name of the organization
- `body` (body, required, `contract.CreateHookOption`)
- HTTP 201: `#/responses/Hook`
- HTTP 404: `#/responses/notFound`

### `orgGetHook`

**GET `/orgs/{org}/hooks/{id}`** — ``client.orgs.orgGetHook(org: String, id: Long): IO[GiteaError, contract.Hook]``

- `org` (path, required, string): name of the organization
- `id` (path, required, integer): id of the hook to get
- HTTP 200: `#/responses/Hook`
- HTTP 404: `#/responses/notFound`

### `orgDeleteHook`

**DELETE `/orgs/{org}/hooks/{id}`** — ``client.orgs.orgDeleteHook(org: String, id: Long): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `id` (path, required, integer): id of the hook to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `orgEditHook`

**PATCH `/orgs/{org}/hooks/{id}`** — ``client.orgs.orgEditHook(org: String, id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook]``

- `org` (path, required, string): name of the organization
- `id` (path, required, integer): id of the hook to update
- `body` (body, optional, `contract.EditHookOption`)
- HTTP 200: `#/responses/Hook`
- HTTP 404: `#/responses/notFound`

### `orgListLabels`

**GET `/orgs/{org}/labels`** — ``client.orgs.orgListLabels(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Label]]``

- `org` (path, required, string): name of the organization
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/LabelList`
- HTTP 404: `#/responses/notFound`

### `orgCreateLabel`

**POST `/orgs/{org}/labels`** — ``client.orgs.orgCreateLabel(org: String, body: contract.CreateLabelOption): IO[GiteaError, contract.Label]``

- `org` (path, required, string): name of the organization
- `body` (body, optional, `contract.CreateLabelOption`)
- HTTP 201: `#/responses/Label`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `orgGetLabel`

**GET `/orgs/{org}/labels/{id}`** — ``client.orgs.orgGetLabel(org: String, id: Long): IO[GiteaError, contract.Label]``

- `org` (path, required, string): name of the organization
- `id` (path, required, integer): id of the label to get
- HTTP 200: `#/responses/Label`
- HTTP 404: `#/responses/notFound`

### `orgDeleteLabel`

**DELETE `/orgs/{org}/labels/{id}`** — ``client.orgs.orgDeleteLabel(org: String, id: Long): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `id` (path, required, integer): id of the label to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `orgEditLabel`

**PATCH `/orgs/{org}/labels/{id}`** — ``client.orgs.orgEditLabel(org: String, id: Long, body: contract.EditLabelOption): IO[GiteaError, contract.Label]``

- `org` (path, required, string): name of the organization
- `id` (path, required, integer): id of the label to edit
- `body` (body, optional, `contract.EditLabelOption`)
- HTTP 200: `#/responses/Label`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `orgIsMember`

**GET `/orgs/{org}/members/{username}`** — ``client.orgs.orgIsMember(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user to check for an organization membership
- HTTP 204: `description: user is a member`
- HTTP 303: `description: redirection to /orgs/{org}/public_members/{username}`
- HTTP 404: `description: user is not a member`

### `orgDeleteMember`

**DELETE `/orgs/{org}/members/{username}`** — ``client.orgs.orgDeleteMember(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user to remove from the organization
- HTTP 204: `description: member removed`
- HTTP 404: `#/responses/notFound`

### `orgIsPublicMember`

**GET `/orgs/{org}/public_members/{username}`** — ``client.orgs.orgIsPublicMember(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user to check for a public organization membership
- HTTP 204: `description: user is a public member`
- HTTP 404: `description: user is not a public member`

### `orgPublicizeMember`

**PUT `/orgs/{org}/public_members/{username}`** — ``client.orgs.orgPublicizeMember(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user whose membership is to be publicized
- HTTP 204: `description: membership publicized`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `orgConcealMember`

**DELETE `/orgs/{org}/public_members/{username}`** — ``client.orgs.orgConcealMember(org: String, username: String): IO[GiteaError, Unit]``

- `org` (path, required, string): name of the organization
- `username` (path, required, string): username of the user whose membership is to be concealed
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `renameOrg`

**POST `/orgs/{org}/rename`** — ``client.orgs.renameOrg(org: String, body: contract.RenameOrgOption): IO[GiteaError, Unit]``

- `org` (path, required, string): existing org name
- `body` (body, required, `contract.RenameOrgOption`)
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 422: `#/responses/validationError`

### `orgListTeams`

**GET `/orgs/{org}/teams`** — ``client.orgs.orgListTeams(org: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Team]]``

- `org` (path, required, string): name of the organization
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/TeamList`
- HTTP 404: `#/responses/notFound`

### `orgCreateTeam`

**POST `/orgs/{org}/teams`** — ``client.orgs.orgCreateTeam(org: String, body: contract.CreateTeamOption): IO[GiteaError, contract.Team]``

- `org` (path, required, string): name of the organization
- `body` (body, optional, `contract.CreateTeamOption`)
- HTTP 201: `#/responses/Team`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `teamSearch`

**GET `/orgs/{org}/teams/search`** — ``client.orgs.teamSearch(org: String, q: Option[String] = None, includeDesc: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.TeamSearchResult]``

- `org` (path, required, string): name of the organization
- `q` (query, optional, string): keywords to search
- `include_desc` (query, optional, boolean): include search within team description (defaults to true)
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `type:object`
- HTTP 404: `#/responses/notFound`

## Packages

### `listPackages`

**GET `/packages/{owner}`** — ``client.packages.listPackages(owner: String, page: Option[Int] = None, limit: Option[Int] = None, `type`: Option[contract.PackageType] = None, q: Option[String] = None): IO[GiteaError, zio.Chunk[contract.Package]]``

- `owner` (path, required, string): owner of the packages
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `type` (query, optional, string): package type filter
- `q` (query, optional, string): name filter
- HTTP 200: `#/responses/PackageList`
- HTTP 404: `#/responses/notFound`

### `listPackageVersions`

**GET `/packages/{owner}/{type}/{name}`** — ``client.packages.listPackageVersions(owner: String, `type`: String, name: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Package]]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/PackageList`
- HTTP 404: `#/responses/notFound`

### `deletePackage`

**DELETE `/packages/{owner}/{type}/{name}`** — ``client.packages.deletePackage(owner: String, `type`: String, name: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `getLatestPackageVersion`

**GET `/packages/{owner}/{type}/{name}/-/latest`** — ``client.packages.getLatestPackageVersion(owner: String, `type`: String, name: String): IO[GiteaError, contract.Package]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- HTTP 200: `#/responses/Package`
- HTTP 404: `#/responses/notFound`

### `linkPackage`

**POST `/packages/{owner}/{type}/{name}/-/link/{repo_name}`** — ``client.packages.linkPackage(owner: String, `type`: String, name: String, repoName: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- `repo_name` (path, required, string): name of the repository to link.
- HTTP 201: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `unlinkPackage`

**POST `/packages/{owner}/{type}/{name}/-/unlink`** — ``client.packages.unlinkPackage(owner: String, `type`: String, name: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- HTTP 201: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `getPackage`

**GET `/packages/{owner}/{type}/{name}/{version}`** — ``client.packages.getPackage(owner: String, `type`: String, name: String, version: String): IO[GiteaError, contract.Package]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- `version` (path, required, string): version of the package
- HTTP 200: `#/responses/Package`
- HTTP 404: `#/responses/notFound`

### `deletePackageVersion`

**DELETE `/packages/{owner}/{type}/{name}/{version}`** — ``client.packages.deletePackageVersion(owner: String, `type`: String, name: String, version: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- `version` (path, required, string): version of the package
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `listPackageFiles`

**GET `/packages/{owner}/{type}/{name}/{version}/files`** — ``client.packages.listPackageFiles(owner: String, `type`: String, name: String, version: String): IO[GiteaError, zio.Chunk[contract.PackageFile]]``

- `owner` (path, required, string): owner of the package
- `type` (path, required, string): type of the package
- `name` (path, required, string): name of the package
- `version` (path, required, string): version of the package
- HTTP 200: `#/responses/PackageFileList`
- HTTP 404: `#/responses/notFound`

## Issues

### `issueSearchIssues`

**GET `/repos/issues/search`** — ``client.issues.issueSearchIssues(state: Option[contract.IssueSearchState] = None, labels: Option[String] = None, milestones: Option[String] = None, q: Option[String] = None, `type`: Option[contract.IssueSearchType] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, assigned: Option[Boolean] = None, created: Option[Boolean] = None, mentioned: Option[Boolean] = None, reviewRequested: Option[Boolean] = None, reviewed: Option[Boolean] = None, owner: Option[String] = None, createdBy: Option[String] = None, team: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Issue]]``

- `state` (query, optional, string): State of the issue
- `labels` (query, optional, string): Comma-separated list of label names. Fetch only issues that have any of these labels. Non existent labels are discarded.
- `milestones` (query, optional, string): Comma-separated list of milestone names. Fetch only issues that have any of these milestones. Non existent milestones are discarded.
- `q` (query, optional, string): Search string
- `type` (query, optional, string): Filter by issue type
- `since` (query, optional, timestamp): Only show issues updated after the given time (RFC 3339 format)
- `before` (query, optional, timestamp): Only show issues updated before the given time (RFC 3339 format)
- `assigned` (query, optional, boolean): Filter issues or pulls assigned to the authenticated user
- `created` (query, optional, boolean): Filter issues or pulls created by the authenticated user
- `mentioned` (query, optional, boolean): Filter issues or pulls mentioning the authenticated user
- `review_requested` (query, optional, boolean): Filter pull requests where the authenticated user's review was requested
- `reviewed` (query, optional, boolean): Filter pull requests reviewed by the authenticated user
- `owner` (query, optional, string): Filter by repository owner
- `created_by` (query, optional, string): Only show items which were created by the given user
- `team` (query, optional, string): Filter by team (requires organization owner parameter)
- `page` (query, optional, integer): Page number of results to return (1-based)
- `limit` (query, optional, integer): Number of items per page
- HTTP 200: `#/responses/IssueList`
- HTTP 400: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `issueListIssueCommentAttachments`

**GET `/repos/{owner}/{repo}/issues/comments/{id}/assets`** — ``client.issues.issueListIssueCommentAttachments(owner: String, repo: String, id: Long): IO[GiteaError, zio.Chunk[contract.Attachment]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the comment
- HTTP 200: `#/responses/AttachmentList`
- HTTP 404: `#/responses/error`

### `issueGetIssueCommentAttachment`

**GET `/repos/{owner}/{repo}/issues/comments/{id}/assets/{attachment_id}`** — ``client.issues.issueGetIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the comment
- `attachment_id` (path, required, integer): id of the attachment to get
- HTTP 200: `#/responses/Attachment`
- HTTP 404: `#/responses/error`

### `issueDeleteIssueCommentAttachment`

**DELETE `/repos/{owner}/{repo}/issues/comments/{id}/assets/{attachment_id}`** — ``client.issues.issueDeleteIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the comment
- `attachment_id` (path, required, integer): id of the attachment to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/error`
- HTTP 423: `#/responses/repoArchivedError`

### `issueEditIssueCommentAttachment`

**PATCH `/repos/{owner}/{repo}/issues/comments/{id}/assets/{attachment_id}`** — ``client.issues.issueEditIssueCommentAttachment(owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the comment
- `attachment_id` (path, required, integer): id of the attachment to edit
- `body` (body, optional, `contract.EditAttachmentOptions`)
- HTTP 201: `#/responses/Attachment`
- HTTP 404: `#/responses/error`
- HTTP 422: `#/responses/validationError`
- HTTP 423: `#/responses/repoArchivedError`

### `issueListIssueAttachments`

**GET `/repos/{owner}/{repo}/issues/{index}/assets`** — ``client.issues.issueListIssueAttachments(owner: String, repo: String, index: Long): IO[GiteaError, zio.Chunk[contract.Attachment]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): index of the issue
- HTTP 200: `#/responses/AttachmentList`
- HTTP 404: `#/responses/error`

### `issueGetIssueAttachment`

**GET `/repos/{owner}/{repo}/issues/{index}/assets/{attachment_id}`** — ``client.issues.issueGetIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): index of the issue
- `attachment_id` (path, required, integer): id of the attachment to get
- HTTP 200: `#/responses/Attachment`
- HTTP 404: `#/responses/error`

### `issueDeleteIssueAttachment`

**DELETE `/repos/{owner}/{repo}/issues/{index}/assets/{attachment_id}`** — ``client.issues.issueDeleteIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): index of the issue
- `attachment_id` (path, required, integer): id of the attachment to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/error`
- HTTP 423: `#/responses/repoArchivedError`

### `issueEditIssueAttachment`

**PATCH `/repos/{owner}/{repo}/issues/{index}/assets/{attachment_id}`** — ``client.issues.issueEditIssueAttachment(owner: String, repo: String, index: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): index of the issue
- `attachment_id` (path, required, integer): id of the attachment to edit
- `body` (body, optional, `contract.EditAttachmentOptions`)
- HTTP 201: `#/responses/Attachment`
- HTTP 404: `#/responses/error`
- HTTP 422: `#/responses/validationError`
- HTTP 423: `#/responses/repoArchivedError`

### `issueDeleteCommentDeprecated`

**DELETE `/repos/{owner}/{repo}/issues/{index}/comments/{id}`** — ``client.issues.issueDeleteCommentDeprecated(owner: String, repo: String, index: Int, id: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): this parameter is ignored
- `id` (path, required, integer): id of comment to delete
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `issueEditCommentDeprecated`

**PATCH `/repos/{owner}/{repo}/issues/{index}/comments/{id}`** — ``client.issues.issueEditCommentDeprecated(owner: String, repo: String, index: Int, id: Long, body: contract.EditIssueCommentOption): IO[GiteaError, Option[contract.Comment]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): this parameter is ignored
- `id` (path, required, integer): id of the comment to edit
- `body` (body, optional, `contract.EditIssueCommentOption`)
- HTTP 200: `#/responses/Comment`
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `issueGetCommentsAndTimeline`

**GET `/repos/{owner}/{repo}/issues/{index}/timeline`** — ``client.issues.issueGetCommentsAndTimeline(owner: String, repo: String, index: Long, since: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None, before: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.TimelineComment]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): index of the issue
- `since` (query, optional, timestamp): if provided, only comments updated since the specified time are returned.
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `before` (query, optional, timestamp): if provided, only comments updated before the provided time are returned.
- HTTP 200: `#/responses/TimelineList`
- HTTP 404: `#/responses/notFound`

### `issueCreateIssueCommentAttachment`

**POST `/repos/{owner}/{repo}/issues/comments/{id}/assets`** — ``client.issues.createCommentAttachment(owner: String, repo: String, id: Long, upload: AttachmentUpload, name: Option[String] = None): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the comment
- `name` (query, optional, string): name of the attachment
- `attachment` (formData, required, file): attachment to upload
- HTTP 201: `#/responses/Attachment`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/error`
- HTTP 413: `#/responses/error`
- HTTP 422: `#/responses/validationError`
- HTTP 423: `#/responses/repoArchivedError`

### `issueCreateIssueAttachment`

**POST `/repos/{owner}/{repo}/issues/{index}/assets`** — ``client.issues.createIssueAttachment(owner: String, repo: String, index: Long, upload: AttachmentUpload, name: Option[String] = None): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `index` (path, required, integer): index of the issue
- `name` (query, optional, string): name of the attachment
- `attachment` (formData, required, file): attachment to upload
- HTTP 201: `#/responses/Attachment`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/error`
- HTTP 413: `#/responses/error`
- HTTP 422: `#/responses/validationError`
- HTTP 423: `#/responses/repoArchivedError`

## Repos

### `repoMigrate`

**POST `/repos/migrate`** — ``client.repos.repoMigrate(body: contract.MigrateRepoOptions): IO[GiteaError, contract.Repository]``

- `body` (body, optional, `contract.MigrateRepoOptions`)
- HTTP 201: `#/responses/Repository`
- HTTP 403: `#/responses/forbidden`
- HTTP 409: `description: The repository with the same name already exists.`
- HTTP 422: `#/responses/validationError`

### `repoSearch`

**GET `/repos/search`** — ``client.repos.repoSearch(q: Option[String] = None, topic: Option[Boolean] = None, includeDesc: Option[Boolean] = None, uid: Option[Long] = None, priorityOwnerId: Option[Long] = None, teamId: Option[Long] = None, starredBy: Option[Long] = None, `private`: Option[Boolean] = None, isPrivate: Option[Boolean] = None, template: Option[Boolean] = None, archived: Option[Boolean] = None, mode: Option[String] = None, exclusive: Option[Boolean] = None, sort: Option[String] = None, order: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.SearchResults]``

- `q` (query, optional, string): keyword
- `topic` (query, optional, boolean): Limit search to repositories with keyword as topic
- `includeDesc` (query, optional, boolean): include search of keyword within repository description
- `uid` (query, optional, integer): search only for repos that the user with the given id owns or contributes to
- `priority_owner_id` (query, optional, integer): repo owner to prioritize in the results
- `team_id` (query, optional, integer): search only for repos that belong to the given team id
- `starredBy` (query, optional, integer): search only for repos that the user with the given id has starred
- `private` (query, optional, boolean): include private repositories this user has access to (defaults to true)
- `is_private` (query, optional, boolean): show only pubic, private or all repositories (defaults to all)
- `template` (query, optional, boolean): include template repositories this user has access to (defaults to true)
- `archived` (query, optional, boolean): show only archived, non-archived or all repositories (defaults to all)
- `mode` (query, optional, string): type of repository to search for. Supported values are "fork", "source", "mirror" and "collaborative"
- `exclusive` (query, optional, boolean): if `uid` is given, search only for repos that the user owns
- `sort` (query, optional, string): sort repos by attribute. Supported values are "alpha", "created", "updated", "size", "git_size", "lfs_size", "stars", "forks" and "id". Default is "alpha"
- `order` (query, optional, string): sort order, either "asc" (ascending) or "desc" (descending). Default is "asc", ignored if "sort" is not specified.
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/SearchResults`
- HTTP 422: `#/responses/validationError`

### `repoListActivityFeeds`

**GET `/repos/{owner}/{repo}/activities/feeds`** — ``client.repos.repoListActivityFeeds(owner: String, repo: String, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `date` (query, optional, string): the date of the activities to be found
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/ActivityFeedsList`
- HTTP 404: `#/responses/notFound`

### `repoUpdateAvatar`

**POST `/repos/{owner}/{repo}/avatar`** — ``client.repos.repoUpdateAvatar(owner: String, repo: String, body: contract.UpdateRepoAvatarOption): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.UpdateRepoAvatarOption`)
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `repoDeleteAvatar`

**DELETE `/repos/{owner}/{repo}/avatar`** — ``client.repos.repoDeleteAvatar(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `repoAddCollaborator`

**PUT `/repos/{owner}/{repo}/collaborators/{collaborator}`** — ``client.repos.repoAddCollaborator(owner: String, repo: String, collaborator: String, body: contract.AddCollaboratorOption): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `collaborator` (path, required, string): username of the user to add or update as a collaborator
- `body` (body, optional, `contract.AddCollaboratorOption`)
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoDeleteCollaborator`

**DELETE `/repos/{owner}/{repo}/collaborators/{collaborator}`** — ``client.repos.repoDeleteCollaborator(owner: String, repo: String, collaborator: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `collaborator` (path, required, string): username of the collaborator to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoGetAllCommits`

**GET `/repos/{owner}/{repo}/commits`** — ``client.repos.repoGetAllCommits(owner: String, repo: String, sha: Option[String] = None, path: Option[String] = None, since: Option[java.time.Instant] = None, until: Option[java.time.Instant] = None, stat: Option[Boolean] = None, verification: Option[Boolean] = None, files: Option[Boolean] = None, page: Option[Int] = None, limit: Option[Int] = None, not: Option[String] = None): IO[GiteaError, zio.Chunk[contract.Commit]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `sha` (query, optional, string): SHA or branch to start listing commits from (usually 'master')
- `path` (query, optional, string): filepath of a file/dir
- `since` (query, optional, timestamp): Only commits after this date will be returned (ISO 8601 format)
- `until` (query, optional, timestamp): Only commits before this date will be returned (ISO 8601 format)
- `stat` (query, optional, boolean): include diff stats for every commit (disable for speedup, default 'true')
- `verification` (query, optional, boolean): include verification for every commit (disable for speedup, default 'true')
- `files` (query, optional, boolean): include a list of affected files for every commit (disable for speedup, default 'true')
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results (ignored if used with 'path')
- `not` (query, optional, string): commits that match the given specifier will not be listed.
- HTTP 200: `#/responses/CommitList`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/EmptyRepository`

### `repoCompareDiff`

**GET `/repos/{owner}/{repo}/compare/{basehead}`** — ``client.repos.repoCompareDiff(owner: String, repo: String, basehead: String, output: Option[contract.CompareOutput] = None): IO[GiteaError, Either[contract.Compare, String]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `basehead` (path, required, string): compare two refs as `base...head` (or `base..head`); refs may be branches, tags, full or short SHAs (including branch names that contain slashes), optionally with a `^` or `~N` revision suffix.
- `output` (query, optional, string): return the raw comparison as `diff` or `patch` instead of JSON
- HTTP 200: `#/responses/Compare`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `repoChangeFiles`

**POST `/repos/{owner}/{repo}/contents`** — ``client.repos.repoChangeFiles(owner: String, repo: String, body: contract.ChangeFilesOptions): IO[GiteaError, contract.FilesResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, required, `contract.ChangeFilesOptions`)
- HTTP 201: `#/responses/FilesResponse`
- HTTP 403: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/error`
- HTTP 423: `#/responses/repoArchivedError`

### `repoGetContentsExt`

**GET `/repos/{owner}/{repo}/contents-ext/{filepath}`** — ``client.repos.repoGetContentsExt(owner: String, repo: String, filepath: String, ref: Option[String] = None, includes: Option[String] = None): IO[GiteaError, contract.ContentsExtResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `filepath` (path, required, string): path of the dir, file, symlink or submodule in the repo. Swagger requires path parameter to be "required", you can leave it empty or pass a single dot (".") to get the root directory.
- `ref` (query, optional, string): the name of the commit/branch/tag, default to the repository’s default branch.
- `includes` (query, optional, string): By default this API's response only contains file's metadata. Use comma-separated "includes" options to retrieve more fields. Option "file_content" will try to retrieve the file content, "lfs_metadata" will try to retrieve LFS metadata, "commit_metadata" will try to retrieve commit metadata, and "commit_message" will try to retrieve commit message.
- HTTP 200: `#/responses/ContentsExtResponse`
- HTTP 404: `#/responses/notFound`

### `repoUpdateFile`

**PUT `/repos/{owner}/{repo}/contents/{filepath}`** — ``client.repos.repoUpdateFile(owner: String, repo: String, filepath: String, body: contract.UpdateFileOptions): IO[GiteaError, contract.FileResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `filepath` (path, required, string): path of the file to update
- `body` (body, required, `contract.UpdateFileOptions`)
- HTTP 200: `#/responses/FileResponse`
- HTTP 201: `#/responses/FileResponse`
- HTTP 403: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/error`
- HTTP 423: `#/responses/repoArchivedError`

### `repoCreateFile`

**POST `/repos/{owner}/{repo}/contents/{filepath}`** — ``client.repos.repoCreateFile(owner: String, repo: String, filepath: String, body: contract.CreateFileOptions): IO[GiteaError, contract.FileResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `filepath` (path, required, string): path of the file to create
- `body` (body, required, `contract.CreateFileOptions`)
- HTTP 201: `#/responses/FileResponse`
- HTTP 403: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/error`
- HTTP 423: `#/responses/repoArchivedError`

### `repoDeleteFile`

**DELETE `/repos/{owner}/{repo}/contents/{filepath}`** — ``client.repos.repoDeleteFile(owner: String, repo: String, filepath: String, body: contract.DeleteFileOptions): IO[GiteaError, contract.FileDeleteResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `filepath` (path, required, string): path of the file to delete
- `body` (body, required, `contract.DeleteFileOptions`)
- HTTP 200: `#/responses/FileDeleteResponse`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/error`
- HTTP 404: `#/responses/error`
- HTTP 422: `#/responses/error`
- HTTP 423: `#/responses/repoArchivedError`

### `repoApplyDiffPatch`

**POST `/repos/{owner}/{repo}/diffpatch`** — ``client.repos.repoApplyDiffPatch(owner: String, repo: String, body: contract.ApplyDiffPatchFileOptions): IO[GiteaError, contract.FileResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, required, `contract.ApplyDiffPatchFileOptions`)
- HTTP 200: `#/responses/FileResponse`
- HTTP 404: `#/responses/notFound`
- HTTP 423: `#/responses/repoArchivedError`

### `repoGetEditorConfig`

**GET `/repos/{owner}/{repo}/editorconfig/{filepath}`** — ``client.repos.repoGetEditorConfig(owner: String, repo: String, filepath: String, ref: Option[String] = None): IO[GiteaError, String]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `filepath` (path, required, string): filepath of file to get
- `ref` (query, optional, string): The name of the commit/branch/tag. Default to the repository’s default branch.
- HTTP 200: `description: success`
- HTTP 404: `#/responses/notFound`

### `repoGetFileContents`

**GET `/repos/{owner}/{repo}/file-contents`** — ``client.repos.repoGetFileContents(owner: String, repo: String, ref: Option[String] = None, body: String): IO[GiteaError, zio.Chunk[contract.ContentsResponse]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `ref` (query, optional, string): The name of the commit/branch/tag. Default to the repository’s default branch.
- `body` (query, required, string): The JSON encoded body (see the POST request): {"files": ["filename1", "filename2"]}
- HTTP 200: `#/responses/ContentsListResponse`
- HTTP 404: `#/responses/notFound`

### `repoGetFileContentsPost`

**POST `/repos/{owner}/{repo}/file-contents`** — ``client.repos.repoGetFileContentsPost(owner: String, repo: String, body: contract.GetFilesOptions, ref: Option[String] = None): IO[GiteaError, zio.Chunk[contract.ContentsResponse]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `ref` (query, optional, string): The name of the commit/branch/tag. Default to the repository’s default branch.
- `body` (body, required, `contract.GetFilesOptions`)
- HTTP 200: `#/responses/ContentsListResponse`
- HTTP 404: `#/responses/notFound`

### `listForks`

**GET `/repos/{owner}/{repo}/forks`** — ``client.repos.listForks(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`
- HTTP 404: `#/responses/notFound`

### `repoListHooks`

**GET `/repos/{owner}/{repo}/hooks`** — ``client.repos.repoListHooks(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/HookList`
- HTTP 404: `#/responses/notFound`

### `repoCreateHook`

**POST `/repos/{owner}/{repo}/hooks`** — ``client.repos.repoCreateHook(owner: String, repo: String, body: contract.CreateHookOption): IO[GiteaError, contract.Hook]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateHookOption`)
- HTTP 201: `#/responses/Hook`
- HTTP 404: `#/responses/notFound`

### `repoGetHook`

**GET `/repos/{owner}/{repo}/hooks/{id}`** — ``client.repos.repoGetHook(owner: String, repo: String, id: Long): IO[GiteaError, contract.Hook]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the hook to get
- HTTP 200: `#/responses/Hook`
- HTTP 404: `#/responses/notFound`

### `repoDeleteHook`

**DELETE `/repos/{owner}/{repo}/hooks/{id}`** — ``client.repos.repoDeleteHook(owner: String, repo: String, id: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the hook to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `repoEditHook`

**PATCH `/repos/{owner}/{repo}/hooks/{id}`** — ``client.repos.repoEditHook(owner: String, repo: String, id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): index of the hook
- `body` (body, optional, `contract.EditHookOption`)
- HTTP 200: `#/responses/Hook`
- HTTP 404: `#/responses/notFound`

### `repoTestHook`

**POST `/repos/{owner}/{repo}/hooks/{id}/tests`** — ``client.repos.repoTestHook(owner: String, repo: String, id: Long, ref: Option[String] = None): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the hook to test
- `ref` (query, optional, string): The name of the commit/branch/tag, indicates which commit will be loaded to the webhook payload.
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `repoGetIssueConfig`

**GET `/repos/{owner}/{repo}/issue_config`** — ``client.repos.repoGetIssueConfig(owner: String, repo: String): IO[GiteaError, contract.IssueConfig]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/RepoIssueConfig`
- HTTP 404: `#/responses/notFound`

### `repoValidateIssueConfig`

**GET `/repos/{owner}/{repo}/issue_config/validate`** — ``client.repos.repoValidateIssueConfig(owner: String, repo: String): IO[GiteaError, contract.IssueConfigValidation]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/RepoIssueConfigValidation`
- HTTP 404: `#/responses/notFound`

### `repoGetIssueTemplates`

**GET `/repos/{owner}/{repo}/issue_templates`** — ``client.repos.repoGetIssueTemplates(owner: String, repo: String): IO[GiteaError, zio.Chunk[contract.IssueTemplate]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/IssueTemplates`
- HTTP 404: `#/responses/notFound`

### `repoListKeys`

**GET `/repos/{owner}/{repo}/keys`** — ``client.repos.repoListKeys(owner: String, repo: String, keyId: Option[Int] = None, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.DeployKey]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `key_id` (query, optional, integer): the key_id to search for
- `fingerprint` (query, optional, string): fingerprint of the key
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/DeployKeyList`
- HTTP 404: `#/responses/notFound`

### `repoCreateKey`

**POST `/repos/{owner}/{repo}/keys`** — ``client.repos.repoCreateKey(owner: String, repo: String, body: contract.CreateKeyOption): IO[GiteaError, contract.DeployKey]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateKeyOption`)
- HTTP 201: `#/responses/DeployKey`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoGetKey`

**GET `/repos/{owner}/{repo}/keys/{id}`** — ``client.repos.repoGetKey(owner: String, repo: String, id: Long): IO[GiteaError, contract.DeployKey]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the key to get
- HTTP 200: `#/responses/DeployKey`
- HTTP 404: `#/responses/notFound`

### `repoDeleteKey`

**DELETE `/repos/{owner}/{repo}/keys/{id}`** — ``client.repos.repoDeleteKey(owner: String, repo: String, id: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the key to delete
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `issueListLabels`

**GET `/repos/{owner}/{repo}/labels`** — ``client.repos.issueListLabels(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Label]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/LabelList`
- HTTP 404: `#/responses/notFound`

### `issueCreateLabel`

**POST `/repos/{owner}/{repo}/labels`** — ``client.repos.issueCreateLabel(owner: String, repo: String, body: contract.CreateLabelOption): IO[GiteaError, contract.Label]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateLabelOption`)
- HTTP 201: `#/responses/Label`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `issueGetLabel`

**GET `/repos/{owner}/{repo}/labels/{id}`** — ``client.repos.issueGetLabel(owner: String, repo: String, id: Long): IO[GiteaError, contract.Label]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the label to get
- HTTP 200: `#/responses/Label`
- HTTP 404: `#/responses/notFound`

### `issueDeleteLabel`

**DELETE `/repos/{owner}/{repo}/labels/{id}`** — ``client.repos.issueDeleteLabel(owner: String, repo: String, id: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the label to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `issueEditLabel`

**PATCH `/repos/{owner}/{repo}/labels/{id}`** — ``client.repos.issueEditLabel(owner: String, repo: String, id: Long, body: contract.EditLabelOption): IO[GiteaError, contract.Label]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the label to edit
- `body` (body, optional, `contract.EditLabelOption`)
- HTTP 200: `#/responses/Label`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoGetLicenses`

**GET `/repos/{owner}/{repo}/licenses`** — ``client.repos.repoGetLicenses(owner: String, repo: String): IO[GiteaError, zio.Chunk[String]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/LicensesList`
- HTTP 404: `#/responses/notFound`

### `repoMergeUpstream`

**POST `/repos/{owner}/{repo}/merge-upstream`** — ``client.repos.repoMergeUpstream(owner: String, repo: String, body: contract.MergeUpstreamRequest): IO[GiteaError, contract.MergeUpstreamResponse]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.MergeUpstreamRequest`)
- HTTP 200: `#/responses/MergeUpstreamResponse`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `issueGetMilestonesList`

**GET `/repos/{owner}/{repo}/milestones`** — ``client.repos.issueGetMilestonesList(owner: String, repo: String, state: Option[String] = None, name: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Milestone]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `state` (query, optional, string): Milestone state, Recognized values are open, closed and all. Defaults to "open"
- `name` (query, optional, string): filter by milestone name
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/MilestoneList`
- HTTP 404: `#/responses/notFound`

### `issueCreateMilestone`

**POST `/repos/{owner}/{repo}/milestones`** — ``client.repos.issueCreateMilestone(owner: String, repo: String, body: contract.CreateMilestoneOption): IO[GiteaError, contract.Milestone]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateMilestoneOption`)
- HTTP 201: `#/responses/Milestone`
- HTTP 404: `#/responses/notFound`

### `issueGetMilestone`

**GET `/repos/{owner}/{repo}/milestones/{id}`** — ``client.repos.issueGetMilestone(owner: String, repo: String, id: String): IO[GiteaError, contract.Milestone]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, string): the milestone to get, identified by ID and if not available by name
- HTTP 200: `#/responses/Milestone`
- HTTP 404: `#/responses/notFound`

### `issueDeleteMilestone`

**DELETE `/repos/{owner}/{repo}/milestones/{id}`** — ``client.repos.issueDeleteMilestone(owner: String, repo: String, id: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, string): the milestone to delete, identified by ID and if not available by name
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `issueEditMilestone`

**PATCH `/repos/{owner}/{repo}/milestones/{id}`** — ``client.repos.issueEditMilestone(owner: String, repo: String, id: String, body: contract.EditMilestoneOption): IO[GiteaError, contract.Milestone]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, string): the milestone to edit, identified by ID and if not available by name
- `body` (body, optional, `contract.EditMilestoneOption`)
- HTTP 200: `#/responses/Milestone`
- HTTP 404: `#/responses/notFound`

### `repoMirrorSync`

**POST `/repos/{owner}/{repo}/mirror-sync`** — ``client.repos.repoMirrorSync(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo to sync
- `repo` (path, required, string): name of the repo to sync
- HTTP 200: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `repoListPushMirrors`

**GET `/repos/{owner}/{repo}/push_mirrors`** — ``client.repos.repoListPushMirrors(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.PushMirror]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/PushMirrorList`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `repoAddPushMirror`

**POST `/repos/{owner}/{repo}/push_mirrors`** — ``client.repos.repoAddPushMirror(owner: String, repo: String, body: contract.CreatePushMirrorOption): IO[GiteaError, contract.PushMirror]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreatePushMirrorOption`)
- HTTP 200: `#/responses/PushMirror`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `repoPushMirrorSync`

**POST `/repos/{owner}/{repo}/push_mirrors-sync`** — ``client.repos.repoPushMirrorSync(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo to sync
- `repo` (path, required, string): name of the repo to sync
- HTTP 200: `#/responses/empty`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoGetPushMirrorByRemoteName`

**GET `/repos/{owner}/{repo}/push_mirrors/{name}`** — ``client.repos.repoGetPushMirrorByRemoteName(owner: String, repo: String, name: String): IO[GiteaError, contract.PushMirror]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `name` (path, required, string): remote name of push mirror
- HTTP 200: `#/responses/PushMirror`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `repoDeletePushMirror`

**DELETE `/repos/{owner}/{repo}/push_mirrors/{name}`** — ``client.repos.repoDeletePushMirror(owner: String, repo: String, name: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `name` (path, required, string): remote name of the pushMirror
- HTTP 204: `#/responses/empty`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `repoSigningKeySSH`

**GET `/repos/{owner}/{repo}/signing-key.pub`** — ``client.repos.repoSigningKeySSH(owner: String, repo: String): IO[GiteaError, String]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `type:string`

### `userCurrentCheckSubscription`

**GET `/repos/{owner}/{repo}/subscription`** — ``client.repos.userCurrentCheckSubscription(owner: String, repo: String): IO[GiteaError, contract.WatchInfo]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/WatchInfo`
- HTTP 404: `description: User is not watching this repo or repo do not exist`

### `userCurrentPutSubscription`

**PUT `/repos/{owner}/{repo}/subscription`** — ``client.repos.userCurrentPutSubscription(owner: String, repo: String): IO[GiteaError, contract.WatchInfo]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 200: `#/responses/WatchInfo`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userCurrentDeleteSubscription`

**DELETE `/repos/{owner}/{repo}/subscription`** — ``client.repos.userCurrentDeleteSubscription(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `repoCreateTag`

**POST `/repos/{owner}/{repo}/tags`** — ``client.repos.repoCreateTag(owner: String, repo: String, body: contract.CreateTagOption): IO[GiteaError, contract.Tag]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateTagOption`)
- HTTP 200: `#/responses/Tag`
- HTTP 404: `#/responses/notFound`
- HTTP 405: `#/responses/empty`
- HTTP 409: `#/responses/conflict`
- HTTP 422: `#/responses/validationError`
- HTTP 423: `#/responses/repoArchivedError`

### `repoDeleteTag`

**DELETE `/repos/{owner}/{repo}/tags/{tag}`** — ``client.repos.repoDeleteTag(owner: String, repo: String, tag: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `tag` (path, required, string): name of tag to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 405: `#/responses/empty`
- HTTP 409: `#/responses/conflict`
- HTTP 422: `#/responses/validationError`
- HTTP 423: `#/responses/repoArchivedError`

### `repoAddTeam`

**PUT `/repos/{owner}/{repo}/teams/{team}`** — ``client.repos.repoAddTeam(owner: String, repo: String, team: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `team` (path, required, string): team name
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 405: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `repoDeleteTeam`

**DELETE `/repos/{owner}/{repo}/teams/{team}`** — ``client.repos.repoDeleteTeam(owner: String, repo: String, team: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `team` (path, required, string): team name
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 405: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `repoTrackedTimes`

**GET `/repos/{owner}/{repo}/times`** — ``client.repos.repoTrackedTimes(owner: String, repo: String, user: Option[String] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.TrackedTime]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `user` (query, optional, string): optional filter by user (available for issue managers)
- `since` (query, optional, timestamp): Only show times updated after the given time. This is a timestamp in RFC 3339 format
- `before` (query, optional, timestamp): Only show times updated before the given time. This is a timestamp in RFC 3339 format
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/TrackedTimeList`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userTrackedTimes`

**GET `/repos/{owner}/{repo}/times/{user}`** — ``client.repos.userTrackedTimes(owner: String, repo: String, user: String): IO[GiteaError, zio.Chunk[contract.TrackedTime]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `user` (path, required, string): username of the user whose tracked times are to be listed
- HTTP 200: `#/responses/TrackedTimeList`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `repoUpdateTopics`

**PUT `/repos/{owner}/{repo}/topics`** — ``client.repos.repoUpdateTopics(owner: String, repo: String, body: contract.RepoTopicOptions): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.RepoTopicOptions`)
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/invalidTopicsError`

### `repoAddTopic`

**PUT `/repos/{owner}/{repo}/topics/{topic}`** — ``client.repos.repoAddTopic(owner: String, repo: String, topic: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `topic` (path, required, string): name of the topic to add
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/invalidTopicsError`

### `repoDeleteTopic`

**DELETE `/repos/{owner}/{repo}/topics/{topic}`** — ``client.repos.repoDeleteTopic(owner: String, repo: String, topic: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `topic` (path, required, string): name of the topic to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/invalidTopicsError`

### `repoCreateWikiPage`

**POST `/repos/{owner}/{repo}/wiki/new`** — ``client.repos.repoCreateWikiPage(owner: String, repo: String, body: contract.CreateWikiPageOptions): IO[GiteaError, contract.WikiPage]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateWikiPageOptions`)
- HTTP 201: `#/responses/WikiPage`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 423: `#/responses/repoArchivedError`

### `repoGetWikiPage`

**GET `/repos/{owner}/{repo}/wiki/page/{pageName}`** — ``client.repos.repoGetWikiPage(owner: String, repo: String, pageName: String): IO[GiteaError, contract.WikiPage]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `pageName` (path, required, string): name of the page
- HTTP 200: `#/responses/WikiPage`
- HTTP 404: `#/responses/notFound`

### `repoDeleteWikiPage`

**DELETE `/repos/{owner}/{repo}/wiki/page/{pageName}`** — ``client.repos.repoDeleteWikiPage(owner: String, repo: String, pageName: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `pageName` (path, required, string): name of the page
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 423: `#/responses/repoArchivedError`

### `repoEditWikiPage`

**PATCH `/repos/{owner}/{repo}/wiki/page/{pageName}`** — ``client.repos.repoEditWikiPage(owner: String, repo: String, pageName: String, body: contract.CreateWikiPageOptions): IO[GiteaError, contract.WikiPage]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `pageName` (path, required, string): name of the page
- `body` (body, optional, `contract.CreateWikiPageOptions`)
- HTTP 200: `#/responses/WikiPage`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 423: `#/responses/repoArchivedError`

### `repoGetWikiPages`

**GET `/repos/{owner}/{repo}/wiki/pages`** — ``client.repos.repoGetWikiPages(owner: String, repo: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.WikiPageMetaData]]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/WikiPageList`
- HTTP 404: `#/responses/notFound`

### `repoGetWikiPageRevisions`

**GET `/repos/{owner}/{repo}/wiki/revisions/{pageName}`** — ``client.repos.repoGetWikiPageRevisions(owner: String, repo: String, pageName: String, page: Option[Int] = None): IO[GiteaError, contract.WikiCommitList]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `pageName` (path, required, string): name of the page
- `page` (query, optional, integer): page number of results to return (1-based)
- HTTP 200: `#/responses/WikiCommitList`
- HTTP 404: `#/responses/notFound`

### `generateRepo`

**POST `/repos/{template_owner}/{template_repo}/generate`** — ``client.repos.generateRepo(templateOwner: String, templateRepo: String, body: contract.GenerateRepoOption): IO[GiteaError, contract.Repository]``

- `template_owner` (path, required, string): owner of the template repository
- `template_repo` (path, required, string): name of the template repository
- `body` (body, optional, `contract.GenerateRepoOption`)
- HTTP 201: `#/responses/Repository`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `description: The repository with the same name already exists.`
- HTTP 422: `#/responses/validationError`

## Releases

### `repoCreateRelease`

**POST `/repos/{owner}/{repo}/releases`** — ``client.releases.repoCreateRelease(owner: String, repo: String, body: contract.CreateReleaseOption): IO[GiteaError, contract.Release]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `body` (body, optional, `contract.CreateReleaseOption`)
- HTTP 201: `#/responses/Release`
- HTTP 404: `#/responses/notFound`
- HTTP 409: `#/responses/error`
- HTTP 422: `#/responses/validationError`

### `repoDeleteReleaseByTag`

**DELETE `/repos/{owner}/{repo}/releases/tags/{tag}`** — ``client.releases.repoDeleteReleaseByTag(owner: String, repo: String, tag: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `tag` (path, required, string): tag name of the release to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoDeleteRelease`

**DELETE `/repos/{owner}/{repo}/releases/{id}`** — ``client.releases.repoDeleteRelease(owner: String, repo: String, id: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the release to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoEditRelease`

**PATCH `/repos/{owner}/{repo}/releases/{id}`** — ``client.releases.repoEditRelease(owner: String, repo: String, id: Long, body: contract.EditReleaseOption): IO[GiteaError, contract.Release]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the release to edit
- `body` (body, optional, `contract.EditReleaseOption`)
- HTTP 200: `#/responses/Release`
- HTTP 404: `#/responses/notFound`

### `repoDeleteReleaseAttachment`

**DELETE `/repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}`** — ``client.releases.repoDeleteReleaseAttachment(owner: String, repo: String, id: Long, attachmentId: Long): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the release
- `attachment_id` (path, required, integer): id of the attachment to delete
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `repoEditReleaseAttachment`

**PATCH `/repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}`** — ``client.releases.repoEditReleaseAttachment(owner: String, repo: String, id: Long, attachmentId: Long, body: contract.EditAttachmentOptions): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the release
- `attachment_id` (path, required, integer): id of the attachment to edit
- `body` (body, optional, `contract.EditAttachmentOptions`)
- HTTP 201: `#/responses/Attachment`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `repoCreateReleaseAttachment`

**POST `/repos/{owner}/{repo}/releases/{id}/assets`** — ``client.releases.createAttachment(owner: String, repo: String, id: Long, upload: AttachmentUpload, name: Option[String] = None): IO[GiteaError, contract.Attachment]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- `id` (path, required, integer): id of the release
- `name` (query, optional, string): name of the attachment
- `attachment` (formData, optional, file): attachment to upload
- HTTP 201: `#/responses/Attachment`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`
- HTTP 413: `#/responses/error`

## Teams

### `orgGetTeam`

**GET `/teams/{id}`** — ``client.teams.orgGetTeam(id: Long): IO[GiteaError, contract.Team]``

- `id` (path, required, integer): id of the team to get
- HTTP 200: `#/responses/Team`
- HTTP 404: `#/responses/notFound`

### `orgDeleteTeam`

**DELETE `/teams/{id}`** — ``client.teams.orgDeleteTeam(id: Long): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the team to delete
- HTTP 204: `description: team deleted`
- HTTP 404: `#/responses/notFound`

### `orgEditTeam`

**PATCH `/teams/{id}`** — ``client.teams.orgEditTeam(id: Int, body: contract.EditTeamOption): IO[GiteaError, contract.Team]``

- `id` (path, required, integer): id of the team to edit
- `body` (body, optional, `contract.EditTeamOption`)
- HTTP 200: `#/responses/Team`
- HTTP 404: `#/responses/notFound`

### `orgListTeamActivityFeeds`

**GET `/teams/{id}/activities/feeds`** — ``client.teams.orgListTeamActivityFeeds(id: Long, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]]``

- `id` (path, required, integer): id of the team
- `date` (query, optional, string): the date of the activities to be found
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/ActivityFeedsList`
- HTTP 404: `#/responses/notFound`

### `orgListTeamMembers`

**GET `/teams/{id}/members`** — ``client.teams.orgListTeamMembers(id: Long, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]``

- `id` (path, required, integer): id of the team
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/UserList`
- HTTP 404: `#/responses/notFound`

### `orgListTeamMember`

**GET `/teams/{id}/members/{username}`** — ``client.teams.orgListTeamMember(id: Long, username: String): IO[GiteaError, contract.User]``

- `id` (path, required, integer): id of the team
- `username` (path, required, string): username of the user whose data is to be listed
- HTTP 200: `#/responses/User`
- HTTP 404: `#/responses/notFound`

### `orgAddTeamMember`

**PUT `/teams/{id}/members/{username}`** — ``client.teams.orgAddTeamMember(id: Long, username: String): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the team
- `username` (path, required, string): username of the user to add to a team
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `orgRemoveTeamMember`

**DELETE `/teams/{id}/members/{username}`** — ``client.teams.orgRemoveTeamMember(id: Long, username: String): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the team
- `username` (path, required, string): username of the user to remove from a team
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `orgListTeamRepos`

**GET `/teams/{id}/repos`** — ``client.teams.orgListTeamRepos(id: Long, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `id` (path, required, integer): id of the team
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`
- HTTP 404: `#/responses/notFound`

### `orgListTeamRepo`

**GET `/teams/{id}/repos/{org}/{repo}`** — ``client.teams.orgListTeamRepo(id: Long, org: String, repo: String): IO[GiteaError, contract.Repository]``

- `id` (path, required, integer): id of the team
- `org` (path, required, string): organization that owns the repo to list
- `repo` (path, required, string): name of the repo to list
- HTTP 200: `#/responses/Repository`
- HTTP 404: `#/responses/notFound`

### `orgAddTeamRepository`

**PUT `/teams/{id}/repos/{org}/{repo}`** — ``client.teams.orgAddTeamRepository(id: Long, org: String, repo: String): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the team
- `org` (path, required, string): organization that owns the repo to add
- `repo` (path, required, string): name of the repo to add
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `orgRemoveTeamRepository`

**DELETE `/teams/{id}/repos/{org}/{repo}`** — ``client.teams.orgRemoveTeamRepository(id: Long, org: String, repo: String): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the team
- `org` (path, required, string): organization that owns the repo to remove
- `repo` (path, required, string): name of the repo to remove
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

## Users

### `userGetOauth2Application`

**GET `/user/applications/oauth2`** — ``client.users.userGetOauth2Application(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.OAuth2Application]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/OAuth2ApplicationList`

### `userCreateOAuth2Application`

**POST `/user/applications/oauth2`** — ``client.users.userCreateOAuth2Application(body: contract.CreateOAuth2ApplicationOptions): IO[GiteaError, contract.OAuth2Application]``

- `body` (body, required, `contract.CreateOAuth2ApplicationOptions`)
- HTTP 201: `#/responses/OAuth2Application`
- HTTP 400: `#/responses/error`

### `userGetOAuth2Application`

**GET `/user/applications/oauth2/{id}`** — ``client.users.userGetOAuth2Application(id: Long): IO[GiteaError, contract.OAuth2Application]``

- `id` (path, required, integer): Application ID to be found
- HTTP 200: `#/responses/OAuth2Application`
- HTTP 404: `#/responses/notFound`

### `userDeleteOAuth2Application`

**DELETE `/user/applications/oauth2/{id}`** — ``client.users.userDeleteOAuth2Application(id: Long): IO[GiteaError, Unit]``

- `id` (path, required, integer): token to be deleted
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `userUpdateOAuth2Application`

**PATCH `/user/applications/oauth2/{id}`** — ``client.users.userUpdateOAuth2Application(id: Long, body: contract.CreateOAuth2ApplicationOptions): IO[GiteaError, contract.OAuth2Application]``

- `id` (path, required, integer): application to be updated
- `body` (body, required, `contract.CreateOAuth2ApplicationOptions`)
- HTTP 200: `#/responses/OAuth2Application`
- HTTP 400: `#/responses/error`
- HTTP 404: `#/responses/notFound`

### `userUpdateAvatar`

**POST `/user/avatar`** — ``client.users.userUpdateAvatar(body: contract.UpdateUserAvatarOption): IO[GiteaError, Unit]``

- `body` (body, optional, `contract.UpdateUserAvatarOption`)
- HTTP 204: `#/responses/empty`

### `userDeleteAvatar`

**DELETE `/user/avatar`** — ``client.users.userDeleteAvatar(): IO[GiteaError, Unit]``

- HTTP 204: `#/responses/empty`

### `userListBlocks`

**GET `/user/blocks`** — ``client.users.userListBlocks(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/UserList`

### `userCheckUserBlock`

**GET `/user/blocks/{username}`** — ``client.users.userCheckUserBlock(username: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to check
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `userBlockUser`

**PUT `/user/blocks/{username}`** — ``client.users.userBlockUser(username: String, note: Option[String] = None): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to block
- `note` (query, optional, string): optional note for the block
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `userUnblockUser`

**DELETE `/user/blocks/{username}`** — ``client.users.userUnblockUser(username: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to unblock
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `userListEmails`

**GET `/user/emails`** — ``client.users.userListEmails(): IO[GiteaError, zio.Chunk[contract.Email]]``

- HTTP 200: `#/responses/EmailList`

### `userAddEmail`

**POST `/user/emails`** — ``client.users.userAddEmail(body: contract.CreateEmailOption): IO[GiteaError, zio.Chunk[contract.Email]]``

- `body` (body, optional, `contract.CreateEmailOption`)
- HTTP 201: `#/responses/EmailList`
- HTTP 422: `#/responses/validationError`

### `userDeleteEmail`

**DELETE `/user/emails`** — ``client.users.userDeleteEmail(body: contract.DeleteEmailOption): IO[GiteaError, Unit]``

- `body` (body, optional, `contract.DeleteEmailOption`)
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `userCurrentListFollowers`

**GET `/user/followers`** — ``client.users.userCurrentListFollowers(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/UserList`

### `userCurrentListFollowing`

**GET `/user/following`** — ``client.users.userCurrentListFollowing(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/UserList`

### `userCurrentCheckFollowing`

**GET `/user/following/{username}`** — ``client.users.userCurrentCheckFollowing(username: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to check for authenticated followers
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `userCurrentPutFollow`

**PUT `/user/following/{username}`** — ``client.users.userCurrentPutFollow(username: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to follow
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userCurrentDeleteFollow`

**DELETE `/user/following/{username}`** — ``client.users.userCurrentDeleteFollow(username: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user to unfollow
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `getVerificationToken`

**GET `/user/gpg_key_token`** — ``client.users.getVerificationToken(): IO[GiteaError, String]``

- HTTP 200: `#/responses/string`
- HTTP 404: `#/responses/notFound`

### `userVerifyGPGKey`

**POST `/user/gpg_key_verify`** — ``client.users.userVerifyGPGKey(): IO[GiteaError, contract.GPGKey]``

- HTTP 201: `#/responses/GPGKey`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `userCurrentListGPGKeys`

**GET `/user/gpg_keys`** — ``client.users.userCurrentListGPGKeys(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.GPGKey]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/GPGKeyList`

### `userCurrentPostGPGKey`

**POST `/user/gpg_keys`** — ``client.users.userCurrentPostGPGKey(body: contract.CreateGPGKeyOption): IO[GiteaError, contract.GPGKey]``

- `Form` (body, optional, `contract.CreateGPGKeyOption`)
- HTTP 201: `#/responses/GPGKey`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/validationError`

### `userCurrentGetGPGKey`

**GET `/user/gpg_keys/{id}`** — ``client.users.userCurrentGetGPGKey(id: Long): IO[GiteaError, contract.GPGKey]``

- `id` (path, required, integer): id of key to get
- HTTP 200: `#/responses/GPGKey`
- HTTP 404: `#/responses/notFound`

### `userCurrentDeleteGPGKey`

**DELETE `/user/gpg_keys/{id}`** — ``client.users.userCurrentDeleteGPGKey(id: Long): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of key to delete
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userListHooks`

**GET `/user/hooks`** — ``client.users.userListHooks(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/HookList`

### `userCreateHook`

**POST `/user/hooks`** — ``client.users.userCreateHook(body: contract.CreateHookOption): IO[GiteaError, contract.Hook]``

- `body` (body, required, `contract.CreateHookOption`)
- HTTP 201: `#/responses/Hook`

### `userGetHook`

**GET `/user/hooks/{id}`** — ``client.users.userGetHook(id: Long): IO[GiteaError, contract.Hook]``

- `id` (path, required, integer): id of the hook to get
- HTTP 200: `#/responses/Hook`

### `userDeleteHook`

**DELETE `/user/hooks/{id}`** — ``client.users.userDeleteHook(id: Long): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of the hook to delete
- HTTP 204: `#/responses/empty`

### `userEditHook`

**PATCH `/user/hooks/{id}`** — ``client.users.userEditHook(id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook]``

- `id` (path, required, integer): id of the hook to update
- `body` (body, optional, `contract.EditHookOption`)
- HTTP 200: `#/responses/Hook`

### `userCurrentListKeys`

**GET `/user/keys`** — ``client.users.userCurrentListKeys(fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.PublicKey]]``

- `fingerprint` (query, optional, string): fingerprint of the key
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/PublicKeyList`

### `userCurrentPostKey`

**POST `/user/keys`** — ``client.users.userCurrentPostKey(body: contract.CreateKeyOption): IO[GiteaError, contract.PublicKey]``

- `body` (body, optional, `contract.CreateKeyOption`)
- HTTP 201: `#/responses/PublicKey`
- HTTP 422: `#/responses/validationError`

### `userCurrentGetKey`

**GET `/user/keys/{id}`** — ``client.users.userCurrentGetKey(id: Long): IO[GiteaError, contract.PublicKey]``

- `id` (path, required, integer): id of key to get
- HTTP 200: `#/responses/PublicKey`
- HTTP 404: `#/responses/notFound`

### `userCurrentDeleteKey`

**DELETE `/user/keys/{id}`** — ``client.users.userCurrentDeleteKey(id: Long): IO[GiteaError, Unit]``

- `id` (path, required, integer): id of key to delete
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `orgListCurrentUserOrgs`

**GET `/user/orgs`** — ``client.users.orgListCurrentUserOrgs(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/OrganizationList`
- HTTP 404: `#/responses/notFound`

### `userCurrentListRepos`

**GET `/user/repos`** — ``client.users.userCurrentListRepos(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`

### `getUserSettings`

**GET `/user/settings`** — ``client.users.getUserSettings(): IO[GiteaError, contract.UserSettings]``

- HTTP 200: `#/responses/UserSettings`

### `updateUserSettings`

**PATCH `/user/settings`** — ``client.users.updateUserSettings(body: contract.UserSettingsOptions): IO[GiteaError, contract.UserSettings]``

- `body` (body, optional, `contract.UserSettingsOptions`)
- HTTP 200: `#/responses/UserSettings`

### `userCurrentListStarred`

**GET `/user/starred`** — ``client.users.userCurrentListStarred(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`
- HTTP 403: `#/responses/forbidden`

### `userCurrentCheckStarring`

**GET `/user/starred/{owner}/{repo}`** — ``client.users.userCurrentCheckStarring(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo
- `repo` (path, required, string): name of the repo
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userCurrentPutStar`

**PUT `/user/starred/{owner}/{repo}`** — ``client.users.userCurrentPutStar(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo to star
- `repo` (path, required, string): name of the repo to star
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userCurrentDeleteStar`

**DELETE `/user/starred/{owner}/{repo}`** — ``client.users.userCurrentDeleteStar(owner: String, repo: String): IO[GiteaError, Unit]``

- `owner` (path, required, string): owner of the repo to unstar
- `repo` (path, required, string): name of the repo to unstar
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userCurrentListSubscriptions`

**GET `/user/subscriptions`** — ``client.users.userCurrentListSubscriptions(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`

### `userListTeams`

**GET `/user/teams`** — ``client.users.userListTeams(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Team]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/TeamList`

### `userCurrentTrackedTimes`

**GET `/user/times`** — ``client.users.userCurrentTrackedTimes(page: Option[Int] = None, limit: Option[Int] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.TrackedTime]]``

- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- `since` (query, optional, timestamp): Only show times updated after the given time. This is a timestamp in RFC 3339 format
- `before` (query, optional, timestamp): Only show times updated before the given time. This is a timestamp in RFC 3339 format
- HTTP 200: `#/responses/TrackedTimeList`

### `userListActivityFeeds`

**GET `/users/{username}/activities/feeds`** — ``client.users.userListActivityFeeds(username: String, onlyPerformedBy: Option[Boolean] = None, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]]``

- `username` (path, required, string): username of the user whose activity feeds are to be listed
- `only-performed-by` (query, optional, boolean): if true, only show actions performed by the requested user
- `date` (query, optional, string): the date of the activities to be found
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/ActivityFeedsList`
- HTTP 404: `#/responses/notFound`

### `userCheckFollowing`

**GET `/users/{username}/following/{target}`** — ``client.users.userCheckFollowing(username: String, target: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the following user
- `target` (path, required, string): username of the followed user
- HTTP 204: `#/responses/empty`
- HTTP 404: `#/responses/notFound`

### `userListGPGKeys`

**GET `/users/{username}/gpg_keys`** — ``client.users.userListGPGKeys(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.GPGKey]]``

- `username` (path, required, string): username of the user whose GPG key list is to be obtained
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/GPGKeyList`
- HTTP 404: `#/responses/notFound`

### `userGetHeatmapData`

**GET `/users/{username}/heatmap`** — ``client.users.userGetHeatmapData(username: String): IO[GiteaError, zio.Chunk[contract.UserHeatmapData]]``

- `username` (path, required, string): username of the user whose heatmap is to be obtained
- HTTP 200: `#/responses/UserHeatmapData`
- HTTP 404: `#/responses/notFound`

### `userListKeys`

**GET `/users/{username}/keys`** — ``client.users.userListKeys(username: String, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.PublicKey]]``

- `username` (path, required, string): username of the user whose public keys are to be listed
- `fingerprint` (query, optional, string): fingerprint of the key
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/PublicKeyList`
- HTTP 404: `#/responses/notFound`

### `orgListUserOrgs`

**GET `/users/{username}/orgs`** — ``client.users.orgListUserOrgs(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]]``

- `username` (path, required, string): username of the user whose organizations are to be listed
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/OrganizationList`
- HTTP 404: `#/responses/notFound`

### `orgGetUserPermissions`

**GET `/users/{username}/orgs/{org}/permissions`** — ``client.users.orgGetUserPermissions(username: String, org: String): IO[GiteaError, contract.OrganizationPermissions]``

- `username` (path, required, string): username of the user whose permissions are to be obtained
- `org` (path, required, string): name of the organization
- HTTP 200: `#/responses/OrganizationPermissions`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userListStarred`

**GET `/users/{username}/starred`** — ``client.users.userListStarred(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `username` (path, required, string): username of the user whose starred repos are to be listed
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`

### `userListSubscriptions`

**GET `/users/{username}/subscriptions`** — ``client.users.userListSubscriptions(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]]``

- `username` (path, required, string): username of the user whose watched repos are to be listed
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/RepositoryList`
- HTTP 404: `#/responses/notFound`

### `userGetTokens`

**GET `/users/{username}/tokens`** — ``client.users.userGetTokens(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.AccessToken]]``

- `username` (path, required, string): username of to user whose access tokens are to be listed
- `page` (query, optional, integer): page number of results to return (1-based)
- `limit` (query, optional, integer): page size of results
- HTTP 200: `#/responses/AccessTokenList`
- HTTP 403: `#/responses/forbidden`

### `userCreateToken`

**POST `/users/{username}/tokens`** — ``client.users.userCreateToken(username: String, body: contract.CreateAccessTokenOption): IO[GiteaError, contract.AccessToken]``

- `username` (path, required, string): username of the user whose token is to be created
- `body` (body, optional, `contract.CreateAccessTokenOption`)
- HTTP 201: `#/responses/AccessToken`
- HTTP 400: `#/responses/error`
- HTTP 403: `#/responses/forbidden`

### `userDeleteAccessToken`

**DELETE `/users/{username}/tokens/{token}`** — ``client.users.userDeleteAccessToken(username: String, token: String): IO[GiteaError, Unit]``

- `username` (path, required, string): username of the user whose token is to be deleted
- `token` (path, required, string): token to be deleted, identified by ID and if not available by name
- HTTP 204: `#/responses/empty`
- HTTP 403: `#/responses/forbidden`
- HTTP 404: `#/responses/notFound`
- HTTP 422: `#/responses/error`
