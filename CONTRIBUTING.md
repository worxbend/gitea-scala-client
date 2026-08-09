# Contributing to gitea4s

Thanks for your interest in improving `gitea4s`. This document covers how the
project is built, tested, and how new API surface is added. User-facing usage
lives in `README.md`; the roadmap lives in `PLAN.md`.

## Build & Validate

```bash
./mill __.compile            # compile all modules
./mill __.test               # hermetic unit tests
GITEA_IT=1 ./mill it.test    # opt-in live integration tests (see below)
./mill examples.run          # hermetic by default
./mill compatibility.check   # public-API binary-compat guard
./mill __.docJar __.sourceJar __.publishArtifacts
```

The standard pre-PR check is:

```bash
./mill __.compile __.test compatibility.check
```

JVM baseline is Java 21; Scala is pinned in `build.mill` (`Versions.scala`). CI
runs the same flow in `.github/workflows/ci.yml`, and `Jenkinsfile` mirrors it.

### Compiler settings

The build compiles with `-Werror`, so a warning fails the build. Alongside the
usual `-deprecation -feature -unchecked`, the settings that most often catch
something real here are:

- `-Wvalue-discard` and `-Wnonunit-statement`. In a ZIO codebase a value dropped
  in statement position is an effect that is described and then never run. It
  type checks and it compiles, and nothing at runtime tells you. If you
  genuinely mean to discard a result, bind it: `val _ = thing`.
- `-Wunused:all`, which covers imports as well as locals.
- `-release 21`, so the Scala compiler links against exactly the Java 21 API
  rather than whichever JDK you happen to have installed.

### Formatting

There is no enforced formatter. Match the surrounding code: two-space indent,
120-column lines, significant indentation rather than braces. A `.scalafmt.conf`
used to be present but had no `version` or `runner.dialect`, so Scalafmt 3
refused to run against it; completing it would have reformatted a third of the
tree, which is not a change worth making incidentally.

## API Design Conventions

These conventions keep the hand-written client coherent as it grows. They are
the reason the codebase looks the way it does.

- **Spec is authoritative.** `plugin-redoc-2.yaml` (Gitea API `1.26.2`, Swagger
  2.0) is the source of truth for operation IDs, paths, parameters, response
  shapes, and payload fields. Do not implement endpoints from memory.
- **Contract audit.** Every implemented endpoint registers typed metadata in
  `GiteaEndpoint`/`GiteaEndpoints` and is audited against the spec in
  `GiteaEndpointAuditSpec` (method, path, operation ID, required path params,
  optional query params and their enum values, success response refs,
  documented non-2xx labels, request-body presence, and retryability).
  Audit-only expectations (e.g. non-2xx labels) stay in test scope so they do
  not bloat the published API.
- **Read-only retryability.** `GET`/`HEAD` requests are retryable; writes are
  not. Retry covers transport failures, `429` (honoring reset headers), and
  selected `5xx`, with jittered exponential backoff.
- **Response typing.** JSON decodes through `GiteaResponseMapper`; binary
  endpoints (`raw`/`media`/`archive`) return `Chunk[Byte]` via an
  `application/octet-stream` boundary; `text/plain` endpoints return `String`.
  These do not pass through JSON decoding.
- **Path encoding.** Slash-containing path parameters (refs, filepaths, team and
  tag names) are encoded as a single path segment; pair unit-level encoded
  assertions with an opt-in live probe before generalizing routing assumptions.
- **"Not implemented" is deliberate.** Adjacent endpoints are not added by
  inference; each is selected and audited against its exact Swagger contract.
- **Public API stability.** `./mill compatibility.check` compares the JVM public
  signatures of the four published modules against `api-snapshot/`. Run
  `./mill compatibility.writeSnapshot` only for an intentional API change, and
  record it in `CHANGELOG.md`.

## Testing

Unit tests are hermetic and use the sttp `BackendStub`, JSON fixtures, response
mapper tests, and pagination tests:

```bash
./mill __.test
```

### Opt-in live integration tests

Live tests run only when their required environment variables are all non-empty;
otherwise ZIO Test reports them as ignored and makes no network calls.

The whole suite additionally requires `GITEA_IT` to be set. Without it every
test is ignored, including on a machine that happens to have `GITEA_URL` and
`GITEA_TOKEN` exported — which is what keeps `./mill __.test` (where `it.test`
also lives) genuinely hermetic for a maintainer.

Baseline (authenticated user + repo stream):

```bash
GITEA_IT=1 GITEA_URL=https://gitea.example GITEA_TOKEN=... ./mill it.test
```

Basic auth works too: `GITEA_USERNAME` + `GITEA_PASSWORD` in place of
`GITEA_TOKEN`. Both paths are gated equally — previously only the token was
checked, so a username/password run reported every test ignored and a green
`SUCCESS` having made no network calls at all.

Additional probes are each gated on their own variables (all must be non-empty,
in addition to `GITEA_URL`/`GITEA_TOKEN`/`GITEA_OWNER`/`GITEA_REPO`):

| Probe | Required extra variables | Notes |
| --- | --- | --- |
| Slash-containing Git ref routing | `GITEA_REF` (e.g. `heads/main`) | Validates single-segment ref encoding. |
| Annotated tag lookup | `GITEA_ANNOTATED_TAG_SHA` | Must be an annotated tag object SHA, not a tag-list entry. |
| Repository contents filepath | `GITEA_CONTENTS_FILEPATH` (e.g. `docs/readme.md`); optional `GITEA_CONTENTS_REF` | |
| Raw file download | `GITEA_RAW_FILEPATH`; optional `GITEA_RAW_REF` | Exercises the buffered byte-response path. |
| Repository archive | `GITEA_ARCHIVE` (e.g. `main.zip`); optional comma-separated `GITEA_ARCHIVE_PATHS` | |
| Release detail / asset list | `GITEA_RELEASE_ID` | Asset-list also asserts membership when `GITEA_RELEASE_ASSET_ID` is set. |
| Single release asset | `GITEA_RELEASE_ID` + `GITEA_RELEASE_ASSET_ID` | |
| Release by tag | `GITEA_RELEASE_TAG` | A normal tag (`v1.0.0`) is generic confidence only; slash-containing routing needs a slash-containing tag. |
| Latest release | `GITEA_LATEST_RELEASE_TAG` | Must be the repo's actual latest non-draft, non-prerelease tag. |

To prove hermetic skipping (no network), unset the full live-variable set:

```bash
env -u GITEA_IT -u GITEA_URL -u GITEA_TOKEN -u GITEA_USERNAME -u GITEA_PASSWORD \
    -u GITEA_OWNER -u GITEA_REPO -u GITEA_REF -u GITEA_ANNOTATED_TAG_SHA \
    -u GITEA_CONTENTS_FILEPATH -u GITEA_CONTENTS_REF -u GITEA_ARCHIVE \
    -u GITEA_RAW_FILEPATH -u GITEA_RAW_REF \
    -u GITEA_ARCHIVE_PATHS -u GITEA_RELEASE_ID -u GITEA_RELEASE_TAG \
    -u GITEA_LATEST_RELEASE_TAG -u GITEA_RELEASE_ASSET_ID \
    ./mill --no-server it.test
```

A credential-stripped run proves hermetic skipping only; record real routing
confidence only after an enabled probe observes the endpoint behavior.

## Documentation Site

`docs/` is a hand-written static site published by GitHub Pages from `master`
(`Settings -> Pages`, source `master` / `/docs`). There is no generator and no
build step: edit the HTML, push to `master`, and Pages republishes.

Everything on it is drawn from this repository — README, CONTRIBUTING, or the
code — so when you change behaviour that the site describes, update `docs/` in
the same commit. `.nojekyll` is present so Pages serves the files as-is.

## Dependency Updates

Renovate is configured with regex managers for the version pins Mill keeps
outside standard manifests:

- `//| mill-version` in `build.mill`
- `DEFAULT_MILL_VERSION` in the checked-in `mill` launcher
- `Versions.scala`, `Versions.zio`, `Versions.zioJson`, `Versions.typesafeConfig`,
  and `Versions.sttp` in `build.mill`

Dependency PRs should run the full release-readiness validation:

```bash
./mill __.compile __.test examples.run compatibility.check
GITEA_IT=1 ./mill it.test    # only with a live server configured
./mill __.docJar __.sourceJar __.publishArtifacts
```

## Releasing

From `1.0.0` the published modules follow Semantic Versioning: breaking public
API changes require a major version, additive endpoints/models go in minors, and
the `api-snapshot/` check enforces it. Release notes are tracked in
`CHANGELOG.md`; the release checklist, compatibility policy, and Sonatype Central
process are in `RELEASE.md`.
