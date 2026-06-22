# Gitea4s From-Scratch Rewrite Plan

## Non-Negotiable Direction

This project must be rewritten from scratch as a Mill-built Scala project.

Use Mill only.

Do not modernize, extend, or depend on an SBT build as the implementation path.

Do not preserve the current generic request-option prototype as the foundation. The new client should use typed domain models, typed request parameters, sttp request builders, ZIO effects, and ZIO streams.

## Target

Library name: `gitea4s`

Package root: `io.worxbend.gitea4s`

Build tool: Mill

Effect system: ZIO 2.x

HTTP client abstraction: sttp client4

Primary backend: `HttpClientZioBackend`

JSON: `zio-json` through sttp's `zio-json` integration

JVM baseline: Java 21

Initial Scala baseline: Scala 3.x

API target: Gitea API v1.26.2, using the local `plugin-redoc-2.yaml` Swagger 2.0 file as the required API documentation reference.

## Current Repo State

The repository now has a compiling Mill-based Scala 3 skeleton for `gitea4s`.
`./mill` is the authoritative build entrypoint.

Existing files of note:

```text
mill
build.mill
core/src/io/worxbend/gitea4s/model/ApiReference.scala
core/test/src/io/worxbend/gitea4s/model/ApiReferenceSpec.scala
client/src/io/worxbend/gitea4s/GiteaClient.scala
client/src/io/worxbend/gitea4s/GiteaConfig.scala
client/src/io/worxbend/gitea4s/api/UsersApi.scala
client/src/io/worxbend/gitea4s/api/ReposApi.scala
client/src/io/worxbend/gitea4s/api/IssuesApi.scala
client/src/io/worxbend/gitea4s/api/PullRequestsApi.scala
client/src/io/worxbend/gitea4s/http/GiteaEndpoint.scala
client/src/io/worxbend/gitea4s/http/GiteaRequest.scala
client/src/io/worxbend/gitea4s/http/GiteaRequests.scala
client/src/io/worxbend/gitea4s/http/GiteaResponseMapper.scala
client/src/io/worxbend/gitea4s/http/IssueListParams.scala
client/src/io/worxbend/gitea4s/http/PullRequestListParams.scala
client/src/io/worxbend/gitea4s/http/UserSearchParams.scala
client/src/io/worxbend/gitea4s/internal/GiteaRequestExecutor.scala
client/src/io/worxbend/gitea4s/internal/Pagination.scala
client/src/io/worxbend/gitea4s/internal/SttpGiteaClient.scala
client/test/src/io/worxbend/gitea4s/GiteaClientSpec.scala
client/test/src/io/worxbend/gitea4s/http/GiteaRequestsSpec.scala
backend-zio/src/io/worxbend/gitea4s/backend/zio/ZioGiteaBackend.scala
backend-okhttp/src/io/worxbend/gitea4s/backend/okhttp/OkHttpGiteaBackend.scala
examples/src/io/worxbend/gitea4s/examples/ShowApiReference.scala
it/src/io/worxbend/gitea4s/it/IntegrationPlaceholder.scala
plugin-redoc-2.yaml
README.md
```

Current checkpoint:

- `build.mill` defines `core`, `client`, `backend-zio`, `backend-okhttp`, `examples`, and `it`.
- The checked-in `mill` launcher uses Mill `1.1.6`.
- Scala is pinned to `3.8.4`.
- Java compilation targets Java 21.
- Dependency versions are centralized in `build.mill`.
- ZIO Test is wired into the shared module trait.
- The old `io.kzonix.gitea.core.dto` prototype sources have been deleted from the compile path.
- `ApiReference.gitea1262` records that `plugin-redoc-2.yaml` is the local Gitea API `1.26.2` contract.
- Core now contains a schema-traceable first model/codecs slice for `User`, `Organization`, `Repository`, `Permission`, `Issue`, `CreateIssue`, `EditIssue`, `CreateIssueComment`, `EditIssueComment`, `Reaction`, `EditReactionOption`, `WatchInfo`, `AddTimeOption`, `TrackedTime`, `StopWatch`, `IssueMeta`, `IssueLabelsOption`, `LockIssueOption`, `EditDeadlineOption`, `IssueDeadline`, `Label`, `Milestone`, `Comment`, `PullRequest`, `CreatePullRequestOption`, `EditPullRequestOption`, `PullReview`, `PullReviewComment`, `CreatePullReviewComment`, `CreatePullReviewOptions`, `SubmitPullReviewOptions`, `DismissPullReviewOptions`, `PullReviewRequestOptions`, `MergePullRequestOption`, `ChangedFile`, `Commit`, `RepoCommit`, `CommitAffectedFile`, `CommitStats`, `CommitUser`, `CommitDiffType`, `Note`, `GitTreeResponse`, `GitEntry`, `GitBlobResponse`, `AnnotatedTag`, `AnnotatedTagObject`, `Reference`, `GitObject`, `ContentsResponse`, `FileLinksResponse`, `CommitStatus`, `CombinedStatus`, `CreateStatusOption`, `Release`, `ReleaseAsset`, `Branch`, `Tag`, `TopicNames`, `NewIssuePinsAllowed`, `NotificationCount`, `NotificationSubject`, `NotificationThread`, and `GiteaErrorPayload`.
- Core supporting types now include `Page`, `Auth`, and the `GiteaError` ADT, including explicit `MethodNotAllowed`, `PreconditionFailed`, and `Locked` cases for documented 405/412/423 responses. `CommitDiffType` is a closed path-value model for the documented commit diff/patch values `diff` and `patch`.
- `CoreModelsSpec` covers JSON decode and round-trip behavior for the first model slice, pull-request create/edit payload codecs, Git tree codecs, Git reference codecs, commit-status codecs, notification codecs, enum validation, pagination codec behavior, auth modes, and the error ADT.
- `CoreModelsSpec` also carries a test-side schema-field checklist for recent Swagger Git and repository contents response models (`Reference`, `GitObject`, `AnnotatedTag`, `AnnotatedTagObject`, `GitBlobResponse`, `ContentsResponse`, and `FileLinksResponse`) so missing local field expectations fail loudly without adding schema-field metadata to public production APIs.
- `GiteaConfig` now carries typed sttp `Uri`, `Auth`, timeout, page size, user agent, OTP, and retry settings.
- Client HTTP now has schema-traceable endpoint metadata and pure sttp request construction for `GET /user` (`userGetCurrent`), `GET /users/{username}` (`userGet`), `GET /users/search` (`userSearch`), `GET /users/{username}/followers` (`userListFollowers`), `GET /users/{username}/following` (`userListFollowing`), `GET /users/{username}/repos` (`userListRepos`), `GET /orgs/{org}` (`orgGet`), `GET /orgs/{org}/members` (`orgListMembers`), `GET /orgs/{org}/public_members` (`orgListPublicMembers`), `GET /orgs/{org}/repos` (`orgListRepos`), `GET /repos/{owner}/{repo}` (`repoGet`), `GET /repos/{owner}/{repo}/topics` (`repoListTopics`), `GET /repos/{owner}/{repo}/new_pin_allowed` (`repoNewPinAllowed`), `GET /repos/{owner}/{repo}/branches` (`repoListBranches`), `GET /repos/{owner}/{repo}/tags` (`repoListTags`), `GET /repos/{owner}/{repo}/issues` (`issueListIssues`), `GET /repos/{owner}/{repo}/issues/pinned` (`repoListPinnedIssues`), `GET/DELETE/PATCH /repos/{owner}/{repo}/issues/{index}` (`issueGetIssue`, `issueDelete`, `issueEditIssue`), `POST /repos/{owner}/{repo}/issues` (`issueCreateIssue`), `POST/DELETE /repos/{owner}/{repo}/issues/{index}/pin` (`pinIssue`, `unpinIssue`), `PATCH /repos/{owner}/{repo}/issues/{index}/pin/{position}` (`moveIssuePin`), `POST /repos/{owner}/{repo}/issues/{index}/comments` (`issueCreateComment`), `GET /repos/{owner}/{repo}/issues/{index}/comments` (`issueGetComments`), `GET /repos/{owner}/{repo}/issues/comments` (`issueGetRepoComments`), `GET/PATCH/DELETE /repos/{owner}/{repo}/issues/comments/{id}` (`issueGetComment`, `issueEditComment`, `issueDeleteComment`), `GET/POST/DELETE /repos/{owner}/{repo}/issues/comments/{id}/reactions` (`issueGetCommentReactions`, `issuePostCommentReaction`, `issueDeleteCommentReaction`), `GET /repos/{owner}/{repo}/issues/{index}/blocks` (`issueListBlocks`), `POST /repos/{owner}/{repo}/issues/{index}/blocks` (`issueCreateIssueBlocking`), `DELETE /repos/{owner}/{repo}/issues/{index}/blocks` (`issueRemoveIssueBlocking`), `GET /repos/{owner}/{repo}/issues/{index}/dependencies` (`issueListIssueDependencies`), `POST /repos/{owner}/{repo}/issues/{index}/dependencies` (`issueCreateIssueDependencies`), `DELETE /repos/{owner}/{repo}/issues/{index}/dependencies` (`issueRemoveIssueDependencies`), `GET /repos/{owner}/{repo}/issues/{index}/labels` (`issueGetLabels`), `PUT /repos/{owner}/{repo}/issues/{index}/labels` (`issueReplaceLabels`), `POST /repos/{owner}/{repo}/issues/{index}/labels` (`issueAddLabel`), `DELETE /repos/{owner}/{repo}/issues/{index}/labels` (`issueClearLabels`), `DELETE /repos/{owner}/{repo}/issues/{index}/labels/{id}` (`issueRemoveLabel`), `PUT /repos/{owner}/{repo}/issues/{index}/lock` (`issueLockIssue`), `DELETE /repos/{owner}/{repo}/issues/{index}/lock` (`issueUnlockIssue`), `POST /repos/{owner}/{repo}/issues/{index}/deadline` (`issueEditIssueDeadline`), `GET/POST/DELETE /repos/{owner}/{repo}/issues/{index}/reactions` (`issueGetIssueReactions`, `issuePostIssueReaction`, `issueDeleteIssueReaction`), `GET /repos/{owner}/{repo}/issues/{index}/subscriptions` (`issueSubscriptions`), `GET /repos/{owner}/{repo}/issues/{index}/subscriptions/check` (`issueCheckSubscription`), `PUT /repos/{owner}/{repo}/issues/{index}/subscriptions/{user}` (`issueAddSubscription`), `DELETE /repos/{owner}/{repo}/issues/{index}/subscriptions/{user}` (`issueDeleteSubscription`), `GET /repos/{owner}/{repo}/commits/{sha}/pull` (`repoGetCommitPullRequest`), `GET /repos/{owner}/{repo}/git/commits/{sha}` (`repoGetSingleCommit`), `GET /repos/{owner}/{repo}/git/commits/{sha}.{diffType}` (`repoDownloadCommitDiffOrPatch`), `GET /repos/{owner}/{repo}/git/notes/{sha}` (`repoGetNote`), `GET /repos/{owner}/{repo}/git/trees/{sha}` (`GetTree`), `GET /repos/{owner}/{repo}/git/blobs/{sha}` (`GetBlob`), `GET /repos/{owner}/{repo}/git/tags/{sha}` (`GetAnnotatedTag`), `GET /repos/{owner}/{repo}/git/refs` (`repoListAllGitRefs`), `GET /repos/{owner}/{repo}/git/refs/{ref}` (`repoListGitRefs`), `GET/POST /repos/{owner}/{repo}/pulls` (`repoListPullRequests`, `repoCreatePullRequest`), `GET /repos/{owner}/{repo}/pulls/pinned` (`repoListPinnedPullRequests`), `GET /repos/{owner}/{repo}/pulls/{base}/{head}` (`repoGetPullRequestByBaseHead`), `GET/PATCH /repos/{owner}/{repo}/pulls/{index}` (`repoGetPullRequest`, `repoEditPullRequest`), `GET /repos/{owner}/{repo}/pulls/{index}.{diffType}` (`repoDownloadPullDiffOrPatch`), `GET /repos/{owner}/{repo}/pulls/{index}/files` (`repoGetPullRequestFiles`), `GET /repos/{owner}/{repo}/pulls/{index}/commits` (`repoGetPullRequestCommits`), `GET /notifications` (`notifyGetList`), `GET /notifications/new` (`notifyNewAvailable`), and `GET /notifications/threads/{id}` (`notifyGetThread`).
- Client HTTP also has schema-traceable pull-request merge-status request construction for `GET /repos/{owner}/{repo}/pulls/{index}/merge` (`repoPullRequestIsMerged`), decoding 204 as merged and the endpoint-specific 404 as not merged.
- Client HTTP also has schema-traceable pull-request review-list/detail/comment/deletion request construction for `GET /repos/{owner}/{repo}/pulls/{index}/reviews` (`repoListPullReviews`), `GET/DELETE /repos/{owner}/{repo}/pulls/{index}/reviews/{id}` (`repoGetPullReview`, `repoDeletePullReview`), and `GET /repos/{owner}/{repo}/pulls/{index}/reviews/{id}/comments` (`repoGetPullReviewComments`).
- Client HTTP also has schema-traceable pull-request review-request management for `POST/DELETE /repos/{owner}/{repo}/pulls/{index}/requested_reviewers` (`repoCreatePullReviewRequests`, `repoDeletePullReviewRequests`) with JSON body construction, non-paginated review-list decoding, and 204/unit response decoding.
- Client HTTP also has schema-traceable pull-request review creation/submission/dismissal/undismissal request construction for `POST /repos/{owner}/{repo}/pulls/{index}/reviews` (`repoCreatePullReview`), `POST /repos/{owner}/{repo}/pulls/{index}/reviews/{id}` (`repoSubmitPullReview`), `POST /repos/{owner}/{repo}/pulls/{index}/reviews/{id}/dismissals` (`repoDismissPullReview`), and `POST /repos/{owner}/{repo}/pulls/{index}/reviews/{id}/undismissals` (`repoUnDismissPullReview`).
- Client HTTP also has schema-traceable pull-request review-comment resolution request construction for `POST /repos/{owner}/{repo}/pulls/comments/{id}/resolve` (`repoResolvePullReviewComment`) and `POST /repos/{owner}/{repo}/pulls/comments/{id}/unresolve` (`repoUnresolvePullReviewComment`) with 204/unit response decoding.
- Client HTTP also has schema-traceable pull-request create/edit request construction for `POST /repos/{owner}/{repo}/pulls` (`repoCreatePullRequest`) and `PATCH /repos/{owner}/{repo}/pulls/{index}` (`repoEditPullRequest`) with `CreatePullRequestOption`, `EditPullRequestOption`, JSON body construction, `PullRequest` response decoding, and non-retryable write semantics.
- Client HTTP also has schema-traceable pull-request merge/update request construction for `POST /repos/{owner}/{repo}/pulls/{index}/merge` (`repoMergePullRequest`), `DELETE /repos/{owner}/{repo}/pulls/{index}/merge` (`repoCancelScheduledAutoMerge`), and `POST /repos/{owner}/{repo}/pulls/{index}/update` (`repoUpdatePullRequest`) with `MergePullRequestOption`, `PullRequestUpdateStyle`, unit response decoding, non-retryable write semantics, and no-body lifecycle command coverage.
- Client HTTP also has schema-traceable issue tracked-time request construction for `GET/POST/DELETE /repos/{owner}/{repo}/issues/{index}/times` (`issueTrackedTimes`, `issueAddTime`, `issueResetTime`) and `DELETE /repos/{owner}/{repo}/issues/{index}/times/{id}` (`issueDeleteTime`).
- Client HTTP also has schema-traceable stopwatch request construction for `GET /user/stopwatches` (`userGetStopWatches`), `POST /repos/{owner}/{repo}/issues/{index}/stopwatch/start` (`issueStartStopWatch`), `POST /repos/{owner}/{repo}/issues/{index}/stopwatch/stop` (`issueStopStopWatch`), and `DELETE /repos/{owner}/{repo}/issues/{index}/stopwatch/delete` (`issueDeleteStopWatch`).
- Client HTTP also has a reusable Swagger-backed endpoint metadata audit guardrail for pull-request review lifecycle, commit-status, pull-request create/edit, pull-request merge/update, commit-to-pull-request, single-commit, commit note, Git tree, Git blob, Git refs, repository contents, raw/media repository files, repository archives, release list/detail/latest, release-by-tag, release assets, and commit diff/patch endpoints against `plugin-redoc-2.yaml`, covering operation IDs, methods, paths, required path parameters, success response labels, documented non-2xx response status/ref labels, request-body presence or absence, retryability, query enum values including `repoUpdatePullRequest` style values, path enum values including commit and pull-request `diffType` values, single-commit and commit-note boolean query toggles, Git tree `recursive`/`page`/`per_page` query toggles, repository contents/raw/media `ref` query handling, archive multi-value `path` query handling, release-list `draft`/`pre-release`/`page`/`limit` query handling, latest-release and release asset no-query/no-body checks, raw/media `application/octet-stream` Swagger `type: file` response shape, archive's Swagger `application/json`/bare `200` success description, Git blob and Git refs no-query/no-body checks, commit diff/patch no-query/no-body checks, no-body lifecycle POST content-type behavior, and loud assertion messages for missing path/method/parameter lookups.
- Audit-only documented non-2xx response label expectations are private to `GiteaEndpointAuditSpec`; the published `GiteaEndpoint` metadata exposes operation method, path, operation ID, parameters, and success response labels only, and the client API snapshot no longer includes `GiteaResponseLabel` or `GiteaEndpoint.nonSuccessResponses`.
- Client HTTP also has schema-traceable commit-status request construction for `GET /repos/{owner}/{repo}/commits/{ref}/status` (`repoGetCombinedStatusByRef`), `GET /repos/{owner}/{repo}/commits/{ref}/statuses` (`repoListStatusesByRef`), `GET /repos/{owner}/{repo}/statuses/{sha}` (`repoListStatuses`), and `POST /repos/{owner}/{repo}/statuses/{sha}` (`repoCreateStatus`).
- Client HTTP also has schema-traceable repository contents metadata request construction for `GET /repos/{owner}/{repo}/contents` (`repoGetContentsList`) and `GET /repos/{owner}/{repo}/contents/{filepath}` (`repoGetContents`) with optional `ref` query handling, slash-containing `filepath` encoding as one path segment, non-paginated list decoding, single-object decoding, and read-only retry eligibility.
- Client HTTP also has a binary-safe raw response boundary and schema-traceable repository file byte-download request construction for `GET /repos/{owner}/{repo}/raw/{filepath}` (`repoGetRawFile`) and `GET /repos/{owner}/{repo}/media/{filepath}` (`repoGetRawFileOrLFS`) with optional `ref` query handling, slash-containing `filepath` encoding as one path segment, `Accept: application/octet-stream`, no request body, successful response decoding as `Chunk[Byte]`, documented 404 propagation through the existing error mapper, and read-only retry eligibility.
- Client HTTP also has schema-traceable repository archive byte-download request construction for `GET /repos/{owner}/{repo}/archive/{archive}` (`repoGetArchive`) with `ArchiveParams` for repeated `path` query values, whole-archive default query omission, slash/dot-containing archive names encoded as one path segment, `Accept: application/octet-stream`, no request body, no JSON `Content-Type`, successful response decoding as buffered `Chunk[Byte]`, documented 404 propagation, and read-only retry eligibility. The audit intentionally records the local Swagger fact that archive success is `produces: application/json` with a bare `200` description, while the client chooses bytes pragmatically for the facade.
- Client HTTP also has schema-traceable release asset metadata request construction for `GET /repos/{owner}/{repo}/releases/{id}/assets` (`repoListReleaseAttachments`) and `GET /repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}` (`repoGetReleaseAttachment`) with no query parameters, no request body, non-paginated `AttachmentList` decoding as `Chunk[ReleaseAsset]`, single `Attachment` decoding as `ReleaseAsset`, documented 404 propagation, and read-only retry eligibility.
- `RepoListParams` covers page/limit for `userListRepos` and `orgListRepos`; `UserSearchParams` covers `q`/page/limit for `userSearch`; `IssueListParams` covers the implemented issue-list query parameters from `plugin-redoc-2.yaml`; `IssueCommentListParams` covers `since` and `before` for `issueGetComments`; `RepositoryCommentListParams` covers `since`, `before`, page, and limit for `issueGetRepoComments`; `IssueTrackedTimeListParams` covers optional user filtering, `since`, `before`, page, and limit for `issueTrackedTimes`; `PullRequestListParams` covers `base_branch`, `state`, `sort`, `milestone`, multi-value `labels`, `poster`, page, and limit for `repoListPullRequests`; `PullRequestFilesParams` covers `skip-to`, `whitespace`, page, and limit for `repoGetPullRequestFiles`; `PullRequestCommitsParams` covers verification/files toggles, page, and limit for `repoGetPullRequestCommits`; `SingleCommitParams` covers `stat`, `verification`, and `files` boolean toggles for `repoGetSingleCommit`; `CommitNoteParams` covers `verification` and `files` boolean toggles for `repoGetNote`; `GitTreeParams` covers `recursive`, `page`, and `per_page` controls for `GetTree`; `ContentsParams` covers optional `ref` selection for `repoGetContentsList` and `repoGetContents`; `ArchiveParams` covers repeated `path` query values for `repoGetArchive`; `ReleaseListParams` covers `draft`, `pre-release`, page, and limit controls for `repoListReleases`; `CombinedStatusParams` covers page and limit for `repoGetCombinedStatusByRef`; `CommitStatusListParams` covers optional `sort`, `state`, page, and limit for commit-status list endpoints; `PullRequestUpdateStyle` covers `merge` and `rebase` for `repoUpdatePullRequest`; `NotificationListParams` covers `all`, multi-value `status-types`, multi-value `subject-type`, `since`, `before`, page, and limit for `notifyGetList`.
- `GiteaResponseMapper` decodes successful JSON responses, non-paginated JSON arrays, paginated issue/repository/branch/tag/release/pull-request/review/reaction/commit-status/notification lists, object-shaped user-search and topic-name pages, repository pin-capacity responses, successful raw byte bodies as `Chunk[Byte]`, 204/unit responses, pull-request merge-status 204/404 booleans, Gitea error payloads, raw failure bodies, explicit 405 `MethodNotAllowed`, 412 `PreconditionFailed`, and 423 `Locked` failures, pagination headers, and rate-limit reset headers. Mapper-level tests cover byte-body success decoding plus global 405/412/423 classification for JSON error payloads, empty bodies, and non-JSON bodies.
- `GiteaRequestsSpec` uses sttp `BackendStub` to cover path encoding, query params, auth/OTP/user-agent/JSON accept headers, JSON content type for body requests, successful decoding, pagination mapping for issue/user/repository/topic/branch/tag/search/release/pull-request/notification lists, organization and notification decoding, Gitea error mapping, and rate-limit mapping.
- Phase 7 has config and retry foundations in place.
- `GiteaConfig` now exposes `withToken`, `withBasic`, `anonymous`, pure `fromEnv(Map[String, String])`, `fromEnvironment`, `layerFromEnv`, and `environmentLayer`.
- `GiteaConfig` now also exposes `fromTypesafeConfig`, `fromTypesafeString`, `layerFromTypesafeConfig`, `layerFromTypesafeString`, and `typesafeLayer`.
- Environment config supports `GITEA_URL`, `GITEA_TOKEN`, `GITEA_USERNAME`, `GITEA_PASSWORD`, `GITEA_PAGE_SIZE`, `GITEA_TIMEOUT`, and `GITEA_MAX_RETRIES`; `GITEA_TOKEN` has precedence over basic auth, basic auth requires both username and password, and validation errors avoid credential values.
- Typesafe config supports `gitea4s.url`, `token`, `username`, `password`, `page-size`, `timeout`, `user-agent`, `otp`, and `max-retries` under the `gitea4s` path with the same credential precedence and safe validation behavior.
- `GiteaConfigSpec` covers token/basic/anonymous env loading, Typesafe config loading, credential precedence, invalid URL/page-size/retry handling, incomplete basic credentials, safe error messages, and hermetic ZLayer construction without reading real environment variables.
- Phase 4 has a small ZIO API facade: `UsersApi`, `ReposApi`, `IssuesApi`, `ReleasesApi`, `PullRequestsApi`, `NotificationsApi`, and a nested `OrgsApi` namespace are wired through `GiteaClient.fromBackend`.
- `GiteaRequest` carries read-only retry eligibility derived from endpoint HTTP method metadata.
- `GiteaRequestExecutor` sends `GiteaRequest[A]` through a sttp `Backend[Task]`, decodes responses through the existing mapper, maps backend failures to `GiteaError.TransportError`, and honors `GiteaConfig.maxRetries` for retryable read-only requests.
- Retry behavior covers transport failures, `429 RateLimited` responses using reset headers when present, and selected `500`/`502`/`503`/`504` responses with exponential backoff and jitter.
- `IssuesApi.get(owner, repo, index)` fetches a single issue, `IssuesApi.list(owner, repo, IssueListParams)` streams paginated issues with `ZStream.paginateChunkZIO`, `IssuesApi.pinned(owner, repo)` lists pinned issues as a `Chunk`, `IssuesApi.create(owner, repo, CreateIssue)` creates an issue through `issueCreateIssue`, `IssuesApi.delete(owner, repo, index)` deletes an issue through `issueDelete`, `IssuesApi.pin(owner, repo, index)` / `IssuesApi.unpin(owner, repo, index)` / `IssuesApi.movePin(owner, repo, index, position)` manage issue pins through `pinIssue`, `unpinIssue`, and `moveIssuePin`, `IssuesApi.edit(owner, repo, index, EditIssue)` edits an issue through `issueEditIssue`, `IssuesApi.close(owner, repo, index)` closes an issue by sending `state = closed`, `IssuesApi.labels(owner, repo, index)` lists issue labels, `IssuesApi.replaceLabels(owner, repo, index, labels)` replaces issue labels, `IssuesApi.addLabels(owner, repo, index, labels)` adds issue labels, `IssuesApi.clearLabels(owner, repo, index)` clears all issue labels, `IssuesApi.removeLabel(owner, repo, index, id)` removes one issue label, `IssuesApi.lock(owner, repo, index, LockIssueOption)` locks an issue, `IssuesApi.unlock(owner, repo, index)` unlocks an issue, `IssuesApi.editDeadline(owner, repo, index, EditDeadlineOption)` sets or clears an issue deadline, `IssuesApi.comment(owner, repo, index, body)` creates an issue comment through `issueCreateComment`, `IssuesApi.comments(owner, repo, index, IssueCommentListParams)` lists comments on a single issue, `IssuesApi.repositoryComments(owner, repo, RepositoryCommentListParams)` streams repository-wide issue comments, `IssuesApi.comment(owner, repo, id)` fetches one issue comment, `IssuesApi.editComment(owner, repo, id, EditIssueComment)` edits one issue comment, `IssuesApi.deleteComment(owner, repo, id)` deletes one issue comment, `IssuesApi.commentReactions(owner, repo, id)` lists reactions on an issue comment, `IssuesApi.reactToComment(owner, repo, id, EditReactionOption)` and `IssuesApi.deleteCommentReaction(owner, repo, id, EditReactionOption)` manage issue-comment reactions, `IssuesApi.blocks(owner, repo, index)` streams issues blocked by an issue, `IssuesApi.block(owner, repo, index, IssueMeta)` and `IssuesApi.unblock(owner, repo, index, IssueMeta)` manage blocking relationships, `IssuesApi.dependencies(owner, repo, index)` / `IssuesApi.addDependency(owner, repo, index, IssueMeta)` / `IssuesApi.removeDependency(owner, repo, index, IssueMeta)` manage issue dependencies, `IssuesApi.reactions(owner, repo, index)` / `IssuesApi.react(owner, repo, index, EditReactionOption)` / `IssuesApi.deleteReaction(owner, repo, index, EditReactionOption)` manage issue reactions, and `IssuesApi.subscribers(owner, repo, index)` / `IssuesApi.subscription(owner, repo, index)` / `IssuesApi.subscribe(owner, repo, index, user)` / `IssuesApi.unsubscribe(owner, repo, index, user)` manage issue subscriptions.
- `IssuesApi.trackedTimes(owner, repo, index, IssueTrackedTimeListParams)` streams paginated tracked-time entries, `IssuesApi.addTrackedTime(owner, repo, index, AddTimeOption)` adds a tracked-time entry, `IssuesApi.resetTrackedTime(owner, repo, index)` resets issue tracked time, and `IssuesApi.deleteTrackedTime(owner, repo, index, id)` deletes one tracked-time entry.
- `IssuesApi.startStopwatch(owner, repo, index)`, `IssuesApi.stopStopwatch(owner, repo, index)`, and `IssuesApi.deleteStopwatch(owner, repo, index)` manage issue stopwatches.
- `UsersApi.followers(username)`, `UsersApi.following(username)`, `UsersApi.search(params)`, and `UsersApi.stopwatches` stream paginated users or active stopwatches through the shared pagination helper.
- `ReposApi.list(owner, RepoListParams)` streams repositories from `userListRepos`, `ReposApi.newIssuePinsAllowed(owner, repo)` checks repository pin capacity through `repoNewPinAllowed`, `ReposApi.topics(owner, repo)` collects all topic pages from `repoListTopics`, and `ReposApi.branches(owner, repo)` / `ReposApi.tags(owner, repo)` stream paginated repository branches and tags.
- `ReposApi.commit(owner, repo, sha, SingleCommitParams)` fetches one repository commit with optional `stat`, `verification`, and `files` controls, `ReposApi.commitNote(owner, repo, sha, CommitNoteParams)` fetches one repository commit note with optional `verification` and `files` controls, `ReposApi.gitTree(owner, repo, sha, GitTreeParams.default)` fetches the exact body-paginated `GitTreeResponse` with optional `recursive`, `page`, and `per_page` controls and a default params argument, `ReposApi.gitBlob(owner, repo, sha)` fetches one `GitBlobResponse`, `ReposApi.annotatedTag(owner, repo, sha)` fetches one Swagger annotated tag object and is distinct from lightweight repository tag listing, `ReposApi.gitRefs(owner, repo)` lists all Git refs, `ReposApi.gitRefs(owner, repo, ref)` lists filtered Git refs such as `heads/main` with the ref encoded as a single path segment, `ReposApi.contents(owner, repo, ContentsParams)` lists root repository contents as `Chunk[ContentsResponse]`, `ReposApi.contents(owner, repo, filepath, ContentsParams)` fetches one repository content entry with slash-containing filepaths such as `docs/readme.md` encoded as one path segment, `ReposApi.rawFile(owner, repo, filepath, ContentsParams.default)` and `ReposApi.mediaFile(owner, repo, filepath, ContentsParams.default)` download raw/media repository file bodies as `Chunk[Byte]`, `ReposApi.archive(owner, repo, archive, ArchiveParams.default)` downloads whole repository archives such as `main.zip` or `v1.0.0.tar.gz` as buffered `Chunk[Byte]`, `ReposApi.archive(owner, repo, archive, ArchiveParams(path = ...))` downloads archives scoped to repeated repository subpaths, `ReposApi.commitDiffOrPatch(owner, repo, sha, CommitDiffType)` downloads commit diffs or patches as raw text with `CommitDiffType.diff` and `CommitDiffType.patch`, `ReposApi.combinedStatusByRef(owner, repo, ref, CombinedStatusParams)` fetches the combined commit status with explicit page/limit controls, `ReposApi.statusesByRef(owner, repo, ref, CommitStatusListParams)` and `ReposApi.statuses(owner, repo, sha, CommitStatusListParams)` stream paginated commit statuses, and `ReposApi.createStatus(owner, repo, sha, CreateStatusOption)` creates a commit status.
- `ReleasesApi` is mixed into `GiteaClient` with unambiguous `client.releases(owner, repo, params = ReleaseListParams.default)`, `client.latestRelease(owner, repo)`, `client.release(owner, repo, id)`, `client.releaseByTag(owner, repo, tag)`, `client.releaseAssets(owner, repo, releaseId)`, and `client.releaseAsset(owner, repo, releaseId, assetId)` facade methods.
- Client HTTP now has schema-traceable endpoint metadata and pure sttp request construction for `GET /repos/{owner}/{repo}/releases` (`repoListReleases`), `GET /repos/{owner}/{repo}/releases/latest` (`repoGetLatestRelease`), and `GET /repos/{owner}/{repo}/releases/{id}` (`repoGetRelease`).
- Release list responses decode through the existing paginated JSON mapper as `#/responses/ReleaseList`; latest and single release responses decode as `#/responses/Release`; release asset lists decode as non-paginated chunks from `#/responses/AttachmentList`; single release assets decode as `#/responses/Attachment`.
- `PullRequestsApi` is mixed into `GiteaClient` with unambiguous `client.pullRequests(owner, repo, params)`, `client.pinnedPullRequests(owner, repo)`, `client.pullRequestByBaseHead(owner, repo, base, head)`, `client.commitPullRequest(owner, repo, sha)`, `client.pullRequest(owner, repo, index)`, `client.pullRequestDiffOrPatch(owner, repo, index, diffType, binary)`, `client.pullRequestFiles(owner, repo, index, params)`, and `client.pullRequestCommits(owner, repo, index, params)` facade methods.
- `PullRequestsApi` also exposes `client.createPullRequest(owner, repo, CreatePullRequestOption)` and `client.editPullRequest(owner, repo, index, EditPullRequestOption)` for typed pull-request creation and editing.
- `PullRequestsApi` also exposes `client.pullRequestIsMerged(owner, repo, index)` for the 204/404 merge-status endpoint.
- `PullRequestsApi` also exposes `client.pullRequestReviews(owner, repo, index)` for paginated pull-request review listing, `client.pullRequestReview(owner, repo, index, id)` for single review lookup, `client.pullRequestReviewComments(owner, repo, index, id)` for review comments, and `client.deletePullRequestReview(owner, repo, index, id)` for review deletion.
- `PullRequestsApi` also exposes `client.requestPullReviews(owner, repo, index, PullReviewRequestOptions)` and `client.cancelPullReviewRequests(owner, repo, index, PullReviewRequestOptions)` for pull-request review-request creation and cancellation.
- `PullRequestsApi` also exposes `client.createPullRequestReview(owner, repo, index, CreatePullReviewOptions)`, `client.submitPullRequestReview(owner, repo, index, id, SubmitPullReviewOptions)`, `client.dismissPullRequestReview(owner, repo, index, id, DismissPullReviewOptions)`, and `client.undismissPullRequestReview(owner, repo, index, id)` for pull-request review write workflows.
- `PullRequestsApi` also exposes `client.resolvePullRequestReviewComment(owner, repo, id)` and `client.unresolvePullRequestReviewComment(owner, repo, id)` for pull-request review-comment lifecycle management.
- `PullRequestsApi` also exposes `client.mergePullRequest(owner, repo, index, MergePullRequestOption)`, `client.cancelScheduledAutoMerge(owner, repo, index)`, and `client.updatePullRequest(owner, repo, index, PullRequestUpdateStyle)` for pull-request merge/update workflows.
- Pull request list responses decode through the existing paginated JSON mapper as `#/responses/PullRequestList`; pinned pull-request lists decode as non-paginated chunks from `#/responses/PullRequestList`; single pull request, base/head lookup, commit-to-pull lookup, create, and edit responses decode as `#/responses/PullRequest`; pull-request and commit diff/patch responses decode as raw strings from `#/responses/string`; merge-status responses decode the operation-specific 204/404 status contract as `Boolean`; pull-request review-list responses decode through the existing paginated JSON mapper as `#/responses/PullReviewList`; review-request creation responses decode as non-paginated chunks from `#/responses/PullReviewList`; single review and review write responses decode as `#/responses/PullReview`; review comments decode as a non-paginated chunk from `#/responses/PullReviewCommentList`; changed-file responses decode through the existing paginated JSON mapper as `#/responses/ChangedFileList`; pull-request commit responses decode through the existing paginated JSON mapper as `#/responses/CommitList`; single repository commit responses decode as `#/responses/Commit`; commit note responses decode as `#/responses/Note`; Git tree responses decode as the exact `#/responses/GitTreeResponse` body shape with `page`, `total_count`, and `truncated` fields; Git blob responses decode as `#/responses/GitBlobResponse`; Git refs responses decode as non-paginated chunks from `#/responses/ReferenceList`; repository root contents decode as non-paginated chunks from `#/responses/ContentsListResponse`; repository filepath contents decode as `#/responses/ContentsResponse`; raw/media repository file responses decode as `Chunk[Byte]` from `application/octet-stream`/Swagger `type: file`; archive repository responses decode pragmatically as buffered `Chunk[Byte]` even though Swagger only records a bare `200`; combined commit-status responses decode as `#/responses/CombinedStatus`; commit-status list responses decode through the existing paginated JSON mapper as `#/responses/CommitStatusList`; created commit statuses decode as `#/responses/CommitStatus`.
- `NotificationsApi` is mixed into `GiteaClient` with unambiguous `client.notificationThreads(params)`, `client.unreadNotificationCount`, and `client.notificationThread(id)` facade methods.
- Notification thread list responses decode through the existing paginated JSON mapper as `#/responses/NotificationThreadList`; unread counts decode as `#/responses/NotificationCount`; single notification threads decode as `#/responses/NotificationThread`.
- `ReposApi.list` intentionally requires an explicit `RepoListParams` argument for now because Scala cannot generate default arguments for both overloaded `list` methods on `ReposApi` and `IssuesApi`.
- `OrgsApi.get(org)` is exposed as `client.orgs.get(org)` to avoid colliding with the existing single-argument `UsersApi.get(username)` method on `GiteaClient`.
- `OrgsApi.members(org)` streams paginated organization members from `orgListMembers` through the shared pagination helper.
- `OrgsApi.publicMembers(org)` streams paginated public organization members from `orgListPublicMembers` through the shared pagination helper.
- `OrgsApi.repos(org, RepoListParams)` streams paginated organization repositories from `orgListRepos` through the shared pagination helper.
- `GiteaClientSpec` covers current-user success, user/repository/issue `get`, issue creation/editing/closing/comment listing/lookup/editing/deletion/pinned-list/deadline/label/lock/dependency/blocking/reaction/subscription/tracked-time/stopwatch management, repository pin-capacity checks, organization lookup through `client.orgs.get`, decode failure, transport failure, retry behavior with ZIO Test clocks, multi-page issue/repository/comment/topic/branch/tag/search/org-member/public-org-member/org-repository streaming, and follower/following/stopwatch stream pagination through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page release streaming, release stream page-1 start semantics when caller params contain a low-level page value, latest-release lookup, single-release lookup, non-paginated release asset listing, single release asset lookup, documented release and release asset not-found propagation, and release read-only retry behavior through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page pull request, changed-file, and commit streaming, pinned pull-request listing, base/head pull-request lookup, commit-to-pull-request lookup, single repository commit lookup, commit note lookup, Git tree lookup, Git refs lookup including slash-containing ref filters, repository contents list and filepath lookups including optional `ref` and slash-containing `docs/readme.md` filepaths, raw/media repository byte downloads including optional `ref`, slash-containing filepaths, documented 404 propagation, and read-only retry behavior, archive repository byte downloads including repeated `ArchiveParams.path` values and whole-archive defaults, commit diff/patch raw text lookup, single-pull-request lookup, pull-request create/edit success and representative error handling, retryable commit-to-pull, single-commit, commit-note, Git tree, Git refs, repository contents, archive, and commit diff/patch GET behavior, and non-retryable create/edit write semantics through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers pull-request merge-status checks for merged and not-merged responses through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page pull-request review streaming through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers pull-request review-request creation and cancellation through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers pull-request review creation, submission, dismissal, and undismissal through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers pull-request review-comment resolution and unresolution through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers successful combined commit-status lookup including custom combined-status page/limit controls, commit-status list streaming by ref and SHA, commit-status creation, transport/decode error propagation, and non-retryable POST semantics through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers pull-request merge, scheduled auto-merge cancellation, update-by-merge/update-by-rebase, representative forbidden/not-found/conflict/method-not-allowed/locked propagation, and non-retryable write semantics through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page notification thread streaming, unread notification counts, and single-notification lookup through a `BackendStub[Task]`.
- `GiteaRequestsSpec` covers release, release asset, commit status, single repository commit, commit note, Git tree, Git blob, Git refs, repository contents metadata, raw/media repository byte downloads, archive repository byte downloads with repeated `path` query values, commit diff/patch raw text downloads, pull request including pinned pull-request listing, base/head lookup, commit-to-pull-request lookup, create/edit, reviews, review-request creation/cancellation, review creation/submission/dismissal/undismissal, review-comment resolution/unresolution, merge/update commands, changed files, and commits, notification, issue-create/edit/delete/pin/pinned-list/comment/deadline/dependency/blocking/reaction/subscription/tracked-time/stopwatch, repository pin-capacity checks, current-user stopwatch listing, issue-comment listing/lookup/editing/deletion/reaction, issue-label, and issue-lock endpoint metadata, path encoding, page/limit and filter query parameters, JSON body construction, JSON decoding, raw byte decoding, empty response decoding, and not-found/method-not-allowed/precondition-failed/locked mapping.
- `GiteaRequestsSpec` also covers pull-request merge-status request construction and endpoint-specific 204/404 boolean decoding.
- README now describes the currently implemented typed API surface instead of the initial skeleton state.
- `backend-zio` now exposes the Phase 5 live Java HttpClient-backed constructors through `ZioGiteaBackend.live`, `configured`, `withToken`, `withBasic`, `anonymous`, and caller-owned custom `java.net.http.HttpClient` support through `usingClient`.
- The client module remains decoupled from the concrete sttp ZIO backend; `backend-zio` builds clients through the existing `GiteaClient.fromBackend` abstraction.
- `backend-zio.test` covers hermetic live-layer construction and custom Java HttpClient layer construction without calling external services.
- `examples.run` remains hermetic when `GITEA_URL` and `GITEA_TOKEN` are absent, and calls `GET /user` through the live ZIO backend when both variables are present.
- `backend-okhttp` now exposes the Phase 6 optional OkHttp bridge through `OkHttpGiteaBackend.live`, `configured`, `withToken`, `withBasic`, `anonymous`, and caller-owned custom `okhttp3.OkHttpClient` support through `usingClient`.
- The optional OkHttp bridge adapts sttp's async `OkHttpFutureBackend` to the client module's `Backend[Task]` boundary; it does not use the blocking sync backend.
- OkHttp dependencies remain confined to `backend-okhttp`; `core`, `client`, and `backend-zio` do not depend on OkHttp.
- `backend-okhttp.test` covers hermetic live-layer construction and custom OkHttp client layer construction without calling external services.
- Phase 8 has started with opt-in live integration tests in the `it` module.
- `LiveGiteaIntegrationSpec` only runs when both `GITEA_URL` and `GITEA_TOKEN` are non-empty; otherwise ZIO Test reports the live tests as ignored and performs no network calls.
- The current live integration slice calls `GET /user` and streams the authenticated user's repositories through the live ZIO backend with `RepoListParams(limit = Some(1))`.
- Additional opt-in live probes validate slash-containing Git ref routing through `ReposApi.gitRefs(owner, repo, ref)` when `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_REF` are all non-empty, with `heads/main` as the documented example value.
- Additional opt-in live probes validate annotated tag lookup through `ReposApi.annotatedTag(owner, repo, sha)` only when `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_ANNOTATED_TAG_SHA` are all non-empty; repository tag-list entries are not treated as annotated tag object SHAs.
- Additional opt-in live probes validate slash-containing repository contents filepath routing through `ReposApi.contents(owner, repo, filepath, ContentsParams)` only when `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_CONTENTS_FILEPATH` are all non-empty; optional `GITEA_CONTENTS_REF` is passed through as `ContentsParams(ref = Some(value))` when set, and `docs/readme.md` is the documented example value.
- Additional opt-in live probes validate latest-release semantics through `client.latestRelease(owner, repo)` only when `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_LATEST_RELEASE_TAG` are all non-empty; the configured tag must be the repository's actual latest non-draft, non-prerelease release tag, and no release ID or arbitrary `GITEA_RELEASE_TAG` value is treated as proof of latest-release behavior.
- README documents how to run or skip live integration tests.
- Phase 9 has started with runnable read-only examples for the existing API surface.
- `ShowApiReference` remains the default `examples.run` entrypoint and is hermetic when live credentials are absent.
- `ListMyRepos` can be run with `examples.runMain` to authenticate with the live ZIO backend, call `GET /user`, and stream up to 25 repositories for the authenticated login.
- `WatchNotifications` can be run with `examples.runMain` to read the unread notification count and stream up to 20 unread notification threads.
- `OrgMembers`, `ListReleases`, and `ListPullRequests` can be run with `examples.runMain` for additional read-only organization and repository workflows.
- `SearchUsers` can be run with `examples.runMain` to stream up to 25 users matching `GITEA_USER_QUERY`.
- Example live calls are gated on `GITEA_URL` plus either `GITEA_TOKEN` or `GITEA_USERNAME`/`GITEA_PASSWORD`; when the URL or credentials are absent they print the target API version and make no network calls.
- README now includes installation status, pasteable quickstart, auth modes, ZLayer usage, pagination streams, error handling, retry/rate-limit behavior, backend choices, examples, integration testing, supported API version, and Mill commands.
- Phase 10 has started with Mill-native publishing metadata and local artifact generation.
- `core`, `client`, `backend-zio`, and `backend-okhttp` now extend a shared publishable module trait with `io.worxbend` Maven coordinates, `0.1.0-SNAPSHOT` versioning, GPL-2.0-only POM license metadata, SCM metadata, and developer metadata.
- Local source and javadoc jar generation works through Mill `sourceJar` and `docJar` targets.
- Local Maven publishing works through `./mill __.publishM2Local` and publishes `gitea4s-core_3`, `gitea4s-client_3`, `gitea4s-backend-zio_3`, and `gitea4s-backend-okhttp_3`.
- README documents local snapshot coordinates, local publish/doc/source commands, and the initial pre-1.0 compatibility policy.
- Phase 10 now includes Java 21 CI validation through `.github/workflows/ci.yml`, with a matrix entry for the pinned Scala `3.8.4` baseline and Mill commands for compile, tests, integration-test gating, examples, and publishable artifacts.
- The checked-in `Jenkinsfile` now runs the same Mill validation flow instead of a placeholder stage.
- Release-process documentation now includes `CHANGELOG.md` and `RELEASE.md` with pre-1.0 versioning, local validation, artifact, and changelog checklist guidance.
- Publishing modules now extend Mill's `SonatypeCentralPublishModule`, so `core`, `client`, `backend-zio`, and `backend-okhttp` expose `publishSonatypeCentral` while preserving local publish tasks.
- Maven Central automation groundwork now includes `.github/workflows/publish-central.yml`, a manual GitHub Actions workflow that validates the build, rejects `-SNAPSHOT` versions, and publishes all library artifacts through `mill.javalib.SonatypeCentralPublishModule/publishAll` when Central Portal and PGP secrets are configured.
- `README.md`, `RELEASE.md`, and `CHANGELOG.md` document the Sonatype Central workflow, required secrets, namespace/key prerequisites, and the release dispatch behavior.
- Renovate now has explicit regex managers for the Mill build directive, checked-in Mill launcher fallback, Scala baseline, and central ZIO/zio-json/zio-config/sttp version pins in `build.mill`.
- `README.md` and `RELEASE.md` document dependency-update validation and alignment expectations for Mill, Scala, and library dependency PRs.
- Phase 10 now includes a checked-in public API snapshot baseline under `api-snapshot/` for `core`, `client`, `backend-zio`, and `backend-okhttp`.
- The Mill `compatibility.check` command compares current published-module JVM public signatures against the baseline; `compatibility.writeSnapshot` refreshes it for intentional API changes.
- GitHub Actions CI, the Central publishing workflow, Jenkins, README, RELEASE, and CHANGELOG now include the compatibility check in release-readiness guidance.
- Validation passed: `./mill backend-zio.test`, `./mill backend-okhttp.test`, `./mill __.compile`, `./mill __.test`, `./mill it.test`, `./mill examples.run`, `./mill compatibility.check`, `./mill __.docJar __.sourceJar __.publishArtifacts`, and `./mill __.publishM2Local`.

Use the existing code only as rough naming inspiration. The rewrite should create a new, coherent project structure.

## Researched Constraints

Gitea:

- Latest verified release during planning: `v1.26.2`, published May 20, 2026.
- Use `plugin-redoc-2.yaml` at the repository root as the source of truth for endpoints, operation IDs, parameters, response shapes, and payload fields.
- Treat upstream Gitea release/docs links as verification and drift-check references only. Do not implement endpoints from memory when `plugin-redoc-2.yaml` has the authoritative local definition.
- The local API reference is Swagger 2.0 and declares Gitea API version `1.26.2`.

sttp4 and ZIO:

- `HttpClientZioBackend` is the canonical async ZIO backend.
- `HttpClientZioBackend` is based on Java's `java.net.http.HttpClient`.
- `HttpClientZioBackend.usingClient` accepts Java `HttpClient`, not OkHttp.
- sttp4 ZIO modules depend on ZIO 2.x.
- sttp4 has `OkHttpSyncBackend`, `OkHttpFutureBackend`, and `OkHttpMonixBackend`.
- sttp4 does not have a dedicated `OkHttpZioBackend`.
- If OkHttp support is added, it must be a separate optional bridge, preferably through `OkHttpFutureBackend` adapted to ZIO or through `OkHttpSyncBackend` wrapped in `ZIO.attemptBlocking`.

References:

- Local Gitea API reference: `plugin-redoc-2.yaml`
- Gitea releases: https://github.com/go-gitea/gitea/releases
- Gitea v1.26.2 Swagger template: https://raw.githubusercontent.com/go-gitea/gitea/v1.26.2/templates/swagger/v1_json.tmpl
- sttp ZIO backend docs: https://sttp.softwaremill.com/en/latest/backends/zio.html
- sttp backend summary: https://sttp.softwaremill.com/en/latest/backends/summary.html
- sttp JSON docs: https://sttp.softwaremill.com/en/latest/other/json.html
- sttp stub backend docs: https://sttp.softwaremill.com/en/latest/testing/stub.html
- ZIO reference: https://zio.dev/reference/
- zio-json docs: https://zio.dev/zio-json/
- Mill Scala docs: https://mill-build.org/mill/scalalib/intro.html

## Desired Project Layout

Create a Mill multi-module build:

```text
.
├── build.mill
├── core/
│   └── src/
│       └── io/worxbend/gitea4s/
│           ├── model/
│           ├── codec/
│           └── error/
├── client/
│   └── src/
│       └── io/worxbend/gitea4s/
│           ├── api/
│           ├── http/
│           ├── internal/
│           ├── GiteaClient.scala
│           └── GiteaConfig.scala
├── backend-zio/
│   └── src/
│       └── io/worxbend/gitea4s/backend/zio/
├── backend-okhttp/
│   └── src/
│       └── io/worxbend/gitea4s/backend/okhttp/
├── examples/
│   └── src/
│       └── io/worxbend/gitea4s/examples/
└── it/
    └── src/
        └── io/worxbend/gitea4s/it/
```

Mill module dependencies:

```text
core
client         -> core
backend-zio    -> client
backend-okhttp -> client
examples       -> backend-zio
it             -> backend-zio
```

Keep `backend-okhttp` optional. Do not let OkHttp dependencies leak into `core`, `client`, or `backend-zio`.

## Mill Build Requirements

The first implementation step is `build.mill`.

Required Mill behavior:

- Compile all modules with Scala 3.x.
- Use Java 21.
- Share scalac options through a common module trait.
- Put dependency versions in one place in the Mill build.
- Add ZIO Test support.
- Add commands that work from a clean checkout.

Expected commands:

```text
./mill __.compile
./mill __.test
./mill examples.run
```

If the repository does not contain a Mill launcher yet, add one or document that the system `mill` command is required. Prefer adding a checked-in launcher if practical.

## Dependency Seed

Resolved versions in `build.mill`:

```scala
val scala = "3.8.4"
val zio = "2.1.26"
val zioJson = "0.9.2"
val zioConfig = "4.0.7"
val sttp = "4.0.25"
```

Implemented dependency families:

```scala
mvn"dev.zio::zio:${Versions.zio}"
mvn"dev.zio::zio-streams:${Versions.zio}"
mvn"dev.zio::zio-json:${Versions.zioJson}"
mvn"dev.zio::zio-test:${Versions.zio}"
mvn"dev.zio::zio-test-sbt:${Versions.zio}"

mvn"com.softwaremill.sttp.client4::core:${Versions.sttp}"
mvn"com.softwaremill.sttp.client4::zio:${Versions.sttp}"
mvn"com.softwaremill.sttp.client4::zio-json:${Versions.sttp}"

mvn"dev.zio::zio-config:${Versions.zioConfig}"
mvn"dev.zio::zio-config-typesafe:${Versions.zioConfig}"
```

Optional OkHttp module only:

```scala
mvn"com.softwaremill.sttp.client4::okhttp-backend:${Versions.sttp}"
```

Do not add SBT build files or plugins.

## Phase 2 - Core Models and Codecs

Goal: establish typed Gitea data models and JSON codecs.

Use `plugin-redoc-2.yaml` as the required source for model fields, request payloads, response payloads, endpoint names, and enum-like values. When designing ergonomic Scala names, preserve a traceable relationship to the Swagger definitions and operation IDs.

Start with the smallest useful model slice:

```text
User
Organization
Repository
Permission
Issue
Label
Milestone
Comment
PullRequest
Release
Branch
Tag
GiteaErrorPayload
```

Rules:

- Use `final case class`.
- Use `enum` for closed sets where the API is stable enough.
- Use `Long` for IDs unless the schema clearly requires another type.
- Use `java.time.Instant` for timestamps.
- Keep uncertain or version-sensitive fields optional.
- Use `zio-json` derivation.
- Use field annotations where Gitea JSON names diverge from Scala names.
- Keep generated or schema-derived fixtures in tests.

Core supporting types:

```scala
final case class Page[A](
  data: Chunk[A],
  totalCount: Option[Long],
  page: Int,
  pageSize: Int,
  hasNext: Boolean
)

enum Auth:
  case Token(value: String)
  case Basic(username: String, password: String)
  case OAuth2(token: String)
  case Anonymous
```

Error ADT:

```scala
sealed trait GiteaError extends Product with Serializable

object GiteaError:
  final case class BadRequest(message: String, body: String) extends GiteaError
  final case class Unauthorized(message: String, body: String) extends GiteaError
  final case class Forbidden(message: String, body: String) extends GiteaError
  final case class NotFound(message: String, body: String) extends GiteaError
  final case class Conflict(message: String, body: String) extends GiteaError
  final case class UnprocessableEntity(message: String, body: String) extends GiteaError
  final case class RateLimited(resetAt: Option[Instant], body: String) extends GiteaError
  final case class ServerError(status: Int, body: String) extends GiteaError
  final case class DecodeError(message: String, body: String) extends GiteaError
  final case class TransportError(cause: Throwable) extends GiteaError
```

Deliverable:

```text
./mill core.test
```

passes JSON round-trip and decode tests for the first model slice.

## Phase 3 - HTTP Request Layer

Goal: pure, testable sttp request construction.

Use `plugin-redoc-2.yaml` as the required endpoint reference. Every implemented request builder must be traceable to a path, HTTP method, operation ID, parameters, and response entry in that file.

Package:

```text
io.worxbend.gitea4s.http
```

Config:

```scala
final case class GiteaConfig(
  baseUrl: Uri,
  auth: Auth,
  timeout: Duration,
  pageSize: Int,
  userAgent: Option[String],
  otp: Option[String],
  maxRetries: Int
)
```

Request responsibilities:

- Resolve `/api/v1` from `baseUrl`.
- Encode path segments safely.
- Encode query params through sttp URI APIs.
- Inject `Authorization`.
- Inject `Accept: application/json`.
- Inject JSON content type for JSON bodies.
- Inject `X-Gitea-OTP` when configured.
- Keep request builders pure.

Response responsibilities:

- Decode successful 2xx JSON bodies.
- Treat 204 as `Unit`.
- Map Gitea errors into `GiteaError`.
- Preserve raw response bodies on failure.
- Extract pagination headers where available.
- Extract rate-limit headers where available.

Deliverable:

```text
./mill client.test
```

uses sttp `BackendStub` to verify methods, paths, query params, headers, request bodies, response mapping, and error mapping.

## Phase 4 - Eloquent ZIO API

Goal: expose a Scala/ZIO client API that feels hand-designed, not like a raw generated OpenAPI dump.

Client shape:

```scala
trait GiteaClient
    extends ReposApi
    with IssuesApi
    with UsersApi
    with OrgsApi
    with PullRequestsApi
    with ReleasesApi
    with NotificationsApi
    with AdminApi
```

Initial traits:

```scala
trait ReposApi:
  def get(owner: String, repo: String): IO[GiteaError, Repository]
  def list(owner: String, params: RepoListParams): Stream[GiteaError, Repository]
  def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]]

trait IssuesApi:
  def get(owner: String, repo: String, index: Long): IO[GiteaError, Issue]
  def list(owner: String, repo: String, params: IssueListParams = IssueListParams.default): Stream[GiteaError, Issue]
  def create(owner: String, repo: String, body: CreateIssue): IO[GiteaError, Issue]
  def edit(owner: String, repo: String, index: Long, body: EditIssue): IO[GiteaError, Issue]
  def close(owner: String, repo: String, index: Long): IO[GiteaError, Issue]
  def addLabels(owner: String, repo: String, index: Long, labels: Chunk[Long]): IO[GiteaError, Chunk[Label]]
  def comment(owner: String, repo: String, index: Long, body: String): IO[GiteaError, Comment]

trait UsersApi:
  def me: IO[GiteaError, User]
  def get(username: String): IO[GiteaError, User]
  def followers(username: String): Stream[GiteaError, User]
  def following(username: String): Stream[GiteaError, User]
  def search(params: UserSearchParams): Stream[GiteaError, User]
```

Pagination helper:

```scala
def paginated[A](
  fetchPage: Int => IO[GiteaError, Page[A]]
): Stream[GiteaError, A] =
  ZStream.paginateChunkZIO(1) { page =>
    fetchPage(page).map { result =>
      val next = Option.when(result.hasNext)(page + 1)
      (result.data, next)
    }
  }
```

Deliverable:

Users, repos, and issues APIs compile and have stub-backed tests.

## Phase 5 - Live ZIO Backend

Goal: provide the real ZIO-native client backed by Java HttpClient through sttp4.

Implementation sketch:

```scala
import sttp.client4.*
import sttp.client4.httpclient.zio.HttpClientZioBackend
import zio.*

final class LiveGiteaClient(
  config: GiteaConfig,
  backend: Backend[Task]
) extends GiteaClient
```

Layer:

```scala
object GiteaClient:
  val live: ZLayer[GiteaConfig, Throwable, GiteaClient] =
    ZLayer.scoped {
      for
        config <- ZIO.service[GiteaConfig]
        backend <- HttpClientZioBackend.scoped()
      yield LiveGiteaClient(config, backend)
    }

  def withToken(baseUrl: Uri, token: String): ZLayer[Any, Throwable, GiteaClient] =
    ZLayer.succeed(GiteaConfig.default(baseUrl, Auth.Token(token))) >>> live
```

Custom Java HttpClient support:

```scala
object ZioGiteaBackend:
  def usingClient(httpClient: java.net.http.HttpClient): ZLayer[GiteaConfig, Nothing, GiteaClient] =
    ZLayer.fromFunction { (config: GiteaConfig) =>
      LiveGiteaClient(config, HttpClientZioBackend.usingClient(httpClient))
    }
```

Deliverable:

An example can call `/user` or list repositories against a real Gitea instance using:

```text
GITEA_URL
GITEA_TOKEN
```

## Phase 6 - Optional OkHttp Backend

Goal: support OkHttp without compromising the primary ZIO backend design.

Rules:

- Keep OkHttp in `backend-okhttp` only.
- Do not pass `okhttp3.OkHttpClient` to `HttpClientZioBackend.usingClient`.
- Prefer `OkHttpFutureBackend` adapted to ZIO.
- Use `OkHttpSyncBackend` plus `ZIO.attemptBlocking` only if the Future path is not viable.
- Clearly document blocking behavior if the sync backend is used.
- Manage backend/client lifecycle explicitly.

Deliverable:

OkHttp-backed client compiles and passes the reusable client contract tests where practical.

## Phase 7 - Config, Retry, and Resilience

Goal: production-grade behavior without hiding failures.

Completed subset:

- Programmatic `GiteaConfig` constructors for token, basic, and anonymous auth.
- Environment-based config parsing and ZLayer construction for `GITEA_URL`, `GITEA_TOKEN`, `GITEA_USERNAME`, `GITEA_PASSWORD`, `GITEA_PAGE_SIZE`, and `GITEA_TIMEOUT`.
- Environment-based retry parsing through `GITEA_MAX_RETRIES`.
- Typesafe config parsing and ZLayer construction under the `gitea4s` path, including `max-retries`, `user-agent`, and `otp`.
- Explicit credential precedence: token auth wins; otherwise basic auth is used only when username and password are both set; otherwise auth is anonymous.
- Hermetic config tests under `client.test` without requiring real environment variables.
- Safe read-only retry infrastructure under `GiteaRequestExecutor`, controlled by `GiteaConfig.maxRetries`.
- Retry handling for transport failures, `429 RateLimited` responses, and selected `5xx` responses without real sleeps in tests.

Config sources:

- Programmatic constructors first.
- Environment-based layer second.
- Typesafe config layer third.

Environment variables:

```text
GITEA_URL
GITEA_TOKEN
GITEA_USERNAME
GITEA_PASSWORD
GITEA_PAGE_SIZE
GITEA_TIMEOUT
GITEA_MAX_RETRIES
```

Implemented retry rules:

- Retry safe transport failures.
- Retry `429 RateLimited` based on rate-limit headers when available.
- Retry selected 5xx responses with exponential backoff and jitter.
- Do not retry non-read-only requests by default.
- Redact secrets from errors and logs.

Deliverable:

Retry and config tests run under:

```text
./mill client.test
```

without sleeping in real time.

## Phase 8 - Integration Tests

Goal: validate behavior against real Gitea while keeping unit tests hermetic.

Unit tests:

- sttp `BackendStub`
- JSON fixtures
- response mapper tests
- pagination tests

Integration tests:

- Environment-driven tests against an existing Gitea instance first.
- Containerized Gitea later if useful for CI.

Required integration env:

```text
GITEA_URL
GITEA_TOKEN
```

Deliverable:

```text
./mill __.test
./mill it.test
```

unit tests run by default; live integration tests are opt-in and documented.

Completed subset:

- Replaced the placeholder integration module with `LiveGiteaIntegrationSpec`.
- Live tests are guarded by `TestAspect.ifEnv` non-empty predicates for `GITEA_URL` and `GITEA_TOKEN`, so default validation remains hermetic and reports the live tests as ignored.
- Covered `GET /user` and one paginated repository stream through `ZioGiteaBackend`.
- Added a read-only slash-containing Git ref live probe for `ReposApi.gitRefs(owner, repo, ref)`, gated on `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_REF`; normal `it.test` remains hermetic unless all five variables are non-empty.
- Added a read-only annotated tag live probe for `ReposApi.annotatedTag(owner, repo, sha)`, gated on `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_ANNOTATED_TAG_SHA`; the probe requires an explicit annotated tag object SHA and does not infer one from repository tag-list entries.
- Added a read-only repository contents filepath live probe for `ReposApi.contents(owner, repo, filepath, ContentsParams)`, gated on `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_CONTENTS_FILEPATH`; optional `GITEA_CONTENTS_REF` is passed through as `ContentsParams(ref = Some(value))`.
- Added a read-only latest-release live probe for `client.latestRelease(owner, repo)`, gated on `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_LATEST_RELEASE_TAG`; the probe asserts the returned `Release.tagName` matches the configured latest non-draft, non-prerelease release tag.
- Documented integration test execution in README.

## Phase 9 - Examples and README

Goal: make the rewritten project understandable by running it.

Examples:

```text
ListMyRepos.scala
CreateIssue.scala
CloseIssue.scala
OrgMembers.scala
WatchNotifications.scala
AdminCreateUser.scala
```

README sections:

- Installation
- Quickstart
- Auth modes
- ZLayer usage
- Pagination as streams
- Error handling
- Retry/rate-limit behavior
- Backend choices
- Testing against Gitea
- Supported Gitea API version
- Mill commands

Deliverable:

README quickstart can be pasted into a small app and run successfully.

Completed subset:

- Added shared example support for safe environment-gated live config loading and consistent Gitea error rendering.
- Added `ListMyRepos.scala` for the current authenticated user's repository stream.
- Added `WatchNotifications.scala` for unread notification count and notification-thread streaming.
- Added `SearchUsers.scala` for streaming up to 25 users matching `GITEA_USER_QUERY`.
- Added `OrgMembers.scala` for streaming up to 25 organization members with `client.orgs.members`.
- Added `ListReleases.scala` for streaming up to 25 releases from `GITEA_OWNER`/`GITEA_REPO`.
- Added `ListPullRequests.scala` for streaming up to 25 pull requests from `GITEA_OWNER`/`GITEA_REPO`.
- Added `ListBranchesAndTags.scala` for streaming up to 25 branches and 25 tags from `GITEA_OWNER`/`GITEA_REPO`.
- Kept `ShowApiReference.scala` as the stable default `examples.run` main class.
- Expanded README across the planned Phase 9 sections for the currently implemented read-only API.

## Phase 10 - Publishing Readiness

Goal: prepare the Mill-built library for release.

Tasks:

- Maven coordinates. Complete for local Mill publishing.
- License metadata. Complete for local generated POMs.
- Semantic versioning policy. Started in README for the pre-1.0 snapshot line.
- API compatibility policy. Started in README for the pre-1.0 snapshot line.
- Scaladoc generation. Complete for local Mill doc jars.
- Source and doc jars. Complete for local Mill artifact generation.
- CI matrix for Java 21 and supported Scala versions. Complete for the current Scala `3.8.4` baseline through GitHub Actions; Jenkins has the same core Mill validation flow.
- Maven Central automation groundwork. Complete as an opt-in manual GitHub Actions workflow using Mill's Sonatype Central Portal publisher; final release execution still requires verified namespace and repository secrets.
- Renovate dependency updates. Complete for the current central Mill, Scala, ZIO, zio-json, zio-config, and sttp version pins through explicit regex managers.
- Changelog. Complete as a checked-in starting changelog for unreleased pre-1.0 work.
- Release checklist. Complete as `RELEASE.md` for local snapshot validation and versioning steps.
- Compatibility-check baseline. Complete as checked-in `api-snapshot/` files and Mill `compatibility.check` / `compatibility.writeSnapshot` commands.
- First typed issue write slice. Complete for `POST /repos/{owner}/{repo}/issues` (`issueCreateIssue`) with `CreateIssue`, JSON request construction, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Second typed issue write slice. Complete for `PATCH /repos/{owner}/{repo}/issues/{index}` (`issueEditIssue`) with `EditIssue`, JSON request construction, facade wiring for edit/close, stub-backed tests, docs, and public API snapshot updates.
- Third typed issue write slice. Complete for `POST /repos/{owner}/{repo}/issues/{index}/comments` (`issueCreateComment`) with `CreateIssueComment`, JSON request construction, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Fourth typed issue label slice. Complete for `GET/PUT/POST/DELETE /repos/{owner}/{repo}/issues/{index}/labels` and `DELETE /repos/{owner}/{repo}/issues/{index}/labels/{id}` with `IssueLabelsOption`, non-paginated label-list decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Fifth typed issue lock slice. Complete for `PUT /repos/{owner}/{repo}/issues/{index}/lock` and `DELETE /repos/{owner}/{repo}/issues/{index}/lock` with `LockIssueOption`, 204/unit response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Sixth typed issue deadline slice. Complete for `POST /repos/{owner}/{repo}/issues/{index}/deadline` (`issueEditIssueDeadline`) with `EditDeadlineOption`, explicit null encoding for deadline clearing, `IssueDeadline` response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Seventh typed issue relationship slice. Complete for issue blocking and dependencies through `GET/POST/DELETE /repos/{owner}/{repo}/issues/{index}/blocks` and `GET/POST/DELETE /repos/{owner}/{repo}/issues/{index}/dependencies` with `IssueMeta`, paginated issue-list decoding, JSON body construction including DELETE bodies, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Eighth typed issue comment management slice. Complete for `GET /repos/{owner}/{repo}/issues/{index}/comments`, `GET /repos/{owner}/{repo}/issues/comments`, `GET/PATCH/DELETE /repos/{owner}/{repo}/issues/comments/{id}` with `IssueCommentListParams`, `RepositoryCommentListParams`, `EditIssueComment`, non-paginated and paginated comment-list decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Ninth typed issue reaction slice. Complete for `GET/POST/DELETE /repos/{owner}/{repo}/issues/{index}/reactions` and `GET/POST/DELETE /repos/{owner}/{repo}/issues/comments/{id}/reactions` with `Reaction`, `EditReactionOption`, paginated issue-reaction decoding, non-paginated comment-reaction decoding, JSON body construction including DELETE bodies, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Tenth typed issue subscription slice. Complete for `GET /repos/{owner}/{repo}/issues/{index}/subscriptions`, `GET /repos/{owner}/{repo}/issues/{index}/subscriptions/check`, and `PUT/DELETE /repos/{owner}/{repo}/issues/{index}/subscriptions/{user}` with `WatchInfo`, paginated subscriber-list decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Eleventh typed issue tracked-time slice. Complete for `GET/POST/DELETE /repos/{owner}/{repo}/issues/{index}/times` and `DELETE /repos/{owner}/{repo}/issues/{index}/times/{id}` with `TrackedTime`, `AddTimeOption`, `IssueTrackedTimeListParams`, paginated tracked-time decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Twelfth typed issue stopwatch slice. Complete for `GET /user/stopwatches`, `POST /repos/{owner}/{repo}/issues/{index}/stopwatch/start`, `POST /repos/{owner}/{repo}/issues/{index}/stopwatch/stop`, and `DELETE /repos/{owner}/{repo}/issues/{index}/stopwatch/delete` with `StopWatch`, paginated stopwatch decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Thirteenth typed issue deletion slice. Complete for `DELETE /repos/{owner}/{repo}/issues/{index}` (`issueDelete`) with 204/unit response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Fourteenth typed issue pin slice. Complete for `POST/DELETE /repos/{owner}/{repo}/issues/{index}/pin` (`pinIssue`, `unpinIssue`) and `PATCH /repos/{owner}/{repo}/issues/{index}/pin/{position}` (`moveIssuePin`) with 204/unit response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Fifteenth typed issue pin-readiness slice. Complete for `GET /repos/{owner}/{repo}/issues/pinned` (`repoListPinnedIssues`) and `GET /repos/{owner}/{repo}/new_pin_allowed` (`repoNewPinAllowed`) with `NewIssuePinsAllowed`, non-paginated pinned issue-list decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Sixteenth typed pull-request pin-readiness slice. Complete for `GET /repos/{owner}/{repo}/pulls/pinned` (`repoListPinnedPullRequests`) with non-paginated pinned pull-request-list decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Seventeenth typed pull-request lookup slice. Complete for `GET /repos/{owner}/{repo}/pulls/{base}/{head}` (`repoGetPullRequestByBaseHead`) with path-safe base/head request construction, `PullRequestsApi.pullRequestByBaseHead`, stub-backed tests, docs, and public API snapshot updates.
- Eighteenth typed pull-request changed-files slice. Complete for `GET /repos/{owner}/{repo}/pulls/{index}/files` (`repoGetPullRequestFiles`) with `ChangedFile`, `PullRequestFilesParams`, whitespace query encoding, paginated response decoding, facade stream wiring, stub-backed tests, docs, and public API snapshot updates.
- Nineteenth typed pull-request commits slice. Complete for `GET /repos/{owner}/{repo}/pulls/{index}/commits` (`repoGetPullRequestCommits`) with `Commit`, `RepoCommit`, `CommitAffectedFile`, `CommitStats`, `CommitUser`, `PullRequestCommitsParams`, verification/files query encoding, paginated response decoding, facade stream wiring, stub-backed tests, docs, and public API snapshot updates.
- Twentieth typed pull-request diff/patch slice. Complete for `GET /repos/{owner}/{repo}/pulls/{index}.{diffType}` (`repoDownloadPullDiffOrPatch`) with `PullRequestDiffType`, optional `binary` query encoding, `text/plain` request acceptance, raw string response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Twenty-first typed pull-request merge-status slice. Complete for `GET /repos/{owner}/{repo}/pulls/{index}/merge` (`repoPullRequestIsMerged`) with 204/404 boolean response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Twenty-second typed pull-request review-list slice. Complete for `GET /repos/{owner}/{repo}/pulls/{index}/reviews` (`repoListPullReviews`) with `PullReview`, `PullReviewState`, paginated response decoding, facade stream wiring, stub-backed tests, docs, and public API snapshot updates.
- Twenty-third typed pull-request review detail/comment/deletion slice. Complete for `GET/DELETE /repos/{owner}/{repo}/pulls/{index}/reviews/{id}` and `GET /repos/{owner}/{repo}/pulls/{index}/reviews/{id}/comments` with `PullReviewComment`, `repoGetPullReview`, `repoDeletePullReview`, `repoGetPullReviewComments`, response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Twenty-fourth typed pull-request review-request slice. Complete for `POST/DELETE /repos/{owner}/{repo}/pulls/{index}/requested_reviewers` (`repoCreatePullReviewRequests`, `repoDeletePullReviewRequests`) with `PullReviewRequestOptions`, JSON request construction including DELETE bodies, review-list and empty-response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Twenty-fifth typed pull-request review write slice. Complete for `POST /repos/{owner}/{repo}/pulls/{index}/reviews` (`repoCreatePullReview`), `POST /repos/{owner}/{repo}/pulls/{index}/reviews/{id}` (`repoSubmitPullReview`), `POST /repos/{owner}/{repo}/pulls/{index}/reviews/{id}/dismissals` (`repoDismissPullReview`), and `POST /repos/{owner}/{repo}/pulls/{index}/reviews/{id}/undismissals` (`repoUnDismissPullReview`) with `CreatePullReviewComment`, `CreatePullReviewOptions`, `SubmitPullReviewOptions`, `DismissPullReviewOptions`, JSON request construction, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Twenty-sixth typed pull-request review-comment resolution slice. Complete for `POST /repos/{owner}/{repo}/pulls/comments/{id}/resolve` (`repoResolvePullReviewComment`) and `POST /repos/{owner}/{repo}/pulls/comments/{id}/unresolve` (`repoUnresolvePullReviewComment`) with 204/unit response decoding, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Endpoint metadata audit guardrail. Complete for the pull-request review lifecycle endpoints, comparing handwritten endpoint metadata and request construction against `plugin-redoc-2.yaml`.
- Twenty-seventh typed commit-status slice. Complete for `GET /repos/{owner}/{repo}/commits/{ref}/status` (`repoGetCombinedStatusByRef`), `GET /repos/{owner}/{repo}/commits/{ref}/statuses` (`repoListStatusesByRef`), `GET /repos/{owner}/{repo}/statuses/{sha}` (`repoListStatuses`), and `POST /repos/{owner}/{repo}/statuses/{sha}` (`repoCreateStatus`) with `CommitStatus`, `CombinedStatus`, `CreateStatusOption`, `CommitStatusState`, `CommitStatusListParams`, paginated response decoding, JSON request construction, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Commit-status metadata audit guardrail. Complete for `repoGetCombinedStatusByRef`, `repoListStatusesByRef`, `repoListStatuses`, and `repoCreateStatus`, including operation IDs, methods, paths, required path parameters, success response labels, request-body presence, retryability, and explicit `CommitStatusSort`/`CommitStatusListState` enum coverage without adding the Swagger-omitted `skipped` query filter state.
- Combined-status pagination control slice. Complete with `CombinedStatusParams`, default facade arguments, page default `1`, configured default limit handling, request/client tests for custom page/limit, docs, and public API snapshot updates.
- Twenty-eighth typed pull-request merge/update slice. Complete for `POST /repos/{owner}/{repo}/pulls/{index}/merge` (`repoMergePullRequest`), `DELETE /repos/{owner}/{repo}/pulls/{index}/merge` (`repoCancelScheduledAutoMerge`), and `POST /repos/{owner}/{repo}/pulls/{index}/update` (`repoUpdatePullRequest`) with `MergePullRequestOption`, `MergePullRequestMethod`, `PullRequestUpdateStyle`, JSON request construction, no-body lifecycle command construction without `Content-Type`, facade wiring, stub-backed tests, docs, and public API snapshot updates.
- Bounded pull-request merge/update contract-hardening gate. Complete with Swagger-backed endpoint metadata audit coverage for `repoMergePullRequest`, `repoCancelScheduledAutoMerge`, and `repoUpdatePullRequest`; reusable loud-failure audit helpers; documented non-2xx response status/ref label checks; `repoUpdatePullRequest` `style` enum validation for `merge` and `rebase`; explicit `GiteaError.MethodNotAllowed` and `GiteaError.Locked` cases for 405/423 resource-state failures; response mapping that preserves decoded payload messages and raw bodies; focused request/client tests; and public API snapshot updates.
- Documentation update for the bounded contract-hardening gate is complete in `README.md`, `CHANGELOG.md`, and this plan. Validation for the implementation gate passed with `./mill core.test`, `./mill client.test`, and `./mill compatibility.check`.
- Contract boundary stabilization is complete: audit-only documented non-2xx response labels moved out of the published endpoint metadata boundary, `GiteaResponseLabel` is test-private, `GiteaEndpoint.nonSuccessResponses` has been removed from the public client API snapshot, and compatibility metadata reflects the intentional signature change.
- Mapper-level 405/423 coverage is complete in `GiteaResponseMapperSpec`, including JSON error payloads, empty bodies, and non-JSON raw bodies for global `MethodNotAllowed` and `Locked` classification.
- Documented non-2xx response status/ref label checks now cover all currently audited endpoint groups: pull-request review lifecycle, commit-status, pull-request create/edit, and pull-request merge/update.
- Documentation alignment for the completed contract-boundary stabilization work is complete in `README.md`, `CHANGELOG.md`, and this plan. Validation passed with `./mill core.test`, `./mill client.test`, and `./mill compatibility.check`.
- Fresh senior review of the contract-boundary stabilization found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaResponseMapperSpec`. Residual follow-up is process-level: keep future Swagger audit expectations registered from the start of each endpoint slice and avoid adding audit-only data back into public client metadata.
- Twenty-ninth typed pull-request create/edit slice. Complete for `POST /repos/{owner}/{repo}/pulls` (`repoCreatePullRequest`) and `PATCH /repos/{owner}/{repo}/pulls/{index}` (`repoEditPullRequest`) with `CreatePullRequestOption`, `EditPullRequestOption`, JSON request construction, facade wiring, `PullRequest` response decoding, documented failure mapping including create's `423` and edit's `412`, non-retryable write semantics, Swagger-backed metadata audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the pull-request create/edit slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill compatibility.writeSnapshot`, `./mill core.test client.test compatibility.check`, and `./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the pull-request create/edit slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up: the documented edit `412` response is currently preserved and tested but still falls through to `GiteaError.ServerError(412, body)`, so the mapper taxonomy should get an explicit precondition/conditional-edit case before more conditional write endpoints are added.
- Explicit 412 taxonomy is complete with `GiteaError.PreconditionFailed`, global `GiteaResponseMapper` classification for JSON error payloads, empty bodies, and non-JSON raw bodies, mapper-level tests, and updated pull-request edit request/facade tests that no longer expect `ServerError(412, ...)`.
- Thirtieth typed pull-request read slice. Complete for `GET /repos/{owner}/{repo}/commits/{sha}/pull` (`repoGetCommitPullRequest`) with safe owner/repo/sha path encoding, JSON accept headers, shared auth/OTP/user-agent headers, `PullRequest` response decoding, read-only retry eligibility, `PullRequestsApi.commitPullRequest`, request-layer tests, facade and retry tests, documented 404 propagation, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the explicit 412 taxonomy and commit-to-pull-request slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill compatibility.writeSnapshot`, `./mill core.test client.test compatibility.check examples.run`, and `./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the explicit 412 taxonomy and commit-to-pull-request slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.http.GiteaResponseMapperSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up: the next `repoGetSingleCommit` plan must include all three documented boolean query toggles, `stat`, `verification`, and `files`; the initial continuation draft omitted `files`.
- Thirty-first typed repository commit read slice. Complete for `GET /repos/{owner}/{repo}/git/commits/{sha}` (`repoGetSingleCommit`) with `SingleCommitParams` for optional `stat`, `verification`, and `files` boolean query toggles, default query omission, safe owner/repo/sha path encoding, JSON accept headers, shared auth/OTP/user-agent headers, `Commit` response decoding, documented 404/422 propagation, read-only retry eligibility, `ReposApi.commit`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the single-commit slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill compatibility.writeSnapshot`, `./mill core.test client.test compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the single-commit slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up is API-shape discipline: before adding more repository-commit methods, keep facade names explicit enough to avoid ambiguous `commit`/diff/patch churn, and continue auditing optional query parameters directly against Swagger.
- Thirty-second typed repository commit diff/patch slice. Complete for `GET /repos/{owner}/{repo}/git/commits/{sha}.{diffType}` (`repoDownloadCommitDiffOrPatch`) with `CommitDiffType.diff` and `CommitDiffType.patch`, `text/plain` request acceptance, raw `String` response decoding, documented 404 propagation, read-only retry eligibility, `ReposApi.commitDiffOrPatch`, request/facade/retry tests, and Swagger-backed endpoint audit coverage proving required path parameters, no query parameters, and no request body.
- Documentation and validation alignment for the commit diff/patch slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill core.test`, `./mill client.test`, and `./mill compatibility.check`.
- Fresh senior review of the commit diff/patch slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up: the Swagger audit proves the endpoint path and the absence of query/body parameters, but it should also compare path enum values such as `diffType` against Swagger so future path-value drift fails early.
- Path enum endpoint audit hardening is complete with a private Swagger-backed helper in `GiteaEndpointAuditSpec` that locates path parameters by name, reads documented enum values, compares them to local typed path values, and registers `diff`/`patch` checks for both `repoDownloadCommitDiffOrPatch` and `repoDownloadPullDiffOrPatch` without adding audit-only enum data to public `GiteaEndpoint` metadata.
- Thirty-third typed repository commit note slice. Complete for `GET /repos/{owner}/{repo}/git/notes/{sha}` (`repoGetNote`) with the minimal `Note` model/codecs, `CommitNoteParams` for optional `verification` and `files` query toggles, default query omission, safe owner/repo/sha path encoding, JSON accept headers, shared auth/OTP/user-agent headers, `Note` response decoding, documented 404/422 propagation, read-only retry eligibility, `ReposApi.commitNote`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the path-enum audit hardening and commit note slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill compatibility.writeSnapshot`, `./mill core.test`, `./mill client.test`, `./mill compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the path-enum audit hardening and commit note slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test`, `./mill --no-server client.test`, `./mill --no-server compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up is process-level: keep path enum audits registered for every typed path-value model and keep the next Git-tree response modeled from Swagger rather than forcing it into header-pagination helpers.
- Thirty-fourth typed Git tree read slice. Complete for `GET /repos/{owner}/{repo}/git/trees/{sha}` (`GetTree`) with minimal `GitTreeResponse` and `GitEntry` models/codecs, `GitTreeParams` for optional `recursive`, `page`, and `per_page` query controls, default query omission, safe owner/repo/sha path encoding, JSON accept headers, shared auth/OTP/user-agent headers, exact body-pagination response decoding through `page`, `total_count`, and `truncated`, documented 400/404 propagation, read-only retry eligibility, `ReposApi.gitTree`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the Git tree slice is complete in `README.md`, `CHANGELOG.md`, and this plan. Validation passed with `./mill core.test`, `./mill client.test`, `./mill compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the Git tree slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up is API ergonomics: consider whether `ReposApi.gitTree` should expose `params: GitTreeParams = GitTreeParams.default` before the repository Git facade settles, since no overload conflict exists and the low-level request builder already has that default.
- Repository Git facade ergonomics are normalized: `ReposApi.gitTree(owner, repo, sha, params: GitTreeParams = GitTreeParams.default)` now matches the surrounding `commit` and `commitNote` defaults, and `SttpGiteaClient` exposes the same default so facade call sites can omit params while the request builder continues to omit `recursive`, `page`, and `per_page`.
- Thirty-fifth typed Git blob read slice. Complete for `GET /repos/{owner}/{repo}/git/blobs/{sha}` (`GetBlob`) with minimal `GitBlobResponse` model/codecs, preserved `lfs_oid`/`lfs_size` JSON field names, encoded `content` retained as `String`, safe owner/repo/sha path encoding, JSON accept headers, shared auth/OTP/user-agent headers, no query parameters, no request body, `GitBlobResponse` decoding, documented 400/404 propagation, read-only retry eligibility, `ReposApi.gitBlob`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the Git tree default-argument and Git blob slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill core.test`, `./mill client.test`, `./mill compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the Git tree default-argument and Git blob slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. The implementation keeps audit-only documented non-2xx expectations private to `GiteaEndpointAuditSpec`; the public snapshots contain only the intentional `gitTree` default method, `gitBlob` facade/request metadata, and `GitBlobResponse` model ABI. Residual follow-up: the next Git refs slice should explicitly test slash-containing ref names such as `heads/main`, because Swagger defines `{ref}` as "part or full name of the ref" and Git refs naturally contain slashes.
- Thirty-sixth typed Git refs read slice. Complete for `GET /repos/{owner}/{repo}/git/refs` (`repoListAllGitRefs`) and `GET /repos/{owner}/{repo}/git/refs/{ref}` (`repoListGitRefs`) with minimal `Reference` and `GitObject` model/codecs, preserved `object` JSON field mapping, safe owner/repo/ref path encoding, explicit slash-containing ref coverage for `heads/main`, JSON accept headers, shared auth/OTP/user-agent headers, no query parameters, no request body, non-paginated `Chunk[Reference]` decoding from `#/responses/ReferenceList`, documented 404 propagation, read-only retry eligibility, overloaded `ReposApi.gitRefs`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the Git refs slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill core.test`, `./mill client.test`, `./mill compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill __.test it.test examples.run`; live integration tests stayed hermetic and were reported as ignored without credentials.
- Fresh senior review of the Git refs slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-up is live-confidence rather than unit correctness: the filtered refs API intentionally encodes slash-containing refs such as `heads/main` as one path segment, so an opt-in live check against real Gitea should confirm that routing behavior before broader ref-like endpoints assume it.
- Thirty-seventh typed annotated Git tag read slice. Complete for `GET /repos/{owner}/{repo}/git/tags/{sha}` (`GetAnnotatedTag`) with minimal `AnnotatedTag` and `AnnotatedTagObject` model/codecs, preserved `object` JSON field mapping, reuse of existing `CommitUser` and `PayloadCommitVerification` support where the Swagger fields match, safe owner/repo/sha path encoding, JSON accept headers, shared auth/OTP/user-agent headers, no query parameters, no request body, `AnnotatedTag` response decoding, documented 400/404 propagation, read-only retry eligibility, `ReposApi.annotatedTag`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the annotated Git tag slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `./mill compatibility.writeSnapshot`, `./mill core.test`, `./mill client.test`, and `./mill compatibility.check`.
- Fresh senior review of the annotated Git tag slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. The implementation keeps annotated-tag lookup distinct from lightweight repository tag listing, keeps audit-only documented non-2xx expectations private to `GiteaEndpointAuditSpec`, and preserves Swagger response fields through the new core model. Residual follow-up is not a blocker: live behavior for slash-bearing refs still needs an opt-in probe, and future Git object response models would benefit from a lightweight schema-field checklist because endpoint audits do not prove model field completeness.
- Live-confidence probes are complete for slash-containing Git ref routing and explicit annotated tag lookup. Both probes are read-only, use the live ZIO backend, require all endpoint-specific environment variables to be non-empty, and remain ignored during normal credential-free `it.test` runs.
- Schema-field checklist coverage is complete in `CoreModelsSpec` for recent Swagger Git response models: `Reference`, `GitObject`, `AnnotatedTag`, `AnnotatedTagObject`, and `GitBlobResponse`. The checklist is test-only and complements the existing codec round-trip/decode coverage without adding schema metadata to public production APIs.
- Documentation and validation alignment for the live-confidence probes and schema-field checklist is complete in `README.md`, `CHANGELOG.md`, and this plan. Expected validation for this slice is `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill --no-server it.test`; the credential-stripped integration run should report the live probes as ignored and make no network calls.
- Fresh senior review of the live-confidence probe and schema-field checklist slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill --no-server it.test`; the integration run reported all four live tests as ignored. Residual follow-ups are process-level: the schema checklist is still hand-maintained rather than parsed from Swagger, and orchestration artifacts such as `ALTERNATIVES.jsonl` should stay out of source-focused commits unless deliberately tracked changes are required.
- Thirty-eighth typed repository contents metadata slice. Complete for `GET /repos/{owner}/{repo}/contents` (`repoGetContentsList`) and `GET /repos/{owner}/{repo}/contents/{filepath}` (`repoGetContents`) with `ContentsResponse`, nested `FileLinksResponse`, `ContentsParams` for optional `ref`, preserved Swagger JSON field names including `_links`, base64/encoded `content` retained as `String`, safe owner/repo/filepath path encoding, explicit slash-containing `docs/readme.md` coverage as one path segment, JSON accept headers, shared auth/OTP/user-agent headers, no request body, non-paginated `ContentsListResponse` decoding, single `ContentsResponse` decoding, documented 404 propagation, read-only retry eligibility, overloaded `ReposApi.contents`, request-layer tests, facade and retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Repository contents schema-field checklist coverage is complete in `CoreModelsSpec` for `ContentsResponse` and `FileLinksResponse`; the checklist remains test-only and keeps field names visibly anchored to `plugin-redoc-2.yaml` without adding schema metadata to public production APIs.
- Documentation and validation alignment for the repository contents metadata slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`.
- The previously noted `ALTERNATIVES.jsonl` orchestration artifact is tracked in this checkout rather than untracked; this documentation/snapshot slice intentionally leaves its existing working-tree modification untouched and out of the source-focused docs/API changes.
- Fresh senior review of the repository contents metadata slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`. Residual follow-ups are release-safety and design-level: validate slash-containing contents filepath routing against real Gitea before copying the convention further, and treat raw/media file downloads as a response-body boundary decision because Swagger declares `application/octet-stream` `type: file` while the current request abstraction is string-oriented.
- Live contents filepath probe is complete for `ReposApi.contents(owner, repo, filepath, ContentsParams)`. The read-only probe uses the live ZIO backend, requires `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_CONTENTS_FILEPATH` to be non-empty, passes optional `GITEA_CONTENTS_REF` through as `ContentsParams(ref = Some(value))`, and remains ignored during normal credential-free `it.test` runs.
- Binary-safe raw response handling is complete in the client HTTP boundary. Successful `application/octet-stream` bodies decode as `Chunk[Byte]` without JSON or `String` assumptions, while existing JSON, text/plain, Unit, pagination, retry, and error mapping behavior is preserved, including raw failure bodies for non-2xx responses.
- Raw/media repository file request builders are complete for `GET /repos/{owner}/{repo}/raw/{filepath}` (`repoGetRawFile`) and `GET /repos/{owner}/{repo}/media/{filepath}` (`repoGetRawFileOrLFS`) with optional `ref` query handling, slash-containing `docs/readme.md` filepath encoding as one path segment, `Accept: application/octet-stream`, no request body, `Chunk[Byte]` response decoding, documented 404 propagation, and read-only retry eligibility.
- Raw/media repository file facade wiring is complete with `ReposApi.rawFile(owner, repo, filepath, ContentsParams.default)` and `ReposApi.mediaFile(owner, repo, filepath, ContentsParams.default)` returning `Chunk[Byte]` through `SttpGiteaClient` and the existing `GiteaClient` facade. Stub-backed `GiteaClientSpec` coverage proves successful byte downloads, optional `ref` query propagation, slash-containing `docs/readme.md` filepath routing, documented 404 propagation, and read-only retry behavior without changing `ReposApi.contents` semantics. Validation passed with `./mill --no-server client.test`.
- Raw/media repository file endpoint audit coverage is complete for `repoGetRawFile` and `repoGetRawFileOrLFS`, including operation IDs, methods, paths, required path parameters, optional `ref` query parameter, absence of request bodies, read-only retryability, success response shape as Swagger `type: file` / `application/octet-stream`, and documented 404 response status/ref labels. The non-2xx expectations remain private to `GiteaEndpointAuditSpec`.
- Documentation and public API snapshot alignment for the live contents filepath probe and raw/media repository file byte-download API is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. The README states that raw/media methods return `Chunk[Byte]` and that `contents` remains metadata-oriented. Validation passed with `git diff --check`, `./mill --no-server compatibility.writeSnapshot`, `./mill --no-server core.test`, `./mill --no-server client.test`, `./mill --no-server compatibility.check`, `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.http.GiteaResponseMapperSpec io.worxbend.gitea4s.GiteaClientSpec`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill --no-server it.test`; the integration run reported all five live tests as ignored.
- Fresh senior review of the live contents filepath probe and raw/media byte-download slice found no functional blocker in the high-level facade path. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.http.GiteaResponseMapperSpec io.worxbend.gitea4s.GiteaClientSpec`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill --no-server it.test`. The main residual design risk is the low-level `GiteaRequest` compatibility surface: byte requests execute correctly through `GiteaRequestExecutor` and the facade, but the public `request: Request[String]` / `decode(Response[String])` view is not a safe external execution path for non-string response bodies.

Deliverable:

Local publish and generated docs work from Mill.

- `GiteaRequest` response-body boundary stabilization is complete: the unsafe public `Request[String]` / `decode(Response[String])` view has been removed from the `GiteaRequest` sealed interface. Both `request: Request[Body]` and `decode(Response[Body])` are now `private[gitea4s]`, preventing external callers from executing requests through unsafe string casts. `GiteaRequestExecutor` is the sole production execution path for all request types.
- Contract boundary tests are complete: `GiteaRequestsSpec` documents that `GiteaRequestExecutor` is the supported execution path with one string test and one byte test; the existing `decodeWith`/`methodOf`/`uriOf`/`headerOf`/`bodyOf` helpers now correctly access the `private[gitea4s]` members because the test is inside `io.worxbend.gitea4s.http`.
- Live raw/media file probe is complete in `LiveGiteaIntegrationSpec`, gated on `GITEA_RAW_FILEPATH` plus base URL/token variables. All six live probes remain hermetic when credentials are absent.
- API snapshot was updated to reflect the removal of `request()`, `typedRequest()`, `decode(Response[String])`, and `decodeTyped(Response[Body])` from the public interface.
- `GiteaRequests.withJsonBody` was narrowed back to `Request[String] => Request[String]`; current JSON-body request builders compile with the narrower string-response helper, preserving the byte-response boundary discipline.
- Focused validation passed: `git diff --check`, `./mill --no-server client.test`, `./mill --no-server compatibility.check`, and `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD ./mill --no-server it.test` (all 6 live probes ignored hermetically).
- Thirty-ninth typed repository archive download slice is complete after cleanup. The accidental repository write surface from this archive iteration was quarantined: `CreateRepo`, `ForkRepo`, `ReposApi.create`, `ReposApi.fork`, `ReposApi.delete`, `createCurrentUserRepo`, `createFork`, `repoDelete`, related request builders/endpoints/imports/tests, and snapshot entries are absent again until a deliberate repository write slice is designed.
- Archive request contract stabilization is complete for `GET /repos/{owner}/{repo}/archive/{archive}` (`repoGetArchive`) with `ArchiveParams(path: Chunk[String] = Chunk.empty)` for the documented optional multi-value `path` query parameter. `ArchiveParams.default` omits query parameters and downloads the whole archive; repeated subpaths are encoded as repeated `path` query values. Archive names such as `main.zip`, `v1.0.0.tar.gz`, and `refs/heads/main.tar.gz` remain one encoded path segment, the request sends `Accept: application/octet-stream`, sends no request body or JSON `Content-Type`, decodes successful responses as buffered `Chunk[Byte]`, propagates documented 404 errors, and remains read-only retry eligible.
- Archive Swagger alignment is explicit in tests and docs: the client pragmatically decodes archive responses as bytes, but the local `plugin-redoc-2.yaml` operation records `produces: application/json` with a bare `200` success description rather than an `application/octet-stream` `type: file` schema. `ArchiveFormat` was removed from core and the public snapshots because it was not part of the coherent facade contract.
- Archive facade/request/audit coverage is complete for default whole-archive calls, repeated `ArchiveParams.path` values, slash/dot-containing archive path encoding, byte response decoding, 404 propagation, read-only retry behavior, absence of request bodies and JSON content type, and the Swagger bare-success audit fact. README and CHANGELOG now document buffered `Chunk[Byte]` archive downloads and distinguish client behavior from local Swagger facts.
- Live archive download probing remains read-only and hermetic. The default probe calls `ReposApi.archive(owner, repo, archive)` only when `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_ARCHIVE` are all non-empty; optional repeated subpath probing is available through `GITEA_ARCHIVE_PATHS`, while credential-stripped `it.test` makes no network calls.
- Validation for the archive stabilization cleanup passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, focused `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS ./mill --no-server it.test`; the integration run reported all seven live probes as ignored.
- Fresh senior review of the archive stabilization cleanup found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS ./mill --no-server it.test`. Residual follow-ups are narrow: document `GITEA_ARCHIVE_PATHS` in the README live-probe section, consider a small convenience constructor for `ArchiveParams` if repeated path call sites appear, keep using `paramsSeq` for `collectionFormat: multi` audits so duplicate query keys are preserved, and narrow `GiteaRequests.withJsonBody` back to string-response requests before adding more body-bearing endpoints if no path-dependent compile issue remains.

- README live integration documentation is aligned with the implemented archive subpath probe: `GITEA_ARCHIVE_PATHS` is documented as a comma-separated list of repository subpaths, trimmed with empty entries ignored, and credential-stripped integration snippets include `GITEA_ARCHIVE_PATHS` alongside `GITEA_ARCHIVE`.
- JSON body helper boundary cleanup is complete: `GiteaRequests.withJsonBody` is back to the narrower string-response-only helper, and focused request tests preserve JSON-body endpoint coverage.
- Fortieth typed release asset metadata slice is complete for `GET /repos/{owner}/{repo}/releases/{id}/assets` (`repoListReleaseAttachments`) and `GET /repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}` (`repoGetReleaseAttachment`) with the `ReleaseAsset` model mapped from Swagger `Attachment`, optional timestamp/metadata fields, `AttachmentList` non-paginated chunk decoding, single `Attachment` decoding, no query parameters, no request bodies, documented 404 propagation, read-only retry eligibility, `ReleasesApi.releaseAssets`, `ReleasesApi.releaseAsset`, request-layer tests, facade tests, retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Release asset schema-field checklist coverage is complete in `CoreModelsSpec` for the Swagger `Attachment` fields exposed by `ReleaseAsset`; the checklist remains test-only and avoids production schema metadata.
- Documentation and validation alignment for the release asset metadata slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `git diff --check`, `./mill --no-server compatibility.writeSnapshot`, `./mill --no-server core.test client.test compatibility.check`, focused `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS ./mill --no-server it.test`; the integration run reported all seven live probes as ignored without credentials.
- Residual follow-ups are narrow: release asset binary download behavior is not modeled by this metadata slice, upload/edit/delete surfaces remain deliberately absent, and future attachment-like endpoint groups should decide whether to reuse `ReleaseAsset` or introduce endpoint-specific names based on Swagger semantics.
- Fresh senior review of the release asset metadata slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS ./mill --no-server it.test`; all seven live probes were ignored. The main follow-up is design discipline: the Swagger `Attachment` schema is generic across release, issue, and comment attachments, so future attachment slices should choose deliberately between reusing `ReleaseAsset`, introducing a generic `Attachment` model, or keeping endpoint-specific facade names.
- Forty-first typed release-by-tag read slice is complete for `GET /repos/{owner}/{repo}/releases/tags/{tag}` (`repoGetReleaseByTag`) with the existing `Release` model and single-release response decoding, safe owner/repo/tag path encoding, explicit punctuation-heavy `v1.0.0` and slash-containing `release/candidate` tag coverage as one encoded path segment, JSON accept headers, shared auth/OTP/user-agent headers, no query parameters, no request body, no JSON `Content-Type`, documented 404 propagation, read-only retry eligibility, `ReleasesApi.releaseByTag(owner, repo, tag)`, request-layer tests, facade tests, retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Documentation and validation alignment for the release-by-tag slice is complete in `README.md`, `CHANGELOG.md`, this plan, and `api-snapshot/`. Validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, focused `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_ASSET_ID -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES ./mill --no-server it.test`; the integration run reported all seven live probes as ignored without credentials.
- Fresh senior review of the release-by-tag slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_ASSET_ID -u GITEA_RELEASE_TAG -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES ./mill --no-server it.test`; all seven existing live probes were ignored. Residual follow-ups are narrow: validate slash-containing release tags against real Gitea before generalizing tag-like routing further, and align README/audit wording because the current Swagger audit covers release-by-tag and release assets but not the older release list/detail endpoint metadata.

## Recent Completed Progress

- Release confidence hardening is complete for the existing read-only release metadata surface. `GiteaEndpointAuditSpec` registers `repoListReleases` and `repoGetRelease` alongside release-by-tag and release asset metadata audits, including operation IDs, methods, paths, required path parameters, release-list optional query names (`draft`, `pre-release`, `page`, `limit`), success response refs, documented non-2xx labels, request-body absence, and read-only retryability.
- Forty-second typed release-list filtering slice is complete for `GET /repos/{owner}/{repo}/releases` (`repoListReleases`) with `ReleaseListParams` for `draft`, `preRelease`/wire `pre-release`, `page`, and `limit`. The params flow through `GiteaRequests.repoReleases`, `ReleasesApi.releases(owner, repo, params = ReleaseListParams.default)`, `SttpGiteaClient`, and the existing `GiteaClient` facade while keeping `client.releases(owner, repo)` source-compatible. `ReleaseListParams.default` omits filter keys; streaming pagination continues to supply page/limit through the shared pagination helper.
- Release-list filtering tests and audit hardening are complete: request-layer coverage verifies explicit `draft = true/false`, `pre-release = true/false`, page, and limit forwarding plus default filter omission; facade coverage verifies default call-site compatibility, filtered paginated streaming, documented 404 propagation, and read-only retry behavior; the Swagger-backed endpoint audit explicitly checks `draft`, `pre-release`, `page`, and `limit`.
- Release stream pagination semantics are now explicit. `ReleaseListParams.page` remains a low-level `GiteaRequests.repoReleases` request-builder control, while the high-level `ReleasesApi.releases(owner, repo, params)` stream always starts at page 1 through the shared pagination helper and carries filter fields plus page-size `limit` into each streamed request. Facade tests cover the case where caller params include `page = Some(7)` and prove the first stream request still sends `page=1`.
- Forty-third typed latest-release read slice is complete for `GET /repos/{owner}/{repo}/releases/latest` (`repoGetLatestRelease`) with the existing `Release` model and single-release response decoding, safe owner/repo path encoding, JSON accept headers, shared auth/OTP/user-agent headers, no query parameters, no request body, no JSON `Content-Type`, documented 404 propagation, read-only retry eligibility, `GiteaRequests.repoLatestRelease`, `ReleasesApi.latestRelease(owner, repo)`, request-layer tests, facade tests, retry tests, Swagger-backed endpoint audit coverage, docs, and public API snapshot updates.
- Live release asset-list confidence is improved without making default integration tests brittle. When `GITEA_RELEASE_ASSET_ID` is non-empty, the release asset-list probe asserts that `client.releaseAssets(owner, repo, releaseId)` contains that asset id; when it is absent, the probe may continue accepting an empty list as a valid read-only endpoint check.
- Documentation and validation alignment for the release-list filtering slice and asset-list probe improvement is complete in `README.md`, `CHANGELOG.md`, and this plan. Focused review validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, focused `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES ./mill --no-server it.test`; the integration run reported all live probes ignored without credentials.
- Documentation alignment for the release pagination contract and latest-release facade is complete in `README.md`, `CHANGELOG.md`, and this plan. README documents `client.latestRelease(owner, repo)`, avoids examples that pass `page = Some(1)` as stream-start control, and states that the high-level release stream starts at page 1 while low-level `GiteaRequests.repoReleases` still honors explicit page values.
- Fresh senior review of the release pagination clarification and latest-release slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES ./mill --no-server it.test`; all 11 live probes were ignored. The latest-release request/facade/audit path matches `plugin-redoc-2.yaml`; residual follow-ups are confidence and maintenance level: add an opt-in latest-release live probe with an explicit latest-tag assertion variable, and consider extracting the custom request-recording test backend if more stream-facade pagination tests need to inspect sequential queries.
- Latest-release live confidence is complete for the existing `client.latestRelease(owner, repo)` facade. `LiveGiteaIntegrationSpec` now has a read-only probe gated on `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_LATEST_RELEASE_TAG`; when all five are non-empty, the live ZIO backend calls `client.latestRelease(owner, repo)` and asserts the returned release tag matches `GITEA_LATEST_RELEASE_TAG`.
- README and CHANGELOG alignment for the latest-release live probe is complete. README documents `GITEA_LATEST_RELEASE_TAG` as the assertion variable for the repository's actual latest non-draft, non-prerelease release tag, states that it is the only release variable that asserts latest-release semantics, and includes it in credential-stripped validation snippets. CHANGELOG records the probe as validation/confidence coverage for existing API surface only.
- Focused validation for the latest-release live-probe slice passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, focused request/audit/facade tests where relevant, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG -u GITEA_LATEST_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES ./mill --no-server it.test`; the integration run reported the live probes ignored without credentials and made no live calls.
- Fresh senior review of the latest-release live-probe slice found no functional blocker. Focused validation passed with `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, and credential-stripped `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG -u GITEA_LATEST_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES ./mill --no-server it.test`; all twelve live probes were ignored without credentials. The implementation uses an exact `Release.tagName.contains(expectedTag)` assertion and keeps latest-release confidence separate from arbitrary release IDs or `GITEA_RELEASE_TAG`. Residual follow-ups are live-routing confidence for slash-bearing release tags, possible extraction of the request-recording stream test backend if more sequential-query assertions are added, and continued restraint around release writes, multipart upload, and asset binary downloads.

## Next Continuation

- Next low-risk continuation: harden the existing `client.releaseByTag(owner, repo, tag)` live probe for slash-containing release tags by documenting and testing `GITEA_RELEASE_TAG` values such as `release/candidate` only when `GITEA_URL`, `GITEA_TOKEN`, `GITEA_OWNER`, `GITEA_REPO`, and `GITEA_RELEASE_TAG` are all non-empty. Keep it read-only, keep default `it.test` hermetic, and do not generalize tag-like path routing further until the real Gitea behavior is observed. If live Gitea rejects encoded slash tags, record that as endpoint-specific behavior rather than changing other one-segment path conventions by assumption.
- If another streaming facade needs query-sequence assertions, extract the ad hoc recording backend pattern from `GiteaClientSpec` into a small local test helper instead of copying another inline `Backend[Task]` implementation.
- Keep release delete-by-tag, release create/edit/delete, release asset upload/edit/delete, and release asset binary download out of the next continuation unless a deliberate release-write or attachment-upload design slice is selected. Multipart/form-data upload and shared `Attachment` versus `ReleaseAsset` naming must be decided explicitly before those endpoints are implemented.
- Keep private endpoint audit expectations for documented non-2xx response labels out of published endpoint metadata. Keep any future Swagger array query parameter that uses `collectionFormat: multi` represented and tested with ordered repeated query pairs, not `paramsMap`, so duplicate keys remain observable.
- Keep README, CHANGELOG, public API snapshots, and this PLAN aligned with newly implemented API surface and validation results; run `git diff --check`, `./mill --no-server core.test client.test compatibility.check`, focused request/audit/facade tests, and credential-stripped `it.test` before the next review.

Always update this PLAN.md based on the progress: remove completed work, describe and add the next continuation and improvements, and keep this exact instruction as the last line at the bottom of the file.
