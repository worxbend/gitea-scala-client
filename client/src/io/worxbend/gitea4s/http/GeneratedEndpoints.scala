package io.worxbend.gitea4s.http

object GeneratedEndpoints:
  val listAdminWorkflowJobs: GiteaEndpoint = GiteaEndpoint("GET", "/admin/actions/jobs", "listAdminWorkflowJobs", List(GiteaParameter("status", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("sort", "query", required = false), GiteaParameter("order", "query", required = false)), "#/responses/WorkflowJobsList")

  val getAdminRunners: GiteaEndpoint = GiteaEndpoint("GET", "/admin/actions/runners", "getAdminRunners", List(GiteaParameter("disabled", "query", required = false)), "#/responses/RunnerList")

  val adminCreateRunnerRegistrationToken: GiteaEndpoint = GiteaEndpoint("POST", "/admin/actions/runners/registration-token", "adminCreateRunnerRegistrationToken", List(), "#/responses/RegistrationToken")

  val getAdminRunner: GiteaEndpoint = GiteaEndpoint("GET", "/admin/actions/runners/{runner_id}", "getAdminRunner", List(GiteaParameter("runner_id", "path", required = true)), "#/responses/Runner")

  val deleteAdminRunner: GiteaEndpoint = GiteaEndpoint("DELETE", "/admin/actions/runners/{runner_id}", "deleteAdminRunner", List(GiteaParameter("runner_id", "path", required = true)), "description: runner has been deleted")

  val updateAdminRunner: GiteaEndpoint = GiteaEndpoint("PATCH", "/admin/actions/runners/{runner_id}", "updateAdminRunner", List(GiteaParameter("runner_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Runner")

  val listAdminWorkflowRuns: GiteaEndpoint = GiteaEndpoint("GET", "/admin/actions/runs", "listAdminWorkflowRuns", List(GiteaParameter("event", "query", required = false), GiteaParameter("branch", "query", required = false), GiteaParameter("status", "query", required = false), GiteaParameter("actor", "query", required = false), GiteaParameter("head_sha", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/WorkflowRunsList")

  val adminCronList: GiteaEndpoint = GiteaEndpoint("GET", "/admin/cron", "adminCronList", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/CronList")

  val adminCronRun: GiteaEndpoint = GiteaEndpoint("POST", "/admin/cron/{task}", "adminCronRun", List(GiteaParameter("task", "path", required = true)), "#/responses/empty")

  val adminGetAllEmails: GiteaEndpoint = GiteaEndpoint("GET", "/admin/emails", "adminGetAllEmails", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/EmailList")

  val adminSearchEmails: GiteaEndpoint = GiteaEndpoint("GET", "/admin/emails/search", "adminSearchEmails", List(GiteaParameter("q", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/EmailList")

  val adminListHooks: GiteaEndpoint = GiteaEndpoint("GET", "/admin/hooks", "adminListHooks", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("type", "query", required = false)), "#/responses/HookList")

  val adminCreateHook: GiteaEndpoint = GiteaEndpoint("POST", "/admin/hooks", "adminCreateHook", List(GiteaParameter("body", "body", required = true)), "#/responses/Hook")

  val adminGetHook: GiteaEndpoint = GiteaEndpoint("GET", "/admin/hooks/{id}", "adminGetHook", List(GiteaParameter("id", "path", required = true)), "#/responses/Hook")

  val adminDeleteHook: GiteaEndpoint = GiteaEndpoint("DELETE", "/admin/hooks/{id}", "adminDeleteHook", List(GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val adminEditHook: GiteaEndpoint = GiteaEndpoint("PATCH", "/admin/hooks/{id}", "adminEditHook", List(GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Hook")

  val adminGetAllOrgs: GiteaEndpoint = GiteaEndpoint("GET", "/admin/orgs", "adminGetAllOrgs", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/OrganizationList")

  val adminUnadoptedList: GiteaEndpoint = GiteaEndpoint("GET", "/admin/unadopted", "adminUnadoptedList", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("pattern", "query", required = false)), "#/responses/StringSlice")

  val adminAdoptRepository: GiteaEndpoint = GiteaEndpoint("POST", "/admin/unadopted/{owner}/{repo}", "adminAdoptRepository", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val adminDeleteUnadoptedRepository: GiteaEndpoint = GiteaEndpoint("DELETE", "/admin/unadopted/{owner}/{repo}", "adminDeleteUnadoptedRepository", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val adminSearchUsers: GiteaEndpoint = GiteaEndpoint("GET", "/admin/users", "adminSearchUsers", List(GiteaParameter("source_id", "query", required = false), GiteaParameter("login_name", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("sort", "query", required = false), GiteaParameter("order", "query", required = false), GiteaParameter("q", "query", required = false), GiteaParameter("visibility", "query", required = false), GiteaParameter("is_active", "query", required = false), GiteaParameter("is_admin", "query", required = false), GiteaParameter("is_restricted", "query", required = false), GiteaParameter("is_2fa_enabled", "query", required = false), GiteaParameter("is_prohibit_login", "query", required = false)), "#/responses/UserList")

  val adminCreateUser: GiteaEndpoint = GiteaEndpoint("POST", "/admin/users", "adminCreateUser", List(GiteaParameter("body", "body", required = false)), "#/responses/User")

  val adminDeleteUser: GiteaEndpoint = GiteaEndpoint("DELETE", "/admin/users/{username}", "adminDeleteUser", List(GiteaParameter("username", "path", required = true), GiteaParameter("purge", "query", required = false)), "#/responses/empty")

  val adminEditUser: GiteaEndpoint = GiteaEndpoint("PATCH", "/admin/users/{username}", "adminEditUser", List(GiteaParameter("username", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/User")

  val adminListUserBadges: GiteaEndpoint = GiteaEndpoint("GET", "/admin/users/{username}/badges", "adminListUserBadges", List(GiteaParameter("username", "path", required = true)), "#/responses/BadgeList")

  val adminAddUserBadges: GiteaEndpoint = GiteaEndpoint("POST", "/admin/users/{username}/badges", "adminAddUserBadges", List(GiteaParameter("username", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val adminDeleteUserBadges: GiteaEndpoint = GiteaEndpoint("DELETE", "/admin/users/{username}/badges", "adminDeleteUserBadges", List(GiteaParameter("username", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val adminCreatePublicKey: GiteaEndpoint = GiteaEndpoint("POST", "/admin/users/{username}/keys", "adminCreatePublicKey", List(GiteaParameter("username", "path", required = true), GiteaParameter("key", "body", required = false)), "#/responses/PublicKey")

  val adminDeleteUserPublicKey: GiteaEndpoint = GiteaEndpoint("DELETE", "/admin/users/{username}/keys/{id}", "adminDeleteUserPublicKey", List(GiteaParameter("username", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val adminCreateOrg: GiteaEndpoint = GiteaEndpoint("POST", "/admin/users/{username}/orgs", "adminCreateOrg", List(GiteaParameter("username", "path", required = true), GiteaParameter("organization", "body", required = true)), "#/responses/Organization")

  val adminRenameUser: GiteaEndpoint = GiteaEndpoint("POST", "/admin/users/{username}/rename", "adminRenameUser", List(GiteaParameter("username", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/empty")

  val adminCreateRepo: GiteaEndpoint = GiteaEndpoint("POST", "/admin/users/{username}/repos", "adminCreateRepo", List(GiteaParameter("username", "path", required = true), GiteaParameter("repository", "body", required = true)), "#/responses/Repository")

  val listGitignoresTemplates: GiteaEndpoint = GiteaEndpoint("GET", "/gitignore/templates", "listGitignoresTemplates", List(), "#/responses/GitignoreTemplateList")

  val getGitignoreTemplateInfo: GiteaEndpoint = GiteaEndpoint("GET", "/gitignore/templates/{name}", "getGitignoreTemplateInfo", List(GiteaParameter("name", "path", required = true)), "#/responses/GitignoreTemplateInfo")

  val listLabelTemplates: GiteaEndpoint = GiteaEndpoint("GET", "/label/templates", "listLabelTemplates", List(), "#/responses/LabelTemplateList")

  val getLabelTemplateInfo: GiteaEndpoint = GiteaEndpoint("GET", "/label/templates/{name}", "getLabelTemplateInfo", List(GiteaParameter("name", "path", required = true)), "#/responses/LabelTemplateInfo")

  val listLicenseTemplates: GiteaEndpoint = GiteaEndpoint("GET", "/licenses", "listLicenseTemplates", List(), "#/responses/LicenseTemplateList")

  val getLicenseTemplateInfo: GiteaEndpoint = GiteaEndpoint("GET", "/licenses/{name}", "getLicenseTemplateInfo", List(GiteaParameter("name", "path", required = true)), "#/responses/LicenseTemplateInfo")

  val renderMarkdown: GiteaEndpoint = GiteaEndpoint("POST", "/markdown", "renderMarkdown", List(GiteaParameter("body", "body", required = false)), "#/responses/MarkdownRender")

  val renderMarkdownRaw: GiteaEndpoint = GiteaEndpoint("POST", "/markdown/raw", "renderMarkdownRaw", List(GiteaParameter("body", "body", required = true)), "#/responses/MarkdownRender")

  val renderMarkup: GiteaEndpoint = GiteaEndpoint("POST", "/markup", "renderMarkup", List(GiteaParameter("body", "body", required = false)), "#/responses/MarkupRender")

  val notifyReadList: GiteaEndpoint = GiteaEndpoint("PUT", "/notifications", "notifyReadList", List(GiteaParameter("last_read_at", "query", required = false), GiteaParameter("all", "query", required = false), GiteaParameter("status-types", "query", required = false), GiteaParameter("to-status", "query", required = false)), "#/responses/NotificationThreadList")

  val notifyReadThread: GiteaEndpoint = GiteaEndpoint("PATCH", "/notifications/threads/{id}", "notifyReadThread", List(GiteaParameter("id", "path", required = true), GiteaParameter("to-status", "query", required = false)), "#/responses/NotificationThread")

  val orgGetAll: GiteaEndpoint = GiteaEndpoint("GET", "/orgs", "orgGetAll", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/OrganizationList")

  val orgCreate: GiteaEndpoint = GiteaEndpoint("POST", "/orgs", "orgCreate", List(GiteaParameter("organization", "body", required = true)), "#/responses/Organization")

  val orgDelete: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}", "orgDelete", List(GiteaParameter("org", "path", required = true)), "#/responses/empty")

  val orgEdit: GiteaEndpoint = GiteaEndpoint("PATCH", "/orgs/{org}", "orgEdit", List(GiteaParameter("org", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/Organization")

  val getOrgWorkflowJobs: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/jobs", "getOrgWorkflowJobs", List(GiteaParameter("org", "path", required = true), GiteaParameter("status", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/WorkflowJobsList")

  val getOrgRunners: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/runners", "getOrgRunners", List(GiteaParameter("org", "path", required = true), GiteaParameter("disabled", "query", required = false)), "#/responses/RunnerList")

  val orgCreateRunnerRegistrationToken: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/actions/runners/registration-token", "orgCreateRunnerRegistrationToken", List(GiteaParameter("org", "path", required = true)), "#/responses/RegistrationToken")

  val getOrgRunner: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/runners/{runner_id}", "getOrgRunner", List(GiteaParameter("org", "path", required = true), GiteaParameter("runner_id", "path", required = true)), "#/responses/Runner")

  val deleteOrgRunner: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/actions/runners/{runner_id}", "deleteOrgRunner", List(GiteaParameter("org", "path", required = true), GiteaParameter("runner_id", "path", required = true)), "description: runner has been deleted")

  val updateOrgRunner: GiteaEndpoint = GiteaEndpoint("PATCH", "/orgs/{org}/actions/runners/{runner_id}", "updateOrgRunner", List(GiteaParameter("org", "path", required = true), GiteaParameter("runner_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Runner")

  val getOrgWorkflowRuns: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/runs", "getOrgWorkflowRuns", List(GiteaParameter("org", "path", required = true), GiteaParameter("event", "query", required = false), GiteaParameter("branch", "query", required = false), GiteaParameter("status", "query", required = false), GiteaParameter("actor", "query", required = false), GiteaParameter("head_sha", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/WorkflowRunsList")

  val orgListActionsSecrets: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/secrets", "orgListActionsSecrets", List(GiteaParameter("org", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/SecretList")

  val updateOrgSecret: GiteaEndpoint = GiteaEndpoint("PUT", "/orgs/{org}/actions/secrets/{secretname}", "updateOrgSecret", List(GiteaParameter("org", "path", required = true), GiteaParameter("secretname", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when creating a secret")

  val deleteOrgSecret: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/actions/secrets/{secretname}", "deleteOrgSecret", List(GiteaParameter("org", "path", required = true), GiteaParameter("secretname", "path", required = true)), "description: delete one secret of the organization")

  val getOrgVariablesList: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/variables", "getOrgVariablesList", List(GiteaParameter("org", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/VariableList")

  val getOrgVariable: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/actions/variables/{variablename}", "getOrgVariable", List(GiteaParameter("org", "path", required = true), GiteaParameter("variablename", "path", required = true)), "#/responses/ActionVariable")

  val updateOrgVariable: GiteaEndpoint = GiteaEndpoint("PUT", "/orgs/{org}/actions/variables/{variablename}", "updateOrgVariable", List(GiteaParameter("org", "path", required = true), GiteaParameter("variablename", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when updating an org-level variable")

  val createOrgVariable: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/actions/variables/{variablename}", "createOrgVariable", List(GiteaParameter("org", "path", required = true), GiteaParameter("variablename", "path", required = true), GiteaParameter("body", "body", required = false)), "description: successfully created the org-level variable")

  val deleteOrgVariable: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/actions/variables/{variablename}", "deleteOrgVariable", List(GiteaParameter("org", "path", required = true), GiteaParameter("variablename", "path", required = true)), "#/responses/ActionVariable")

  val orgListActivityFeeds: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/activities/feeds", "orgListActivityFeeds", List(GiteaParameter("org", "path", required = true), GiteaParameter("date", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/ActivityFeedsList")

  val orgUpdateAvatar: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/avatar", "orgUpdateAvatar", List(GiteaParameter("org", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val orgDeleteAvatar: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/avatar", "orgDeleteAvatar", List(GiteaParameter("org", "path", required = true)), "#/responses/empty")

  val organizationListBlocks: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/blocks", "organizationListBlocks", List(GiteaParameter("org", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/UserList")

  val organizationCheckUserBlock: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/blocks/{username}", "organizationCheckUserBlock", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val organizationBlockUser: GiteaEndpoint = GiteaEndpoint("PUT", "/orgs/{org}/blocks/{username}", "organizationBlockUser", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true), GiteaParameter("note", "query", required = false)), "#/responses/empty")

  val organizationUnblockUser: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/blocks/{username}", "organizationUnblockUser", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val orgListHooks: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/hooks", "orgListHooks", List(GiteaParameter("org", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/HookList")

  val orgCreateHook: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/hooks", "orgCreateHook", List(GiteaParameter("org", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/Hook")

  val orgGetHook: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/hooks/{id}", "orgGetHook", List(GiteaParameter("org", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/Hook")

  val orgDeleteHook: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/hooks/{id}", "orgDeleteHook", List(GiteaParameter("org", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val orgEditHook: GiteaEndpoint = GiteaEndpoint("PATCH", "/orgs/{org}/hooks/{id}", "orgEditHook", List(GiteaParameter("org", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Hook")

  val orgListLabels: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/labels", "orgListLabels", List(GiteaParameter("org", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/LabelList")

  val orgCreateLabel: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/labels", "orgCreateLabel", List(GiteaParameter("org", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Label")

  val orgGetLabel: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/labels/{id}", "orgGetLabel", List(GiteaParameter("org", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/Label")

  val orgDeleteLabel: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/labels/{id}", "orgDeleteLabel", List(GiteaParameter("org", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val orgEditLabel: GiteaEndpoint = GiteaEndpoint("PATCH", "/orgs/{org}/labels/{id}", "orgEditLabel", List(GiteaParameter("org", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Label")

  val orgIsMember: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/members/{username}", "orgIsMember", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "description: user is a member")

  val orgDeleteMember: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/members/{username}", "orgDeleteMember", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "description: member removed")

  val orgIsPublicMember: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/public_members/{username}", "orgIsPublicMember", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "description: user is a public member")

  val orgPublicizeMember: GiteaEndpoint = GiteaEndpoint("PUT", "/orgs/{org}/public_members/{username}", "orgPublicizeMember", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "description: membership publicized")

  val orgConcealMember: GiteaEndpoint = GiteaEndpoint("DELETE", "/orgs/{org}/public_members/{username}", "orgConcealMember", List(GiteaParameter("org", "path", required = true), GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val renameOrg: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/rename", "renameOrg", List(GiteaParameter("org", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/empty")

  val orgListTeams: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/teams", "orgListTeams", List(GiteaParameter("org", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/TeamList")

  val orgCreateTeam: GiteaEndpoint = GiteaEndpoint("POST", "/orgs/{org}/teams", "orgCreateTeam", List(GiteaParameter("org", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Team")

  val teamSearch: GiteaEndpoint = GiteaEndpoint("GET", "/orgs/{org}/teams/search", "teamSearch", List(GiteaParameter("org", "path", required = true), GiteaParameter("q", "query", required = false), GiteaParameter("include_desc", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "type:object")

  val listPackages: GiteaEndpoint = GiteaEndpoint("GET", "/packages/{owner}", "listPackages", List(GiteaParameter("owner", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("type", "query", required = false), GiteaParameter("q", "query", required = false)), "#/responses/PackageList")

  val listPackageVersions: GiteaEndpoint = GiteaEndpoint("GET", "/packages/{owner}/{type}/{name}", "listPackageVersions", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/PackageList")

  val deletePackage: GiteaEndpoint = GiteaEndpoint("DELETE", "/packages/{owner}/{type}/{name}", "deletePackage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true)), "#/responses/empty")

  val getLatestPackageVersion: GiteaEndpoint = GiteaEndpoint("GET", "/packages/{owner}/{type}/{name}/-/latest", "getLatestPackageVersion", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true)), "#/responses/Package")

  val linkPackage: GiteaEndpoint = GiteaEndpoint("POST", "/packages/{owner}/{type}/{name}/-/link/{repo_name}", "linkPackage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true), GiteaParameter("repo_name", "path", required = true)), "#/responses/empty")

  val unlinkPackage: GiteaEndpoint = GiteaEndpoint("POST", "/packages/{owner}/{type}/{name}/-/unlink", "unlinkPackage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true)), "#/responses/empty")

  val getPackage: GiteaEndpoint = GiteaEndpoint("GET", "/packages/{owner}/{type}/{name}/{version}", "getPackage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true), GiteaParameter("version", "path", required = true)), "#/responses/Package")

  val deletePackageVersion: GiteaEndpoint = GiteaEndpoint("DELETE", "/packages/{owner}/{type}/{name}/{version}", "deletePackageVersion", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true), GiteaParameter("version", "path", required = true)), "#/responses/empty")

  val listPackageFiles: GiteaEndpoint = GiteaEndpoint("GET", "/packages/{owner}/{type}/{name}/{version}/files", "listPackageFiles", List(GiteaParameter("owner", "path", required = true), GiteaParameter("type", "path", required = true), GiteaParameter("name", "path", required = true), GiteaParameter("version", "path", required = true)), "#/responses/PackageFileList")

  val issueSearchIssues: GiteaEndpoint = GiteaEndpoint("GET", "/repos/issues/search", "issueSearchIssues", List(GiteaParameter("state", "query", required = false), GiteaParameter("labels", "query", required = false), GiteaParameter("milestones", "query", required = false), GiteaParameter("q", "query", required = false), GiteaParameter("type", "query", required = false), GiteaParameter("since", "query", required = false), GiteaParameter("before", "query", required = false), GiteaParameter("assigned", "query", required = false), GiteaParameter("created", "query", required = false), GiteaParameter("mentioned", "query", required = false), GiteaParameter("review_requested", "query", required = false), GiteaParameter("reviewed", "query", required = false), GiteaParameter("owner", "query", required = false), GiteaParameter("created_by", "query", required = false), GiteaParameter("team", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/IssueList")

  val repoMigrate: GiteaEndpoint = GiteaEndpoint("POST", "/repos/migrate", "repoMigrate", List(GiteaParameter("body", "body", required = false)), "#/responses/Repository")

  val repoSearch: GiteaEndpoint = GiteaEndpoint("GET", "/repos/search", "repoSearch", List(GiteaParameter("q", "query", required = false), GiteaParameter("topic", "query", required = false), GiteaParameter("includeDesc", "query", required = false), GiteaParameter("uid", "query", required = false), GiteaParameter("priority_owner_id", "query", required = false), GiteaParameter("team_id", "query", required = false), GiteaParameter("starredBy", "query", required = false), GiteaParameter("private", "query", required = false), GiteaParameter("is_private", "query", required = false), GiteaParameter("template", "query", required = false), GiteaParameter("archived", "query", required = false), GiteaParameter("mode", "query", required = false), GiteaParameter("exclusive", "query", required = false), GiteaParameter("sort", "query", required = false), GiteaParameter("order", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/SearchResults")

  val getArtifacts: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/artifacts", "getArtifacts", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("name", "query", required = false)), "#/responses/ArtifactsList")

  val getArtifact: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/artifacts/{artifact_id}", "getArtifact", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("artifact_id", "path", required = true)), "#/responses/Artifact")

  val deleteArtifact: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/actions/artifacts/{artifact_id}", "deleteArtifact", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("artifact_id", "path", required = true)), "description: No Content")

  val listWorkflowJobs: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/jobs", "listWorkflowJobs", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("status", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("sort", "query", required = false), GiteaParameter("order", "query", required = false)), "#/responses/WorkflowJobsList")

  val getWorkflowJob: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/jobs/{job_id}", "getWorkflowJob", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("job_id", "path", required = true)), "#/responses/WorkflowJob")

  val downloadActionsRunJobLogs: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/jobs/{job_id}/logs", "downloadActionsRunJobLogs", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("job_id", "path", required = true)), "description: output blob content")

  val getRepoRunners: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/runners", "getRepoRunners", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("disabled", "query", required = false)), "#/responses/RunnerList")

  val repoCreateRunnerRegistrationToken: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/actions/runners/registration-token", "repoCreateRunnerRegistrationToken", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/RegistrationToken")

  val getRepoRunner: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/runners/{runner_id}", "getRepoRunner", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("runner_id", "path", required = true)), "#/responses/Runner")

  val deleteRepoRunner: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/actions/runners/{runner_id}", "deleteRepoRunner", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("runner_id", "path", required = true)), "description: runner has been deleted")

  val updateRepoRunner: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/actions/runners/{runner_id}", "updateRepoRunner", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("runner_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Runner")

  val getWorkflowRuns: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/runs", "getWorkflowRuns", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("event", "query", required = false), GiteaParameter("branch", "query", required = false), GiteaParameter("status", "query", required = false), GiteaParameter("actor", "query", required = false), GiteaParameter("head_sha", "query", required = false), GiteaParameter("exclude_pull_requests", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/WorkflowRunsList")

  val GetWorkflowRun: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/runs/{run}", "GetWorkflowRun", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true)), "#/responses/WorkflowRun")

  val deleteActionRun: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/actions/runs/{run}", "deleteActionRun", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true)), "description: No Content")

  val getArtifactsOfRun: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/runs/{run}/artifacts", "getArtifactsOfRun", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true), GiteaParameter("name", "query", required = false)), "#/responses/ArtifactsList")

  val listWorkflowRunJobs: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/runs/{run}/jobs", "listWorkflowRunJobs", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true), GiteaParameter("status", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("sort", "query", required = false), GiteaParameter("order", "query", required = false)), "#/responses/WorkflowJobsList")

  val rerunWorkflowJob: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/actions/runs/{run}/jobs/{job_id}/rerun", "rerunWorkflowJob", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true), GiteaParameter("job_id", "path", required = true)), "#/responses/WorkflowJob")

  val rerunWorkflowRun: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/actions/runs/{run}/rerun", "rerunWorkflowRun", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true)), "#/responses/WorkflowRun")

  val rerunFailedWorkflowRun: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/actions/runs/{run}/rerun-failed-jobs", "rerunFailedWorkflowRun", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("run", "path", required = true)), "#/responses/empty")

  val repoListActionsSecrets: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/secrets", "repoListActionsSecrets", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/SecretList")

  val updateRepoSecret: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/actions/secrets/{secretname}", "updateRepoSecret", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("secretname", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when creating a secret")

  val deleteRepoSecret: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/actions/secrets/{secretname}", "deleteRepoSecret", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("secretname", "path", required = true)), "description: delete one secret of the repository")

  val ListActionTasks: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/tasks", "ListActionTasks", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/TasksList")

  val getRepoVariablesList: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/variables", "getRepoVariablesList", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/VariableList")

  val getRepoVariable: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/variables/{variablename}", "getRepoVariable", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("variablename", "path", required = true)), "#/responses/ActionVariable")

  val updateRepoVariable: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/actions/variables/{variablename}", "updateRepoVariable", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("variablename", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when updating a repo-level variable")

  val createRepoVariable: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/actions/variables/{variablename}", "createRepoVariable", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("variablename", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when creating a repo-level variable")

  val deleteRepoVariable: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/actions/variables/{variablename}", "deleteRepoVariable", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("variablename", "path", required = true)), "#/responses/ActionVariable")

  val ActionsListRepositoryWorkflows: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/workflows", "ActionsListRepositoryWorkflows", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/ActionWorkflowList")

  val ActionsGetWorkflow: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/actions/workflows/{workflow_id}", "ActionsGetWorkflow", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("workflow_id", "path", required = true)), "#/responses/ActionWorkflow")

  val ActionsDisableWorkflow: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/actions/workflows/{workflow_id}/disable", "ActionsDisableWorkflow", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("workflow_id", "path", required = true)), "description: No Content")

  val ActionsDispatchWorkflow: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/actions/workflows/{workflow_id}/dispatches", "ActionsDispatchWorkflow", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("workflow_id", "path", required = true), GiteaParameter("body", "body", required = false), GiteaParameter("return_run_details", "query", required = false), GiteaParameter("scoped_workflow_source_repo_id", "query", required = false)), "#/responses/RunDetails")

  val ActionsEnableWorkflow: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/actions/workflows/{workflow_id}/enable", "ActionsEnableWorkflow", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("workflow_id", "path", required = true)), "description: No Content")

  val repoListActivityFeeds: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/activities/feeds", "repoListActivityFeeds", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("date", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/ActivityFeedsList")

  val repoUpdateAvatar: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/avatar", "repoUpdateAvatar", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val repoDeleteAvatar: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/avatar", "repoDeleteAvatar", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val repoAddCollaborator: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/collaborators/{collaborator}", "repoAddCollaborator", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("collaborator", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val repoDeleteCollaborator: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/collaborators/{collaborator}", "repoDeleteCollaborator", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("collaborator", "path", required = true)), "#/responses/empty")

  val repoGetAllCommits: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/commits", "repoGetAllCommits", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("sha", "query", required = false), GiteaParameter("path", "query", required = false), GiteaParameter("since", "query", required = false), GiteaParameter("until", "query", required = false), GiteaParameter("stat", "query", required = false), GiteaParameter("verification", "query", required = false), GiteaParameter("files", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("not", "query", required = false)), "#/responses/CommitList")

  val repoCompareDiff: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/compare/{basehead}", "repoCompareDiff", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("basehead", "path", required = true), GiteaParameter("output", "query", required = false)), "#/responses/Compare")

  val repoChangeFiles: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/contents", "repoChangeFiles", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/FilesResponse")

  val repoGetContentsExt: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/contents-ext/{filepath}", "repoGetContentsExt", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("filepath", "path", required = true), GiteaParameter("ref", "query", required = false), GiteaParameter("includes", "query", required = false)), "#/responses/ContentsExtResponse")

  val repoUpdateFile: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/contents/{filepath}", "repoUpdateFile", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("filepath", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/FileResponse")

  val repoCreateFile: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/contents/{filepath}", "repoCreateFile", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("filepath", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/FileResponse")

  val repoDeleteFile: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/contents/{filepath}", "repoDeleteFile", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("filepath", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/FileDeleteResponse")

  val repoApplyDiffPatch: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/diffpatch", "repoApplyDiffPatch", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/FileResponse")

  val repoGetEditorConfig: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/editorconfig/{filepath}", "repoGetEditorConfig", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("filepath", "path", required = true), GiteaParameter("ref", "query", required = false)), "description: success")

  val repoGetFileContents: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/file-contents", "repoGetFileContents", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("ref", "query", required = false), GiteaParameter("body", "query", required = true)), "#/responses/ContentsListResponse")

  val repoGetFileContentsPost: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/file-contents", "repoGetFileContentsPost", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("ref", "query", required = false), GiteaParameter("body", "body", required = true)), "#/responses/ContentsListResponse")

  val listForks: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/forks", "listForks", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val repoListHooks: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/hooks", "repoListHooks", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/HookList")

  val repoCreateHook: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/hooks", "repoCreateHook", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Hook")

  val repoGetHook: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/hooks/{id}", "repoGetHook", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/Hook")

  val repoDeleteHook: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/hooks/{id}", "repoDeleteHook", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val repoEditHook: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/hooks/{id}", "repoEditHook", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Hook")

  val repoTestHook: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/hooks/{id}/tests", "repoTestHook", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("ref", "query", required = false)), "#/responses/empty")

  val repoGetIssueConfig: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issue_config", "repoGetIssueConfig", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/RepoIssueConfig")

  val repoValidateIssueConfig: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issue_config/validate", "repoValidateIssueConfig", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/RepoIssueConfigValidation")

  val repoGetIssueTemplates: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issue_templates", "repoGetIssueTemplates", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/IssueTemplates")

  val issueListIssueCommentAttachments: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issues/comments/{id}/assets", "issueListIssueCommentAttachments", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/AttachmentList")

  val issueGetIssueCommentAttachment: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issues/comments/{id}/assets/{attachment_id}", "issueGetIssueCommentAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("attachment_id", "path", required = true)), "#/responses/Attachment")

  val issueDeleteIssueCommentAttachment: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/issues/comments/{id}/assets/{attachment_id}", "issueDeleteIssueCommentAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("attachment_id", "path", required = true)), "#/responses/empty")

  val issueEditIssueCommentAttachment: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/issues/comments/{id}/assets/{attachment_id}", "issueEditIssueCommentAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("attachment_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Attachment")

  val issueListIssueAttachments: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issues/{index}/assets", "issueListIssueAttachments", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true)), "#/responses/AttachmentList")

  val issueGetIssueAttachment: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issues/{index}/assets/{attachment_id}", "issueGetIssueAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true), GiteaParameter("attachment_id", "path", required = true)), "#/responses/Attachment")

  val issueDeleteIssueAttachment: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/issues/{index}/assets/{attachment_id}", "issueDeleteIssueAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true), GiteaParameter("attachment_id", "path", required = true)), "#/responses/empty")

  val issueEditIssueAttachment: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/issues/{index}/assets/{attachment_id}", "issueEditIssueAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true), GiteaParameter("attachment_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Attachment")

  val issueDeleteCommentDeprecated: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/issues/{index}/comments/{id}", "issueDeleteCommentDeprecated", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val issueEditCommentDeprecated: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/issues/{index}/comments/{id}", "issueEditCommentDeprecated", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Comment")

  val issueGetCommentsAndTimeline: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/issues/{index}/timeline", "issueGetCommentsAndTimeline", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("index", "path", required = true), GiteaParameter("since", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("before", "query", required = false)), "#/responses/TimelineList")

  val repoListKeys: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/keys", "repoListKeys", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("key_id", "query", required = false), GiteaParameter("fingerprint", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/DeployKeyList")

  val repoCreateKey: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/keys", "repoCreateKey", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/DeployKey")

  val repoGetKey: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/keys/{id}", "repoGetKey", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/DeployKey")

  val repoDeleteKey: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/keys/{id}", "repoDeleteKey", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val issueListLabels: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/labels", "issueListLabels", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/LabelList")

  val issueCreateLabel: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/labels", "issueCreateLabel", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Label")

  val issueGetLabel: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/labels/{id}", "issueGetLabel", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/Label")

  val issueDeleteLabel: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/labels/{id}", "issueDeleteLabel", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val issueEditLabel: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/labels/{id}", "issueEditLabel", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Label")

  val repoGetLicenses: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/licenses", "repoGetLicenses", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/LicensesList")

  val repoMergeUpstream: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/merge-upstream", "repoMergeUpstream", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/MergeUpstreamResponse")

  val issueGetMilestonesList: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/milestones", "issueGetMilestonesList", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("state", "query", required = false), GiteaParameter("name", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/MilestoneList")

  val issueCreateMilestone: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/milestones", "issueCreateMilestone", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Milestone")

  val issueGetMilestone: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/milestones/{id}", "issueGetMilestone", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/Milestone")

  val issueDeleteMilestone: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/milestones/{id}", "issueDeleteMilestone", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val issueEditMilestone: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/milestones/{id}", "issueEditMilestone", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Milestone")

  val repoMirrorSync: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/mirror-sync", "repoMirrorSync", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val notifyGetRepoList: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/notifications", "notifyGetRepoList", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("all", "query", required = false), GiteaParameter("status-types", "query", required = false), GiteaParameter("subject-type", "query", required = false), GiteaParameter("since", "query", required = false), GiteaParameter("before", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/NotificationThreadList")

  val notifyReadRepoList: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/notifications", "notifyReadRepoList", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("all", "query", required = false), GiteaParameter("status-types", "query", required = false), GiteaParameter("to-status", "query", required = false), GiteaParameter("last_read_at", "query", required = false)), "#/responses/NotificationThreadList")

  val repoListPushMirrors: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/push_mirrors", "repoListPushMirrors", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/PushMirrorList")

  val repoAddPushMirror: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/push_mirrors", "repoAddPushMirror", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/PushMirror")

  val repoPushMirrorSync: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/push_mirrors-sync", "repoPushMirrorSync", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val repoGetPushMirrorByRemoteName: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/push_mirrors/{name}", "repoGetPushMirrorByRemoteName", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("name", "path", required = true)), "#/responses/PushMirror")

  val repoDeletePushMirror: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/push_mirrors/{name}", "repoDeletePushMirror", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("name", "path", required = true)), "#/responses/empty")

  val repoCreateRelease: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/releases", "repoCreateRelease", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Release")

  val repoDeleteReleaseByTag: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/releases/tags/{tag}", "repoDeleteReleaseByTag", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("tag", "path", required = true)), "#/responses/empty")

  val repoDeleteRelease: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/releases/{id}", "repoDeleteRelease", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val repoEditRelease: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/releases/{id}", "repoEditRelease", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Release")

  val repoDeleteReleaseAttachment: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}", "repoDeleteReleaseAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("attachment_id", "path", required = true)), "#/responses/empty")

  val repoEditReleaseAttachment: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}", "repoEditReleaseAttachment", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("id", "path", required = true), GiteaParameter("attachment_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Attachment")

  val repoSigningKeySSH: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/signing-key.pub", "repoSigningKeySSH", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "type:string")

  val userCurrentCheckSubscription: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/subscription", "userCurrentCheckSubscription", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/WatchInfo")

  val userCurrentPutSubscription: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/subscription", "userCurrentPutSubscription", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/WatchInfo")

  val userCurrentDeleteSubscription: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/subscription", "userCurrentDeleteSubscription", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val repoCreateTag: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/tags", "repoCreateTag", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Tag")

  val repoDeleteTag: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/tags/{tag}", "repoDeleteTag", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("tag", "path", required = true)), "#/responses/empty")

  val repoAddTeam: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/teams/{team}", "repoAddTeam", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("team", "path", required = true)), "#/responses/empty")

  val repoDeleteTeam: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/teams/{team}", "repoDeleteTeam", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("team", "path", required = true)), "#/responses/empty")

  val repoTrackedTimes: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/times", "repoTrackedTimes", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("user", "query", required = false), GiteaParameter("since", "query", required = false), GiteaParameter("before", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/TrackedTimeList")

  val userTrackedTimes: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/times/{user}", "userTrackedTimes", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("user", "path", required = true)), "#/responses/TrackedTimeList")

  val repoUpdateTopics: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/topics", "repoUpdateTopics", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val repoAddTopic: GiteaEndpoint = GiteaEndpoint("PUT", "/repos/{owner}/{repo}/topics/{topic}", "repoAddTopic", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("topic", "path", required = true)), "#/responses/empty")

  val repoDeleteTopic: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/topics/{topic}", "repoDeleteTopic", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("topic", "path", required = true)), "#/responses/empty")

  val repoCreateWikiPage: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{owner}/{repo}/wiki/new", "repoCreateWikiPage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/WikiPage")

  val repoGetWikiPage: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/wiki/page/{pageName}", "repoGetWikiPage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("pageName", "path", required = true)), "#/responses/WikiPage")

  val repoDeleteWikiPage: GiteaEndpoint = GiteaEndpoint("DELETE", "/repos/{owner}/{repo}/wiki/page/{pageName}", "repoDeleteWikiPage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("pageName", "path", required = true)), "#/responses/empty")

  val repoEditWikiPage: GiteaEndpoint = GiteaEndpoint("PATCH", "/repos/{owner}/{repo}/wiki/page/{pageName}", "repoEditWikiPage", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("pageName", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/WikiPage")

  val repoGetWikiPages: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/wiki/pages", "repoGetWikiPages", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/WikiPageList")

  val repoGetWikiPageRevisions: GiteaEndpoint = GiteaEndpoint("GET", "/repos/{owner}/{repo}/wiki/revisions/{pageName}", "repoGetWikiPageRevisions", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true), GiteaParameter("pageName", "path", required = true), GiteaParameter("page", "query", required = false)), "#/responses/WikiCommitList")

  val generateRepo: GiteaEndpoint = GiteaEndpoint("POST", "/repos/{template_owner}/{template_repo}/generate", "generateRepo", List(GiteaParameter("template_owner", "path", required = true), GiteaParameter("template_repo", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Repository")

  val repoGetByID: GiteaEndpoint = GiteaEndpoint("GET", "/repositories/{id}", "repoGetByID", List(GiteaParameter("id", "path", required = true)), "#/responses/Repository")

  val getSigningKey: GiteaEndpoint = GiteaEndpoint("GET", "/signing-key.gpg", "getSigningKey", List(), "type:string")

  val getSigningKeySSH: GiteaEndpoint = GiteaEndpoint("GET", "/signing-key.pub", "getSigningKeySSH", List(), "type:string")

  val orgGetTeam: GiteaEndpoint = GiteaEndpoint("GET", "/teams/{id}", "orgGetTeam", List(GiteaParameter("id", "path", required = true)), "#/responses/Team")

  val orgDeleteTeam: GiteaEndpoint = GiteaEndpoint("DELETE", "/teams/{id}", "orgDeleteTeam", List(GiteaParameter("id", "path", required = true)), "description: team deleted")

  val orgEditTeam: GiteaEndpoint = GiteaEndpoint("PATCH", "/teams/{id}", "orgEditTeam", List(GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Team")

  val orgListTeamActivityFeeds: GiteaEndpoint = GiteaEndpoint("GET", "/teams/{id}/activities/feeds", "orgListTeamActivityFeeds", List(GiteaParameter("id", "path", required = true), GiteaParameter("date", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/ActivityFeedsList")

  val orgListTeamMembers: GiteaEndpoint = GiteaEndpoint("GET", "/teams/{id}/members", "orgListTeamMembers", List(GiteaParameter("id", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/UserList")

  val orgListTeamMember: GiteaEndpoint = GiteaEndpoint("GET", "/teams/{id}/members/{username}", "orgListTeamMember", List(GiteaParameter("id", "path", required = true), GiteaParameter("username", "path", required = true)), "#/responses/User")

  val orgAddTeamMember: GiteaEndpoint = GiteaEndpoint("PUT", "/teams/{id}/members/{username}", "orgAddTeamMember", List(GiteaParameter("id", "path", required = true), GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val orgRemoveTeamMember: GiteaEndpoint = GiteaEndpoint("DELETE", "/teams/{id}/members/{username}", "orgRemoveTeamMember", List(GiteaParameter("id", "path", required = true), GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val orgListTeamRepos: GiteaEndpoint = GiteaEndpoint("GET", "/teams/{id}/repos", "orgListTeamRepos", List(GiteaParameter("id", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val orgListTeamRepo: GiteaEndpoint = GiteaEndpoint("GET", "/teams/{id}/repos/{org}/{repo}", "orgListTeamRepo", List(GiteaParameter("id", "path", required = true), GiteaParameter("org", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/Repository")

  val orgAddTeamRepository: GiteaEndpoint = GiteaEndpoint("PUT", "/teams/{id}/repos/{org}/{repo}", "orgAddTeamRepository", List(GiteaParameter("id", "path", required = true), GiteaParameter("org", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val orgRemoveTeamRepository: GiteaEndpoint = GiteaEndpoint("DELETE", "/teams/{id}/repos/{org}/{repo}", "orgRemoveTeamRepository", List(GiteaParameter("id", "path", required = true), GiteaParameter("org", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val topicSearch: GiteaEndpoint = GiteaEndpoint("GET", "/topics/search", "topicSearch", List(GiteaParameter("q", "query", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/TopicListResponse")

  val getUserWorkflowJobs: GiteaEndpoint = GiteaEndpoint("GET", "/user/actions/jobs", "getUserWorkflowJobs", List(GiteaParameter("status", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("sort", "query", required = false), GiteaParameter("order", "query", required = false)), "#/responses/WorkflowJobsList")

  val getUserRunners: GiteaEndpoint = GiteaEndpoint("GET", "/user/actions/runners", "getUserRunners", List(GiteaParameter("disabled", "query", required = false)), "#/responses/RunnerList")

  val userCreateRunnerRegistrationToken: GiteaEndpoint = GiteaEndpoint("POST", "/user/actions/runners/registration-token", "userCreateRunnerRegistrationToken", List(), "#/responses/RegistrationToken")

  val getUserRunner: GiteaEndpoint = GiteaEndpoint("GET", "/user/actions/runners/{runner_id}", "getUserRunner", List(GiteaParameter("runner_id", "path", required = true)), "#/responses/Runner")

  val deleteUserRunner: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/actions/runners/{runner_id}", "deleteUserRunner", List(GiteaParameter("runner_id", "path", required = true)), "description: runner has been deleted")

  val updateUserRunner: GiteaEndpoint = GiteaEndpoint("PATCH", "/user/actions/runners/{runner_id}", "updateUserRunner", List(GiteaParameter("runner_id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Runner")

  val getUserWorkflowRuns: GiteaEndpoint = GiteaEndpoint("GET", "/user/actions/runs", "getUserWorkflowRuns", List(GiteaParameter("event", "query", required = false), GiteaParameter("branch", "query", required = false), GiteaParameter("status", "query", required = false), GiteaParameter("actor", "query", required = false), GiteaParameter("head_sha", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/WorkflowRunsList")

  val updateUserSecret: GiteaEndpoint = GiteaEndpoint("PUT", "/user/actions/secrets/{secretname}", "updateUserSecret", List(GiteaParameter("secretname", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when creating a secret")

  val deleteUserSecret: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/actions/secrets/{secretname}", "deleteUserSecret", List(GiteaParameter("secretname", "path", required = true)), "description: delete one secret of the user")

  val getUserVariablesList: GiteaEndpoint = GiteaEndpoint("GET", "/user/actions/variables", "getUserVariablesList", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/VariableList")

  val getUserVariable: GiteaEndpoint = GiteaEndpoint("GET", "/user/actions/variables/{variablename}", "getUserVariable", List(GiteaParameter("variablename", "path", required = true)), "#/responses/ActionVariable")

  val updateUserVariable: GiteaEndpoint = GiteaEndpoint("PUT", "/user/actions/variables/{variablename}", "updateUserVariable", List(GiteaParameter("variablename", "path", required = true), GiteaParameter("body", "body", required = false)), "description: response when updating a variable")

  val createUserVariable: GiteaEndpoint = GiteaEndpoint("POST", "/user/actions/variables/{variablename}", "createUserVariable", List(GiteaParameter("variablename", "path", required = true), GiteaParameter("body", "body", required = false)), "description: successfully created the user-level variable")

  val deleteUserVariable: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/actions/variables/{variablename}", "deleteUserVariable", List(GiteaParameter("variablename", "path", required = true)), "description: response when deleting a variable")

  val userGetOauth2Application: GiteaEndpoint = GiteaEndpoint("GET", "/user/applications/oauth2", "userGetOauth2Application", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/OAuth2ApplicationList")

  val userCreateOAuth2Application: GiteaEndpoint = GiteaEndpoint("POST", "/user/applications/oauth2", "userCreateOAuth2Application", List(GiteaParameter("body", "body", required = true)), "#/responses/OAuth2Application")

  val userGetOAuth2Application: GiteaEndpoint = GiteaEndpoint("GET", "/user/applications/oauth2/{id}", "userGetOAuth2Application", List(GiteaParameter("id", "path", required = true)), "#/responses/OAuth2Application")

  val userDeleteOAuth2Application: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/applications/oauth2/{id}", "userDeleteOAuth2Application", List(GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val userUpdateOAuth2Application: GiteaEndpoint = GiteaEndpoint("PATCH", "/user/applications/oauth2/{id}", "userUpdateOAuth2Application", List(GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = true)), "#/responses/OAuth2Application")

  val userUpdateAvatar: GiteaEndpoint = GiteaEndpoint("POST", "/user/avatar", "userUpdateAvatar", List(GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val userDeleteAvatar: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/avatar", "userDeleteAvatar", List(), "#/responses/empty")

  val userListBlocks: GiteaEndpoint = GiteaEndpoint("GET", "/user/blocks", "userListBlocks", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/UserList")

  val userCheckUserBlock: GiteaEndpoint = GiteaEndpoint("GET", "/user/blocks/{username}", "userCheckUserBlock", List(GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val userBlockUser: GiteaEndpoint = GiteaEndpoint("PUT", "/user/blocks/{username}", "userBlockUser", List(GiteaParameter("username", "path", required = true), GiteaParameter("note", "query", required = false)), "#/responses/empty")

  val userUnblockUser: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/blocks/{username}", "userUnblockUser", List(GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val userListEmails: GiteaEndpoint = GiteaEndpoint("GET", "/user/emails", "userListEmails", List(), "#/responses/EmailList")

  val userAddEmail: GiteaEndpoint = GiteaEndpoint("POST", "/user/emails", "userAddEmail", List(GiteaParameter("body", "body", required = false)), "#/responses/EmailList")

  val userDeleteEmail: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/emails", "userDeleteEmail", List(GiteaParameter("body", "body", required = false)), "#/responses/empty")

  val userCurrentListFollowers: GiteaEndpoint = GiteaEndpoint("GET", "/user/followers", "userCurrentListFollowers", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/UserList")

  val userCurrentListFollowing: GiteaEndpoint = GiteaEndpoint("GET", "/user/following", "userCurrentListFollowing", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/UserList")

  val userCurrentCheckFollowing: GiteaEndpoint = GiteaEndpoint("GET", "/user/following/{username}", "userCurrentCheckFollowing", List(GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val userCurrentPutFollow: GiteaEndpoint = GiteaEndpoint("PUT", "/user/following/{username}", "userCurrentPutFollow", List(GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val userCurrentDeleteFollow: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/following/{username}", "userCurrentDeleteFollow", List(GiteaParameter("username", "path", required = true)), "#/responses/empty")

  val getVerificationToken: GiteaEndpoint = GiteaEndpoint("GET", "/user/gpg_key_token", "getVerificationToken", List(), "#/responses/string")

  val userVerifyGPGKey: GiteaEndpoint = GiteaEndpoint("POST", "/user/gpg_key_verify", "userVerifyGPGKey", List(), "#/responses/GPGKey")

  val userCurrentListGPGKeys: GiteaEndpoint = GiteaEndpoint("GET", "/user/gpg_keys", "userCurrentListGPGKeys", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/GPGKeyList")

  val userCurrentPostGPGKey: GiteaEndpoint = GiteaEndpoint("POST", "/user/gpg_keys", "userCurrentPostGPGKey", List(GiteaParameter("Form", "body", required = false)), "#/responses/GPGKey")

  val userCurrentGetGPGKey: GiteaEndpoint = GiteaEndpoint("GET", "/user/gpg_keys/{id}", "userCurrentGetGPGKey", List(GiteaParameter("id", "path", required = true)), "#/responses/GPGKey")

  val userCurrentDeleteGPGKey: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/gpg_keys/{id}", "userCurrentDeleteGPGKey", List(GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val userListHooks: GiteaEndpoint = GiteaEndpoint("GET", "/user/hooks", "userListHooks", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/HookList")

  val userCreateHook: GiteaEndpoint = GiteaEndpoint("POST", "/user/hooks", "userCreateHook", List(GiteaParameter("body", "body", required = true)), "#/responses/Hook")

  val userGetHook: GiteaEndpoint = GiteaEndpoint("GET", "/user/hooks/{id}", "userGetHook", List(GiteaParameter("id", "path", required = true)), "#/responses/Hook")

  val userDeleteHook: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/hooks/{id}", "userDeleteHook", List(GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val userEditHook: GiteaEndpoint = GiteaEndpoint("PATCH", "/user/hooks/{id}", "userEditHook", List(GiteaParameter("id", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/Hook")

  val userCurrentListKeys: GiteaEndpoint = GiteaEndpoint("GET", "/user/keys", "userCurrentListKeys", List(GiteaParameter("fingerprint", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/PublicKeyList")

  val userCurrentPostKey: GiteaEndpoint = GiteaEndpoint("POST", "/user/keys", "userCurrentPostKey", List(GiteaParameter("body", "body", required = false)), "#/responses/PublicKey")

  val userCurrentGetKey: GiteaEndpoint = GiteaEndpoint("GET", "/user/keys/{id}", "userCurrentGetKey", List(GiteaParameter("id", "path", required = true)), "#/responses/PublicKey")

  val userCurrentDeleteKey: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/keys/{id}", "userCurrentDeleteKey", List(GiteaParameter("id", "path", required = true)), "#/responses/empty")

  val orgListCurrentUserOrgs: GiteaEndpoint = GiteaEndpoint("GET", "/user/orgs", "orgListCurrentUserOrgs", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/OrganizationList")

  val userCurrentListRepos: GiteaEndpoint = GiteaEndpoint("GET", "/user/repos", "userCurrentListRepos", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val getUserSettings: GiteaEndpoint = GiteaEndpoint("GET", "/user/settings", "getUserSettings", List(), "#/responses/UserSettings")

  val updateUserSettings: GiteaEndpoint = GiteaEndpoint("PATCH", "/user/settings", "updateUserSettings", List(GiteaParameter("body", "body", required = false)), "#/responses/UserSettings")

  val userCurrentListStarred: GiteaEndpoint = GiteaEndpoint("GET", "/user/starred", "userCurrentListStarred", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val userCurrentCheckStarring: GiteaEndpoint = GiteaEndpoint("GET", "/user/starred/{owner}/{repo}", "userCurrentCheckStarring", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val userCurrentPutStar: GiteaEndpoint = GiteaEndpoint("PUT", "/user/starred/{owner}/{repo}", "userCurrentPutStar", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val userCurrentDeleteStar: GiteaEndpoint = GiteaEndpoint("DELETE", "/user/starred/{owner}/{repo}", "userCurrentDeleteStar", List(GiteaParameter("owner", "path", required = true), GiteaParameter("repo", "path", required = true)), "#/responses/empty")

  val userCurrentListSubscriptions: GiteaEndpoint = GiteaEndpoint("GET", "/user/subscriptions", "userCurrentListSubscriptions", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val userListTeams: GiteaEndpoint = GiteaEndpoint("GET", "/user/teams", "userListTeams", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/TeamList")

  val userCurrentTrackedTimes: GiteaEndpoint = GiteaEndpoint("GET", "/user/times", "userCurrentTrackedTimes", List(GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false), GiteaParameter("since", "query", required = false), GiteaParameter("before", "query", required = false)), "#/responses/TrackedTimeList")

  val userListActivityFeeds: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/activities/feeds", "userListActivityFeeds", List(GiteaParameter("username", "path", required = true), GiteaParameter("only-performed-by", "query", required = false), GiteaParameter("date", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/ActivityFeedsList")

  val userCheckFollowing: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/following/{target}", "userCheckFollowing", List(GiteaParameter("username", "path", required = true), GiteaParameter("target", "path", required = true)), "#/responses/empty")

  val userListGPGKeys: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/gpg_keys", "userListGPGKeys", List(GiteaParameter("username", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/GPGKeyList")

  val userGetHeatmapData: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/heatmap", "userGetHeatmapData", List(GiteaParameter("username", "path", required = true)), "#/responses/UserHeatmapData")

  val userListKeys: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/keys", "userListKeys", List(GiteaParameter("username", "path", required = true), GiteaParameter("fingerprint", "query", required = false), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/PublicKeyList")

  val orgListUserOrgs: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/orgs", "orgListUserOrgs", List(GiteaParameter("username", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/OrganizationList")

  val orgGetUserPermissions: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/orgs/{org}/permissions", "orgGetUserPermissions", List(GiteaParameter("username", "path", required = true), GiteaParameter("org", "path", required = true)), "#/responses/OrganizationPermissions")

  val userListStarred: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/starred", "userListStarred", List(GiteaParameter("username", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val userListSubscriptions: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/subscriptions", "userListSubscriptions", List(GiteaParameter("username", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/RepositoryList")

  val userGetTokens: GiteaEndpoint = GiteaEndpoint("GET", "/users/{username}/tokens", "userGetTokens", List(GiteaParameter("username", "path", required = true), GiteaParameter("page", "query", required = false), GiteaParameter("limit", "query", required = false)), "#/responses/AccessTokenList")

  val userCreateToken: GiteaEndpoint = GiteaEndpoint("POST", "/users/{username}/tokens", "userCreateToken", List(GiteaParameter("username", "path", required = true), GiteaParameter("body", "body", required = false)), "#/responses/AccessToken")

  val userDeleteAccessToken: GiteaEndpoint = GiteaEndpoint("DELETE", "/users/{username}/tokens/{token}", "userDeleteAccessToken", List(GiteaParameter("username", "path", required = true), GiteaParameter("token", "path", required = true)), "#/responses/empty")

  val getVersion: GiteaEndpoint = GiteaEndpoint("GET", "/version", "getVersion", List(), "#/responses/ServerVersion")

  val all: List[GiteaEndpoint] = List(
    listAdminWorkflowJobs,
    getAdminRunners,
    adminCreateRunnerRegistrationToken,
    getAdminRunner,
    deleteAdminRunner,
    updateAdminRunner,
    listAdminWorkflowRuns,
    adminCronList,
    adminCronRun,
    adminGetAllEmails,
    adminSearchEmails,
    adminListHooks,
    adminCreateHook,
    adminGetHook,
    adminDeleteHook,
    adminEditHook,
    adminGetAllOrgs,
    adminUnadoptedList,
    adminAdoptRepository,
    adminDeleteUnadoptedRepository,
    adminSearchUsers,
    adminCreateUser,
    adminDeleteUser,
    adminEditUser,
    adminListUserBadges,
    adminAddUserBadges,
    adminDeleteUserBadges,
    adminCreatePublicKey,
    adminDeleteUserPublicKey,
    adminCreateOrg,
    adminRenameUser,
    adminCreateRepo,
    listGitignoresTemplates,
    getGitignoreTemplateInfo,
    listLabelTemplates,
    getLabelTemplateInfo,
    listLicenseTemplates,
    getLicenseTemplateInfo,
    renderMarkdown,
    renderMarkdownRaw,
    renderMarkup,
    notifyReadList,
    notifyReadThread,
    orgGetAll,
    orgCreate,
    orgDelete,
    orgEdit,
    getOrgWorkflowJobs,
    getOrgRunners,
    orgCreateRunnerRegistrationToken,
    getOrgRunner,
    deleteOrgRunner,
    updateOrgRunner,
    getOrgWorkflowRuns,
    orgListActionsSecrets,
    updateOrgSecret,
    deleteOrgSecret,
    getOrgVariablesList,
    getOrgVariable,
    updateOrgVariable,
    createOrgVariable,
    deleteOrgVariable,
    orgListActivityFeeds,
    orgUpdateAvatar,
    orgDeleteAvatar,
    organizationListBlocks,
    organizationCheckUserBlock,
    organizationBlockUser,
    organizationUnblockUser,
    orgListHooks,
    orgCreateHook,
    orgGetHook,
    orgDeleteHook,
    orgEditHook,
    orgListLabels,
    orgCreateLabel,
    orgGetLabel,
    orgDeleteLabel,
    orgEditLabel,
    orgIsMember,
    orgDeleteMember,
    orgIsPublicMember,
    orgPublicizeMember,
    orgConcealMember,
    renameOrg,
    orgListTeams,
    orgCreateTeam,
    teamSearch,
    listPackages,
    listPackageVersions,
    deletePackage,
    getLatestPackageVersion,
    linkPackage,
    unlinkPackage,
    getPackage,
    deletePackageVersion,
    listPackageFiles,
    issueSearchIssues,
    repoMigrate,
    repoSearch,
    getArtifacts,
    getArtifact,
    deleteArtifact,
    listWorkflowJobs,
    getWorkflowJob,
    downloadActionsRunJobLogs,
    getRepoRunners,
    repoCreateRunnerRegistrationToken,
    getRepoRunner,
    deleteRepoRunner,
    updateRepoRunner,
    getWorkflowRuns,
    GetWorkflowRun,
    deleteActionRun,
    getArtifactsOfRun,
    listWorkflowRunJobs,
    rerunWorkflowJob,
    rerunWorkflowRun,
    rerunFailedWorkflowRun,
    repoListActionsSecrets,
    updateRepoSecret,
    deleteRepoSecret,
    ListActionTasks,
    getRepoVariablesList,
    getRepoVariable,
    updateRepoVariable,
    createRepoVariable,
    deleteRepoVariable,
    ActionsListRepositoryWorkflows,
    ActionsGetWorkflow,
    ActionsDisableWorkflow,
    ActionsDispatchWorkflow,
    ActionsEnableWorkflow,
    repoListActivityFeeds,
    repoUpdateAvatar,
    repoDeleteAvatar,
    repoAddCollaborator,
    repoDeleteCollaborator,
    repoGetAllCommits,
    repoCompareDiff,
    repoChangeFiles,
    repoGetContentsExt,
    repoUpdateFile,
    repoCreateFile,
    repoDeleteFile,
    repoApplyDiffPatch,
    repoGetEditorConfig,
    repoGetFileContents,
    repoGetFileContentsPost,
    listForks,
    repoListHooks,
    repoCreateHook,
    repoGetHook,
    repoDeleteHook,
    repoEditHook,
    repoTestHook,
    repoGetIssueConfig,
    repoValidateIssueConfig,
    repoGetIssueTemplates,
    issueListIssueCommentAttachments,
    issueGetIssueCommentAttachment,
    issueDeleteIssueCommentAttachment,
    issueEditIssueCommentAttachment,
    issueListIssueAttachments,
    issueGetIssueAttachment,
    issueDeleteIssueAttachment,
    issueEditIssueAttachment,
    issueDeleteCommentDeprecated,
    issueEditCommentDeprecated,
    issueGetCommentsAndTimeline,
    repoListKeys,
    repoCreateKey,
    repoGetKey,
    repoDeleteKey,
    issueListLabels,
    issueCreateLabel,
    issueGetLabel,
    issueDeleteLabel,
    issueEditLabel,
    repoGetLicenses,
    repoMergeUpstream,
    issueGetMilestonesList,
    issueCreateMilestone,
    issueGetMilestone,
    issueDeleteMilestone,
    issueEditMilestone,
    repoMirrorSync,
    notifyGetRepoList,
    notifyReadRepoList,
    repoListPushMirrors,
    repoAddPushMirror,
    repoPushMirrorSync,
    repoGetPushMirrorByRemoteName,
    repoDeletePushMirror,
    repoCreateRelease,
    repoDeleteReleaseByTag,
    repoDeleteRelease,
    repoEditRelease,
    repoDeleteReleaseAttachment,
    repoEditReleaseAttachment,
    repoSigningKeySSH,
    userCurrentCheckSubscription,
    userCurrentPutSubscription,
    userCurrentDeleteSubscription,
    repoCreateTag,
    repoDeleteTag,
    repoAddTeam,
    repoDeleteTeam,
    repoTrackedTimes,
    userTrackedTimes,
    repoUpdateTopics,
    repoAddTopic,
    repoDeleteTopic,
    repoCreateWikiPage,
    repoGetWikiPage,
    repoDeleteWikiPage,
    repoEditWikiPage,
    repoGetWikiPages,
    repoGetWikiPageRevisions,
    generateRepo,
    repoGetByID,
    getSigningKey,
    getSigningKeySSH,
    orgGetTeam,
    orgDeleteTeam,
    orgEditTeam,
    orgListTeamActivityFeeds,
    orgListTeamMembers,
    orgListTeamMember,
    orgAddTeamMember,
    orgRemoveTeamMember,
    orgListTeamRepos,
    orgListTeamRepo,
    orgAddTeamRepository,
    orgRemoveTeamRepository,
    topicSearch,
    getUserWorkflowJobs,
    getUserRunners,
    userCreateRunnerRegistrationToken,
    getUserRunner,
    deleteUserRunner,
    updateUserRunner,
    getUserWorkflowRuns,
    updateUserSecret,
    deleteUserSecret,
    getUserVariablesList,
    getUserVariable,
    updateUserVariable,
    createUserVariable,
    deleteUserVariable,
    userGetOauth2Application,
    userCreateOAuth2Application,
    userGetOAuth2Application,
    userDeleteOAuth2Application,
    userUpdateOAuth2Application,
    userUpdateAvatar,
    userDeleteAvatar,
    userListBlocks,
    userCheckUserBlock,
    userBlockUser,
    userUnblockUser,
    userListEmails,
    userAddEmail,
    userDeleteEmail,
    userCurrentListFollowers,
    userCurrentListFollowing,
    userCurrentCheckFollowing,
    userCurrentPutFollow,
    userCurrentDeleteFollow,
    getVerificationToken,
    userVerifyGPGKey,
    userCurrentListGPGKeys,
    userCurrentPostGPGKey,
    userCurrentGetGPGKey,
    userCurrentDeleteGPGKey,
    userListHooks,
    userCreateHook,
    userGetHook,
    userDeleteHook,
    userEditHook,
    userCurrentListKeys,
    userCurrentPostKey,
    userCurrentGetKey,
    userCurrentDeleteKey,
    orgListCurrentUserOrgs,
    userCurrentListRepos,
    getUserSettings,
    updateUserSettings,
    userCurrentListStarred,
    userCurrentCheckStarring,
    userCurrentPutStar,
    userCurrentDeleteStar,
    userCurrentListSubscriptions,
    userListTeams,
    userCurrentTrackedTimes,
    userListActivityFeeds,
    userCheckFollowing,
    userListGPGKeys,
    userGetHeatmapData,
    userListKeys,
    orgListUserOrgs,
    orgGetUserPermissions,
    userListStarred,
    userListSubscriptions,
    userGetTokens,
    userCreateToken,
    userDeleteAccessToken,
    getVersion
  )
