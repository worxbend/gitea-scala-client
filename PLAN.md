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
- Core now contains a schema-traceable first model/codecs slice for `User`, `Organization`, `Repository`, `Permission`, `Issue`, `CreateIssue`, `EditIssue`, `CreateIssueComment`, `IssueLabelsOption`, `Label`, `Milestone`, `Comment`, `PullRequest`, `Release`, `Branch`, `Tag`, `TopicNames`, `NotificationCount`, `NotificationSubject`, `NotificationThread`, and `GiteaErrorPayload`.
- Core supporting types now include `Page`, `Auth`, and the `GiteaError` ADT.
- `CoreModelsSpec` covers JSON decode and round-trip behavior for the first model slice, notification codecs, enum validation, pagination codec behavior, auth modes, and the error ADT.
- `GiteaConfig` now carries typed sttp `Uri`, `Auth`, timeout, page size, user agent, OTP, and retry settings.
- Client HTTP now has schema-traceable endpoint metadata and pure sttp request construction for `GET /user` (`userGetCurrent`), `GET /users/{username}` (`userGet`), `GET /users/search` (`userSearch`), `GET /users/{username}/followers` (`userListFollowers`), `GET /users/{username}/following` (`userListFollowing`), `GET /users/{username}/repos` (`userListRepos`), `GET /orgs/{org}` (`orgGet`), `GET /orgs/{org}/members` (`orgListMembers`), `GET /orgs/{org}/public_members` (`orgListPublicMembers`), `GET /orgs/{org}/repos` (`orgListRepos`), `GET /repos/{owner}/{repo}` (`repoGet`), `GET /repos/{owner}/{repo}/topics` (`repoListTopics`), `GET /repos/{owner}/{repo}/branches` (`repoListBranches`), `GET /repos/{owner}/{repo}/tags` (`repoListTags`), `GET /repos/{owner}/{repo}/issues` (`issueListIssues`), `GET /repos/{owner}/{repo}/issues/{index}` (`issueGetIssue`), `POST /repos/{owner}/{repo}/issues` (`issueCreateIssue`), `PATCH /repos/{owner}/{repo}/issues/{index}` (`issueEditIssue`), `POST /repos/{owner}/{repo}/issues/{index}/comments` (`issueCreateComment`), `GET /repos/{owner}/{repo}/issues/{index}/labels` (`issueGetLabels`), `PUT /repos/{owner}/{repo}/issues/{index}/labels` (`issueReplaceLabels`), `POST /repos/{owner}/{repo}/issues/{index}/labels` (`issueAddLabel`), `DELETE /repos/{owner}/{repo}/issues/{index}/labels` (`issueClearLabels`), `DELETE /repos/{owner}/{repo}/issues/{index}/labels/{id}` (`issueRemoveLabel`), `GET /repos/{owner}/{repo}/pulls` (`repoListPullRequests`), `GET /repos/{owner}/{repo}/pulls/{index}` (`repoGetPullRequest`), `GET /notifications` (`notifyGetList`), `GET /notifications/new` (`notifyNewAvailable`), and `GET /notifications/threads/{id}` (`notifyGetThread`).
- `IssueListParams` covers the implemented issue-list query parameters from `plugin-redoc-2.yaml`.
- `RepoListParams` covers page/limit for `userListRepos` and `orgListRepos`; `UserSearchParams` covers `q`/page/limit for `userSearch`; `IssueListParams` covers the implemented issue-list query parameters from `plugin-redoc-2.yaml`; `PullRequestListParams` covers `base_branch`, `state`, `sort`, `milestone`, multi-value `labels`, `poster`, page, and limit for `repoListPullRequests`; `NotificationListParams` covers `all`, multi-value `status-types`, multi-value `subject-type`, `since`, `before`, page, and limit for `notifyGetList`.
- `GiteaResponseMapper` decodes successful JSON responses, non-paginated JSON arrays, paginated issue/repository/branch/tag/release/pull-request/notification lists, object-shaped user-search and topic-name pages, 204/unit responses, Gitea error payloads, raw failure bodies, pagination headers, and rate-limit reset headers.
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
- `IssuesApi.get(owner, repo, index)` fetches a single issue, `IssuesApi.list(owner, repo, IssueListParams)` streams paginated issues with `ZStream.paginateChunkZIO`, `IssuesApi.create(owner, repo, CreateIssue)` creates an issue through `issueCreateIssue`, `IssuesApi.edit(owner, repo, index, EditIssue)` edits an issue through `issueEditIssue`, `IssuesApi.close(owner, repo, index)` closes an issue by sending `state = closed`, `IssuesApi.labels(owner, repo, index)` lists issue labels, `IssuesApi.replaceLabels(owner, repo, index, labels)` replaces issue labels, `IssuesApi.addLabels(owner, repo, index, labels)` adds issue labels, `IssuesApi.clearLabels(owner, repo, index)` clears all issue labels, `IssuesApi.removeLabel(owner, repo, index, id)` removes one issue label, and `IssuesApi.comment(owner, repo, index, body)` creates an issue comment through `issueCreateComment`.
- `UsersApi.followers(username)`, `UsersApi.following(username)`, and `UsersApi.search(params)` stream paginated users through the shared pagination helper.
- `ReposApi.list(owner, RepoListParams)` streams repositories from `userListRepos`, `ReposApi.topics(owner, repo)` collects all topic pages from `repoListTopics`, and `ReposApi.branches(owner, repo)` / `ReposApi.tags(owner, repo)` stream paginated repository branches and tags.
- `ReleasesApi` is mixed into `GiteaClient` with unambiguous `client.releases(owner, repo)` and `client.release(owner, repo, id)` facade methods.
- Client HTTP now has schema-traceable endpoint metadata and pure sttp request construction for `GET /repos/{owner}/{repo}/releases` (`repoListReleases`) and `GET /repos/{owner}/{repo}/releases/{id}` (`repoGetRelease`).
- Release list responses decode through the existing paginated JSON mapper as `#/responses/ReleaseList`; single release responses decode as `#/responses/Release`.
- `PullRequestsApi` is mixed into `GiteaClient` with unambiguous `client.pullRequests(owner, repo, params)` and `client.pullRequest(owner, repo, index)` facade methods.
- Pull request list responses decode through the existing paginated JSON mapper as `#/responses/PullRequestList`; single pull request responses decode as `#/responses/PullRequest`.
- `NotificationsApi` is mixed into `GiteaClient` with unambiguous `client.notificationThreads(params)`, `client.unreadNotificationCount`, and `client.notificationThread(id)` facade methods.
- Notification thread list responses decode through the existing paginated JSON mapper as `#/responses/NotificationThreadList`; unread counts decode as `#/responses/NotificationCount`; single notification threads decode as `#/responses/NotificationThread`.
- `ReposApi.list` intentionally requires an explicit `RepoListParams` argument for now because Scala cannot generate default arguments for both overloaded `list` methods on `ReposApi` and `IssuesApi`.
- `OrgsApi.get(org)` is exposed as `client.orgs.get(org)` to avoid colliding with the existing single-argument `UsersApi.get(username)` method on `GiteaClient`.
- `OrgsApi.members(org)` streams paginated organization members from `orgListMembers` through the shared pagination helper.
- `OrgsApi.publicMembers(org)` streams paginated public organization members from `orgListPublicMembers` through the shared pagination helper.
- `OrgsApi.repos(org, RepoListParams)` streams paginated organization repositories from `orgListRepos` through the shared pagination helper.
- `GiteaClientSpec` covers current-user success, user/repository/issue `get`, issue creation/editing/closing/commenting/label management, organization lookup through `client.orgs.get`, decode failure, transport failure, retry behavior with ZIO Test clocks, multi-page issue/repository/topic/branch/tag/search/org-member/public-org-member/org-repository streaming, and follower/following stream pagination through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page release streaming and single-release lookup through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page pull request streaming and single-pull-request lookup through a `BackendStub[Task]`.
- `GiteaClientSpec` also covers multi-page notification thread streaming, unread notification counts, and single-notification lookup through a `BackendStub[Task]`.
- `GiteaRequestsSpec` covers release, pull request, notification, issue-create/edit/comment, and issue-label endpoint metadata, path encoding, page/limit and filter query parameters, JSON body construction, JSON decoding, empty response decoding, and not-found mapping.
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
  def create(owner: String, body: CreateRepo): IO[GiteaError, Repository]
  def delete(owner: String, repo: String): IO[GiteaError, Unit]
  def fork(owner: String, repo: String, body: ForkRepo): IO[GiteaError, Repository]
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

Deliverable:

Local publish and generated docs work from Mill.

## Immediate Next Step

Continue with the next small vertical slice:

- return to the next typed API slice now that the release infrastructure has CI, Central publishing groundwork, Renovate tracking, compatibility snapshots, and several issue write endpoints,
- likely continue with another small issue-management slice from `plugin-redoc-2.yaml`, such as issue lock/unlock or issue dependencies,
- keep examples and README aligned with any build or publishing commands that become runnable,
- keep `./mill __.test`, `./mill it.test`, and `./mill examples.run` passing without external services when live credentials are absent.

Always update this PLAN.md based on the progress: remove completed work, describe and add the next continuation and improvements, and keep this exact instruction as the last line at the bottom of the file.
