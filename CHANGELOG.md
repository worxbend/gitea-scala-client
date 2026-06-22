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
- Read-only repository collaborator APIs for
  `GET /repos/{owner}/{repo}/collaborators`
  (`repoListCollaborators`),
  `GET /repos/{owner}/{repo}/collaborators/{collaborator}`
  (`repoCheckCollaborator`), and
  `GET /repos/{owner}/{repo}/collaborators/{collaborator}/permission`
  (`repoGetRepoPermissions`) with `ReposApi.collaborators`,
  `ReposApi.isCollaborator`, and `ReposApi.collaboratorPermission`. The
  collaborator list streams paginated `User` values, the check endpoint maps
  `204` to `true` and endpoint-specific `404` to `false`, and the permission
  endpoint decodes `RepoCollaboratorPermission`. These methods are read-only
  retryable; collaborator add/delete/write operations remain out of scope.
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
- Typed commit-to-pull-request lookup for
  `GET /repos/{owner}/{repo}/commits/{sha}/pull` with
  `repoGetCommitPullRequest`, `GiteaRequests.repoCommitPullRequest`, and
  `PullRequestsApi.commitPullRequest`; the read-only request is retryable and
  decodes a single `PullRequest`.
- Typed single-commit lookup for
  `GET /repos/{owner}/{repo}/git/commits/{sha}` with
  `SingleCommitParams`, `repoGetSingleCommit`,
  `GiteaRequests.repoSingleCommit`, and `ReposApi.commit`; the read-only
  request is retryable, omits `stat`, `verification`, and `files` by default,
  and encodes explicitly supplied boolean controls.
- Typed commit note lookup for
  `GET /repos/{owner}/{repo}/git/notes/{sha}` with `Note`,
  `CommitNoteParams`, `repoGetNote`, `GiteaRequests.repoCommitNote`, and
  `ReposApi.commitNote`; the read-only request is retryable, omits
  `verification` and `files` by default, decodes a single `Note`, and
  propagates documented `404`/`422` failures through the shared error mapper.
- Typed Git tree lookup for
  `GET /repos/{owner}/{repo}/git/trees/{sha}` with `GitTreeResponse`,
  `GitEntry`, `GitTreeParams`, `GetTree` endpoint metadata,
  `GiteaRequests.gitTree`, and `ReposApi.gitTree`; the read-only request is
  retryable, omits `recursive`, `page`, and `per_page` by default, preserves
  the Swagger body-pagination response shape through `page`, `total_count`,
  and `truncated`, and propagates documented `400`/`404` failures through the
  shared error mapper.
- `ReposApi.gitTree` now has a `GitTreeParams.default` default argument,
  matching the surrounding `commit` and `commitNote` repository Git facades.
- Typed Git blob lookup for
  `GET /repos/{owner}/{repo}/git/blobs/{sha}` with `GitBlobResponse`,
  `GetBlob` endpoint metadata, `GiteaRequests.gitBlob`, and
  `ReposApi.gitBlob`; the read-only request has no query parameters or request
  body, keeps blob content as the documented encoded string, and propagates
  documented `400`/`404` failures through the shared error mapper.
- Typed annotated Git tag lookup for
  `GET /repos/{owner}/{repo}/git/tags/{sha}` with `AnnotatedTag`,
  `AnnotatedTagObject`, `GetAnnotatedTag` endpoint metadata,
  `GiteaRequests.annotatedTag`, and `ReposApi.annotatedTag`; the read-only
  request has no query parameters or request body, decodes the Swagger
  annotated-tag object response including `object`, `tagger`, and
  `verification`, stays distinct from repository tag-list/lightweight tag
  handling, and propagates documented `400`/`404` failures through the shared
  error mapper.
- Typed Git reference lookup for
  `GET /repos/{owner}/{repo}/git/refs` and
  `GET /repos/{owner}/{repo}/git/refs/{ref}` with `Reference`, `GitObject`,
  `repoListAllGitRefs`, `repoListGitRefs`, non-paginated `ReferenceList`
  decoding, and overloaded `ReposApi.gitRefs` facade methods; the read-only
  requests have no query parameters or request bodies, encode slash-containing
  refs such as `heads/main` as one path segment, and propagate documented `404`
  failures through the shared error mapper.
- Typed repository contents metadata lookup for
  `GET /repos/{owner}/{repo}/contents` and
  `GET /repos/{owner}/{repo}/contents/{filepath}` with `ContentsResponse`,
  nested `FileLinksResponse`, `ContentsParams`, `repoGetContentsList`,
  `repoGetContents`, non-paginated `ContentsListResponse` decoding, single
  `ContentsResponse` decoding, and overloaded `ReposApi.contents` facade
  methods; the read-only requests omit `ref` by default, encode supplied
  `ref` query values, encode slash-containing filepaths such as
  `docs/readme.md` as one path segment, keep `content` as the encoded string
  returned by Gitea, and propagate documented `404` failures through the
  shared error mapper.
- Binary-safe raw response handling for successful `application/octet-stream`
  bodies, returning `Chunk[Byte]` without passing byte payloads through JSON or
  `String` response assumptions while preserving existing JSON, text, unit,
  pagination, and error mapping behavior.
- Raw and media repository file byte downloads for
  `GET /repos/{owner}/{repo}/raw/{filepath}` (`repoGetRawFile`) and
  `GET /repos/{owner}/{repo}/media/{filepath}` (`repoGetRawFileOrLFS`) with
  `GiteaRequests.repoRawFile`, `GiteaRequests.repoMediaFile`,
  `ReposApi.rawFile`, and `ReposApi.mediaFile`; both read-only APIs return
  `Chunk[Byte]`, accept `ContentsParams` for optional `ref`, encode
  slash-containing filepaths such as `docs/readme.md` as one path segment,
  send `Accept: application/octet-stream`, send no request body, and propagate
  documented `404` failures through the shared error mapper.
- Repository archive byte downloads for
  `GET /repos/{owner}/{repo}/archive/{archive}` (`repoGetArchive`) with
  `GiteaRequests.repoGetArchive` and `ReposApi.archive`; the read-only API
  returns buffered `Chunk[Byte]`, accepts archive values such as `main.zip` and
  `v1.0.0.tar.gz`, encodes slash-containing archive values as one path
  segment, accepts `ArchiveParams` for repeated `path` query values such as
  `path=src&path=docs/readme.md`, omits query parameters by default for whole
  archive downloads, sends `Accept: application/octet-stream`, sends no request
  body, and propagates documented `404` failures through the shared error
  mapper. This is a pragmatic buffered byte-download facade; the local
  `plugin-redoc-2.yaml` operation records `produces: application/json` and a
  bare `200` success description, not an `application/octet-stream` Swagger
  `type: file` response schema.
- Release asset metadata reads for
  `GET /repos/{owner}/{repo}/releases/{id}/assets`
  (`repoListReleaseAttachments`) and
  `GET /repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}`
  (`repoGetReleaseAttachment`) with `ReleaseAsset`,
  `GiteaRequests.repoReleaseAssets`, `GiteaRequests.repoReleaseAsset`,
  `ReleasesApi.releaseAssets`, and `ReleasesApi.releaseAsset`; the read-only
  list decodes the non-paginated Swagger `AttachmentList` response as
  `Chunk[ReleaseAsset]`, has no documented query params, and the detail lookup
  decodes one `Attachment` as `ReleaseAsset`.
- Release-by-tag metadata lookup for
  `GET /repos/{owner}/{repo}/releases/tags/{tag}` (`repoGetReleaseByTag`) with
  `GiteaRequests.repoReleaseByTag` and `ReleasesApi.releaseByTag`; the
  read-only request is retryable, has no query parameters or request body,
  returns `Release` metadata, and encodes punctuation-heavy tags such as
  `v1.0.0` and slash-containing tags such as `release/candidate` as one path
  segment. Release create/edit/delete and release asset
  upload/edit/delete/download surfaces remain out of scope.
- Latest-release metadata lookup for
  `GET /repos/{owner}/{repo}/releases/latest` (`repoGetLatestRelease`) with
  `GiteaRequests.repoLatestRelease` and
  `ReleasesApi.latestRelease(owner, repo)`; the read-only request is retryable,
  has no query parameters or request body, returns the server-selected latest
  `Release` metadata, and propagates documented `404` failures through the
  shared error mapper.
- Release confidence hardening for the existing read-only release metadata
  surface: Swagger audit coverage now explicitly includes release list/detail
  (`repoListReleases`, `repoGetRelease`) and latest-release
  (`repoGetLatestRelease`) alongside release-by-tag and release asset metadata
  endpoints, and opt-in live probes document the variables for release detail,
  release-by-tag, release asset list, and single release asset metadata
  lookups. This adds validation coverage only; it does not add release
  create/edit/delete, release asset upload/edit/delete, or asset binary
  download APIs.
- Typed release-list filtering for
  `GET /repos/{owner}/{repo}/releases` (`repoListReleases`) with
  `ReleaseListParams`, `GiteaRequests.repoReleases`, and
  `ReleasesApi.releases(owner, repo, params)`. The read-only stream keeps
  existing `client.releases(owner, repo)` call sites source-compatible through
  `ReleaseListParams.default`, omits absent filters by default, preserves the
  wire query name `pre-release` through the Scala field `preRelease`, and
  preserves explicit `draft`, `pre-release`, and page-size `limit` controls.
  The high-level stream facade always starts at page 1 through the shared
  pagination helper; `ReleaseListParams.page` remains a low-level
  request-builder control for `GiteaRequests.repoReleases`, not a
  `client.releases` stream start-page selector.
- Typed commit diff/patch downloads for
  `GET /repos/{owner}/{repo}/git/commits/{sha}.{diffType}` with
  `CommitDiffType.diff`, `CommitDiffType.patch`,
  `repoDownloadCommitDiffOrPatch`, `GiteaRequests.repoCommitDiffOrPatch`, and
  `ReposApi.commitDiffOrPatch`; the read-only request is retryable, accepts
  `text/plain`, decodes successful responses as raw `String` content, and
  propagates the documented `404` through the shared error mapper.
- Pull-request create/edit endpoint metadata audit coverage for operation IDs,
  methods, paths, required path parameters, success responses, request-body
  presence, retryability, and documented non-2xx response status/ref labels.
- Commit-to-pull-request endpoint metadata audit coverage for operation ID,
  method, path, required path parameters, success response, request-body
  absence, retryability, and documented non-2xx response status/ref labels.
- Single-commit endpoint metadata audit coverage for operation ID, method, path,
  required path parameters, optional `stat`/`verification`/`files` query
  parameters, success response, request-body absence, retryability, and
  documented 404/422 response status/ref labels.
- Commit note endpoint metadata audit coverage for operation ID, method, path,
  required path parameters, optional `verification`/`files` query parameters,
  success response, request-body absence, retryability, and documented 404/422
  response status/ref labels.
- Git tree endpoint metadata audit coverage for uppercase operation ID
  `GetTree`, method, path, required `owner`/`repo`/`sha` path parameters,
  optional `recursive`/`page`/`per_page` query parameters, success response,
  request-body absence, retryability, and documented 400/404 response
  status/ref labels.
- Git blob endpoint metadata audit coverage for uppercase operation ID
  `GetBlob`, method, path, required `owner`/`repo`/`sha` path parameters, no
  query parameters, success response, request-body absence, retryability, and
  documented 400/404 response status/ref labels.
- Annotated Git tag endpoint metadata audit coverage for uppercase operation ID
  `GetAnnotatedTag`, method, path, required `owner`/`repo`/`sha` path
  parameters, no query parameters, success response `AnnotatedTag`,
  request-body absence, retryability, and documented 400/404 response
  status/ref labels.
- Git refs endpoint metadata audit coverage for operation IDs
  `repoListAllGitRefs` and `repoListGitRefs`, methods, paths, required
  path parameters, no query parameters, success response `ReferenceList`,
  request-body absence, retryability, and documented 404 response status/ref
  labels.
- Repository contents endpoint metadata audit coverage for operation IDs
  `repoGetContentsList` and `repoGetContents`, methods, paths, required
  `owner`/`repo`/`filepath` path parameters, optional `ref` query parameter,
  success responses `ContentsListResponse` and `ContentsResponse`,
  request-body absence, retryability, and documented 404 response status/ref
  labels.
- Raw/media repository file endpoint metadata audit coverage for operation IDs
  `repoGetRawFile` and `repoGetRawFileOrLFS`, methods, paths, required
  `owner`/`repo`/`filepath` path parameters, optional `ref` query parameter,
  absence of request bodies, read-only retryability, success response shape as
  Swagger `type: file` / `application/octet-stream`, and documented 404
  response status/ref labels.
- Repository archive endpoint metadata audit coverage for operation ID
  `repoGetArchive`, method, path, required `owner`/`repo`/`archive` path
  parameters, optional repeated `path` query parameter, absence of request
  bodies, read-only retryability, bare `200` success description from
  `plugin-redoc-2.yaml`, and documented 404 response status/ref labels.
- Release asset endpoint metadata audit coverage for operation IDs
  `repoListReleaseAttachments` and `repoGetReleaseAttachment`, methods, paths,
  required `owner`/`repo`/`id`/`attachment_id` path parameters, no query
  parameters, absence of request bodies, read-only retryability, success
  response refs `AttachmentList` and `Attachment`, and documented 404 response
  status/ref labels.
- Release list/detail/latest endpoint metadata audit coverage for operation
  IDs `repoListReleases`, `repoGetRelease`, and `repoGetLatestRelease`,
  methods, paths, required `owner`/`repo` path parameters plus release-detail
  `id`, optional release-list query parameters `draft`, `pre-release`, `page`,
  and `limit`, absence of request bodies, read-only retryability, success
  response refs `ReleaseList` and `Release`, and documented non-2xx response
  status/ref labels.
- Commit diff/patch endpoint metadata audit coverage for operation ID, method,
  path, required `owner`/`repo`/`sha`/`diffType` path parameters, success
  response, documented 404 response status/ref label, request-body absence,
  retryability, and proof that the operation has no query parameters.
- Path enum audit coverage for commit and pull-request diff/patch `diffType`
  values, comparing local typed path values against `plugin-redoc-2.yaml`
  without adding audit-only fields to published endpoint metadata.
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
- Explicit `GiteaError.PreconditionFailed` with response mapping for documented
  412 conditional-write failures while preserving decoded payload messages and
  raw bodies.
- Mapper-level tests for global 405/423 classification with JSON error payloads,
  empty bodies, and non-JSON raw bodies.
- Mapper-level tests for global 412 classification with JSON error payloads,
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
- Opt-in live integration probes for slash-containing Git refs and annotated
  tag lookup. The Git ref probe requires `GITEA_URL`, `GITEA_TOKEN`,
  `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_REF` such as `heads/main`; the
  annotated tag probe requires `GITEA_ANNOTATED_TAG_SHA` and does not infer
  annotated tag object SHAs from repository tag listings.
- Opt-in live integration probe for repository contents filepath routing
  through `ReposApi.contents(owner, repo, filepath, ContentsParams)`, gated on
  non-empty `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and
  `GITEA_CONTENTS_FILEPATH`, with optional `GITEA_CONTENTS_REF` passed through
  as `ContentsParams(ref = Some(value))`.
- Opt-in live integration probes for existing release metadata lookups. The
  release detail and release asset list probes require `GITEA_RELEASE_ID`, the
  release-by-tag probe requires `GITEA_RELEASE_TAG`, and the single release
  asset probe requires both `GITEA_RELEASE_ID` and
  `GITEA_RELEASE_ASSET_ID`, in addition to non-empty `GITEA_URL`,
  `GITEA_TOKEN`, `GITEA_OWNER`, and `GITEA_REPO`.
- Release asset-list live probe confidence now improves when
  `GITEA_RELEASE_ASSET_ID` is configured by asserting that the listed release
  assets include the configured id; without that variable, the probe remains a
  read-only endpoint check that can accept an empty list.
- Opt-in latest-release live confidence probe for the existing
  `client.latestRelease(owner, repo)` facade. The probe runs only when
  `GITEA_LATEST_RELEASE_TAG` is configured alongside the required live
  repository credentials, and asserts that the returned tag matches the
  repository's actual latest non-draft, non-prerelease release tag.
- Opt-in release-by-tag live confidence probe for the existing read-only
  `client.releaseByTag(owner, repo, tag)` facade. This is validation coverage
  for already implemented release metadata lookup, not a new release API. The
  probe is gated by `GITEA_RELEASE_TAG` plus non-empty `GITEA_URL`,
  `GITEA_TOKEN`, `GITEA_OWNER`, and `GITEA_REPO`. A live run with a normal tag
  such as `v1.0.0` is only generic release-by-tag confidence; slash-containing
  release-tag routing for values such as `release/candidate` remains pending
  live observation until an enabled probe is run with a slash-containing
  `GITEA_RELEASE_TAG`.
- Test-side schema-field checklist coverage for recent Swagger Git response
  models: `Reference`, `GitObject`, `AnnotatedTag`, `AnnotatedTagObject`, and
  `GitBlobResponse`.
- Test-side schema-field checklist coverage for Swagger repository contents
  response models: `ContentsResponse` and nested `FileLinksResponse`.
- Test-side schema-field checklist coverage for Swagger release attachment
  metadata through the public `ReleaseAsset` model.
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
- Git refs slice validation passed with `./mill core.test`, `./mill client.test`,
  `./mill compatibility.check`, and
  `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill __.test it.test examples.run`;
  the credential-stripped run reported live integration tests as ignored.
- Repository contents metadata validation passed with `git diff --check`,
  `./mill --no-server core.test client.test compatibility.check`, and
  `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`.
- Raw/media repository file documentation and snapshot validation passed with
  `git diff --check`, `./mill --no-server compatibility.writeSnapshot`,
  `./mill --no-server core.test client.test compatibility.check`, and
  `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.http.GiteaResponseMapperSpec io.worxbend.gitea4s.GiteaClientSpec`.
- Credential-stripped integration validation passed with
  `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill --no-server it.test`; all five live probes were reported as ignored and no live credentials were required.
- Release asset metadata documentation and snapshot validation passed with
  `git diff --check`, `./mill --no-server compatibility.writeSnapshot`,
  `./mill --no-server core.test client.test compatibility.check`, focused
  request/audit/facade specs, and credential-stripped integration testing with
  `GITEA_ARCHIVE_PATHS` unset alongside the other live-probe variables; all
  seven live probes were reported as ignored.
- Release audit and live-probe documentation now records
  `GITEA_RELEASE_ID`, `GITEA_RELEASE_TAG`, and `GITEA_RELEASE_ASSET_ID`; the
  credential-stripped integration command unsets those release variables
  alongside the existing live-probe variables.
- Release-list filtering documentation is aligned with `ReleaseListParams`.
  Documentation whitespace validation passed with `git diff --check`; full
  implementation validation is expected through
  `./mill --no-server compatibility.writeSnapshot`,
  `git diff --check`,
  `./mill --no-server core.test client.test compatibility.check`, focused
  request/audit/facade specs, and credential-stripped `it.test`.
