# gitea4s

A Scala 3 client library for the [Gitea](https://gitea.io) API, built with
[Mill](https://mill-build.org), [ZIO 2](https://zio.dev),
[sttp client4](https://sttp.softwaremill.com), and zio-json.

- **Package root:** `io.worxbend.gitea4s`
- **JVM baseline:** Java 21 · **Scala:** 3.x
- **API target:** Gitea `1.26.2` (local `plugin-redoc-2.yaml` is the contract)
- **Version:** `0.1.0-SNAPSHOT` · **License:** Apache-2.0
- **Backends:** `backend-zio` (Java `HttpClient`, default) and an optional
  `backend-okhttp` bridge

> Status: read-only coverage for users, organizations, repositories, issues,
> releases, pull requests, and notifications, plus a growing set of issue and
> pull-request writes. See `PLAN.md` for the roadmap and `CHANGELOG.md` for the
> detailed surface.

## Installation

Not yet published to Maven Central. Build or publish locally:

```bash
./mill __.compile
./mill __.test
./mill __.publishM2Local   # publishes to your local Maven repo
```

Local snapshot coordinates:

```scala
"io.worxbend" %% "gitea4s-core"          % "0.1.0-SNAPSHOT"
"io.worxbend" %% "gitea4s-client"        % "0.1.0-SNAPSHOT"
"io.worxbend" %% "gitea4s-backend-zio"   % "0.1.0-SNAPSHOT"
"io.worxbend" %% "gitea4s-backend-okhttp" % "0.1.0-SNAPSHOT"
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
        me    <- client.me
        login <- ZIO.fromOption(me.login).orElseFail(new RuntimeException("missing login"))
        repos <- client.list(login, RepoListParams(limit = Some(25))).take(25).runCollect
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
client.list(owner, RepoListParams(limit = Some(50))).take(100).runCollect
client.notificationThreads().take(100).runCollect
```

## Usage Cookbook

Representative calls by area. The published surface is broader — see
`CHANGELOG.md` for the full list.

**Repositories & Git data**

```scala
client.get(owner, repo)
client.branches(owner, repo).runCollect
client.tags(owner, repo).runCollect
client.commit(owner, repo, sha)
client.gitTree(owner, repo, sha)
client.contents(owner, repo, filepath = "docs/readme.md", ContentsParams(ref = Some("main")))
client.rawFile(owner, repo, filepath = "README.md", ContentsParams.default)   // Chunk[Byte]
client.archive(owner, repo, archive = "main.zip")                              // Chunk[Byte]
client.languages(owner, repo)
client.collaborators(owner, repo).runCollect
```

**Issues**

```scala
client.create(owner, repo, CreateIssue(title = "Bug report", body = Some("...")))
client.edit(owner, repo, index = 12, EditIssue(title = Some("Updated")))
client.close(owner, repo, index = 12)
client.comment(owner, repo, index = 12, body = "Confirmed")
client.addLabels(owner, repo, index = 12, labels = Chunk(1L, 2L))
client.list(owner, repo, IssueListParams.default).runCollect
```

**Pull requests**

```scala
client.pullRequests(owner, repo).runCollect
client.createPullRequest(owner, repo,
  CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("...")))
client.createPullRequestReview(owner, repo, index = 7,
  CreatePullReviewOptions(body = Some("LGTM"), event = Some(PullReviewState.Approved)))
client.mergePullRequest(owner, repo, index = 7,
  MergePullRequestOption(mergeMethod = MergePullRequestMethod.Squash))
client.pullRequestDiffOrPatch(owner, repo, index = 7, PullRequestDiffType.Diff)
```

**Releases & commit statuses**

```scala
client.releases(owner, repo).runCollect
client.latestRelease(owner, repo)
client.releaseByTag(owner, repo, tag = "v1.0.0")
client.createStatus(owner, repo, sha,
  CreateStatusOption(state = Some(CommitStatusState.Success), context = Some("ci/build")))
client.combinedStatusByRef(owner, repo, ref = "main")
```

**Notifications**

```scala
client.unreadNotificationCount
client.notificationThreads().take(20).runCollect
```

## Error Handling

Calls fail with the `GiteaError` ADT. HTTP failures preserve response bodies;
decode failures include the raw body; transport failures preserve the cause;
rate-limit errors carry the reset time when Gitea sends one. Resource-state
failures map to explicit cases (`MethodNotAllowed` 405, `PreconditionFailed`
412, `Locked` 423, etc.).

```scala
client.me.foldZIO(
  err  => Console.printLineError(s"Gitea call failed: $err"),
  user => Console.printLine(user.login.getOrElse("<unknown>"))
)
```

## Retry & Rate Limits

Read-only requests honor `GiteaConfig.maxRetries` with jittered exponential
backoff. Retries cover transport failures, `429` (using reset headers when
present), and selected `500`/`502`/`503`/`504` responses. Writes are not retried.

## Backends

Use `backend-zio` by default (Java `HttpClient` via sttp's
`HttpClientZioBackend`). Use `backend-okhttp` only when your application already
standardizes on OkHttp; it adapts sttp's async OkHttp backend to ZIO and keeps
OkHttp off the core dependency path.

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
