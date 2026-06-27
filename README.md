# gitea4s

A Scala 3 client library for the [Gitea](https://gitea.io) API, built with
[Mill](https://mill-build.org), [ZIO 2](https://zio.dev),
[sttp client4](https://sttp.softwaremill.com), and zio-json.

- **Package root:** `io.worxbend.gitea4s`
- **JVM baseline:** Java 21 · **Scala:** 3.x
- **API target:** Gitea `1.26.2` (local `plugin-redoc-2.yaml` is the contract)
- **Version:** `1.0.0` · **License:** Apache-2.0
- **Backends:** `backend-zio` (Java `HttpClient`, default) and an optional
  `backend-okhttp` bridge

> Status: read-only coverage for users, organizations, repositories, issues,
> releases, pull requests, and notifications, plus a growing set of issue and
> pull-request writes. See `PLAN.md` for the roadmap and `CHANGELOG.md` for the
> detailed surface.

## Installation

```scala
"io.worxbend" %% "gitea4s-core"           % "1.0.0"
"io.worxbend" %% "gitea4s-client"         % "1.0.0"
"io.worxbend" %% "gitea4s-backend-zio"    % "1.0.0"
"io.worxbend" %% "gitea4s-backend-okhttp" % "1.0.0"
```

Most applications only need `gitea4s-backend-zio` (it pulls in `client` and
`core`). Maven Central publishing runs through the gated `Publish Central`
workflow; see `RELEASE.md`. To build or publish to your local Maven repo
instead:

```bash
./mill __.compile __.test
./mill __.publishM2Local
```

Module dependency direction:

```text
core
client          -> core
backend-zio     -> client
backend-okhttp  -> client
```

Most applications depend on `backend-zio`. OkHttp is confined to
`backend-okhttp`; `core`, `client`, and `backend-zio` do not depend on OkHttp.

## Quickstart

```scala
import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.RepoListParams
import sttp.client4.*
import zio.{Console, ZIO, ZIOAppDefault}

object Main extends ZIOAppDefault:
  private val layer =
    ZioGiteaBackend.withToken(uri"https://gitea.example", sys.env("GITEA_TOKEN"))

  def run =
    ZIO.serviceWithZIO[GiteaClient] { client =>
      for
        me    <- client.users.me
        login <- ZIO.fromOption(me.login).orElseFail(new RuntimeException("missing login"))
        repos <- client.repos.list(login, RepoListParams(limit = Some(25))).take(25).runCollect
        _     <- Console.printLine(s"Repositories for $login")
        _     <- ZIO.foreachDiscard(repos)(r =>
                   Console.printLine(s"- ${r.fullName.orElse(r.name).getOrElse("<unknown>")}"))
      yield ()
    }.provideLayer(layer)
```

## Authentication

```scala
GiteaConfig.withToken(baseUrl, token)
GiteaConfig.withBasic(baseUrl, username, password)
GiteaConfig.anonymous(baseUrl)
```

Or load from the environment (`GITEA_TOKEN` takes precedence; basic auth needs
both `GITEA_USERNAME` and `GITEA_PASSWORD`):

```text
GITEA_URL  GITEA_TOKEN  GITEA_USERNAME  GITEA_PASSWORD
GITEA_PAGE_SIZE  GITEA_TIMEOUT  GITEA_MAX_RETRIES
```

HOCON via Typesafe config under the `gitea4s` path is also supported:

```hocon
gitea4s {
  url        = "https://gitea.example"
  token      = "..."
  page-size  = 50
  timeout    = 30s
  user-agent = "my-app"
  max-retries = 2
}
```

Config errors name the invalid setting without echoing credential values.

## ZLayer Usage

```scala
val layer = ZioGiteaBackend.configured(config)              // explicit config
val layer = GiteaConfig.environmentLayer >>> ZioGiteaBackend.live  // from env
val layer = ZioGiteaBackend.usingClient(config, javaHttpClient)    // caller-owned client
```

`OkHttpGiteaBackend` has the same shape when you need the OkHttp bridge.

## Pagination

Paginated list APIs return `ZStream[Any, GiteaError, A]`; the client fetches
pages lazily and follows pagination headers. Endpoints that Gitea returns as
plain (non-paginated) lists return `IO[GiteaError, Chunk[A]]` instead.

```scala
client.repos.list(owner, RepoListParams(limit = Some(50))).take(100).runCollect
client.notifications.list().take(100).runCollect
```

## Namespaces

The client is organized into resource namespaces; every call goes through one:

| Namespace | Covers |
| --- | --- |
| `client.repos` | repositories, Git data, contents, collaborators, statuses |
| `client.issues` | issues, comments, labels, reactions, tracked time |
| `client.pulls` | pull requests, reviews, merges, diffs |
| `client.releases` | releases and release assets |
| `client.notifications` | notification threads and counts |
| `client.users` | the current user (`users.me`), lookup, search, followers |
| `client.orgs` | organizations and their members/repositories |

## Usage Cookbook

Representative calls by namespace. The published surface is broader — see
`CHANGELOG.md` for the full list.

**Repositories & Git data**

```scala
client.repos.get(owner, repo)
client.repos.branches(owner, repo).runCollect
client.repos.tags(owner, repo).runCollect
client.repos.commit(owner, repo, sha)
client.repos.gitTree(owner, repo, sha)
client.repos.contents(owner, repo, filepath = "docs/readme.md", ContentsParams(ref = Some("main")))
client.repos.rawFile(owner, repo, filepath = "README.md", ContentsParams.default) // Chunk[Byte]
client.repos.archive(owner, repo, archive = "main.zip")                            // Chunk[Byte]
client.repos.languages(owner, repo)
client.repos.collaborators(owner, repo).runCollect
```

**Issues**

```scala
client.issues.create(owner, repo, CreateIssue(title = "Bug report", body = Some("...")))
client.issues.edit(owner, repo, index = 12, EditIssue(title = Some("Updated")))
client.issues.close(owner, repo, index = 12)
client.issues.comment(owner, repo, index = 12, body = "Confirmed")
client.issues.addLabels(owner, repo, index = 12, labels = Chunk(1L, 2L))
client.issues.list(owner, repo).runCollect
```

**Pull requests**

```scala
client.pulls.list(owner, repo).runCollect
client.pulls.create(owner, repo,
  CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("...")))
client.pulls.createReview(owner, repo, index = 7,
  CreatePullReviewOptions(body = Some("LGTM"), event = Some(PullReviewState.Approved)))
client.pulls.merge(owner, repo, index = 7,
  MergePullRequestOption(mergeMethod = MergePullRequestMethod.Squash))
client.pulls.diffOrPatch(owner, repo, index = 7, PullRequestDiffType.Diff)
```

**Releases & commit statuses**

```scala
client.releases.list(owner, repo).runCollect
client.releases.latest(owner, repo)
client.releases.byTag(owner, repo, tag = "v1.0.0")
client.repos.createStatus(owner, repo, sha,
  CreateStatusOption(state = Some(CommitStatusState.Success), context = Some("ci/build")))
client.repos.combinedStatusByRef(owner, repo, ref = "main")
```

**Notifications**

```scala
client.notifications.unreadCount
client.notifications.list().take(20).runCollect
```

## Error Handling

Calls fail with the `GiteaError` ADT. HTTP failures preserve response bodies;
decode failures include the raw body; transport failures preserve the cause;
rate-limit errors carry the reset time when Gitea sends one. Resource-state
failures map to explicit cases (`MethodNotAllowed` 405, `PreconditionFailed`
412, `Locked` 423, etc.).

```scala
client.users.me.foldZIO(
  err  => Console.printLineError(s"Gitea call failed: $err"),
  user => Console.printLine(user.login.getOrElse("<unknown>"))
)
```

## Observability

Set a `GiteaObserver` to hook logging, metrics, or tracing into every request.
It runs after each call completes (with the endpoint, total duration, and
outcome), cannot change the result, and a faulty observer can never break a
request. The default is a no-op with zero overhead.

```scala
import io.worxbend.gitea4s.observability.GiteaObserver

val config = GiteaConfig
  .withToken(uri"https://gitea.example", token)
  .copy(observer = GiteaObserver.logging ++ GiteaObserver.metrics)
```

- `GiteaObserver.logging` — one ZIO log line per request (error type only,
  never bodies or credentials).
- `GiteaObserver.metrics` — a `gitea4s_requests_total` counter and a
  `gitea4s_request_duration_ms` histogram, tagged by method/operation/outcome.
- `GiteaObserver.fromFunction(event => ...)` — write your own (e.g. an
  OpenTelemetry span).

## Retry & Rate Limits

Read-only requests honor `GiteaConfig.maxRetries` with jittered exponential
backoff. Retries cover transport failures, `429` (using reset headers when
present), and selected `500`/`502`/`503`/`504` responses. Writes are not retried.

## Backends

Use `backend-zio` by default (Java `HttpClient` via sttp's
`HttpClientZioBackend`). Use `backend-okhttp` only when your application already
standardizes on OkHttp; it adapts sttp's async OkHttp backend to ZIO and keeps
OkHttp off the core dependency path.

## Streaming Downloads (backend-zio)

`client.repos.rawFile`/`mediaFile`/`archive` buffer the whole body into a
`Chunk[Byte]`. For large files and archives, `backend-zio` also exposes a
`GiteaDownloads` service that streams the body lazily as
`ZStream[Any, GiteaError, Byte]`, so it never has to fit in memory:

```scala
import io.worxbend.gitea4s.backend.zio.{GiteaDownloads, ZioGiteaBackend}
import zio.ZIO
import zio.stream.ZSink

val layer = ZioGiteaBackend.downloadsConfigured(config)

ZIO.serviceWithZIO[GiteaDownloads] { downloads =>
  downloads.archive("my-org", "my-repo", "main.zip")
    .run(ZSink.fromFileName("main.zip"))   // streamed straight to disk
}.provideLayer(layer)
```

This requires a `ZioStreams` backend, so it is `backend-zio` only (the OkHttp
bridge keeps the buffered methods). Streaming downloads are not retried, since a
partially consumed body cannot be safely replayed.

## Examples

```bash
./mill examples.run    # hermetic when no credentials are present

GITEA_URL=https://gitea.example GITEA_TOKEN=... \
  ./mill examples.runMain io.worxbend.gitea4s.examples.ListMyRepos
```

Other example mains live under `examples/src/.../examples/` (repos, releases,
pull requests, branches/tags, org members, user search, notifications). Without
credentials they print the target API version and make no network calls.

## More

- **Roadmap:** `PLAN.md`
- **Detailed API surface & release notes:** `CHANGELOG.md`
- **Building, testing (incl. live integration), API conventions:** `CONTRIBUTING.md`
- **Release process & publishing:** `RELEASE.md`

## License

Apache-2.0. See `LICENSE`.
