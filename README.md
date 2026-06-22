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
  downloads, Git tree reads, Git blob reads, Git reference reads, annotated tag
  reads, repository contents metadata reads, raw/media repository file byte
  downloads, repository archive byte downloads, repository collaborator
  list/check/permission reads, commit-to-pull-request lookup, release listing
  with typed filters, release detail/latest/tag lookup, release asset metadata
  reads, pull requests including reviews,
  pinned pull-request reads, pull-request create/edit writes, diff/patch
  downloads, merge-status checks, merge/update commands, review-comment
  resolution, and notifications through a ZIO client API
- Contract checks: implemented endpoint metadata is audited against
  `plugin-redoc-2.yaml` for pull-request review lifecycle, commit-status,
  pull-request create/edit, merge/update, commit-to-pull-request,
  single-commit, commit note, commit diff/patch, Git tree, Git blob, Git refs,
  annotated tag, repository contents, raw/media repository file, repository
  archive, release list/detail, latest-release, release-by-tag, and release
  asset metadata endpoints,
  including documented non-2xx response labels, optional query parameters,
  `application/octet-stream`/Swagger `type: file` response shape for raw/media
  downloads, the archive operation's bare `200` success description, path enum
  values, and clear path/method/parameter mismatch failures
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
issue comments, branches, tags, repository collaborators, releases, pull
requests, and notification threads. Commit-status list APIs are also paginated
streams. Pinned issues and pinned pull requests are exposed as non-paginated
chunks because Gitea returns those endpoints as plain list responses without
pagination parameters. Release asset metadata lists are also non-paginated
chunks in the local Swagger contract.

## Repository Collaborators

Repository collaborator reads cover the three Swagger-documented `GET`
operations under `/repos/{owner}/{repo}/collaborators`:

- `client.collaborators(owner, repo)` streams paginated collaborators as
  `User` values from `repoListCollaborators`. The stream starts at page 1 and
  sends `page` plus the configured `GiteaConfig.pageSize` as `limit` on each
  request.
- `client.isCollaborator(owner, repo, collaborator)` calls
  `repoCheckCollaborator` and returns `IO[GiteaError, Boolean]`. A `204`
  response decodes as `true`, and the endpoint-specific `404` response decodes
  as `false`; other failures flow through the shared error mapper.
- `client.collaboratorPermission(owner, repo, collaborator)` calls
  `repoGetRepoPermissions` and returns `RepoCollaboratorPermission` in
  `IO[GiteaError, RepoCollaboratorPermission]`, with optional `permission`,
  `roleName`, and `user` fields.

All three collaborator methods are read-only and retryable under
`GiteaConfig.maxRetries`. Owner, repository, and collaborator username values
are encoded as path segments. Collaborator add/delete/write operations are not
part of this API slice.

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

Repository contents metadata lookup covers
`GET /repos/{owner}/{repo}/contents` and
`GET /repos/{owner}/{repo}/contents/{filepath}` through overloaded
`client.contents` methods. Root contents decode as a non-paginated
`Chunk[ContentsResponse]`; a filepath lookup decodes one `ContentsResponse`.
`ContentsParams(ref = Some(...))` sends the documented optional `ref` query
parameter, while `ContentsParams.default` omits it. Slash-containing filepaths
such as `docs/readme.md` are encoded as one path parameter
(`docs%2Freadme.md`) instead of being split into multiple route segments.
Successful responses preserve the Swagger JSON names through
`ContentsResponse` and nested `FileLinksResponse`, including `_links`,
`download_url`, `git_url`, `html_url`, `last_author_date`,
`last_commit_message`, `last_commit_sha`, `last_committer_date`, `lfs_oid`,
`lfs_size`, and `submodule_git_url`. The `content` field stays as the encoded
string returned by Gitea; this layer does not base64-decode it. Both requests
are read-only retryable and propagate documented `404` failures through the
shared error mapper.

Raw repository file downloads cover `GET /repos/{owner}/{repo}/raw/{filepath}`
through `client.rawFile`, and media/LFS-aware downloads cover
`GET /repos/{owner}/{repo}/media/{filepath}` through `client.mediaFile`.
Successful responses return `Chunk[Byte]` and advertise
`Accept: application/octet-stream`; they do not pass through JSON or `String`
decoding. Both methods accept `ContentsParams`, so `ContentsParams(ref =
Some(...))` sends the documented optional `ref` query parameter and
`ContentsParams.default` omits it. Slash-containing filepaths such as
`docs/readme.md` use the same one-segment encoding convention as `contents`.
These byte-download methods are intentionally separate from `contents`, which
remains metadata-oriented and continues returning `ContentsResponse`.

Repository archive downloads cover
`GET /repos/{owner}/{repo}/archive/{archive}` through `client.archive`.
Successful responses return buffered `Chunk[Byte]` and advertise
`Accept: application/octet-stream`; this is a pragmatic client behavior for
archive bytes. The local `plugin-redoc-2.yaml` operation is less precise: it
records `produces: application/json` and a bare `200` success description, not
an `application/octet-stream` Swagger `type: file` response schema. Pass the
archive path exactly as Gitea documents it, such as `main.zip` or
`v1.0.0.tar.gz`. Slash-containing archive values are encoded as one path
segment, so refs such as `refs/heads/main.tar.gz` are preserved as a single
`archive` parameter. `ArchiveParams.default` omits query parameters and
downloads the whole archive. To request one or more subpaths, pass
`ArchiveParams(path = Chunk("src", "docs/readme.md"))`; each value is sent as
a repeated `path` query parameter. Archive downloads are separate from
metadata-oriented `contents` and from raw/media single-file downloads.

Repository release metadata covers paginated release listing through
`client.releases(owner, repo)`, latest release lookup through
`client.latestRelease(owner, repo)`, and single release lookup through
`client.release(owner, repo, id)`. Release listing accepts
`ReleaseListParams` for the documented `draft`, `pre-release`, and page-size
`limit` query controls. `ReleaseListParams.default` omits filter keys, while
the paginated stream helper always starts at page 1 and supplies page values as
it fetches pages. `ReleaseListParams.page` remains a lower-level
request-builder control for `GiteaRequests.repoReleases`; it is not a stream
start-page selector for `client.releases`. Use the Scala field `preRelease`
for the wire query name `pre-release`. Latest release lookup uses
`GET /repos/{owner}/{repo}/releases/latest` to return the server-selected
latest `Release` metadata. It has no query parameters or request body, sends
JSON accept headers, is read-only retryable, and propagates documented `404`
failures through the shared error mapper. Tag lookup uses
`client.releaseByTag(owner, repo, tag)` and the Gitea endpoint
`GET /repos/{owner}/{repo}/releases/tags/{tag}` to return `Release` metadata
for a tag. Tag values with punctuation such as `v1.0.0` and values with
slashes such as `release/candidate` are encoded as one `tag` path segment. The
lookup has no query parameters or request body, sends JSON accept headers, is
read-only retryable, and propagates documented `404` failures through the
shared error mapper.

Release asset metadata covers
`GET /repos/{owner}/{repo}/releases/{id}/assets` through
`client.releaseAssets(owner, repo, releaseId)` and
`GET /repos/{owner}/{repo}/releases/{id}/assets/{attachment_id}` through
`client.releaseAsset(owner, repo, releaseId, assetId)`. The local Swagger
contract names these response schemas `AttachmentList` and `Attachment`; this
client exposes them as `ReleaseAsset` to keep the public API release-scoped.
The asset list endpoint has no documented `page`, `limit`, or other query
parameters, so it returns `IO[GiteaError, Chunk[ReleaseAsset]]` rather than a
pagination stream. `ReleaseAsset` preserves metadata fields such as
`browser_download_url`, `created_at`, `download_count`, `id`, `name`, `size`,
and `uuid`. Release create, edit, and delete operations are not implied by the
release metadata API. Release asset upload, edit, delete, and binary download
surfaces are also not part of the current read-only API.

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
  ArchiveParams,
  CommitNoteParams,
  ContentsParams,
  GitTreeParams,
  ReleaseListParams,
  SingleCommitParams
}
import io.worxbend.gitea4s.model.CommitDiffType
import zio.Chunk

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

client.contents(owner = "my-org", repo = "my-repo", params = ContentsParams.default)

client.contents(
  owner = "my-org",
  repo = "my-repo",
  filepath = "docs/readme.md",
  params = ContentsParams(ref = Some("main"))
)

client.rawFile(
  owner = "my-org",
  repo = "my-repo",
  filepath = "docs/readme.md",
  params = ContentsParams(ref = Some("main"))
)

client.mediaFile(
  owner = "my-org",
  repo = "my-repo",
  filepath = "docs/readme.md",
  params = ContentsParams.default
)

client.archive(owner = "my-org", repo = "my-repo", archive = "main.zip")

client.archive(
  owner = "my-org",
  repo = "my-repo",
  archive = "main.zip",
  params = ArchiveParams(path = Chunk("src", "docs/readme.md"))
)

client.collaborators(owner = "my-org", repo = "my-repo").take(25).runCollect

client.isCollaborator(
  owner = "my-org",
  repo = "my-repo",
  collaborator = "alice"
)

client.collaboratorPermission(
  owner = "my-org",
  repo = "my-repo",
  collaborator = "alice"
)

client.releases(owner = "my-org", repo = "my-repo").take(25).runCollect

client
  .releases(
    owner = "my-org",
    repo = "my-repo",
    params = ReleaseListParams(
      draft = Some(false),
      preRelease = Some(true),
      limit = Some(25)
    )
  )
  .take(25)
  .runCollect

client.release(owner = "my-org", repo = "my-repo", id = 7)

client.latestRelease(owner = "my-org", repo = "my-repo")

client.releaseByTag(owner = "my-org", repo = "my-repo", tag = "release/candidate")

client.releaseAssets(owner = "my-org", repo = "my-repo", releaseId = 7)

client.releaseAsset(
  owner = "my-org",
  repo = "my-repo",
  releaseId = 7,
  assetId = 91
)

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
tree/blob/annotated-tag/refs, repository contents, raw/media repository file,
repository archive, release list/detail, latest-release, release-by-tag, and
release asset metadata endpoint groups against
`plugin-redoc-2.yaml`, including
documented non-2xx response status/ref labels, optional query parameters such as
`recursive`/`page`/`per_page`, contents/raw/media `ref`, and archive `path`,
`application/octet-stream`/Swagger `type: file` response shape for raw/media
downloads, the archive operation's bare `200` success description,
no-query/no-body checks for Git blob, annotated tag, and refs requests, no-body
checks for contents, raw/media, archive, release list/detail, release-by-tag,
latest-release, and release asset requests, release list/detail/latest
`ReleaseList`/`Release` response refs, release asset
`AttachmentList`/`Attachment` response refs, and path enum values such as
`diffType`.

Live integration tests are opt-in:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
./mill it.test
```

Without both integration variables, `it.test` reports the live tests as ignored
and makes no external calls. Additional live probes stay hermetic unless every
variable required by that probe is non-empty.

The slash-containing Git ref routing probe calls `ReposApi.gitRefs(owner, repo,
ref)` and requires all of:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
GITEA_REF=heads/main \
./mill it.test
```

The annotated tag lookup probe calls `ReposApi.annotatedTag(owner, repo, sha)`
and requires an explicit annotated tag object SHA:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
GITEA_ANNOTATED_TAG_SHA=<annotated-tag-sha> \
./mill it.test
```

Repository tag-list entries are not assumed to be annotated tag objects; supply
`GITEA_ANNOTATED_TAG_SHA` only when you already have the SHA for an annotated
tag object.

The repository contents filepath probe calls
`ReposApi.contents(owner, repo, filepath, params)` and requires a configured
filepath. Use a slash-containing value such as `docs/readme.md` when validating
the current one-segment filepath encoding against a live Gitea instance.
`GITEA_CONTENTS_REF` is optional and is passed as `ContentsParams(ref =
Some(value))` when non-empty:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
GITEA_CONTENTS_FILEPATH=docs/readme.md \
GITEA_CONTENTS_REF=main \
./mill it.test
```

The repository archive probe calls `ReposApi.archive(owner, repo, archive,
params)` and requires a configured archive value such as `main.zip`.
`GITEA_ARCHIVE_PATHS` is optional; when set, it is parsed as a comma-separated
list of repository subpaths, with entries trimmed and empty entries ignored,
then sent as repeated archive `path` query values. Omit
`GITEA_ARCHIVE_PATHS` for the default whole-archive probe; include it when you
want the live probe to exercise subpath archive queries:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
GITEA_ARCHIVE=main.zip \
GITEA_ARCHIVE_PATHS=src,docs/readme.md \
./mill it.test
```

Release metadata APIs are read-only and metadata-only. The live probes call
`client.release(owner, repo, id)`, `client.releaseByTag(owner, repo, tag)`,
`client.releaseAssets(owner, repo, releaseId)`,
`client.releaseAsset(owner, repo, releaseId, assetId)`, and
`client.latestRelease(owner, repo)`. In addition to `GITEA_URL`,
`GITEA_TOKEN`, `GITEA_OWNER`, and `GITEA_REPO`, configure `GITEA_RELEASE_ID`
for release detail and asset-list probes, and both `GITEA_RELEASE_ID` and
`GITEA_RELEASE_ASSET_ID` for the single asset lookup. `GITEA_RELEASE_TAG`
enables the release-by-tag probe only when all of `GITEA_URL`, `GITEA_TOKEN`,
`GITEA_OWNER`, `GITEA_REPO`, and `GITEA_RELEASE_TAG` are non-empty. That probe
passes the configured tag unchanged to `client.releaseByTag(owner, repo, tag)`
and asserts that the returned `Release.tagName` option contains exactly that
configured value. Slash-containing tags such as `release/candidate` are the
intended confidence case for validating this endpoint's live routing behavior,
but no live slash-containing `GITEA_RELEASE_TAG` observation is currently
recorded in this repository. Until that enabled probe succeeds against a real
Gitea repository, slash-containing release-tag routing remains unverified live
behavior. A probe run with a normal tag such as `v1.0.0` is useful generic
release-by-tag confidence only; it should not be treated as slash-routing
evidence.
`GITEA_LATEST_RELEASE_TAG` enables the latest-release probe and must name the
repository's actual latest non-draft, non-prerelease release tag; it is the
only release variable that asserts latest-release semantics. When
`GITEA_RELEASE_ASSET_ID` is set, the asset-list probe also checks that the
configured asset id appears in `client.releaseAssets(owner, repo, releaseId)`:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
GITEA_OWNER=my-org \
GITEA_REPO=my-repo \
GITEA_RELEASE_ID=7 \
GITEA_RELEASE_TAG=release/candidate \
GITEA_LATEST_RELEASE_TAG=v1.2.3 \
GITEA_RELEASE_ASSET_ID=42 \
./mill it.test
```

For credential-stripped integration validation, unset the full live-variable
set so `it.test` reports the probes as ignored and makes no network calls:

```bash
env -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF -u GITEA_ARCHIVE -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG -u GITEA_LATEST_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID ./mill --no-server it.test
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
