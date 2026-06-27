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

### Phase C — Production hardening *(in progress)*
- [x] Observability seam: `GiteaObserver` hook around `GiteaRequestExecutor`
      (endpoint + duration + outcome), with `noop`/`logging`/`metrics` built-ins,
      `++` composition, and defect-safety. Threaded via `GiteaConfig.observer`.
      OpenTelemetry is a `GiteaObserver.fromFunction` user implementation.
- [ ] Streaming byte downloads: `ZStream[Any, GiteaError, Byte]` variants for
      archive/raw/media. *(Needs a design decision: streaming responses require a
      `StreamBackend[Task, ZioStreams]`, but the client boundary is
      `Backend[Task]` — either capability-type the backend or make streaming a
      backend-zio-only addition. Surfaced for review before implementing.)*
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

### Phase E — 1.0 cut
- [ ] Freeze the public API; publish a Scaladoc site; formalize binary-compat
      guarantees.
- [ ] Design multi-host config (profiles + token storage) so the CLI inherits it.

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
