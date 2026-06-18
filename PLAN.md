# Gitea4s From-Scratch Rewrite Plan

## Non-Negotiable Direction

This project must be rewritten from scratch as a Mill-built Scala project.

Use Mill instead of SBT.

Do not modernize, extend, or depend on the current SBT build as the implementation path. The existing `build.sbt`, `project/Dependencies.scala`, and `project/Config.scala` are legacy artifacts only. They may be deleted once the Mill build is in place, or kept temporarily only as historical context during migration.

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
The legacy SBT files remain as historical artifacts only; `./mill` is the
authoritative build entrypoint.

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
client/src/io/worxbend/gitea4s/http/GiteaEndpoint.scala
client/src/io/worxbend/gitea4s/http/GiteaRequest.scala
client/src/io/worxbend/gitea4s/http/GiteaRequests.scala
client/src/io/worxbend/gitea4s/http/GiteaResponseMapper.scala
client/src/io/worxbend/gitea4s/http/IssueListParams.scala
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
build.sbt
project/build.properties
project/Dependencies.scala
project/Config.scala
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
- Core now contains a schema-traceable first model/codecs slice for `User`, `Organization`, `Repository`, `Permission`, `Issue`, `Label`, `Milestone`, `Comment`, `PullRequest`, `Release`, `Branch`, `Tag`, `TopicNames`, and `GiteaErrorPayload`.
- Core supporting types now include `Page`, `Auth`, and the `GiteaError` ADT.
- `CoreModelsSpec` covers JSON decode and round-trip behavior for the first model slice, enum validation, pagination codec behavior, auth modes, and the error ADT.
- `GiteaConfig` now carries typed sttp `Uri`, `Auth`, timeout, page size, user agent, OTP, and retry settings.
- Client HTTP now has schema-traceable endpoint metadata and pure sttp request construction for `GET /user` (`userGetCurrent`), `GET /users/{username}` (`userGet`), `GET /users/{username}/followers` (`userListFollowers`), `GET /users/{username}/following` (`userListFollowing`), `GET /users/{username}/repos` (`userListRepos`), `GET /repos/{owner}/{repo}` (`repoGet`), `GET /repos/{owner}/{repo}/topics` (`repoListTopics`), `GET /repos/{owner}/{repo}/issues` (`issueListIssues`), and `GET /repos/{owner}/{repo}/issues/{index}` (`issueGetIssue`).
- `IssueListParams` covers the implemented issue-list query parameters from `plugin-redoc-2.yaml`.
- `RepoListParams` covers page/limit for `userListRepos`; `IssueListParams` covers the implemented issue-list query parameters from `plugin-redoc-2.yaml`.
- `GiteaResponseMapper` decodes successful JSON responses, paginated issue/repository lists, object-shaped topic-name pages, 204/unit responses, Gitea error payloads, raw failure bodies, pagination headers, and rate-limit reset headers.
- `GiteaRequestsSpec` uses sttp `BackendStub` to cover path encoding, query params, auth/OTP/user-agent/JSON accept headers, JSON content type for body requests, successful decoding, pagination mapping for issue/user/repository/topic lists, Gitea error mapping, and rate-limit mapping.
- Phase 4 has started with a small ZIO API facade: `UsersApi`, `ReposApi`, and `IssuesApi` are wired through `GiteaClient.fromBackend`.
- `GiteaRequestExecutor` sends `GiteaRequest[A]` through a sttp `Backend[Task]`, decodes responses through the existing mapper, and maps backend failures to `GiteaError.TransportError`.
- `IssuesApi.get(owner, repo, index)` fetches a single issue and `IssuesApi.list(owner, repo, IssueListParams)` streams paginated issues with `ZStream.paginateChunkZIO`.
- `UsersApi.followers(username)` and `UsersApi.following(username)` stream paginated users through the shared pagination helper.
- `ReposApi.list(owner, RepoListParams)` streams repositories from `userListRepos`, and `ReposApi.topics(owner, repo)` collects all topic pages from `repoListTopics`.
- `ReposApi.list` intentionally requires an explicit `RepoListParams` argument for now because Scala cannot generate default arguments for both overloaded `list` methods on `ReposApi` and `IssuesApi`.
- `GiteaClientSpec` covers current-user success, user/repository/issue `get`, decode failure, transport failure, multi-page issue/repository/topic streaming, and follower/following stream pagination through a `BackendStub[Task]`.
- Validation passed: `./mill core.test`, `./mill client.test`, `./mill __.compile`, `./mill __.test`, and `./mill examples.run`.

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

Do not add SBT plugins or SBT test dependencies.

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
```

Retry rules:

- Retry safe transport failures.
- Retry `429 RateLimited` based on rate-limit headers when available.
- Retry selected 5xx responses with exponential backoff and jitter.
- Do not retry non-idempotent requests by default unless explicitly configured.
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

## Phase 10 - Publishing Readiness

Goal: prepare the Mill-built library for release.

Tasks:

- Maven coordinates.
- License metadata.
- Semantic versioning policy.
- API compatibility policy.
- Scaladoc generation.
- Source and doc jars.
- CI matrix for Java 21 and supported Scala versions.
- Renovate dependency updates.
- Changelog.

Deliverable:

Local publish and generated docs work from Mill.

## Immediate Next Step

Continue with the next small vertical slice:

- continue Phase 4 by adding schema-traceable request builders for the next read-only endpoint in the planned API traits, starting with `GET /users/search` (`userSearch`) from `plugin-redoc-2.yaml`,
- introduce a minimal `UserSearchParams` type that covers the documented search query/page/limit fields and wire `UsersApi.search(params)` through the existing executor and pagination helper,
- add stub-backed tests that verify endpoint metadata, query encoding, successful decoding, error mapping, and stream pagination for user search.

Always update this PLAN.md based on the progress: remove completed work, describe and add the next continuation and improvements, and keep this exact instruction as the last line at the bottom of the file.
