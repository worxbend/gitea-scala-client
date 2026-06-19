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
  repositories, issue list/get/pinned-list/create/delete/pin/deadline/label/lock/
  dependency/blocking/reaction/subscription/tracked-time/stopwatch management,
  commit statuses, single-commit lookup, commit note lookup, commit diff/patch
  downloads, Git tree reads, Git blob reads, commit-to-pull-request lookup,
  releases, pull requests including reviews, pinned pull-request reads,
  pull-request create/edit writes, diff/patch downloads, merge-status checks,
  merge/update commands, review-comment resolution, and notifications through a
  ZIO client API
- Contract checks: implemented endpoint metadata is audited against
  `plugin-redoc-2.yaml` for pull-request review lifecycle, commit-status,
  pull-request create/edit, merge/update, commit-to-pull-request,
  single-commit, commit note, commit diff/patch, Git tree, and Git blob endpoints,
  including documented non-2xx response labels, optional query parameters, path
  enum values, and clear path/method/parameter mismatch failures
- Endpoint audit-only non-success response labels live in test scope; the
  published client endpoint metadata exposes operation method, path, operation
  ID, parameters, and success response labels only
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

Current stream-oriented APIs include user followers/following/search, user and
organization repositories, organization members, issues, issue reactions, issue
subscribers, issue tracked times, current-user stopwatches, repository-wide
issue comments, branches, tags, releases, pull requests, and notification
threads. Commit-status list APIs are also paginated streams. Pinned issues and
pinned pull requests are exposed as non-paginated chunks because Gitea returns
those endpoints as plain list responses without pagination parameters.

## Commit Statuses

Repository commit-status support covers combined status lookup by ref, status
listing by ref or SHA, and status creation. `CombinedStatusParams` controls the
`page` and `limit` query parameters for combined status lookup; when omitted the
client sends page `1` and the configured default page size.

```scala
import io.worxbend.gitea4s.http.{
  CombinedStatusParams,
  CommitStatusListParams,
  CommitStatusListState,
  CommitStatusSort
}
import io.worxbend.gitea4s.model.{CommitStatusState, CreateStatusOption}

client.combinedStatusByRef(owner = "my-org", repo = "my-repo", ref = "main")

client.combinedStatusByRef(
  owner = "my-org",
  repo = "my-repo",
  ref = "main",
  params = CombinedStatusParams(page = Some(2), limit = Some(25))
)

client.statusesByRef(
  owner = "my-org",
  repo = "my-repo",
  ref = "main",
  params = CommitStatusListParams(
    sort = Some(CommitStatusSort.RecentUpdate),
    state = Some(CommitStatusListState.Success),
    limit = Some(25)
  )
).take(25).runCollect

client.statuses(owner = "my-org", repo = "my-repo", sha = "abc123").take(25).runCollect

client.createStatus(
  owner = "my-org",
  repo = "my-repo",
  sha = "abc123",
  body = CreateStatusOption(
    state = Some(CommitStatusState.Success),
    context = Some("ci/build"),
    targetUrl = Some("https://ci.example/build/123"),
    description = Some("Build passed")
  )
)
```

## Repository Commits

Repository commit lookup covers `GET /repos/{owner}/{repo}/git/commits/{sha}`
through `client.commit`. By default the client omits the documented `stat`,
`verification`, and `files` query parameters. Use `SingleCommitParams` when a
call needs to explicitly enable or disable those response details.

Repository commit note lookup covers
`GET /repos/{owner}/{repo}/git/notes/{sha}` through `client.commitNote`. By
default the client omits the documented `verification` and `files` query
parameters. Use `CommitNoteParams` when a call needs explicit control over
those optional response details. Successful responses decode as `Note`, and
documented `404`/`422` failures flow through the shared error mapper.

Repository Git tree lookup covers
`GET /repos/{owner}/{repo}/git/trees/{sha}` through `client.gitTree`. It accepts
`GitTreeParams` for the documented optional `recursive`, `page`, and `per_page`
query parameters, and `client.gitTree(owner, repo, sha)` omits all three by
default. Successful responses decode as
the exact Swagger-shaped `GitTreeResponse` object: `page`, `sha`, `total_count`,
`tree`, `truncated`, and `url` are body fields, with nested `GitEntry` values in
`tree`. This endpoint is intentionally not modeled as `Page[A]`; pagination
state comes from the JSON response body. Documented `400`/`404` failures flow
through the shared error mapper, and the read-only request is retryable.

Repository Git blob lookup covers
`GET /repos/{owner}/{repo}/git/blobs/{sha}` through `client.gitBlob`. Successful
responses decode as `GitBlobResponse` with optional `content`, `encoding`,
`lfsOid`, `lfsSize`, `sha`, `size`, and `url` fields. The `content` value stays
as the documented encoded string at this layer. The request has no query
parameters or body, is read-only retryable, and propagates documented
`400`/`404` failures through the shared error mapper.

Annotated Git tag lookup covers
`GET /repos/{owner}/{repo}/git/tags/{sha}` through `client.annotatedTag`.
Successful responses decode as `AnnotatedTag` with optional `message`, `object`,
`sha`, `tag`, `tagger`, `url`, and `verification` fields. This Swagger operation
returns annotated tag objects only; it is separate from `client.tags`, which
streams the repository tag-list endpoint and does not imply lightweight tag
object lookup support. The request has no query parameters or body, is read-only
retryable, and propagates documented `400`/`404` failures through the shared
error mapper.

Repository Git reference lookup covers
`GET /repos/{owner}/{repo}/git/refs` and
`GET /repos/{owner}/{repo}/git/refs/{ref}` through `client.gitRefs`. Successful
responses decode as a non-paginated `Chunk[Reference]` with optional `ref`,
`url`, and nested `GitObject` fields. The filtered lookup treats `ref` as one
encoded path segment, so a value like `heads/main` is sent as `heads%2Fmain`
rather than split into multiple path segments. Both requests have no query
parameters or body, are read-only retryable, and propagate documented `404`
failures through the shared error mapper.

Commit diff/patch downloads cover
`GET /repos/{owner}/{repo}/git/commits/{sha}.{diffType}` through
`client.commitDiffOrPatch`. Use `CommitDiffType.diff` or
`CommitDiffType.patch`; successful responses are returned as raw `String`
content, and the request advertises `Accept: text/plain`. The request is
read-only retryable, propagates the documented `404` through the shared error
mapper, and its Swagger audit proves the operation has no query parameters and
no request body.

```scala
import io.worxbend.gitea4s.http.{
  CommitNoteParams,
  GitTreeParams,
  SingleCommitParams
}
import io.worxbend.gitea4s.model.CommitDiffType

client.commit(owner = "my-org", repo = "my-repo", sha = "abc123")

client.commit(
  owner = "my-org",
  repo = "my-repo",
  sha = "abc123",
  params = SingleCommitParams(
    stat = Some(true),
    verification = Some(false),
    files = Some(true)
  )
)

client.commitNote(owner = "my-org", repo = "my-repo", sha = "abc123")

client.commitNote(
  owner = "my-org",
  repo = "my-repo",
  sha = "abc123",
  params = CommitNoteParams(
    verification = Some(true),
    files = Some(true)
  )
)

client.gitTree(
  owner = "my-org",
  repo = "my-repo",
  sha = "tree123"
)

client.gitTree(
  owner = "my-org",
  repo = "my-repo",
  sha = "tree123",
  params = GitTreeParams(
    recursive = Some(true),
    page = Some(2),
    perPage = Some(50)
  )
)

client.gitBlob(owner = "my-org", repo = "my-repo", sha = "blob123")

client.annotatedTag(owner = "my-org", repo = "my-repo", sha = "tag-object-sha")

client.gitRefs(owner = "my-org", repo = "my-repo")

client.gitRefs(owner = "my-org", repo = "my-repo", ref = "heads/main")

client.commitDiffOrPatch(
  owner = "my-org",
  repo = "my-repo",
  sha = "abc123",
  diffType = CommitDiffType.diff
)

client.commitDiffOrPatch(
  owner = "my-org",
  repo = "my-repo",
  sha = "abc123",
  diffType = CommitDiffType.patch
)
```

## Issue Writes

The current write endpoints cover issue creation, deletion, pinning, editing,
closing, deadlines, labels, locks, comments, reactions, subscriptions, tracked
times, stopwatches, dependencies, and blocking relationships:

```scala
import io.worxbend.gitea4s.http.{IssueCommentListParams, IssueTrackedTimeListParams, RepositoryCommentListParams}
import io.worxbend.gitea4s.model.{
  AddTimeOption,
  CreateIssue,
  EditDeadlineOption,
  EditIssue,
  EditIssueComment,
  EditReactionOption,
  IssueMeta,
  LockIssueOption
}
import zio.Chunk
import java.time.Instant

client.create(
  owner = "my-org",
  repo = "my-repo",
  body = CreateIssue(title = "Bug report", body = Some("Observed behavior..."))
)

client.pinned(owner = "my-org", repo = "my-repo")
client.newIssuePinsAllowed(owner = "my-org", repo = "my-repo")

client.delete(owner = "my-org", repo = "my-repo", index = 12)
client.pin(owner = "my-org", repo = "my-repo", index = 12)
client.movePin(owner = "my-org", repo = "my-repo", index = 12, position = 1)
client.unpin(owner = "my-org", repo = "my-repo", index = 12)

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
client.comments(owner = "my-org", repo = "my-repo", index = 12, params = IssueCommentListParams.default)
client.repositoryComments(owner = "my-org", repo = "my-repo", params = RepositoryCommentListParams.default)
client.comment(owner = "my-org", repo = "my-repo", id = 42)
client.editComment(owner = "my-org", repo = "my-repo", id = 42, body = EditIssueComment("Updated"))
client.deleteComment(owner = "my-org", repo = "my-repo", id = 42)

client.reactions(owner = "my-org", repo = "my-repo", index = 12)
client.react(owner = "my-org", repo = "my-repo", index = 12, body = EditReactionOption("+1"))
client.deleteReaction(owner = "my-org", repo = "my-repo", index = 12, body = EditReactionOption("+1"))
client.commentReactions(owner = "my-org", repo = "my-repo", id = 42)
client.reactToComment(owner = "my-org", repo = "my-repo", id = 42, body = EditReactionOption("eyes"))
client.deleteCommentReaction(owner = "my-org", repo = "my-repo", id = 42, body = EditReactionOption("eyes"))

client.subscribers(owner = "my-org", repo = "my-repo", index = 12)
client.subscription(owner = "my-org", repo = "my-repo", index = 12)
client.subscribe(owner = "my-org", repo = "my-repo", index = 12, user = "octo")
client.unsubscribe(owner = "my-org", repo = "my-repo", index = 12, user = "octo")

client.trackedTimes(owner = "my-org", repo = "my-repo", index = 12, params = IssueTrackedTimeListParams.default)
client.addTrackedTime(owner = "my-org", repo = "my-repo", index = 12, body = AddTimeOption(time = 1800L))
client.deleteTrackedTime(owner = "my-org", repo = "my-repo", index = 12, id = 44L)
client.resetTrackedTime(owner = "my-org", repo = "my-repo", index = 12)

client.stopwatches
client.startStopwatch(owner = "my-org", repo = "my-repo", index = 12)
client.stopStopwatch(owner = "my-org", repo = "my-repo", index = 12)
client.deleteStopwatch(owner = "my-org", repo = "my-repo", index = 12)

client.addDependency(owner = "my-org", repo = "my-repo", index = 12, dependency = IssueMeta(index = 10))
client.removeDependency(owner = "my-org", repo = "my-repo", index = 12, dependency = IssueMeta(index = 10))

client.block(owner = "my-org", repo = "my-repo", index = 12, blockedIssue = IssueMeta(index = 13))
client.unblock(owner = "my-org", repo = "my-repo", index = 12, blockedIssue = IssueMeta(index = 13))
```

Write requests are not retried by default.

## Pull Requests

Pull-request support includes paginated list/get methods, review creation,
submission, dismissal, undismissal, review request creation/cancellation,
review detail and comment methods, review-comment resolve/unresolve commands,
changed-file and commit streams, commit-to-pull-request lookup, raw diff/patch
downloads, merge-status checks, create/edit writes, merge/update commands, and
the repository pinned pull-request list. Create/edit use `CreatePullRequestOption` and
`EditPullRequestOption`; their JSON codecs preserve Gitea field names such as
`allow_maintainer_edit`, `team_reviewers`, `content_version`, and
`unset_due_date`.
`MergePullRequestOption` supports merge methods `merge`, `rebase`,
`rebase-merge`, `squash`, `fast-forward-only`, and `manually-merged`.
Pull-request update style values are `merge` and `rebase`:

```scala
import io.worxbend.gitea4s.http.{PullRequestDiffType, PullRequestUpdateStyle}
import io.worxbend.gitea4s.model.{
  CreatePullRequestOption,
  CreatePullReviewOptions,
  DismissPullReviewOptions,
  EditPullRequestOption,
  IssueState,
  MergePullRequestMethod,
  MergePullRequestOption,
  PullReviewRequestOptions,
  PullReviewState,
  SubmitPullReviewOptions
}

client.pullRequests(owner = "my-org", repo = "my-repo").take(25).runCollect
client.pullRequest(owner = "my-org", repo = "my-repo", index = 7)
client.pullRequestByBaseHead(owner = "my-org", repo = "my-repo", base = "main", head = "feature")
client.commitPullRequest(owner = "my-org", repo = "my-repo", sha = "abc123")
client.createPullRequest(
  owner = "my-org",
  repo = "my-repo",
  body = CreatePullRequestOption(
    base = Some("main"),
    head = Some("feature"),
    title = Some("Add typed client support"),
    body = Some("Implements a small API slice."),
    allowMaintainerEdit = Some(true)
  )
)
client.editPullRequest(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  body = EditPullRequestOption(
    title = Some("Add typed client support"),
    state = Some(IssueState.Open),
    contentVersion = Some(3L)
  )
)
client.pullRequestIsMerged(owner = "my-org", repo = "my-repo", index = 7)
client.mergePullRequest(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  body = MergePullRequestOption(
    mergeMethod = MergePullRequestMethod.Squash,
    deleteBranchAfterMerge = Some(true)
  )
)
client.cancelScheduledAutoMerge(owner = "my-org", repo = "my-repo", index = 7)
client.updatePullRequest(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  style = PullRequestUpdateStyle.Rebase
)
client.requestPullReviews(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  body = PullReviewRequestOptions(reviewers = Some(List("reviewer")))
)
client.cancelPullReviewRequests(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  body = PullReviewRequestOptions(reviewers = Some(List("reviewer")))
)
client.pullRequestReviews(owner = "my-org", repo = "my-repo", index = 7).take(50).runCollect
client.createPullRequestReview(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  body = CreatePullReviewOptions(body = Some("Looks good"), event = Some(PullReviewState.Comment))
)
client.pullRequestReview(owner = "my-org", repo = "my-repo", index = 7, id = 20)
client.submitPullRequestReview(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  id = 20,
  body = SubmitPullReviewOptions(body = Some("Approved"), event = Some(PullReviewState.Approved))
)
client.dismissPullRequestReview(
  owner = "my-org",
  repo = "my-repo",
  index = 7,
  id = 20,
  body = DismissPullReviewOptions(message = Some("outdated"))
)
client.undismissPullRequestReview(owner = "my-org", repo = "my-repo", index = 7, id = 20)
client.pullRequestReviewComments(owner = "my-org", repo = "my-repo", index = 7, id = 20)
client.deletePullRequestReview(owner = "my-org", repo = "my-repo", index = 7, id = 20)
client.resolvePullRequestReviewComment(owner = "my-org", repo = "my-repo", id = 91)
client.unresolvePullRequestReviewComment(owner = "my-org", repo = "my-repo", id = 91)
client.pullRequestDiffOrPatch(owner = "my-org", repo = "my-repo", index = 7, diffType = PullRequestDiffType.Diff)
client.pullRequestFiles(owner = "my-org", repo = "my-repo", index = 7).take(50).runCollect
client.pullRequestCommits(owner = "my-org", repo = "my-repo", index = 7).take(50).runCollect
client.pinnedPullRequests(owner = "my-org", repo = "my-repo")
```

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
the reset time when Gitea sends one. Resource-state failures map globally to
explicit cases, including `GiteaError.MethodNotAllowed` for `405`,
`GiteaError.PreconditionFailed` for `412`, and `GiteaError.Locked` for `423`;
mapper-level tests cover decoded JSON error messages, empty bodies, and
non-JSON bodies for these statuses.

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

Endpoint audit tests compare the current pull-request review lifecycle,
commit-status, pull-request create/edit, pull-request merge/update,
commit-to-pull-request, single-commit, commit note, commit diff/patch, and Git
tree/blob/annotated-tag/refs endpoint groups against `plugin-redoc-2.yaml`, including
documented non-2xx response status/ref labels, optional query parameters such as
`recursive`/`page`/`per_page`, no-query/no-body checks for Git blob, annotated
tag, and refs requests, and path enum values such as `diffType`.

Live integration tests are opt-in:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
./mill it.test
```

Without both integration variables, `it.test` reports the live tests as ignored
and makes no external calls.

Latest annotated Git tag validation passed with:

```bash
./mill core.test
./mill client.test
./mill compatibility.check
```

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
