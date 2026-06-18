# gitea4s

Scala 3 client library for the Gitea API, built with Mill, ZIO 2, sttp client4,
and zio-json.

## Status

- Build tool: Mill `1.1.6` through the checked-in `./mill` launcher
- Package root: `io.worxbend.gitea4s`
- JVM target: Java 21
- Version: `0.1.0-SNAPSHOT`
- API reference: local `plugin-redoc-2.yaml` for Gitea API `1.26.2`
- Implemented surface: typed core models/codecs plus read-only users, organizations,
  repositories, issues, releases, pull requests, and notifications through a ZIO client API
- Primary backend: `backend-zio`, using sttp's Java `HttpClientZioBackend`
- Optional backend: `backend-okhttp`, using sttp's async `OkHttpFutureBackend` adapted to ZIO

## Installation

This rewrite is not published to Maven Central yet. Use the modules directly
from this repository:

```bash
./mill __.compile
./mill __.test
```

Or publish the library modules to your local Maven repository:

```bash
./mill __.publishM2Local
```

Local snapshot coordinates:

```scala
"io.worxbend" %% "gitea4s-core" % "0.1.0-SNAPSHOT"
"io.worxbend" %% "gitea4s-client" % "0.1.0-SNAPSHOT"
"io.worxbend" %% "gitea4s-backend-zio" % "0.1.0-SNAPSHOT"
"io.worxbend" %% "gitea4s-backend-okhttp" % "0.1.0-SNAPSHOT"
```

Mill publishes source and javadoc jars for these four library modules. The
`examples` and `it` modules are runnable project modules, not published
artifacts.

Module dependency direction:

```text
core
client         -> core
backend-zio    -> client
backend-okhttp -> client
examples       -> backend-zio
it             -> backend-zio
```

OkHttp dependencies are confined to `backend-okhttp`; `core`, `client`, and
`backend-zio` do not depend on OkHttp.

## Quickstart

Paste this into a ZIO app that depends on `backend-zio`:

```scala
import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.RepoListParams
import sttp.client4.*
import zio.{Console, ZIO, ZIOAppDefault}

object Main extends ZIOAppDefault:
  private val layer =
    ZioGiteaBackend.withToken(
      uri"https://gitea.example",
      sys.env("GITEA_TOKEN")
    )

  def run =
    ZIO.serviceWithZIO[GiteaClient] { client =>
      for
        me <- client.me
        login <- ZIO.fromOption(me.login).orElseFail(new RuntimeException("missing login"))
        repos <- client.list(login, RepoListParams(limit = Some(25))).take(25).runCollect
        _ <- Console.printLine(s"Repositories for $login")
        _ <- ZIO.foreachDiscard(repos)(repo =>
          Console.printLine(s"- ${repo.fullName.orElse(repo.name).getOrElse("<unknown>")}")
        )
      yield ()
    }.provideLayer(layer)
```

## Auth Modes

Programmatic constructors:

```scala
GiteaConfig.withToken(baseUrl, token)
GiteaConfig.withBasic(baseUrl, username, password)
GiteaConfig.anonymous(baseUrl)
```

Environment loading:

```text
GITEA_URL
GITEA_TOKEN
GITEA_USERNAME
GITEA_PASSWORD
GITEA_PAGE_SIZE
GITEA_TIMEOUT
GITEA_MAX_RETRIES
```

`GITEA_TOKEN` has precedence. If token auth is absent, basic auth is used only
when both `GITEA_USERNAME` and `GITEA_PASSWORD` are present. Config errors name
the invalid setting without echoing credential values.

Typesafe config is supported under the `gitea4s` path:

```hocon
gitea4s {
  url = "https://gitea.example"
  token = "..."
  page-size = 50
  timeout = 30s
  user-agent = "my-app"
  max-retries = 2
}
```

## ZLayer Usage

Use a complete config:

```scala
val layer = ZioGiteaBackend.configured(config)
```

Load config from the environment:

```scala
val layer = GiteaConfig.environmentLayer >>> ZioGiteaBackend.live
```

Use a caller-owned Java HTTP client:

```scala
val layer = ZioGiteaBackend.usingClient(config, javaHttpClient)
```

The optional OkHttp bridge has the same shape through `OkHttpGiteaBackend`.

## Pagination Streams

Paginated list APIs return `ZStream[Any, GiteaError, A]`. The client fetches
pages lazily and follows pagination headers.

```scala
client
  .notificationThreads()
  .take(100)
  .runCollect
```

Current stream-oriented APIs include user followers/following/search,
user and organization repositories, organization members, issues, branches,
tags, releases, pull requests, and notification threads.

## Error Handling

Client calls fail with `GiteaError`:

```scala
client.me.foldZIO(
  error => Console.printLineError(s"Gitea call failed: $error"),
  user => Console.printLine(user.login.getOrElse("<unknown>"))
)
```

HTTP failures preserve response bodies where available. Decode failures include
the raw body, transport failures preserve the cause, and rate-limit errors carry
the reset time when Gitea sends one.

## Retry And Rate Limits

Read-only requests honor `GiteaConfig.maxRetries`.

Implemented retry cases:

- transport failures
- `429` rate-limit responses, using reset headers when present
- selected `500`, `502`, `503`, and `504` server responses

Write requests are not retried by default. Tests use the ZIO Test clock, so retry
coverage does not sleep in real time.

## Backend Choices

Use `backend-zio` by default. It is backed by Java `HttpClient` through sttp's
`HttpClientZioBackend`.

Use `backend-okhttp` only when an application already standardizes on OkHttp.
That module adapts sttp's async OkHttp backend to ZIO and keeps OkHttp off the
main client and core dependency path.

## Publishing Policy

Current versioning starts at `0.1.0-SNAPSHOT`. Before a stable `1.0.0`, source
and binary compatibility may change while the API surface is still being filled
out from the Gitea `1.26.2` contract. Patch releases should preserve behavior
except for bug fixes; minor releases may add endpoints, models, and parameters.

The checked-in license is GPL-2.0-only, and the generated POM metadata uses the
same license identifier.

## Examples

The default example is hermetic when live credentials are absent:

```bash
./mill examples.run
```

Run the read-only live examples with credentials:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
./mill examples.runMain io.worxbend.gitea4s.examples.ListMyRepos

GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
./mill examples.runMain io.worxbend.gitea4s.examples.WatchNotifications

GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_USER_QUERY=alice \
./mill examples.runMain io.worxbend.gitea4s.examples.SearchUsers

GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_ORG=my-org \
./mill examples.runMain io.worxbend.gitea4s.examples.OrgMembers

GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
./mill examples.runMain io.worxbend.gitea4s.examples.ListReleases

GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
./mill examples.runMain io.worxbend.gitea4s.examples.ListPullRequests

GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
./mill examples.runMain io.worxbend.gitea4s.examples.ListBranchesAndTags
```

If `GITEA_URL`, credentials, or example-specific inputs such as `GITEA_USER_QUERY`
and `GITEA_ORG` are missing, examples print the target API version and exit
without making network calls.

## Testing Against Gitea

Unit tests are hermetic and use sttp `BackendStub`, JSON fixtures, response mapper
tests, and pagination tests:

```bash
./mill __.test
```

Live integration tests are opt-in:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
./mill it.test
```

Without both integration variables, `it.test` reports the live tests as ignored
and makes no external calls.

## Mill Commands

```bash
./mill __.compile
./mill __.test
./mill it.test
./mill __.docJar
./mill __.sourceJar
./mill __.publishArtifacts
./mill __.publishM2Local
./mill examples.run
./mill examples.runMain io.worxbend.gitea4s.examples.ListMyRepos
./mill examples.runMain io.worxbend.gitea4s.examples.WatchNotifications
./mill examples.runMain io.worxbend.gitea4s.examples.SearchUsers
./mill examples.runMain io.worxbend.gitea4s.examples.OrgMembers
./mill examples.runMain io.worxbend.gitea4s.examples.ListReleases
./mill examples.runMain io.worxbend.gitea4s.examples.ListPullRequests
./mill examples.runMain io.worxbend.gitea4s.examples.ListBranchesAndTags
```

The rewrite is still in progress. Maven Central publishing automation, CI,
compatibility checks, changelog workflow, and write endpoints remain planned
work.
