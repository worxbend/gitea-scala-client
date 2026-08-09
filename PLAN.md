# gitea4s — Roadmap to a Production-Ready Library (and a Gitea CLI)

> This file is the forward-looking roadmap. Historical implementation notes
> live in `CHANGELOG.md` and the contributor/agent logs. The previous
> changelog-style `PLAN.md` was intentionally discarded and replaced by this
> roadmap on 2026-06-27.

## Mission

`gitea4s` is a Scala 3 / ZIO 2 / sttp4 client for the Gitea API
(`io.worxbend.gitea4s`, Mill build). The near-term goal is a **production-ready,
widely adoptable library**. The longer-term goal is a **`gh`-style Gitea CLI**
built as a separate module on top of the library.

## Locked Strategic Decisions (2026-06-27)

| Decision | Choice | Rationale |
| --- | --- | --- |
| **License** | **Apache-2.0** (relicense from GPL-2.0-only) | Permissive licensing is required for a library others depend on; copyleft is the single biggest adoption blocker. |
| **Build approach** | **Hand-written** (no Swagger codegen) | Preserves curated ergonomics and the spec-driven contract-audit discipline; decompose modules as breadth grows. |
| **Next milestone** | **Harden + reshape toward a stable 1.0** | Do the breaking reshape and cross-cutting concerns before chasing write-endpoint breadth or the CLI. |

## Current State (baseline)

- ~130 of 471 spec operations implemented (~28%), skewed to read-only
  repo/issue/PR/release/notification flows.
- Clean layering: `core` → `client` → `backend-zio` / `backend-okhttp` →
  `examples` / `it`. Build is green (`./mill __.compile __.test`, 760 tests).
- Strong foundations: `GiteaRequest`/`GiteaResponseMapper`/`GiteaRequestExecutor`
  execution boundary, jittered retry with rate-limit awareness, spec contract
  audit (`GiteaEndpointAuditSpec`), API-snapshot binary-compat guard, CI +
  Jenkins + Renovate + Sonatype Central publishing.

## Known Risks Driving This Plan

1. License is GPL-2.0-only — blocks library adoption. *(Phase A)*
2. Flat `GiteaClient` god-trait forces method-name collisions and snapshot
   churn; needs sub-client namespaces. Breaking, so pre-1.0. *(Phase B)*
3. No observability (zero logging/metrics/tracing). *(Phase C)*
4. Byte downloads (archive/raw/media) buffer fully into `Chunk[Byte]`. *(Phase C)*
5. Docs read like an audit log, not a user guide. *(Phase A)*
6. Single-host config vs. the CLI's multi-host needs. *(Phase E)*

---

## Phases

### Phase A — Relicense + cleanup *(done 2026-06-27)*
- [x] Relicense GPL-2.0-only → Apache-2.0: `LICENSE`, `build.mill` POM
      `pomSettings.licenses`, and README references. POM verified via
      `./mill core.pom`.
- [x] Remove cruft: `core/.hiden`, `http-client/.hiden`, `util/.hiden`, and the
      empty `http-client/` & `util/` directories.
- [x] Split docs: trimmed `README.md` (1301 → ~280 lines) to a user guide; moved
      testing matrix, API conventions, and dependency process to new
      `CONTRIBUTING.md`.
- [x] Gate: `./mill __.compile __.test compatibility.check` green.
- Note: `.github/unicorns` is a misnamed/dead Mergify config (the live one is
  `.mergify.yml`); left in place pending owner confirmation before deletion.

### Phase B — Reshape to sub-clients *(done 2026-06-27)*
- [x] Replaced the flat `GiteaClient` god-trait with namespaces:
      `client.repos`, `client.issues`, `client.pulls`, `client.releases`,
      `client.notifications`, `client.users`, `client.orgs`. `me` lives at
      `client.users.me`. (`client.admin` will arrive in Phase D.)
- [x] Split the 779-line `SttpGiteaClient` into one `Sttp*Api` impl per
      namespace under `internal/`, sharing a single `GiteaRequestExecutor`.
- [x] Resolved the `get`/`list` collisions and restored `ReposApi.list`'s
      default argument.
- [x] Regenerated `api-snapshot/client.txt` as the new baseline; validated with
      `./mill __.compile __.test compatibility.check`.
- [x] Follow-up (done 2026-06-27): dropped namespace-echoing method prefixes in
      `pulls`/`releases`/`notifications` (e.g. `pulls.createPullRequest` →
      `pulls.create`, `releases.releases` → `releases.list`,
      `notifications.unreadNotificationCount` → `notifications.unreadCount`).
      Signatures unchanged; snapshot regenerated; gate green.
- Note: `compatibility.check`/`writeSnapshot` need `jar`/`javap` on `PATH`
  (CI provides them via setup-java; locally, add the JDK `bin` and use
  `./mill --no-server`).

### Phase C — Production hardening *(done 2026-06-27)*
- [x] Observability seam: `GiteaObserver` hook around `GiteaRequestExecutor`
      (endpoint + duration + outcome), with `noop`/`logging`/`metrics` built-ins,
      `++` composition, and defect-safety. Threaded via `GiteaConfig.observer`.
      OpenTelemetry is a `GiteaObserver.fromFunction` user implementation.
- [x] Streaming byte downloads: backend-zio-only `GiteaDownloads` service
      (`rawFile`/`mediaFile`/`archive` → `ZStream[Any, GiteaError, Byte]`),
      decided as a backend-zio add-on so the core `Backend[Task]` boundary and
      the OkHttp bridge stay intact. Shared request shape lives in the client
      module as `GiteaDownloadRequest` + `GiteaRequests.*Download`.
- [x] Pagination ergonomics: added an empty-page guard in `Pagination.paginated`
      so a missing/misleading `rel="next"`/total-count header can never fetch a
      trailing empty page or loop past the end. Covered by `PaginationSpec`.

### Phase D — CLI-critical write surface *(hand-written, same audit discipline)*
- [ ] Prioritized writes: repo create/edit/delete/fork; branch create/delete;
      releases create/edit/delete + asset upload; file contents
      create/update/delete; labels & milestones CRUD; collaborator add/remove;
      SSH/GPG/token key management.
- [ ] Split `GiteaRequests.scala` by resource group as it grows past ~2k lines.
- [ ] Each endpoint keeps contract-audit + hermetic-stub + opt-in-live tests.

### Phase E — 1.0 cut *(in progress)*
- [x] Bumped `Versions.library` to `1.0.0`; verified `__.publishM2Local`
      produces jar/sources/javadoc/pom for all four modules.
- [x] Formalized the post-1.0 SemVer compatibility policy (README, RELEASE.md,
      CONTRIBUTING.md, CHANGELOG.md), backed by the `api-snapshot/` check.
- [x] Scaladoc on the public entry points (`GiteaClient`, the seven API traits,
      `GiteaObserver`, `GiteaDownloads`, `GiteaError`); `__.docJar` builds clean.
- [ ] Publish to Maven Central via the gated `Publish Central` workflow (needs
      the configured secrets; manual, user-triggered).
- [ ] Optional: a hosted Scaladoc site (GitHub Pages) and multi-host config
      (profiles + token storage) — the latter is really a Phase F / CLI concern,
      since the library already supports per-host clients.

### Phase G — Post-1.0 hardening *(done 2026-08-09)*

A full security, correctness and performance review of the existing surface,
before growing it further. Everything landed without removing or changing the
signature of a published member.

- [x] Credential hygiene: redacted `toString` on `Auth`, `GiteaConfig` and
      `GiteaDownloadRequest`; trimmed credentials read from the environment;
      userinfo stripped from the base URL; HOCON parse errors no longer echo
      source text.
- [x] Denial-of-service bounds: capped server-supplied retry delays, a 32 MiB
      ceiling on buffered JSON bodies, 8 KiB truncation of bodies retained on a
      `GiteaError`, and a total per-attempt time budget (`readTimeout` alone
      does not bound a body, only the wait for headers).
- [x] Fixed the pagination bug that silently dropped data whenever the server
      clamped `limit` below the requested page size, and made streams honour
      `params.page` as their start page.
- [x] `Retry-After` support, and retries on by default (`maxRetries = 3`,
      GET/HEAD only).
- [x] `repos.checkCollaborator` / `pulls.isMerged` fail closed on an unexpected
      2xx instead of answering "yes".
- [x] Observability: HTTP status and attempt count on `RequestEvent`, events for
      defects, and a latency histogram that spans the configured timeout.
- [x] Compiler hardening (`-Werror -Wunused:all -Wvalue-discard
      -Wnonunit-statement -Xcheck-macros -release 21`) and least-privilege CI.
- [x] The contract audit now covers all 130 endpoints instead of 57, and fails
      on an unregistered one.

Deliberately not done, with reasons recorded in the review:

- Migrating from zio-json to jsoniter-scala. The ~170 published
  `given_JsonCodec_X` methods are frozen API and jsoniter's `JsonValueCodec` is
  a different typeclass, so it is a replacement rather than a substitution. The
  measured win on a 50-item page is 1–3 ms of CPU against a 20–200 ms round
  trip. Revisit additively for the four hottest models if a benchmark on real
  captured payloads ever justifies it.
- Rewriting the ~20 optional-query-parameter helpers to avoid
  `List[Option[...]].flatten`. It is a handful of objects once per HTTP round
  trip, and the current shape is the clearest way to express the intent.
- Making `GiteaError` extend `Throwable`, and adding an `UnexpectedStatus`
  case. Both belong to 2.0: the first forces every case carrying a `message`
  parameter to change, the second shifts `ordinal` on a public sealed trait and
  breaks exhaustive matches compiled against 1.0.0.
- Cancelling in-flight OkHttp calls on fiber interruption. sttp's
  `FutureMonad.async` discards the canceller, so this needs an application
  interceptor; documented as a known limitation instead, with `ZioGiteaBackend`
  recommended where cancellation matters.

### Phase F — `cli` module
- [ ] New `cli` module → `backend-zio`; command framework; auth-profile storage;
      table/JSON output; mirror `gh` UX. Not a published library artifact.

---

## Working Agreements

- Validate every change with `./mill __.compile __.test compatibility.check`;
  run `./mill compatibility.writeSnapshot` only for intentional public-API
  changes.
- Keep the spec contract-audit (`plugin-redoc-2.yaml`) authoritative for any
  new or changed endpoint.
- Pre-1.0, breaking changes are allowed but should be deliberate and recorded in
  `CHANGELOG.md`.
