# gitea4s

Scala 3 client library for the Gitea API, built with Mill, ZIO 2, sttp client4,
and zio-json.

## Status

- Build tool: Mill `1.1.6` through the checked-in `./mill` launcher
- Package root: `io.worxbend.gitea4s`
- JVM target: Java 21
- Version: `0.1.0-SNAPSHOT`
- API reference: local `plugin-redoc-2.yaml` for Gitea API `1.26.2`
- Implemented surface: typed core models/codecs plus users, organizations,
  repositories, issue list/get/create/deadline/label/lock management, releases, pull requests, and notifications through a ZIO client API
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

## Issue Writes

The current write endpoints cover issue creation, editing, closing, deadlines, labels, locks, and comments:

```scala
import io.worxbend.gitea4s.model.{CreateIssue, EditDeadlineOption, EditIssue, LockIssueOption}
import zio.Chunk
import java.time.Instant

client.create(
  owner = "my-org",
  repo = "my-repo",
  body = CreateIssue(title = "Bug report", body = Some("Observed behavior..."))
)

client.edit(
  owner = "my-org",
  repo = "my-repo",
  index = 12,
  body = EditIssue(title = Some("Updated title"))
)

client.close(owner = "my-org", repo = "my-repo", index = 12)

client.editDeadline(
  owner = "my-org",
  repo = "my-repo",
  index = 12,
  body = EditDeadlineOption(dueDate = Some(Instant.parse("2026-07-03T00:00:00Z")))
)
client.editDeadline(owner = "my-org", repo = "my-repo", index = 12, body = EditDeadlineOption(dueDate = None))

client.addLabels(owner = "my-org", repo = "my-repo", index = 12, labels = Chunk(1L, 2L))
client.replaceLabels(owner = "my-org", repo = "my-repo", index = 12, labels = Chunk(3L))
client.removeLabel(owner = "my-org", repo = "my-repo", index = 12, id = 3L)
client.clearLabels(owner = "my-org", repo = "my-repo", index = 12)

client.lock(
  owner = "my-org",
  repo = "my-repo",
  index = 12,
  body = LockIssueOption(lockReason = Some("resolved"))
)
client.unlock(owner = "my-org", repo = "my-repo", index = 12)

client.comment(owner = "my-org", repo = "my-repo", index = 12, body = "Confirmed")
```

Write requests are not retried by default.

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

Release notes are tracked in `CHANGELOG.md`. Local release readiness checks and
the pre-`1.0.0` version checklist are tracked in `RELEASE.md`.

The build is wired for Sonatype Central Portal publishing through Mill's
`SonatypeCentralPublishModule`. The manual GitHub Actions workflow
`Publish Central` validates the build, refuses `-SNAPSHOT` versions, and then
publishes the four library modules with `publishAll` when the Maven Central
secrets described in `RELEASE.md` are configured.

## Dependency Updates

Renovate is configured with regex managers for the version pins that Mill keeps
outside standard dependency manifests:

- `//| mill-version` in `build.mill`
- `DEFAULT_MILL_VERSION` in the checked-in `mill` launcher
- `Versions.scala`, `Versions.zio`, `Versions.zioJson`,
  `Versions.zioConfig`, and `Versions.sttp` in `build.mill`

Dependency PRs should run the same local validation used for release readiness:

```bash
./mill __.compile
./mill __.test
./mill it.test
./mill examples.run
./mill compatibility.check
./mill __.docJar __.sourceJar __.publishArtifacts
```

`./mill compatibility.check` compares the JVM public signatures of the four
published modules against the checked-in `api-snapshot/` baseline. Run
`./mill compatibility.writeSnapshot` only when an API change is intentional and
belongs in the next release notes.

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
./mill compatibility.check
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

CI runs the Java 21 Mill validation flow in `.github/workflows/ci.yml`. The
checked-in `Jenkinsfile` runs the same core commands for Jenkins-based
environments.

The rewrite is still in progress. Additional typed write endpoints remain
planned work.
