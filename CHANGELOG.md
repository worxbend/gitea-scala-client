# Changelog

All notable changes to this project will be documented in this file.

This project follows [Semantic Versioning](https://semver.org). From `1.0.0`
onward the public API of the published modules is stable: breaking changes
require a major version. `1.0.0` does not imply full Gitea API coverage — the
typed endpoint surface keeps growing in backward-compatible minor releases,
guarded by the `api-snapshot/` binary-compatibility check.

## Unreleased

A hardening release. Everything here is source-compatible: code that compiled
against `1.0.0` still compiles. Two things nonetheless need reading before you
upgrade — some behaviour changed on purpose, and two case classes gained a
field, which is a **binary** change even though it is not a source one.

**Recompile against this release; do not drop it onto a classpath built against
`1.0.0`.** `UserSearchParams` and `RequestEvent` each gained a field with a
default. Scala generates a new `apply`, `copy` and constructor signature for the
wider arity, so pre-compiled callers of the old three-argument forms would fail
with `NoSuchMethodError` at runtime. Recompiling is enough; no source edit is
needed. Nothing was removed: `Auth`'s per-case `toString` moved onto the `Auth`
parent class, which callers still reach through normal virtual dispatch.

### Security

- **Credentials no longer appear in `toString`.** `Auth`, `GiteaConfig` and
  `GiteaDownloadRequest` used the `toString` the compiler generates for a case
  class or enum case, so `Auth.Token("ghp_…").toString` printed the token in
  full and `s"starting with $config"` printed it along with the one-time
  password. All three now redact. Read the fields directly when you need the
  real values.
- **Credentials read from the environment are trimmed.** `GITEA_TOKEN` and
  friends were tested for emptiness after trimming but returned untrimmed, so a
  secret read out of a file kept its trailing newline. With a token, the JDK
  rejected the resulting header with an `IllegalArgumentException` that quoted
  the credential — and that failure was classified retryable, so it repeated.
  With basic auth it was quieter and worse: the newline base64-encoded into a
  valid header and the server simply answered 401 forever.
- **Userinfo is stripped from the configured base URL.** A `GITEA_URL` of
  `https://user:secret@gitea.example` was stored whole, and sttp renders the
  authority into every exception message, so a connection failure surfaced the
  password. Nothing ever transmitted those credentials, so stripping them
  changes no request.
- **HOCON parse failures no longer echo the source text.** A syntax error
  quoted the offending input, which is frequently adjacent to the token — a
  missing `=` after `token` put the token into the message. Parse failures now
  report the origin and line number instead. Type errors were already safe and
  are unchanged.
- **Server-supplied retry delays are bounded.** A 429 carrying
  `x-ratelimit-reset` was believed unconditionally. A proxy that reports the
  reset in milliseconds sends a value that is a valid epoch *second* tens of
  thousands of years out, which parked the fiber indefinitely. Retry instants
  more than 24 hours ahead are now ignored, and the wait is capped at 60
  seconds regardless, so a remote header can no longer override your timeout.
- **JSON responses have a size ceiling.** sttp's default is unlimited, and
  `readTimeout` does not help because a body that arrives steadily never trips
  it. JSON and diff/patch responses are now capped at 32 MiB. The buffered
  binary downloads (`repos.rawFile`, `repos.mediaFile`, `repos.archive`) are
  deliberately left uncapped, because an archive larger than any sensible
  ceiling is ordinary; use `backend-zio`'s streaming `GiteaDownloads` for those.
- **Error bodies retained on a `GiteaError` are truncated to 8 KiB.** A failing
  request could otherwise put a multi-megabyte payload into whatever log line
  the error reached.
- **`repos.checkCollaborator` and `pulls.isMerged` fail closed.** Any
  unexpected 2xx was read as an affirmative answer, so an identity-aware proxy
  whose session had lapsed could answer `200 OK` with an HTML login page and
  have it read as "yes, this user is a collaborator". Only 204 now means yes
  and only 404 means no; anything else is an error.
- **Redirects are not followed when an OTP is configured.** sttp strips
  `Authorization` on every redirect, so the API token was never forwarded, but
  `X-Gitea-OTP` is not in its sensitive-header set and would have been sent to
  whatever host a `Location` named. Configs that set `otp` now decline to
  follow redirects; configs without one are unaffected.

### Fixed

- **Pagination silently dropped data when the server clamped the page size.**
  `hasNext` compared the page number against the page size that was *requested*
  rather than the number of items that actually arrived. Gitea clamps `limit` to
  its `MAX_RESPONSE_ITEMS` setting (50 by default), so with
  `GITEA_PAGE_SIZE=100` against a 200-item collection the stream ended after 100
  items with no error. Endpoints that send a `Link` header were protected by
  it; the ones that send only `X-Total-Count` — issue comments and repository
  topics among them — were not. The count is now taken from the response.
  Because that count is a lower bound on how much has been seen, a final
  partial page can read as "there may be more", so a stream may make one extra
  request that comes back empty and ends it. Responses carrying neither a
  `Link` header nor a total count are unchanged: with no evidence of a further
  page, the collection is treated as complete.
- **Streaming list methods ignored the requested start page.** Of the two paging
  fields on the public `*Params` types, `limit` reached the request while `page`
  was overwritten with 1 on every call. A caller resuming an interrupted crawl
  at page 7 started again at page 1 and re-emitted everything before it.
- **`Retry-After` is honoured.** The standard header for 429 and 503 — and the
  one nginx, HAProxy and Cloudflare actually send — was ignored entirely, so a
  rate-limited request was retried after about 100 ms, three times, all inside
  the window the proxy was trying to protect. Both the delay-seconds and
  HTTP-date forms are now understood, and `Retry-After` takes precedence over
  `x-ratelimit-reset`.
- **A total time budget bounds every attempt.** `GiteaConfig.timeout` reaches
  the JDK as `HttpRequest.timeout`, which is cancelled the moment response
  *headers* arrive, leaving the body untimed. A server that answered `200 OK`
  immediately and then dribbled the body one byte at a time hung the call
  forever. Each attempt is now capped at five minutes.
- **Observability no longer loses requests that die with a defect.**
  `Cause.failureOption` sees only typed failures, so a defect produced no event
  at all and the success and failure counters stopped summing to the real
  request count. Interruption still deliberately produces no event.
- **The latency histogram spans the configured timeout.** Its buckets topped out
  at 4096 ms against a 30-second default timeout, so every slow, retried or
  rate-limited call fell into the overflow bucket and p95/p99 read as 4096 ms
  whether the truth was five seconds or thirty.
- **`GiteaEndpoints` is audited in full.** The contract audit checked 57 of the
  130 declared endpoints and silently skipped any operation that was not
  registered, so a new endpoint with a wrong path, operation id or parameter
  list could ship green. Every endpoint is now audited, and an unregistered one
  fails the build.
- **The API compatibility check catches orphaned baselines.** It only iterated
  the generated snapshots, so a baseline whose module had been dropped was never
  compared and the check passed while a published artifact went unverified.

### Changed

- **Idempotent requests are now retried by default** (`maxRetries` moves from
  `0` to `3`). Only GET and HEAD are ever retried, so this cannot duplicate a
  write, and delays are jittered, capped and `Retry-After`-aware. **This can
  affect your tests:** a suite that stubs a 5xx or 429 and runs under zio-test's
  `TestClock` will now block, because the retry sleeps on a clock the test never
  advances. Either set `maxRetries = 0` on the config your tests use — the
  recommended fix, since those tests are usually about request building rather
  than retry policy — or advance the test clock. Set
  `GITEA_MAX_RETRIES=0`, `gitea4s.max-retries = 0`, or
  `config.withMaxRetries(0)` to restore the old behaviour everywhere.
- **The jars now require Java 21.** `-release 21` is enforced, moving the
  class-file major version from 61 to 65. Java 21 has always been the
  documented baseline; the previous jars happened to load on 17 through 20 and
  no longer will.
- **`client` no longer depends on `com.softwaremill.sttp.client4::zio-json`.**
  It was declared but unused — the decode path goes through this library's own
  mapper — and it appeared in the published POM of every consumer. zio-json
  itself is unaffected; it comes from `core`. Declare the sttp integration
  yourself if you were relying on it arriving transitively.

### Added

- **`GiteaConfig` builder methods.** `withBaseUrl`, `withAuth`, `withTimeout`,
  `withPageSize`, `withUserAgent`, `withOtp`, `withMaxRetries` and
  `withObserver`. `copy` still works; the builders exist so that configuration
  options can be added in future minor releases without the case class's
  generated `apply`/`copy`/`unapply` arity becoming a compatibility problem.
- **`RequestEvent` reports the HTTP status and the attempt count.** `status` is
  the status of the last response, absent when no response arrived at all;
  `attempts` is how many HTTP requests were actually issued, and `retried` is
  the convenience predicate. `GiteaObserver.metrics` gains a
  `gitea4s_request_attempts_total` counter, which read against
  `gitea4s_requests_total` gives the retry amplification. Both fields have
  defaults, so existing construction sites keep compiling — but as with
  `UserSearchParams`, the generated `apply`/`copy`/constructor arity changes and
  callers must be recompiled.
- **`UserSearchParams.uid`.** The endpoint has always declared this query
  parameter; the params type was the only one in `http/` that did not match its
  endpoint, leaving no way to look a user up by numeric id. It is the last
  field, so existing positional constructions keep compiling — but the
  generated `apply`/`copy`/constructor arity changes, so callers must be
  recompiled (see the note at the top).
- **More distribution channels.** Besides Maven Central, the four modules are now
  published to GitHub Packages and attached as jars to each GitHub Release by the
  new `Release` workflow (`.github/workflows/release.yml`, triggered on `v*`
  tags), and can be built on demand through JitPack via `jitpack.yml`. See the
  "Distribution channels" table in `README.md`.

### Performance

- **List responses decode directly into `Chunk`.** They were decoded into a
  `List` and then copied, so every item cost a cons cell and a second traversal
  on every page of every stream.
- **Request headers are derived once per config** rather than rebuilt per
  request. For basic auth this also stops a fresh cleartext `username:password`
  string and its backing byte array being left on the heap by every call.
- **The `Page` codec is no longer re-derived on every summon.** Because it takes
  a type parameter the compiler could not cache it in a lazy val, so it compiled
  to a plain method carrying 58 allocations — magnolia metadata, the field-name
  arrays and 34 list cells — that ran again for each element of
  `pages.map(_.toJson)`. It is now memoised against the element codec, with a
  cap so that a caller whose own `given` builds a fresh codec per summon cannot
  grow the cache without limit.

## 1.0.0 - 2026-06-27

First stable release. The public API of `gitea4s-core`, `gitea4s-client`,
`gitea4s-backend-zio`, and `gitea4s-backend-okhttp` is now covered by the
semantic-versioning guarantee above. Notable work since the pre-1.0 line:

### Changed

- **Breaking: reshaped `GiteaClient` into resource namespaces.** Operations are
  now reached through `client.repos`, `client.issues`, `client.pulls`,
  `client.releases`, `client.notifications`, `client.users`, and `client.orgs`
  instead of being mixed flat onto `GiteaClient`. For example, `client.me`
  becomes `client.users.me`, `client.list(owner, params)` becomes
  `client.repos.list(owner, params)`, and `client.create(...)` becomes
  `client.issues.create(...)`. This removes the cross-resource method-name
  collisions (`get`/`list`) that previously blocked default arguments, and the
  `SttpGiteaClient` god-object is now split into one `Sttp*Api` impl per
  namespace sharing a single request executor. `ReposApi.list` regains its
  `RepoListParams` default argument.
- **Breaking: dropped redundant method-name prefixes now that operations are
  namespaced.** Within `client.pulls`, `client.releases`, and
  `client.notifications` the methods no longer echo their resource. Examples:
  `pulls.createPullRequest` → `pulls.create`, `pulls.mergePullRequest` →
  `pulls.merge`, `pulls.pullRequestReviews` → `pulls.reviews`,
  `pulls.pullRequests` → `pulls.list`, `releases.releases` → `releases.list`,
  `releases.latestRelease` → `releases.latest`, `releases.releaseByTag` →
  `releases.byTag`, `notifications.notificationThreads` → `notifications.list`,
  and `notifications.unreadNotificationCount` → `notifications.unreadCount`.
  Method signatures are unchanged.
- **Relicensed from GPL-2.0-only to Apache-2.0** (`LICENSE` and generated POM
  metadata) to make the library freely depended upon. No code changes.
- Reworked documentation: `README.md` slimmed to a user guide, with build,
  testing, and API-design conventions moved to a new `CONTRIBUTING.md`. Added a
  forward-looking `PLAN.md` roadmap (replacing the prior changelog-style plan).

### Removed

- Stray empty marker files and directories (`core/.hiden`, `http-client/`,
  `util/`).

### Fixed

- Pagination now stops after an empty page even if the response still advertises
  a next page, so a missing or misleading `rel="next"`/total-count header can no
  longer cause a trailing empty fetch or an unbounded loop.

### Added

- Streaming binary downloads on `backend-zio` through a new `GiteaDownloads`
  service (`rawFile`/`mediaFile`/`archive`), returning
  `ZStream[Any, GiteaError, Byte]` so large files and archives are streamed
  lazily instead of buffered into a `Chunk[Byte]`. Obtained via
  `ZioGiteaBackend.downloads` / `downloadsConfigured` / `downloadsUsingClient`.
  Requires a `ZioStreams` backend (backend-zio only; OkHttp keeps the buffered
  `GiteaClient` methods). Streaming downloads are not retried. The shared request
  shape is exposed in the client module as `GiteaDownloadRequest` plus
  `GiteaRequests.rawFileDownload`/`mediaFileDownload`/`archiveDownload`.
- Observability seam: a `GiteaObserver` hook (in
  `io.worxbend.gitea4s.observability`) invoked after every request with the
  endpoint, total duration (including retries), and success/failure outcome.
  Set it through `GiteaConfig.copy(observer = ...)`. Built-ins: `noop` (default,
  fully short-circuited so zero overhead), `logging` (per-request ZIO log lines;
  error type only, never bodies/credentials), and `metrics` (a
  `gitea4s_requests_total` counter and `gitea4s_request_duration_ms` histogram
  tagged by method/operation/outcome). Observers compose with `++` and a
  defecting observer can never break the underlying request.
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
- Read-only repository team visibility APIs for
  `GET /repos/{owner}/{repo}/teams` (`repoListTeams`) and
  `GET /repos/{owner}/{repo}/teams/{team}` (`repoCheckTeam`) with the new
  `Team` model, `GiteaRequests.repoTeams`, `GiteaRequests.repoTeam`,
  `ReposApi.teams`, and `ReposApi.team`. Team lists stream paginated `Team`
  values from page 1 with the configured page size, single-team lookups encode
  slash/space team names as one path segment, documented `404`/`405` failures
  use the shared mapper, and both methods are read-only retryable. Team
  add/delete/write operations are not implemented.
- Read-only repository tag lookup for
  `GET /repos/{owner}/{repo}/tags/{tag}` (`repoGetTag`) with the existing
  `Tag` model, `GiteaRequests.repoTag`, and `ReposApi.tag(owner, repo, tag)`.
  The request has no query parameters or body, propagates documented `404`
  failures through the shared mapper, and is read-only retryable. Tags with
  punctuation such as `v1.0.0` and slash-containing names such as
  `release/candidate` are covered by request-layer and facade tests as one
  encoded path segment. This is repository tag metadata, not
  `ReleasesApi.releaseByTag`; slash-containing repository tag routing still
  needs real Gitea live observation before it should be claimed as live
  evidence.
- Read-only repository language statistics for
  `GET /repos/{owner}/{repo}/languages` (`repoGetLanguages`) with the new
  map-shaped `LanguageStatistics` model, `GiteaRequests.repoLanguages`, and
  `ReposApi.languages(owner, repo)`. The response preserves Gitea's plain JSON
  object from language name to byte count, for example
  `{"Scala":1234,"Java":55}`, rather than wrapping it in a nested wire field.
  The request has no query parameters or body, sends no JSON `Content-Type`,
  propagates documented `404` failures through the shared mapper, and is
  read-only retryable. Repository language write behavior is not implemented.
- Read-only repository GPG signing-key lookup for
  `GET /repos/{owner}/{repo}/signing-key.gpg` (`repoSigningKey`) with
  `GiteaRequests.repoSigningKey` and `ReposApi.gpgSigningKey(owner, repo)`.
  The API returns the raw `text/plain` response body as `String`, sends
  `Accept: text/plain`, sends no query parameters or request body, sends no
  JSON `Content-Type`, and is read-only retryable. SSH signing-key reads,
  default `/signing-key.*` endpoints, repository key management, and
  signing-key write endpoints are not implemented.
- Read-only repository assignee metadata for
  `GET /repos/{owner}/{repo}/assignees` (`repoGetAssignees`) with the existing
  `User` model, `GiteaRequests.repoAssignees`, and
  `ReposApi.assignees(owner, repo)`. The local Swagger contract declares no
  `page`, `limit`, or other query parameters, so the facade returns
  `IO[GiteaError, Chunk[User]]` rather than a stream. The request sends no
  body, sends no JSON `Content-Type`, propagates documented `404` failures
  through the shared mapper, and is read-only retryable. Assignee assignment
  and removal write operations are not implemented.
- Read-only repository social metadata for
  `GET /repos/{owner}/{repo}/reviewers` (`repoGetReviewers`),
  `GET /repos/{owner}/{repo}/stargazers` (`repoListStargazers`), and
  `GET /repos/{owner}/{repo}/subscribers` (`repoListSubscribers`) with the
  existing `User` model, `GiteaRequests.repoReviewers`,
  `GiteaRequests.repoStargazers`, `GiteaRequests.repoSubscribers`,
  `ReposApi.reviewers`, `ReposApi.stargazers`, and `ReposApi.watchers`.
  Reviewers return `IO[GiteaError, Chunk[User]]` because the local Swagger
  contract declares no pagination parameters for that endpoint. Stargazers and
  watchers stream paginated `User` values from page 1 with the configured page
  size; the public `watchers` facade maps to the `/subscribers` operation to
  match Gitea's summary wording while keeping request names traceable to
  Swagger. These methods send no request body, send no JSON `Content-Type`,
  propagate documented reviewer `404`, stargazer `403`/`404`, and subscriber
  `404` failures through the shared mapper, and are read-only retryable. Star,
  watch, and reviewer write behavior is not implemented.
- Read-only repository tag-protection metadata APIs for
  `GET /repos/{owner}/{repo}/tag_protections` (`repoListTagProtection`) and
  `GET /repos/{owner}/{repo}/tag_protections/{id}` (`repoGetTagProtection`)
  with the new `TagProtection` model, `GiteaRequests.repoTagProtections`,
  `GiteaRequests.repoTagProtection`, `ReposApi.tagProtections`, and
  `ReposApi.tagProtection`. The list endpoint decodes the Swagger
  `TagProtectionList` array as a non-paginated `Chunk[TagProtection]`, because
  the local Swagger contract declares no `page`, `limit`, or other query
  parameters. Both methods are read-only retryable, send no request body, send
  no JSON `Content-Type`, and preserve `created_at`, `id`, `name_pattern`,
  `updated_at`, `whitelist_teams`, and `whitelist_usernames`. Tag-protection
  create/edit/delete write operations are not implemented.
- Read-only repository branch-protection metadata APIs for
  `GET /repos/{owner}/{repo}/branch_protections`
  (`repoListBranchProtection`) and
  `GET /repos/{owner}/{repo}/branch_protections/{name}`
  (`repoGetBranchProtection`) with the new `BranchProtection` model,
  `GiteaRequests.repoBranchProtections`, `GiteaRequests.repoBranchProtection`,
  `ReposApi.branchProtections`, and `ReposApi.branchProtection`. The list
  endpoint decodes the Swagger `BranchProtectionList` array as a
  non-paginated `Chunk[BranchProtection]`, because the local Swagger contract
  declares no `page`, `limit`, or other query parameters. The detail endpoint
  encodes slash-containing branch/rule names such as `release/2026` as one
  path segment. Both methods are read-only retryable, send no request body,
  send no JSON `Content-Type`, and preserve the broad local Swagger metadata
  response, including approval whitelists, review blockers, deprecated
  `branch_name`, timestamps, `enable_*` toggles, force-push/merge/push
  allowlists, priority, file patterns, required approvals, rule names, and
  status-check contexts. Branch-protection create/edit/delete/priority write
  operations are not implemented.
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
- Repository tag-protection endpoint metadata audit coverage for operation IDs
  `repoListTagProtection` and `repoGetTagProtection`, methods, paths, required
  `owner`/`repo`/`id` path parameters, exact absence of query parameters,
  absence of request bodies, read-only retryability, success response refs
  `TagProtectionList` and `TagProtection`, no documented non-2xx responses for
  the list endpoint, and documented `404` response status/ref labels for the
  detail endpoint.
- Repository branch-protection endpoint metadata audit coverage for operation
  IDs `repoListBranchProtection` and `repoGetBranchProtection`, methods,
  paths, required `owner`/`repo`/`name` path parameters, exact absence of query
  parameters, absence of request bodies, read-only retryability, success
  response refs `BranchProtectionList` and `BranchProtection`, and documented
  non-2xx response status/ref labels kept private to endpoint audit tests.
- Repository assignee endpoint metadata audit coverage for operation ID
  `repoGetAssignees`, method, path, required `owner`/`repo` path parameters,
  exact absence of query parameters, absence of request bodies, read-only
  retryability, success response ref `UserList`, and documented `404`
  response status/ref labels kept private to endpoint audit tests.
- Repository social metadata endpoint audit coverage for operation IDs
  `repoGetReviewers`, `repoListStargazers`, and `repoListSubscribers`, methods,
  paths, required `owner`/`repo` path parameters, exact reviewers no-query
  contract, stargazers/subscribers `page` and `limit` query parameters,
  absence of request bodies, read-only retryability, success response ref
  `UserList`, and documented reviewer `404`, stargazer `403`/`404`, and
  subscriber `404` response status/ref labels kept private to endpoint audit
  tests.
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
- Test-side schema-field checklist coverage for Swagger repository
  tag-protection metadata through the public `TagProtection` model.
- Test-side schema-field checklist coverage for Swagger repository
  branch-protection metadata through the public `BranchProtection` model.
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

- `1.0.0` is the first version published from this Mill rewrite. The Maven
  Central deployment runs through the gated `Publish Central` workflow described
  in `RELEASE.md`.
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
- Repository social metadata documentation and validation are aligned with the
  implemented `client.reviewers(owner, repo)`,
  `client.stargazers(owner, repo)`, and `client.watchers(owner, repo)` facade.
  Validation passed with `git diff --check`,
  `./mill --no-server core.test client.test compatibility.check`, focused
  `./mill --no-server client.test.testOnly io.worxbend.gitea4s.http.GiteaRequestsSpec io.worxbend.gitea4s.http.GiteaEndpointAuditSpec io.worxbend.gitea4s.GiteaClientSpec`,
  and credential-stripped
  `env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_COLLABORATOR -u GITEA_TEAM -u GITEA_REPO_TAG -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG -u GITEA_LATEST_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID -u GITEA_ORG -u GITEA_USER_QUERY -u GITEA_PAGE_SIZE -u GITEA_TIMEOUT -u GITEA_MAX_RETRIES -u GITEA_BRANCH_PROTECTION_NAME -u GITEA_TAG_PROTECTION_ID ./mill --no-server it.test`;
  all twelve live probes were ignored without credentials, so this is
  hermetic skip evidence only and not live reviewers/stargazers/watchers
  evidence.
