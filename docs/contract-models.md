# Gitea 1.27.3 wire models

Generated Scala models for every schema definition in `gitea-v1.27.3.yaml`.
Wire names are preserved with `@jsonField`; absent optional fields remain `None`.
Schema fields without a declared type use `zio.json.ast.Json` rather than guessing a shape.

## `APIError`

- `message` — string (optional)
- `url` — string (optional)

## `AccessToken`

- `created_at` — timestamp (optional)
- `id` — integer (optional)
- `last_used_at` — timestamp (optional)
- `name` — string (optional)
- `scopes` — list of string (optional)
- `sha1` — string (optional)
- `token_last_eight` — string (optional)

## `ActionArtifact`

- `archive_download_url` — string (optional)
- `created_at` — timestamp (optional)
- `expired` — boolean (optional)
- `expires_at` — timestamp (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `size_in_bytes` — integer (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)
- `workflow_run` — `contract.ActionWorkflowRun` (optional)

## `ActionArtifactsResponse`

- `artifacts` — list of `contract.ActionArtifact` (optional)
- `total_count` — integer (optional)

## `ActionRunner`

- `busy` — boolean (optional)
- `disabled` — boolean (optional)
- `ephemeral` — boolean (optional)
- `id` — integer (optional)
- `labels` — list of `contract.ActionRunnerLabel` (optional)
- `name` — string (optional)
- `status` — string (optional)

## `ActionRunnerLabel`

- `id` — integer (optional)
- `name` — string (optional)
- `type` — string (optional)

## `ActionRunnersResponse`

- `runners` — list of `contract.ActionRunner` (optional)
- `total_count` — integer (optional)

## `ActionTask`

- `created_at` — timestamp (optional)
- `display_title` — string (optional)
- `event` — string (optional)
- `head_branch` — string (optional)
- `head_sha` — string (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `run_number` — integer (optional)
- `run_started_at` — timestamp (optional)
- `status` — string (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)
- `workflow_id` — string (optional)

## `ActionTaskResponse`

- `total_count` — integer (optional)
- `workflow_runs` — list of `contract.ActionTask` (optional)

## `ActionVariable`

- `data` — string (optional)
- `description` — string (optional)
- `name` — string (optional)
- `owner_id` — integer (optional)
- `repo_id` — integer (optional)

## `ActionWorkflow`

- `badge_url` — string (optional)
- `created_at` — timestamp (optional)
- `deleted_at` — timestamp (optional)
- `html_url` — string (optional)
- `id` — string (optional)
- `name` — string (optional)
- `path` — string (optional)
- `state` — string (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)

## `ActionWorkflowJob`

- `completed_at` — timestamp (optional)
- `conclusion` — string (optional)
- `created_at` — timestamp (optional)
- `head_branch` — string (optional)
- `head_sha` — string (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `labels` — list of string (optional)
- `name` — string (optional)
- `run_attempt` — integer (optional)
- `run_id` — integer (optional)
- `run_url` — string (optional)
- `runner_id` — integer (optional)
- `runner_name` — string (optional)
- `started_at` — timestamp (optional)
- `status` — string (optional)
- `steps` — list of `contract.ActionWorkflowStep` (optional)
- `url` — string (optional)

## `ActionWorkflowJobsResponse`

- `jobs` — list of `contract.ActionWorkflowJob` (optional)
- `total_count` — integer (optional)

## `ActionWorkflowResponse`

- `total_count` — integer (optional)
- `workflows` — list of `contract.ActionWorkflow` (optional)

## `ActionWorkflowRun`

- `actor` — `contract.User` (optional)
- `completed_at` — timestamp (optional)
- `conclusion` — string (optional)
- `display_title` — string (optional)
- `event` — string (optional)
- `head_branch` — string (optional)
- `head_repository` — `contract.Repository` (optional)
- `head_sha` — string (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `path` — string (optional)
- `previous_attempt_url` — string (optional)
- `pull_requests` — list of `contract.PullRequestMinimal` (optional)
- `repository` — `contract.Repository` (optional)
- `repository_id` — integer (optional)
- `run_attempt` — integer (optional)
- `run_number` — integer (optional)
- `started_at` — timestamp (optional)
- `status` — string (optional)
- `trigger_actor` — `contract.User` (optional)
- `url` — string (optional)

## `ActionWorkflowRunsResponse`

- `total_count` — integer (optional)
- `workflow_runs` — list of `contract.ActionWorkflowRun` (optional)

## `ActionWorkflowStep`

- `completed_at` — timestamp (optional)
- `conclusion` — string (optional)
- `name` — string (optional)
- `number` — integer (optional)
- `started_at` — timestamp (optional)
- `status` — string (optional)

## `Activity`

- `act_user` — `contract.User` (optional)
- `act_user_id` — integer (optional)
- `comment` — `contract.Comment` (optional)
- `comment_id` — integer (optional)
- `content` — string (optional)
- `created` — timestamp (optional)
- `id` — integer (optional)
- `is_private` — boolean (optional)
- `op_type` — string (optional)
- `ref_name` — string (optional)
- `repo` — `contract.Repository` (optional)
- `repo_id` — integer (optional)
- `user_id` — integer (optional)

## `AddCollaboratorOption`

- `permission` — string (optional)

## `AddTimeOption`

- `created` — timestamp (optional)
- `time` — integer (required)
- `user_name` — string (optional)

## `AnnotatedTag`

- `message` — string (optional)
- `object` — `contract.AnnotatedTagObject` (optional)
- `sha` — string (optional)
- `tag` — string (optional)
- `tagger` — `contract.CommitUser` (optional)
- `url` — string (optional)
- `verification` — `contract.PayloadCommitVerification` (optional)

## `AnnotatedTagObject`

- `sha` — string (optional)
- `type` — string (optional)
- `url` — string (optional)

## `ApplyDiffPatchFileOptions`

- `author` — `contract.Identity` (optional)
- `branch` — string (optional)
- `committer` — `contract.Identity` (optional)
- `content` — string (required)
- `dates` — `contract.CommitDateOptions` (optional)
- `force_push` — boolean (optional)
- `message` — string (optional)
- `new_branch` — string (optional)
- `signoff` — boolean (optional)

## `Attachment`

- `browser_download_url` — string (optional)
- `created_at` — timestamp (optional)
- `download_count` — integer (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `size` — integer (optional)
- `uuid` — string (optional)

## `Badge`

- `description` — string (optional)
- `id` — integer (optional)
- `image_url` — string (optional)
- `slug` — string (optional)

## `Branch`

- `commit` — `contract.PayloadCommit` (optional)
- `effective_branch_protection_name` — string (optional)
- `enable_status_check` — boolean (optional)
- `name` — string (optional)
- `protected` — boolean (optional)
- `required_approvals` — integer (optional)
- `status_check_contexts` — list of string (optional)
- `user_can_merge` — boolean (optional)
- `user_can_push` — boolean (optional)

## `BranchProtection`

- `approvals_whitelist_teams` — list of string (optional)
- `approvals_whitelist_username` — list of string (optional)
- `block_admin_merge_override` — boolean (optional)
- `block_on_official_review_requests` — boolean (optional)
- `block_on_outdated_branch` — boolean (optional)
- `block_on_rejected_reviews` — boolean (optional)
- `branch_name` — string (optional)
- `bypass_allowlist_teams` — list of string (optional)
- `bypass_allowlist_usernames` — list of string (optional)
- `created_at` — timestamp (optional)
- `dismiss_stale_approvals` — boolean (optional)
- `enable_approvals_whitelist` — boolean (optional)
- `enable_bypass_allowlist` — boolean (optional)
- `enable_force_push` — boolean (optional)
- `enable_force_push_allowlist` — boolean (optional)
- `enable_merge_whitelist` — boolean (optional)
- `enable_push` — boolean (optional)
- `enable_push_whitelist` — boolean (optional)
- `enable_status_check` — boolean (optional)
- `force_push_allowlist_deploy_keys` — boolean (optional)
- `force_push_allowlist_teams` — list of string (optional)
- `force_push_allowlist_usernames` — list of string (optional)
- `ignore_stale_approvals` — boolean (optional)
- `merge_whitelist_teams` — list of string (optional)
- `merge_whitelist_usernames` — list of string (optional)
- `priority` — integer (optional)
- `protected_file_patterns` — string (optional)
- `push_whitelist_deploy_keys` — boolean (optional)
- `push_whitelist_teams` — list of string (optional)
- `push_whitelist_usernames` — list of string (optional)
- `require_signed_commits` — boolean (optional)
- `required_approvals` — integer (optional)
- `rule_name` — string (optional)
- `status_check_contexts` — list of string (optional)
- `unprotected_file_patterns` — string (optional)
- `updated_at` — timestamp (optional)

## `ChangeFileOperation`

- `content` — string (optional)
- `from_path` — string (optional)
- `operation` — string (required)
- `path` — string (required)
- `sha` — string (optional)

## `ChangeFilesOptions`

- `author` — `contract.Identity` (optional)
- `branch` — string (optional)
- `committer` — `contract.Identity` (optional)
- `dates` — `contract.CommitDateOptions` (optional)
- `files` — list of `contract.ChangeFileOperation` (required)
- `force_push` — boolean (optional)
- `message` — string (optional)
- `new_branch` — string (optional)
- `signoff` — boolean (optional)

## `ChangedFile`

- `additions` — integer (optional)
- `changes` — integer (optional)
- `contents_url` — string (optional)
- `deletions` — integer (optional)
- `filename` — string (optional)
- `html_url` — string (optional)
- `previous_filename` — string (optional)
- `raw_url` — string (optional)
- `status` — string (optional)

## `CombinedStatus`

- `commit_url` — string (optional)
- `repository` — `contract.Repository` (optional)
- `sha` — string (optional)
- `state` — string (optional)
- `statuses` — list of `contract.CommitStatus` (optional)
- `total_count` — integer (optional)
- `url` — string (optional)

## `Comment`

- `assets` — list of `contract.Attachment` (optional)
- `body` — string (optional)
- `created_at` — timestamp (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `issue_url` — string (optional)
- `original_author` — string (optional)
- `original_author_id` — integer (optional)
- `pull_request_url` — string (optional)
- `updated_at` — timestamp (optional)
- `user` — `contract.User` (optional)

## `Commit`

- `author` — `contract.User` (optional)
- `commit` — `contract.RepoCommit` (optional)
- `committer` — `contract.User` (optional)
- `created` — timestamp (optional)
- `files` — list of `contract.CommitAffectedFiles` (optional)
- `html_url` — string (optional)
- `parents` — list of `contract.CommitMeta` (optional)
- `sha` — string (optional)
- `stats` — `contract.CommitStats` (optional)
- `url` — string (optional)

## `CommitAffectedFiles`

- `filename` — string (optional)
- `status` — string (optional)

## `CommitDateOptions`

- `author` — timestamp (optional)
- `committer` — timestamp (optional)

## `CommitMeta`

- `created` — timestamp (optional)
- `sha` — string (optional)
- `url` — string (optional)

## `CommitStats`

- `additions` — integer (optional)
- `deletions` — integer (optional)
- `total` — integer (optional)

## `CommitStatus`

- `context` — string (optional)
- `created_at` — timestamp (optional)
- `creator` — `contract.User` (optional)
- `description` — string (optional)
- `id` — integer (optional)
- `status` — string (optional)
- `target_url` — string (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)

## `CommitUser`

- `date` — string (optional)
- `email` — string (optional)
- `name` — string (optional)

## `Compare`

- `commits` — list of `contract.Commit` (optional)
- `total_commits` — integer (optional)

## `ContentsExtResponse`

- `dir_contents` — list of `contract.ContentsResponse` (optional)
- `file_contents` — `contract.ContentsResponse` (optional)

## `ContentsResponse`

- `_links` — `contract.FileLinksResponse` (optional)
- `content` — string (optional)
- `download_url` — string (optional)
- `encoding` — string (optional)
- `git_url` — string (optional)
- `html_url` — string (optional)
- `last_author_date` — timestamp (optional)
- `last_commit_message` — string (optional)
- `last_commit_sha` — string (optional)
- `last_committer_date` — timestamp (optional)
- `lfs_oid` — string (optional)
- `lfs_size` — integer (optional)
- `name` — string (optional)
- `path` — string (optional)
- `sha` — string (optional)
- `size` — integer (optional)
- `submodule_git_url` — string (optional)
- `target` — string (optional)
- `type` — string (optional)
- `url` — string (optional)

## `CreateAccessTokenOption`

- `name` — string (required)
- `scopes` — list of string (optional)

## `CreateActionWorkflowDispatch`

- `inputs` — map of string (optional)
- `ref` — string (required)

## `CreateBranchProtectionOption`

- `approvals_whitelist_teams` — list of string (optional)
- `approvals_whitelist_username` — list of string (optional)
- `block_admin_merge_override` — boolean (optional)
- `block_on_official_review_requests` — boolean (optional)
- `block_on_outdated_branch` — boolean (optional)
- `block_on_rejected_reviews` — boolean (optional)
- `branch_name` — string (optional)
- `bypass_allowlist_teams` — list of string (optional)
- `bypass_allowlist_usernames` — list of string (optional)
- `dismiss_stale_approvals` — boolean (optional)
- `enable_approvals_whitelist` — boolean (optional)
- `enable_bypass_allowlist` — boolean (optional)
- `enable_force_push` — boolean (optional)
- `enable_force_push_allowlist` — boolean (optional)
- `enable_merge_whitelist` — boolean (optional)
- `enable_push` — boolean (optional)
- `enable_push_whitelist` — boolean (optional)
- `enable_status_check` — boolean (optional)
- `force_push_allowlist_deploy_keys` — boolean (optional)
- `force_push_allowlist_teams` — list of string (optional)
- `force_push_allowlist_usernames` — list of string (optional)
- `ignore_stale_approvals` — boolean (optional)
- `merge_whitelist_teams` — list of string (optional)
- `merge_whitelist_usernames` — list of string (optional)
- `priority` — integer (optional)
- `protected_file_patterns` — string (optional)
- `push_whitelist_deploy_keys` — boolean (optional)
- `push_whitelist_teams` — list of string (optional)
- `push_whitelist_usernames` — list of string (optional)
- `require_signed_commits` — boolean (optional)
- `required_approvals` — integer (optional)
- `rule_name` — string (optional)
- `status_check_contexts` — list of string (optional)
- `unprotected_file_patterns` — string (optional)

## `CreateBranchRepoOption`

- `new_branch_name` — string (required)
- `old_branch_name` — string (optional)
- `old_ref_name` — string (optional)

## `CreateEmailOption`

- `emails` — list of string (optional)

## `CreateFileOptions`

- `author` — `contract.Identity` (optional)
- `branch` — string (optional)
- `committer` — `contract.Identity` (optional)
- `content` — string (required)
- `dates` — `contract.CommitDateOptions` (optional)
- `force_push` — boolean (optional)
- `message` — string (optional)
- `new_branch` — string (optional)
- `signoff` — boolean (optional)

## `CreateForkOption`

- `name` — string (optional)
- `organization` — string (optional)

## `CreateGPGKeyOption`

- `armored_public_key` — string (required)
- `armored_signature` — string (optional)

## `CreateHookOption`

- `active` — boolean (optional)
- `authorization_header` — string (optional)
- `branch_filter` — string (optional)
- `config` — `contract.CreateHookOptionConfig` (required)
- `events` — list of string (optional)
- `name` — string (optional)
- `type` — string (required)

## `CreateHookOptionConfig`

Value: map of string.

## `CreateIssueCommentOption`

- `body` — string (required)

## `CreateIssueOption`

- `assignee` — string (optional)
- `assignees` — list of string (optional)
- `body` — string (optional)
- `closed` — boolean (optional)
- `due_date` — timestamp (optional)
- `labels` — list of integer (optional)
- `milestone` — integer (optional)
- `projects` — list of integer (optional)
- `ref` — string (optional)
- `title` — string (required)

## `CreateKeyOption`

- `key` — string (required)
- `read_only` — boolean (optional)
- `title` — string (required)

## `CreateLabelOption`

- `color` — string (required)
- `description` — string (optional)
- `exclusive` — boolean (optional)
- `is_archived` — boolean (optional)
- `name` — string (required)

## `CreateMilestoneOption`

- `description` — string (optional)
- `due_on` — timestamp (optional)
- `state` — string (optional)
- `title` — string (optional)

## `CreateOAuth2ApplicationOptions`

- `confidential_client` — boolean (optional)
- `name` — string (optional)
- `redirect_uris` — list of string (optional)
- `skip_secondary_authorization` — boolean (optional)

## `CreateOrUpdateSecretOption`

- `data` — string (required)
- `description` — string (optional)

## `CreateOrgOption`

- `description` — string (optional)
- `email` — string (optional)
- `full_name` — string (optional)
- `location` — string (optional)
- `repo_admin_change_team_access` — boolean (optional)
- `username` — string (required)
- `visibility` — string (optional)
- `website` — string (optional)

## `CreatePullRequestOption`

- `allow_maintainer_edit` — boolean (optional)
- `assignee` — string (optional)
- `assignees` — list of string (optional)
- `base` — string (optional)
- `body` — string (optional)
- `due_date` — timestamp (optional)
- `head` — string (optional)
- `labels` — list of integer (optional)
- `milestone` — integer (optional)
- `reviewers` — list of string (optional)
- `team_reviewers` — list of string (optional)
- `title` — string (optional)

## `CreatePullReviewComment`

- `body` — string (optional)
- `new_position` — integer (optional)
- `old_position` — integer (optional)
- `path` — string (optional)

## `CreatePullReviewCommentReplyOptions`

- `body` — string (optional)

## `CreatePullReviewOptions`

- `body` — string (optional)
- `comments` — list of `contract.CreatePullReviewComment` (optional)
- `commit_id` — string (optional)
- `event` — string (optional)

## `CreatePushMirrorOption`

- `interval` — string (optional)
- `remote_address` — string (optional)
- `remote_password` — string (optional)
- `remote_username` — string (optional)
- `sync_on_commit` — boolean (optional)

## `CreateReleaseOption`

- `body` — string (optional)
- `draft` — boolean (optional)
- `name` — string (optional)
- `prerelease` — boolean (optional)
- `tag_message` — string (optional)
- `tag_name` — string (required)
- `target_commitish` — string (optional)

## `CreateRepoOption`

- `auto_init` — boolean (optional)
- `default_branch` — string (optional)
- `description` — string (optional)
- `gitignores` — string (optional)
- `issue_labels` — string (optional)
- `license` — string (optional)
- `name` — string (required)
- `object_format_name` — string (optional)
- `private` — boolean (optional)
- `readme` — string (optional)
- `template` — boolean (optional)
- `trust_model` — string (optional)

## `CreateStatusOption`

- `context` — string (optional)
- `description` — string (optional)
- `state` — string (optional)
- `target_url` — string (optional)

## `CreateTagOption`

- `message` — string (optional)
- `tag_name` — string (required)
- `target` — string (optional)

## `CreateTagProtectionOption`

- `name_pattern` — string (optional)
- `whitelist_teams` — list of string (optional)
- `whitelist_usernames` — list of string (optional)

## `CreateTeamOption`

- `can_create_org_repo` — boolean (optional)
- `description` — string (optional)
- `includes_all_repositories` — boolean (optional)
- `name` — string (required)
- `permission` — string (optional)
- `units` — list of string (optional)
- `units_map` — map of string (optional)
- `visibility` — string (optional)

## `CreateUserOption`

- `created_at` — timestamp (optional)
- `email` — string (required)
- `full_name` — string (optional)
- `login_name` — string (optional)
- `must_change_password` — boolean (optional)
- `password` — string (optional)
- `restricted` — boolean (optional)
- `send_notify` — boolean (optional)
- `source_id` — integer (optional)
- `username` — string (required)
- `visibility` — string (optional)

## `CreateVariableOption`

- `description` — string (optional)
- `value` — string (required)

## `CreateWikiPageOptions`

- `content_base64` — string (optional)
- `message` — string (optional)
- `title` — string (optional)

## `Cron`

- `exec_times` — integer (optional)
- `name` — string (optional)
- `next` — timestamp (optional)
- `prev` — timestamp (optional)
- `schedule` — string (optional)

## `CurrentAccessToken`

- `created_at` — timestamp (optional)
- `id` — integer (optional)
- `last_used_at` — timestamp (optional)
- `name` — string (optional)
- `scopes` — list of string (optional)
- `user` — `contract.UserMeta` (optional)

## `DeleteEmailOption`

- `emails` — list of string (optional)

## `DeleteFileOptions`

- `author` — `contract.Identity` (optional)
- `branch` — string (optional)
- `committer` — `contract.Identity` (optional)
- `dates` — `contract.CommitDateOptions` (optional)
- `force_push` — boolean (optional)
- `message` — string (optional)
- `new_branch` — string (optional)
- `sha` — string (required)
- `signoff` — boolean (optional)

## `DeployKey`

- `created_at` — timestamp (optional)
- `fingerprint` — string (optional)
- `id` — integer (optional)
- `key` — string (optional)
- `key_id` — integer (optional)
- `read_only` — boolean (optional)
- `repository` — `contract.Repository` (optional)
- `title` — string (optional)
- `url` — string (optional)

## `DismissPullReviewOptions`

- `message` — string (optional)
- `priors` — boolean (optional)

## `EditActionRunnerOption`

- `disabled` — boolean (required)

## `EditAttachmentOptions`

- `name` — string (optional)

## `EditBranchProtectionOption`

- `approvals_whitelist_teams` — list of string (optional)
- `approvals_whitelist_username` — list of string (optional)
- `block_admin_merge_override` — boolean (optional)
- `block_on_official_review_requests` — boolean (optional)
- `block_on_outdated_branch` — boolean (optional)
- `block_on_rejected_reviews` — boolean (optional)
- `bypass_allowlist_teams` — list of string (optional)
- `bypass_allowlist_usernames` — list of string (optional)
- `dismiss_stale_approvals` — boolean (optional)
- `enable_approvals_whitelist` — boolean (optional)
- `enable_bypass_allowlist` — boolean (optional)
- `enable_force_push` — boolean (optional)
- `enable_force_push_allowlist` — boolean (optional)
- `enable_merge_whitelist` — boolean (optional)
- `enable_push` — boolean (optional)
- `enable_push_whitelist` — boolean (optional)
- `enable_status_check` — boolean (optional)
- `force_push_allowlist_deploy_keys` — boolean (optional)
- `force_push_allowlist_teams` — list of string (optional)
- `force_push_allowlist_usernames` — list of string (optional)
- `ignore_stale_approvals` — boolean (optional)
- `merge_whitelist_teams` — list of string (optional)
- `merge_whitelist_usernames` — list of string (optional)
- `priority` — integer (optional)
- `protected_file_patterns` — string (optional)
- `push_whitelist_deploy_keys` — boolean (optional)
- `push_whitelist_teams` — list of string (optional)
- `push_whitelist_usernames` — list of string (optional)
- `require_signed_commits` — boolean (optional)
- `required_approvals` — integer (optional)
- `status_check_contexts` — list of string (optional)
- `unprotected_file_patterns` — string (optional)

## `EditDeadlineOption`

- `due_date` — timestamp (required)

## `EditGitHookOption`

- `content` — string (optional)

## `EditHookOption`

- `active` — boolean (optional)
- `authorization_header` — string (optional)
- `branch_filter` — string (optional)
- `config` — map of string (optional)
- `events` — list of string (optional)
- `name` — string (optional)

## `EditIssueCommentOption`

- `body` — string (required)

## `EditIssueOption`

- `assignee` — string (optional)
- `assignees` — list of string (optional)
- `body` — string (optional)
- `content_version` — integer (optional)
- `due_date` — timestamp (optional)
- `milestone` — integer (optional)
- `projects` — list of integer (optional)
- `ref` — string (optional)
- `state` — string (optional)
- `title` — string (optional)
- `unset_due_date` — boolean (optional)

## `EditLabelOption`

- `color` — string (optional)
- `description` — string (optional)
- `exclusive` — boolean (optional)
- `is_archived` — boolean (optional)
- `name` — string (optional)

## `EditMilestoneOption`

- `description` — string (optional)
- `due_on` — timestamp (optional)
- `state` — string (optional)
- `title` — string (optional)

## `EditOrgOption`

- `description` — string (optional)
- `email` — string (optional)
- `full_name` — string (optional)
- `location` — string (optional)
- `repo_admin_change_team_access` — boolean (optional)
- `visibility` — string (optional)
- `website` — string (optional)

## `EditPullRequestOption`

- `allow_maintainer_edit` — boolean (optional)
- `assignee` — string (optional)
- `assignees` — list of string (optional)
- `base` — string (optional)
- `body` — string (optional)
- `content_version` — integer (optional)
- `due_date` — timestamp (optional)
- `labels` — list of integer (optional)
- `milestone` — integer (optional)
- `state` — string (optional)
- `title` — string (optional)
- `unset_due_date` — boolean (optional)

## `EditReactionOption`

- `content` — string (optional)

## `EditReleaseOption`

- `body` — string (optional)
- `draft` — boolean (optional)
- `name` — string (optional)
- `prerelease` — boolean (optional)
- `tag_name` — string (optional)
- `target_commitish` — string (optional)

## `EditRepoOption`

- `allow_fast_forward_only_merge` — boolean (optional)
- `allow_manual_merge` — boolean (optional)
- `allow_merge_commits` — boolean (optional)
- `allow_merge_update` — boolean (optional)
- `allow_rebase` — boolean (optional)
- `allow_rebase_explicit` — boolean (optional)
- `allow_rebase_update` — boolean (optional)
- `allow_squash_merge` — boolean (optional)
- `archived` — boolean (optional)
- `autodetect_manual_merge` — boolean (optional)
- `default_allow_maintainer_edit` — boolean (optional)
- `default_branch` — string (optional)
- `default_delete_branch_after_merge` — boolean (optional)
- `default_merge_style` — string (optional)
- `default_update_style` — string (optional)
- `description` — string (optional)
- `enable_prune` — boolean (optional)
- `external_tracker` — `contract.ExternalTracker` (optional)
- `external_wiki` — `contract.ExternalWiki` (optional)
- `has_actions` — boolean (optional)
- `has_code` — boolean (optional)
- `has_issues` — boolean (optional)
- `has_packages` — boolean (optional)
- `has_projects` — boolean (optional)
- `has_pull_requests` — boolean (optional)
- `has_releases` — boolean (optional)
- `has_wiki` — boolean (optional)
- `ignore_whitespace_conflicts` — boolean (optional)
- `internal_tracker` — `contract.InternalTracker` (optional)
- `mirror_interval` — string (optional)
- `mirror_password` — string (optional)
- `mirror_token` — string (optional)
- `mirror_username` — string (optional)
- `name` — string (optional)
- `private` — boolean (optional)
- `projects_mode` — string (optional)
- `template` — boolean (optional)
- `website` — string (optional)

## `EditTagProtectionOption`

- `name_pattern` — string (optional)
- `whitelist_teams` — list of string (optional)
- `whitelist_usernames` — list of string (optional)

## `EditTeamOption`

- `can_create_org_repo` — boolean (optional)
- `description` — string (optional)
- `includes_all_repositories` — boolean (optional)
- `name` — string (required)
- `permission` — string (optional)
- `units` — list of string (optional)
- `units_map` — map of string (optional)
- `visibility` — string (optional)

## `EditUserOption`

- `active` — boolean (optional)
- `admin` — boolean (optional)
- `allow_create_organization` — boolean (optional)
- `allow_git_hook` — boolean (optional)
- `allow_import_local` — boolean (optional)
- `description` — string (optional)
- `email` — string (optional)
- `full_name` — string (optional)
- `location` — string (optional)
- `login_name` — string (optional)
- `max_repo_creation` — integer (optional)
- `must_change_password` — boolean (optional)
- `password` — string (optional)
- `prohibit_login` — boolean (optional)
- `restricted` — boolean (optional)
- `source_id` — integer (required)
- `visibility` — string (optional)
- `website` — string (optional)

## `Email`

- `email` — string (optional)
- `primary` — boolean (optional)
- `user_id` — integer (optional)
- `username` — string (optional)
- `verified` — boolean (optional)

## `ExternalTracker`

- `external_tracker_format` — string (optional)
- `external_tracker_regexp_pattern` — string (optional)
- `external_tracker_style` — string (optional)
- `external_tracker_url` — string (optional)

## `ExternalWiki`

- `external_wiki_url` — string (optional)

## `FileCommitResponse`

- `author` — `contract.CommitUser` (optional)
- `committer` — `contract.CommitUser` (optional)
- `created` — timestamp (optional)
- `html_url` — string (optional)
- `message` — string (optional)
- `parents` — list of `contract.CommitMeta` (optional)
- `sha` — string (optional)
- `tree` — `contract.CommitMeta` (optional)
- `url` — string (optional)

## `FileDeleteResponse`

- `commit` — `contract.FileCommitResponse` (optional)
- `content` — unspecified JSON value (optional)
- `verification` — `contract.PayloadCommitVerification` (optional)

## `FileLinksResponse`

- `git` — string (optional)
- `html` — string (optional)
- `self` — string (optional)

## `FileResponse`

- `commit` — `contract.FileCommitResponse` (optional)
- `content` — `contract.ContentsResponse` (optional)
- `verification` — `contract.PayloadCommitVerification` (optional)

## `FilesResponse`

- `commit` — `contract.FileCommitResponse` (optional)
- `files` — list of `contract.ContentsResponse` (optional)
- `verification` — `contract.PayloadCommitVerification` (optional)

## `GPGKey`

- `can_certify` — boolean (optional)
- `can_encrypt_comms` — boolean (optional)
- `can_encrypt_storage` — boolean (optional)
- `can_sign` — boolean (optional)
- `created_at` — timestamp (optional)
- `emails` — list of `contract.GPGKeyEmail` (optional)
- `expires_at` — timestamp (optional)
- `id` — integer (optional)
- `key_id` — string (optional)
- `primary_key_id` — string (optional)
- `public_key` — string (optional)
- `subkeys` — list of `contract.GPGKey` (optional)
- `verified` — boolean (optional)

## `GPGKeyEmail`

- `email` — string (optional)
- `verified` — boolean (optional)

## `GeneralAPISettings`

- `default_git_trees_per_page` — integer (optional)
- `default_max_blob_size` — integer (optional)
- `default_max_response_size` — integer (optional)
- `default_paging_num` — integer (optional)
- `max_response_items` — integer (optional)

## `GeneralAttachmentSettings`

- `allowed_types` — string (optional)
- `enabled` — boolean (optional)
- `max_files` — integer (optional)
- `max_size` — integer (optional)

## `GeneralRepoSettings`

- `http_git_disabled` — boolean (optional)
- `lfs_disabled` — boolean (optional)
- `migrations_disabled` — boolean (optional)
- `mirrors_disabled` — boolean (optional)
- `stars_disabled` — boolean (optional)
- `time_tracking_disabled` — boolean (optional)

## `GeneralUISettings`

- `allowed_reactions` — list of string (optional)
- `custom_emojis` — list of string (optional)
- `default_theme` — string (optional)

## `GenerateRepoOption`

- `avatar` — boolean (optional)
- `default_branch` — string (optional)
- `description` — string (optional)
- `git_content` — boolean (optional)
- `git_hooks` — boolean (optional)
- `labels` — boolean (optional)
- `name` — string (required)
- `owner` — string (required)
- `private` — boolean (optional)
- `protected_branch` — boolean (optional)
- `topics` — boolean (optional)
- `webhooks` — boolean (optional)

## `GetFilesOptions`

- `files` — list of string (optional)

## `GitBlobResponse`

- `content` — string (optional)
- `encoding` — string (optional)
- `lfs_oid` — string (optional)
- `lfs_size` — integer (optional)
- `sha` — string (optional)
- `size` — integer (optional)
- `url` — string (optional)

## `GitEntry`

- `mode` — string (optional)
- `path` — string (optional)
- `sha` — string (optional)
- `size` — integer (optional)
- `type` — string (optional)
- `url` — string (optional)

## `GitHook`

- `content` — string (optional)
- `is_active` — boolean (optional)
- `name` — string (optional)

## `GitObject`

- `sha` — string (optional)
- `type` — string (optional)
- `url` — string (optional)

## `GitTreeResponse`

- `page` — integer (optional)
- `sha` — string (optional)
- `total_count` — integer (optional)
- `tree` — list of `contract.GitEntry` (optional)
- `truncated` — boolean (optional)
- `url` — string (optional)

## `GitignoreTemplateInfo`

- `name` — string (optional)
- `source` — string (optional)

## `Hook`

- `active` — boolean (optional)
- `authorization_header` — string (optional)
- `branch_filter` — string (optional)
- `config` — map of string (optional)
- `created_at` — timestamp (optional)
- `events` — list of string (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `type` — string (optional)
- `updated_at` — timestamp (optional)

## `Identity`

- `email` — string (optional)
- `name` — string (optional)

## `InternalTracker`

- `allow_only_contributors_to_track_time` — boolean (optional)
- `enable_issue_dependencies` — boolean (optional)
- `enable_time_tracker` — boolean (optional)

## `Issue`

- `assets` — list of `contract.Attachment` (optional)
- `assignee` — `contract.User` (optional)
- `assignees` — list of `contract.User` (optional)
- `body` — string (optional)
- `closed_at` — timestamp (optional)
- `comments` — integer (optional)
- `content_version` — integer (optional)
- `created_at` — timestamp (optional)
- `due_date` — timestamp (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `is_locked` — boolean (optional)
- `labels` — list of `contract.Label` (optional)
- `milestone` — `contract.Milestone` (optional)
- `number` — integer (optional)
- `original_author` — string (optional)
- `original_author_id` — integer (optional)
- `pin_order` — integer (optional)
- `projects` — list of `contract.Project` (optional)
- `pull_request` — `contract.PullRequestMeta` (optional)
- `ref` — string (optional)
- `repository` — `contract.RepositoryMeta` (optional)
- `state` — string (optional)
- `time_estimate` — integer (optional)
- `title` — string (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)
- `user` — `contract.User` (optional)

## `IssueAssigneesOption`

- `assignees` — list of string (optional)

## `IssueConfig`

- `blank_issues_enabled` — boolean (optional)
- `contact_links` — list of `contract.IssueConfigContactLink` (optional)

## `IssueConfigContactLink`

- `about` — string (optional)
- `name` — string (optional)
- `url` — string (optional)

## `IssueConfigValidation`

- `message` — string (optional)
- `valid` — boolean (optional)

## `IssueDeadline`

- `due_date` — timestamp (optional)

## `IssueFormField`

- `attributes` — object (optional)
- `id` — string (optional)
- `type` — string (optional)
- `validations` — object (optional)
- `visible` — list of string (optional)

## `IssueLabelsOption`

- `labels` — list of unspecified JSON value (optional)

## `IssueMeta`

- `index` — integer (optional)
- `owner` — string (optional)
- `repo` — string (optional)

## `IssueTemplate`

- `about` — string (optional)
- `assignees` — `contract.IssueTemplateStringSlice` (optional)
- `body` — list of `contract.IssueFormField` (optional)
- `content` — string (optional)
- `file_name` — string (optional)
- `labels` — `contract.IssueTemplateStringSlice` (optional)
- `name` — string (optional)
- `ref` — string (optional)
- `title` — string (optional)

## `IssueTemplateStringSlice`

Value: list of string.

## `Label`

- `color` — string (optional)
- `description` — string (optional)
- `exclusive` — boolean (optional)
- `id` — integer (optional)
- `is_archived` — boolean (optional)
- `name` — string (optional)
- `url` — string (optional)

## `LabelTemplate`

- `color` — string (optional)
- `description` — string (optional)
- `exclusive` — boolean (optional)
- `name` — string (optional)

## `LicenseTemplateInfo`

- `body` — string (optional)
- `implementation` — string (optional)
- `key` — string (optional)
- `name` — string (optional)
- `url` — string (optional)

## `LicensesTemplateListEntry`

- `key` — string (optional)
- `name` — string (optional)
- `url` — string (optional)

## `LockIssueOption`

- `lock_reason` — string (optional)

## `MarkdownOption`

- `Context` — string (optional)
- `Mode` — string (optional)
- `Text` — string (optional)
- `Wiki` — boolean (optional)

## `MarkupOption`

- `Context` — string (optional)
- `FilePath` — string (optional)
- `Mode` — string (optional)
- `Text` — string (optional)
- `Wiki` — boolean (optional)

## `MergePullRequestOption`

- `delete_branch_after_merge` — boolean (optional)
- `do` — string (required)
- `force_merge` — boolean (optional)
- `head_commit_id` — string (optional)
- `merge_commit_id` — string (optional)
- `merge_message_field` — string (optional)
- `merge_title_field` — string (optional)
- `merge_when_checks_succeed` — boolean (optional)

## `MergeUpstreamRequest`

- `branch` — string (optional)
- `ff_only` — boolean (optional)

## `MergeUpstreamResponse`

- `merge_type` — string (optional)

## `MigrateRepoOptions`

- `auth_password` — string (optional)
- `auth_token` — string (optional)
- `auth_username` — string (optional)
- `aws_access_key_id` — string (optional)
- `aws_secret_access_key` — string (optional)
- `clone_addr` — string (required)
- `description` — string (optional)
- `issues` — boolean (optional)
- `labels` — boolean (optional)
- `lfs` — boolean (optional)
- `lfs_endpoint` — string (optional)
- `milestones` — boolean (optional)
- `mirror` — boolean (optional)
- `mirror_interval` — string (optional)
- `private` — boolean (optional)
- `pull_requests` — boolean (optional)
- `releases` — boolean (optional)
- `repo_name` — string (required)
- `repo_owner` — string (optional)
- `service` — string (optional)
- `uid` — integer (optional)
- `wiki` — boolean (optional)

## `Milestone`

- `closed_at` — timestamp (optional)
- `closed_issues` — integer (optional)
- `created_at` — timestamp (optional)
- `description` — string (optional)
- `due_on` — timestamp (optional)
- `id` — integer (optional)
- `open_issues` — integer (optional)
- `state` — string (optional)
- `title` — string (optional)
- `updated_at` — timestamp (optional)

## `NewIssuePinsAllowed`

- `issues` — boolean (optional)
- `pull_requests` — boolean (optional)

## `NodeInfo`

- `metadata` — object (optional)
- `openRegistrations` — boolean (optional)
- `protocols` — list of string (optional)
- `services` — `contract.NodeInfoServices` (optional)
- `software` — `contract.NodeInfoSoftware` (optional)
- `usage` — `contract.NodeInfoUsage` (optional)
- `version` — string (optional)

## `NodeInfoServices`

- `inbound` — list of string (optional)
- `outbound` — list of string (optional)

## `NodeInfoSoftware`

- `homepage` — string (optional)
- `name` — string (optional)
- `repository` — string (optional)
- `version` — string (optional)

## `NodeInfoUsage`

- `localComments` — integer (optional)
- `localPosts` — integer (optional)
- `users` — `contract.NodeInfoUsageUsers` (optional)

## `NodeInfoUsageUsers`

- `activeHalfyear` — integer (optional)
- `activeMonth` — integer (optional)
- `total` — integer (optional)

## `Note`

- `commit` — `contract.Commit` (optional)
- `message` — string (optional)

## `NotificationCount`

- `new` — integer (optional)

## `NotificationSubject`

- `html_url` — string (optional)
- `latest_comment_html_url` — string (optional)
- `latest_comment_url` — string (optional)
- `state` — string (optional)
- `title` — string (optional)
- `type` — string (optional)
- `url` — string (optional)

## `NotificationThread`

- `id` — integer (optional)
- `pinned` — boolean (optional)
- `repository` — `contract.Repository` (optional)
- `subject` — `contract.NotificationSubject` (optional)
- `unread` — boolean (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)

## `OAuth2Application`

- `client_id` — string (optional)
- `client_secret` — string (optional)
- `confidential_client` — boolean (optional)
- `created` — timestamp (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `redirect_uris` — list of string (optional)
- `skip_secondary_authorization` — boolean (optional)

## `Organization`

- `avatar_url` — string (optional)
- `description` — string (optional)
- `email` — string (optional)
- `full_name` — string (optional)
- `id` — integer (optional)
- `location` — string (optional)
- `name` — string (optional)
- `repo_admin_change_team_access` — boolean (optional)
- `username` — string (optional)
- `visibility` — string (optional)
- `website` — string (optional)

## `OrganizationPermissions`

- `can_create_repository` — boolean (optional)
- `can_read` — boolean (optional)
- `can_write` — boolean (optional)
- `is_admin` — boolean (optional)
- `is_owner` — boolean (optional)

## `PRBranchInfo`

- `label` — string (optional)
- `ref` — string (optional)
- `repo` — `contract.Repository` (optional)
- `repo_id` — integer (optional)
- `sha` — string (optional)

## `Package`

- `created_at` — timestamp (optional)
- `creator` — `contract.User` (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `owner` — `contract.User` (optional)
- `repository` — `contract.Repository` (optional)
- `type` — string (optional)
- `version` — string (optional)

## `PackageFile`

- `id` — integer (optional)
- `md5` — string (optional)
- `name` — string (optional)
- `sha1` — string (optional)
- `sha256` — string (optional)
- `sha512` — string (optional)
- `size` — integer (optional)

## `PayloadCommit`

- `added` — list of string (optional)
- `author` — `contract.PayloadUser` (optional)
- `committer` — `contract.PayloadUser` (optional)
- `id` — string (optional)
- `message` — string (optional)
- `modified` — list of string (optional)
- `removed` — list of string (optional)
- `timestamp` — timestamp (optional)
- `url` — string (optional)
- `verification` — `contract.PayloadCommitVerification` (optional)

## `PayloadCommitVerification`

- `payload` — string (optional)
- `reason` — string (optional)
- `signature` — string (optional)
- `signer` — `contract.PayloadUser` (optional)
- `verified` — boolean (optional)

## `PayloadUser`

- `email` — string (optional)
- `name` — string (optional)
- `username` — string (optional)

## `Permission`

- `admin` — boolean (optional)
- `pull` — boolean (optional)
- `push` — boolean (optional)

## `Project`

- `closed_at` — timestamp (optional)
- `created_at` — timestamp (optional)
- `creator_id` — integer (optional)
- `description` — string (optional)
- `id` — integer (optional)
- `is_closed` — boolean (optional)
- `owner_id` — integer (optional)
- `repo_id` — integer (optional)
- `title` — string (optional)
- `updated_at` — timestamp (optional)

## `PublicKey`

- `created_at` — timestamp (optional)
- `fingerprint` — string (optional)
- `id` — integer (optional)
- `key` — string (optional)
- `key_type` — string (optional)
- `last_used_at` — timestamp (optional)
- `read_only` — boolean (optional)
- `title` — string (optional)
- `url` — string (optional)
- `user` — `contract.User` (optional)

## `PullRequest`

- `additions` — integer (optional)
- `allow_maintainer_edit` — boolean (optional)
- `assignee` — `contract.User` (optional)
- `assignees` — list of `contract.User` (optional)
- `base` — `contract.PRBranchInfo` (optional)
- `body` — string (optional)
- `changed_files` — integer (optional)
- `closed_at` — timestamp (optional)
- `comments` — integer (optional)
- `content_version` — integer (optional)
- `created_at` — timestamp (optional)
- `deletions` — integer (optional)
- `diff_url` — string (optional)
- `draft` — boolean (optional)
- `due_date` — timestamp (optional)
- `head` — `contract.PRBranchInfo` (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `is_locked` — boolean (optional)
- `labels` — list of `contract.Label` (optional)
- `merge_base` — string (optional)
- `merge_commit_sha` — string (optional)
- `mergeable` — boolean (optional)
- `merged` — boolean (optional)
- `merged_at` — timestamp (optional)
- `merged_by` — `contract.User` (optional)
- `milestone` — `contract.Milestone` (optional)
- `number` — integer (optional)
- `patch_url` — string (optional)
- `pin_order` — integer (optional)
- `requested_reviewers` — list of `contract.User` (optional)
- `requested_reviewers_teams` — list of `contract.Team` (optional)
- `review_comments` — integer (optional)
- `state` — string (optional)
- `title` — string (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)
- `user` — `contract.User` (optional)

## `PullRequestMeta`

- `draft` — boolean (optional)
- `html_url` — string (optional)
- `merged` — boolean (optional)
- `merged_at` — timestamp (optional)

## `PullRequestMinimal`

- `base` — `contract.PullRequestMinimalHead` (optional)
- `head` — `contract.PullRequestMinimalHead` (optional)
- `id` — integer (optional)
- `number` — integer (optional)
- `url` — string (optional)

## `PullRequestMinimalHead`

- `ref` — string (optional)
- `repo` — `contract.PullRequestMinimalHeadRepo` (optional)
- `sha` — string (optional)

## `PullRequestMinimalHeadRepo`

- `id` — integer (optional)
- `name` — string (optional)
- `url` — string (optional)

## `PullReview`

- `body` — string (optional)
- `comments_count` — integer (optional)
- `commit_id` — string (optional)
- `dismissed` — boolean (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `official` — boolean (optional)
- `pull_request_url` — string (optional)
- `stale` — boolean (optional)
- `state` — string (optional)
- `submitted_at` — timestamp (optional)
- `team` — `contract.Team` (optional)
- `updated_at` — timestamp (optional)
- `user` — `contract.User` (optional)

## `PullReviewComment`

- `body` — string (optional)
- `commit_id` — string (optional)
- `created_at` — timestamp (optional)
- `diff_hunk` — string (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `original_commit_id` — string (optional)
- `original_position` — integer (optional)
- `path` — string (optional)
- `position` — integer (optional)
- `pull_request_review_id` — integer (optional)
- `pull_request_url` — string (optional)
- `resolver` — `contract.User` (optional)
- `updated_at` — timestamp (optional)
- `user` — `contract.User` (optional)

## `PullReviewRequestOptions`

- `reviewers` — list of string (optional)
- `team_reviewers` — list of string (optional)

## `PushMirror`

- `created` — timestamp (optional)
- `interval` — string (optional)
- `last_error` — string (optional)
- `last_update` — timestamp (optional)
- `remote_address` — string (optional)
- `remote_name` — string (optional)
- `repo_name` — string (optional)
- `sync_on_commit` — boolean (optional)

## `Reaction`

- `content` — string (optional)
- `created_at` — timestamp (optional)
- `user` — `contract.User` (optional)

## `Reference`

- `object` — `contract.GitObject` (optional)
- `ref` — string (optional)
- `url` — string (optional)

## `Release`

- `assets` — list of `contract.Attachment` (optional)
- `author` — `contract.User` (optional)
- `body` — string (optional)
- `created_at` — timestamp (optional)
- `draft` — boolean (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `prerelease` — boolean (optional)
- `published_at` — timestamp (optional)
- `tag_name` — string (optional)
- `tarball_url` — string (optional)
- `target_commitish` — string (optional)
- `upload_url` — string (optional)
- `url` — string (optional)
- `zipball_url` — string (optional)

## `RenameBranchRepoOption`

- `name` — string (required)

## `RenameOrgOption`

- `new_name` — string (required)

## `RenameUserOption`

- `new_username` — string (required)

## `RepoCollaboratorPermission`

- `permission` — string (optional)
- `role_name` — string (optional)
- `user` — `contract.User` (optional)

## `RepoCommit`

- `author` — `contract.CommitUser` (optional)
- `committer` — `contract.CommitUser` (optional)
- `message` — string (optional)
- `tree` — `contract.CommitMeta` (optional)
- `url` — string (optional)
- `verification` — `contract.PayloadCommitVerification` (optional)

## `RepoTopicOptions`

- `topics` — list of string (optional)

## `RepoTransfer`

- `doer` — `contract.User` (optional)
- `recipient` — `contract.User` (optional)
- `teams` — list of `contract.Team` (optional)

## `Repository`

- `allow_fast_forward_only_merge` — boolean (optional)
- `allow_manual_merge` — boolean (optional)
- `allow_merge_commits` — boolean (optional)
- `allow_merge_update` — boolean (optional)
- `allow_rebase` — boolean (optional)
- `allow_rebase_explicit` — boolean (optional)
- `allow_rebase_update` — boolean (optional)
- `allow_squash_merge` — boolean (optional)
- `archived` — boolean (optional)
- `archived_at` — timestamp (optional)
- `autodetect_manual_merge` — boolean (optional)
- `avatar_url` — string (optional)
- `branch_count` — integer (optional)
- `clone_url` — string (optional)
- `created_at` — timestamp (optional)
- `default_allow_maintainer_edit` — boolean (optional)
- `default_branch` — string (optional)
- `default_delete_branch_after_merge` — boolean (optional)
- `default_merge_style` — string (optional)
- `default_target_branch` — string (optional)
- `default_update_style` — string (optional)
- `description` — string (optional)
- `empty` — boolean (optional)
- `external_tracker` — `contract.ExternalTracker` (optional)
- `external_wiki` — `contract.ExternalWiki` (optional)
- `fork` — boolean (optional)
- `forks_count` — integer (optional)
- `full_name` — string (optional)
- `has_actions` — boolean (optional)
- `has_code` — boolean (optional)
- `has_issues` — boolean (optional)
- `has_packages` — boolean (optional)
- `has_projects` — boolean (optional)
- `has_pull_requests` — boolean (optional)
- `has_releases` — boolean (optional)
- `has_wiki` — boolean (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `ignore_whitespace_conflicts` — boolean (optional)
- `internal` — boolean (optional)
- `internal_tracker` — `contract.InternalTracker` (optional)
- `language` — string (optional)
- `languages_url` — string (optional)
- `licenses` — list of string (optional)
- `link` — string (optional)
- `mirror` — boolean (optional)
- `mirror_interval` — string (optional)
- `mirror_last_sync_at` — timestamp (optional)
- `mirror_updated` — timestamp (optional)
- `name` — string (optional)
- `object_format_name` — string (optional)
- `open_issues_count` — integer (optional)
- `open_pr_counter` — integer (optional)
- `original_url` — string (optional)
- `owner` — `contract.User` (optional)
- `parent` — `contract.Repository` (optional)
- `permissions` — `contract.Permission` (optional)
- `private` — boolean (optional)
- `projects_mode` — string (optional)
- `release_counter` — integer (optional)
- `repo_transfer` — `contract.RepoTransfer` (optional)
- `size` — integer (optional)
- `ssh_url` — string (optional)
- `stars_count` — integer (optional)
- `template` — boolean (optional)
- `topics` — list of string (optional)
- `updated_at` — timestamp (optional)
- `url` — string (optional)
- `watchers_count` — integer (optional)
- `website` — string (optional)

## `RepositoryMeta`

- `full_name` — string (optional)
- `id` — integer (optional)
- `name` — string (optional)
- `owner` — string (optional)

## `RunDetails`

- `html_url` — string (optional)
- `run_url` — string (optional)
- `workflow_run_id` — integer (optional)

## `SearchResults`

- `data` — list of `contract.Repository` (optional)
- `ok` — boolean (optional)

## `Secret`

- `created_at` — timestamp (optional)
- `description` — string (optional)
- `name` — string (optional)

## `ServerVersion`

- `version` — string (optional)

## `StopWatch`

- `created` — timestamp (optional)
- `duration` — string (optional)
- `issue_index` — integer (optional)
- `issue_title` — string (optional)
- `repo_name` — string (optional)
- `repo_owner_name` — string (optional)
- `seconds` — integer (optional)

## `SubmitPullReviewOptions`

- `body` — string (optional)
- `event` — string (optional)

## `Tag`

- `commit` — `contract.CommitMeta` (optional)
- `id` — string (optional)
- `message` — string (optional)
- `name` — string (optional)
- `tarball_url` — string (optional)
- `zipball_url` — string (optional)

## `TagProtection`

- `created_at` — timestamp (optional)
- `id` — integer (optional)
- `name_pattern` — string (optional)
- `updated_at` — timestamp (optional)
- `whitelist_teams` — list of string (optional)
- `whitelist_usernames` — list of string (optional)

## `Team`

- `can_create_org_repo` — boolean (optional)
- `description` — string (optional)
- `id` — integer (optional)
- `includes_all_repositories` — boolean (optional)
- `name` — string (optional)
- `organization` — `contract.Organization` (optional)
- `permission` — string (optional)
- `units` — list of string (optional)
- `units_map` — map of string (optional)
- `visibility` — string (optional)

## `TimeStamp`

Value: integer.

## `TimelineComment`

- `assignee` — `contract.User` (optional)
- `assignee_team` — `contract.Team` (optional)
- `body` — string (optional)
- `created_at` — timestamp (optional)
- `dependent_issue` — `contract.Issue` (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `issue_url` — string (optional)
- `label` — `contract.Label` (optional)
- `milestone` — `contract.Milestone` (optional)
- `new_ref` — string (optional)
- `new_title` — string (optional)
- `old_milestone` — `contract.Milestone` (optional)
- `old_project_id` — integer (optional)
- `old_ref` — string (optional)
- `old_title` — string (optional)
- `project_id` — integer (optional)
- `pull_request_url` — string (optional)
- `ref_action` — string (optional)
- `ref_comment` — `contract.Comment` (optional)
- `ref_commit_sha` — string (optional)
- `ref_issue` — `contract.Issue` (optional)
- `removed_assignee` — boolean (optional)
- `resolve_doer` — `contract.User` (optional)
- `review_id` — integer (optional)
- `tracked_time` — `contract.TrackedTime` (optional)
- `type` — string (optional)
- `updated_at` — timestamp (optional)
- `user` — `contract.User` (optional)

## `TopicListResponse`

- `topics` — list of `contract.TopicResponse` (optional)

## `TopicName`

- `topics` — list of string (optional)

## `TopicResponse`

- `created` — timestamp (optional)
- `id` — integer (optional)
- `repo_count` — integer (optional)
- `topic_name` — string (optional)
- `updated` — timestamp (optional)

## `TrackedTime`

- `created` — timestamp (optional)
- `id` — integer (optional)
- `issue` — `contract.Issue` (optional)
- `issue_id` — integer (optional)
- `time` — integer (optional)
- `user_id` — integer (optional)
- `user_name` — string (optional)

## `TransferRepoOption`

- `new_owner` — string (required)
- `team_ids` — list of integer (optional)

## `UpdateBranchProtectionPriories`

- `ids` — list of integer (optional)

## `UpdateBranchRepoOption`

- `force` — boolean (optional)
- `new_commit_id` — string (required)
- `old_commit_id` — string (optional)

## `UpdateFileOptions`

- `author` — `contract.Identity` (optional)
- `branch` — string (optional)
- `committer` — `contract.Identity` (optional)
- `content` — string (required)
- `dates` — `contract.CommitDateOptions` (optional)
- `force_push` — boolean (optional)
- `from_path` — string (optional)
- `message` — string (optional)
- `new_branch` — string (optional)
- `sha` — string (optional)
- `signoff` — boolean (optional)

## `UpdateRepoAvatarOption`

- `image` — string (optional)

## `UpdateUserAvatarOption`

- `image` — string (optional)

## `UpdateVariableOption`

- `description` — string (optional)
- `name` — string (optional)
- `value` — string (required)

## `User`

- `active` — boolean (optional)
- `avatar_url` — string (optional)
- `created` — timestamp (optional)
- `description` — string (optional)
- `email` — string (optional)
- `followers_count` — integer (optional)
- `following_count` — integer (optional)
- `full_name` — string (optional)
- `html_url` — string (optional)
- `id` — integer (optional)
- `is_admin` — boolean (optional)
- `language` — string (optional)
- `last_login` — timestamp (optional)
- `location` — string (optional)
- `login` — string (optional)
- `login_name` — string (optional)
- `prohibit_login` — boolean (optional)
- `restricted` — boolean (optional)
- `source_id` — integer (optional)
- `starred_repos_count` — integer (optional)
- `visibility` — string (optional)
- `website` — string (optional)

## `UserBadgeOption`

- `badge_slugs` — list of string (optional)

## `UserHeatmapData`

- `contributions` — integer (optional)
- `timestamp` — `contract.TimeStamp` (optional)

## `UserMeta`

- `id` — integer (optional)
- `login` — string (optional)

## `UserSettings`

- `description` — string (optional)
- `diff_view_style` — string (optional)
- `full_name` — string (optional)
- `hide_activity` — boolean (optional)
- `hide_email` — boolean (optional)
- `language` — string (optional)
- `location` — string (optional)
- `theme` — string (optional)
- `website` — string (optional)

## `UserSettingsOptions`

- `description` — string (optional)
- `diff_view_style` — string (optional)
- `full_name` — string (optional)
- `hide_activity` — boolean (optional)
- `hide_email` — boolean (optional)
- `language` — string (optional)
- `location` — string (optional)
- `theme` — string (optional)
- `website` — string (optional)

## `WatchInfo`

- `created_at` — timestamp (optional)
- `ignored` — boolean (optional)
- `reason` — unspecified JSON value (optional)
- `repository_url` — string (optional)
- `subscribed` — boolean (optional)
- `url` — string (optional)

## `WikiCommit`

- `author` — `contract.CommitUser` (optional)
- `commiter` — `contract.CommitUser` (optional)
- `message` — string (optional)
- `sha` — string (optional)

## `WikiCommitList`

- `commits` — list of `contract.WikiCommit` (optional)
- `count` — integer (optional)

## `WikiPage`

- `commit_count` — integer (optional)
- `content_base64` — string (optional)
- `footer` — string (optional)
- `html_url` — string (optional)
- `last_commit` — `contract.WikiCommit` (optional)
- `sidebar` — string (optional)
- `sub_url` — string (optional)
- `title` — string (optional)

## `WikiPageMetaData`

- `html_url` — string (optional)
- `last_commit` — `contract.WikiCommit` (optional)
- `sub_url` — string (optional)
- `title` — string (optional)
