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

### Distribution channels

All four modules ship through several channels. Maven Central is canonical; the
others are conveniences (pre-release commits, air-gapped mirrors, or grabbing a
prebuilt jar without a build tool).

| Channel | Resolver | Coordinates | Auth |
| --- | --- | --- | --- |
| Maven Central | default | `io.worxbend %% gitea4s-* % 1.0.0` | none |
| GitHub Packages | `https://maven.pkg.github.com/worxbend/gitea-scala-client` | `io.worxbend %% gitea4s-* % 1.0.0` | GitHub token with `read:packages` |
| JitPack | `https://jitpack.io` | `com.github.worxbend.gitea-scala-client %% gitea4s-* % v1.0.0` | none |
| GitHub Releases | n/a | prebuilt `*.jar` attached to each `v*` release | none |

The `Release` workflow (`.github/workflows/release.yml`) runs on every `v*` tag:
it validates the build, publishes all four modules to GitHub Packages, and
attaches their `jar`, `-sources.jar`, and `-javadoc.jar` files to the GitHub
Release. JitPack builds on demand from `jitpack.yml`.

**GitHub Packages.** Add the repository and authenticate with a token that has
`read:packages` — GitHub Packages requires authentication even for public
packages. With sbt:

```scala
resolvers += "gitea4s-github-packages" at
  "https://maven.pkg.github.com/worxbend/gitea-scala-client"
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  sys.env("GITHUB_ACTOR"),
  sys.env("GITHUB_TOKEN") // a PAT with read:packages
)
"io.worxbend" %% "gitea4s-backend-zio" % "1.0.0"
```

**JitPack.** No account needed: JitPack builds the tag on first request and
serves each module under the `com.github.worxbend.gitea-scala-client` group (the
`_3` Scala suffix is preserved, so `%%` still works). Inter-module dependencies
keep their `io.worxbend` coordinates, so also keep Maven Central on the resolver
list. Confirm the exact published coordinates at
<https://jitpack.io/#worxbend/gitea-scala-client>:

```scala
resolvers += "jitpack" at "https://jitpack.io"
"com.github.worxbend.gitea-scala-client" %% "gitea4s-backend-zio" % "v1.0.0"
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

`GITEA_URL` is the **server root**, not the API root: use
`https://gitea.example`, not `https://gitea.example/api/v1`. The `/api/v1`
prefix is added for you, so including it yields `/api/v1/api/v1/...` and a
`NotFound` on every call.

HOCON via Typesafe config under the `gitea4s` path is also supported:

```hocon
gitea4s {
  url        = "https://gitea.example"
  token      = "..."
  page-size  = 50
  timeout    = 30s
  user-agent = "my-app"
  max-retries = 3
}
```

Every setting can also be changed on an existing config:

```scala
config.withMaxRetries(0).withPageSize(100).withObserver(GiteaObserver.metrics)
```

Config errors name the invalid setting without echoing credential values, and
`toString` on a `GiteaConfig` or an `Auth` redacts the token, password and
one-time password. Read the fields directly when you need the real value.

Values that become HTTP header content — `token`, `username`, `password`,
`user-agent` and `otp` — are rejected at parse time if they contain a control
character. Surrounding whitespace is still trimmed, as before; what is new is
that a CR or LF *inside* the value is an error. Previously such a value parsed
cleanly and then failed on the first request, as an untyped
`IllegalArgumentException` from the JDK that quoted the credential back. This
applies to `fromEnv` and the HOCON readers; `GiteaConfig.withToken` and
`Auth.Token(...)` construct a config directly and are not validated.

Credentials embedded in the URL (`https://user:pass@gitea.example`) are stripped
rather than rejected: nothing ever transmitted them, and keeping them only
risked leaking the password through an exception message. Note also that a
plain-`http://` base URL is accepted, so a token would travel in cleartext —
that is your choice to make, and the library does not second-guess it.

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

Both paging fields on a `*Params` are honoured: `limit` sets the page size and
`page` sets where the stream starts, so an interrupted crawl can be resumed
without replaying what it already emitted.

```scala
client.issues.list(owner, repo, IssueListParams(page = Some(7), limit = Some(50)))
```

Note that Gitea clamps `limit` to its own `MAX_RESPONSE_ITEMS` setting (50 by
default), so asking for more per page than the server allows is not an error —
you simply get the server's maximum, and the stream keeps paging until the
collection is exhausted.

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
- `GiteaObserver.metrics` — `gitea4s_requests_total` and
  `gitea4s_request_attempts_total` counters plus a
  `gitea4s_request_duration_ms` histogram, tagged by method/operation/outcome.
  Reading attempts against requests gives the retry amplification.
- `GiteaObserver.fromFunction(event => ...)` — write your own (e.g. an
  OpenTelemetry span).

## Retry & Rate Limits

Read-only requests honour `GiteaConfig.maxRetries`, which **defaults to 3**,
with jittered exponential backoff. Retries cover transport failures, `429`, and
`500`/`502`/`503`/`504`. Only GET and HEAD are ever retried, so a retry can
never duplicate a write.

On a `429` the client honours `Retry-After` — both the delay-seconds and
HTTP-date forms — and falls back to `x-ratelimit-reset`. Those values are
server-controlled, so they are bounded: an instant more than 24 hours out is
ignored entirely, and the wait is capped at 60 seconds, so a bad or hostile
header cannot silently override your timeout.

Each attempt is also capped end to end (five minutes). `GiteaConfig.timeout`
alone cannot do this: it reaches the JDK as `HttpRequest.timeout`, which stops
applying once response *headers* arrive, so a server that answers immediately
and then sends the body one byte at a time would otherwise hang the call
indefinitely.

An exhausted attempt budget is surfaced, not retried. A stalled connection is
the most expensive failure the client has and the one least likely to have
cleared a moment later, so spending the budget `maxRetries + 1` times bought
nothing — it only turned a five-minute stall into a twenty-minute one. Note
that this budget bounds one *attempt*: it is not a deadline for the whole call,
which also includes backoff sleeps. If you need a single deadline covering
everything, wrap the call in ZIO's own `.timeout`.

> **Upgrading from 1.0.0:** `maxRetries` used to default to `0`. If your tests
> stub a `5xx` or `429` and run under zio-test's `TestClock`, they will now
> block on a clock the test never advances. Set `maxRetries = 0` on the config
> your tests use, or advance the clock.

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

A download that stops producing fails rather than hanging: the stream fails if
five minutes pass with no data at all. The budget measures the *gap between
chunks*, not total download time, so a genuinely large archive is never cut off
as long as it keeps arriving.

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
