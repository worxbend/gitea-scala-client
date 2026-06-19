# Changelog

All notable changes to this project will be documented in this file.

This project follows semantic versioning after `1.0.0`. Before `1.0.0`, minor
versions may change source or binary compatibility while the typed Gitea API
surface is still being filled out.

## Unreleased

### Added

- Mill-built Scala 3 rewrite under the `io.worxbend.gitea4s` package root.
- Typed core models, zio-json codecs, and the `GiteaError` ADT.
- Read-only ZIO client APIs for users, organizations, repositories, issues,
  releases, pull requests, and notifications.
- Typed commit-status models and repository APIs with `CommitStatus`,
  `CombinedStatus`, `CreateStatusOption`, `CommitStatusState`,
  `CommitStatusListParams`, and request construction for
  `repoGetCombinedStatusByRef`, `repoListStatusesByRef`, `repoListStatuses`,
  and `repoCreateStatus`.
- Commit-status endpoint metadata audit coverage for operation IDs, methods,
  paths, required path parameters, success responses, body presence,
  retryability, and the documented sort/state query enum values.
- Public combined-status pagination controls through `CombinedStatusParams` on
  `ReposApi.combinedStatusByRef`.
- Typed pull-request merge, scheduled auto-merge cancellation, and branch update
  commands with `MergePullRequestOption`, `MergePullRequestMethod`,
  `PullRequestUpdateStyle`, `repoMergePullRequest`,
  `repoCancelScheduledAutoMerge`, and `repoUpdatePullRequest`.
- Typed pull-request creation and editing with `CreatePullRequestOption`,
  `EditPullRequestOption`, `repoCreatePullRequest`, `repoEditPullRequest`,
  `PullRequestsApi.createPullRequest`, and `PullRequestsApi.editPullRequest`;
  the POST/PATCH request builders send JSON bodies and remain non-retryable
  writes.
- Pull-request create/edit endpoint metadata audit coverage for operation IDs,
  methods, paths, required path parameters, success responses, request-body
  presence, retryability, and documented non-2xx response status/ref labels.
- Pull-request merge/update endpoint metadata audit coverage for operation IDs,
  methods, paths, required path parameters, success responses, request-body
  presence, retryability, `repoUpdatePullRequest` `style` enum values, and
  documented non-2xx response status/ref labels.
- Documented non-2xx response status/ref label audit coverage for the already
  audited pull-request review lifecycle and commit-status endpoint groups.
- Audit-only non-success response metadata kept out of the published client API:
  `GiteaEndpoint` no longer exposes `nonSuccessResponses`, and
  `GiteaResponseLabel` is confined to endpoint audit tests.
- Reusable Swagger audit helpers that fail loudly when path, method, or
  parameter lookup cannot be matched against `plugin-redoc-2.yaml`.
- Explicit `GiteaError.MethodNotAllowed` and `GiteaError.Locked` cases with
  response mapping for documented 405/423 resource-state failures while
  preserving decoded payload messages and raw bodies.
- Mapper-level tests for global 405/423 classification with JSON error payloads,
  empty bodies, and non-JSON raw bodies.
- Typed issue creation with `CreateIssue` and `issueCreateIssue` request
  construction.
- Typed issue deletion with `issueDelete` request construction.
- Typed issue pinning, unpinning, and pin moving with `pinIssue`,
  `unpinIssue`, and `moveIssuePin` request construction.
- Typed pinned issue listing and repository pin-capacity checks with
  `repoListPinnedIssues`, `repoNewPinAllowed`, and `NewIssuePinsAllowed`.
- Typed pinned pull-request listing with `repoListPinnedPullRequests` request
  construction.
- Typed pull-request lookup by base and head with
  `repoGetPullRequestByBaseHead` request construction.
- Typed pull-request changed-file streaming with `ChangedFile`,
  `PullRequestFilesParams`, and `repoGetPullRequestFiles` request construction.
- Typed pull-request commit streaming with `Commit`, `RepoCommit`,
  `PullRequestCommitsParams`, and `repoGetPullRequestCommits` request
  construction.
- Typed pull-request diff/patch downloads with `PullRequestDiffType` and
  `repoDownloadPullDiffOrPatch` request construction.
- Typed pull-request merge status checks with `repoPullRequestIsMerged` request
  construction and endpoint-specific 204/404 boolean decoding.
- Typed pull-request review streaming with `PullReview`, `PullReviewState`, and
  `repoListPullReviews` request construction.
- Typed pull-request review detail, comment listing, and deletion with
  `PullReviewComment`, `repoGetPullReview`, `repoGetPullReviewComments`, and
  `repoDeletePullReview` request construction.
- Typed pull-request review request creation and cancellation with
  `PullReviewRequestOptions`, `repoCreatePullReviewRequests`, and
  `repoDeletePullReviewRequests` request construction.
- Typed pull-request review creation, submission, dismissal, and undismissal
  with `CreatePullReviewOptions`, `SubmitPullReviewOptions`,
  `DismissPullReviewOptions`, `repoCreatePullReview`, `repoSubmitPullReview`,
  `repoDismissPullReview`, and `repoUnDismissPullReview` request construction.
- Typed pull-request review-comment resolution and unresolution with
  `repoResolvePullReviewComment` and `repoUnresolvePullReviewComment` request
  construction.
- Typed issue editing and closing with `EditIssue` and `issueEditIssue` request
  construction.
- Typed issue comments with `CreateIssueComment` and `issueCreateComment` request
  construction.
- Typed issue comment listing, lookup, editing, and deletion with
  `IssueCommentListParams`, `RepositoryCommentListParams`, `EditIssueComment`,
  and schema-traceable issue comment request construction.
- Typed issue and issue-comment reactions with `Reaction`, `EditReactionOption`,
  and schema-traceable reaction request construction.
- Typed issue subscriptions with `WatchInfo`, paginated subscriber listing, and
  schema-traceable subscription request construction.
- Typed issue tracked-time listing, addition, reset, and deletion with
  `TrackedTime`, `AddTimeOption`, `IssueTrackedTimeListParams`, and
  schema-traceable tracked-time request construction.
- Typed current-user stopwatch listing and issue stopwatch start/stop/delete
  commands with `StopWatch` and schema-traceable stopwatch request construction.
- Typed issue label listing, replacement, addition, clearing, and removal with
  `IssueLabelsOption` and schema-traceable issue label request construction.
- Typed issue locking and unlocking with `LockIssueOption` and
  schema-traceable issue lock request construction.
- Typed issue deadline editing and clearing with `EditDeadlineOption`,
  `IssueDeadline`, and schema-traceable deadline request construction.
- Typed issue dependency and blocking relationship management with `IssueMeta`
  and schema-traceable dependency/blocking request construction.
- Primary Java `HttpClient` backend through sttp `HttpClientZioBackend`.
- Optional OkHttp backend bridge isolated in `backend-okhttp`.
- Environment and Typesafe config loading, retry handling, examples, and
  opt-in live integration tests.
- Local Maven publishing metadata, source jars, and javadoc jars.
- Java 21 CI validation and release-process documentation.
- Sonatype Central Portal publishing groundwork through Mill and a manual
  GitHub Actions workflow.
- Renovate regex managers for Mill, Scala, ZIO, zio-json, zio-config, and sttp
  version pins.
- Checked-in public API snapshots and a Mill `compatibility.check` release
  guard for the published modules.

### Notes

- No Maven Central release has been cut yet. Local snapshots use
  `0.1.0-SNAPSHOT`.
