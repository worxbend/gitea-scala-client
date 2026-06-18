# Changelog

All notable changes to this project will be documented in this file.

This project follows semantic versioning after `1.0.0`. Before `1.0.0`, minor
versions may change source or binary compatibility while the typed Gitea API
surface is still being filled out.

## Unreleased

### Added

- Mill-built Scala 3 rewrite under the `io.worxbend.gitea4s` package root.
- Typed core models, zio-json codecs, and the `GiteaError` ADT.
- Read-only ZIO client APIs for users, organizations, repositories, issues,
  releases, pull requests, and notifications.
- Typed issue creation with `CreateIssue` and `issueCreateIssue` request
  construction.
- Typed issue deletion with `issueDelete` request construction.
- Typed issue pinning, unpinning, and pin moving with `pinIssue`,
  `unpinIssue`, and `moveIssuePin` request construction.
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
- Local Maven publishing metadata, source jars, and javadoc jars.
- Java 21 CI validation and release-process documentation.
- Sonatype Central Portal publishing groundwork through Mill and a manual
  GitHub Actions workflow.
- Renovate regex managers for Mill, Scala, ZIO, zio-json, zio-config, and sttp
  version pins.
- Checked-in public API snapshots and a Mill `compatibility.check` release
  guard for the published modules.

### Notes

- No Maven Central release has been cut yet. Local snapshots use
  `0.1.0-SNAPSHOT`.
