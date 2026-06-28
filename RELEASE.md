# Release Process

This project publishes to Maven Central through Mill's Sonatype Central Portal
support. These notes define local release readiness checks and the manual
Central publishing path. The project is at `1.0.0`.

## Preconditions

- Use Java 21.
- Keep `./mill` as the build entrypoint.
- Keep the supported Scala version aligned across `build.mill`, CI, README
  coordinates, and this document.
- Keep the Mill version aligned between the `build.mill` `//| mill-version`
  directive and the checked-in `mill` launcher's `DEFAULT_MILL_VERSION`.
- Do not require live Gitea credentials for default validation. Live integration
  tests must remain opt-in through `GITEA_URL` and `GITEA_TOKEN`.
- Configure the GitHub Actions `maven-central` environment before publishing.
- Use a non-`-SNAPSHOT` `Versions.library` value for Maven Central releases.

## Local Validation

Run these commands before a release checkpoint:

```bash
./mill __.compile
./mill __.test
./mill it.test
./mill examples.run
./mill compatibility.check
./mill __.docJar __.sourceJar __.publishArtifacts
./mill __.publishM2Local
```

`it.test` is expected to report live tests as ignored when `GITEA_URL` and
`GITEA_TOKEN` are absent.

## Version Checklist

1. Update `Versions.library` in `build.mill`.
2. Update README dependency coordinates.
3. Move the relevant `CHANGELOG.md` entries from `Unreleased` to the release
   version and add the release date.
4. Run `./mill compatibility.check`. For intentional API changes, update the
   checked-in baseline with `./mill compatibility.writeSnapshot`.
5. Regenerate local source and javadoc jars with Mill.
6. Publish to the local Maven repository with `./mill __.publishM2Local`.
7. Verify a sample application can resolve the local coordinates.

## Dependency Updates

Renovate tracks the central dependency pins in `build.mill` and the checked-in
Mill launcher fallback through explicit regex managers. Dependency PRs should
keep related version values together:

- Mill updates must change both `//| mill-version` and `DEFAULT_MILL_VERSION`.
- Scala updates must keep CI's matrix value aligned with `Versions.scala`.
- Library version updates must pass compile, tests, examples, and publishable
  artifact generation before merging.
- Intentional public API changes must include refreshed `api-snapshot/`
  baselines from `./mill compatibility.writeSnapshot`.

Use the Local Validation commands for Renovate PRs unless the PR only changes
documentation.

## Maven Central Publishing

`core`, `client`, `backend-zio`, and `backend-okhttp` extend Mill's
`SonatypeCentralPublishModule`. The manual GitHub Actions workflow
`.github/workflows/publish-central.yml` validates the build and publishes all
library artifacts with:

```bash
./mill mill.javalib.SonatypeCentralPublishModule/publishAll
```

Required GitHub environment secrets:

```text
MILL_SONATYPE_USERNAME
MILL_SONATYPE_PASSWORD
MILL_PGP_SECRET_BASE64
MILL_PGP_PASSPHRASE
```

The Sonatype values are Central Portal user-token credentials. The PGP secret is
the base64-encoded armored secret key accepted by Mill, and the public key must
be available to Sonatype's verification infrastructure before publishing.

The workflow refuses `-SNAPSHOT` versions. Its `release` dispatch input controls
whether Mill automatically publishes the validated Central deployment. Leave it
`false` when staging a deployment for manual review.

Before the first Central release:

1. Verify the `io.worxbend` namespace in Sonatype Central Portal.
2. Generate a Central Portal user token and store it as the Sonatype secrets.
3. Generate or select the release PGP key.
4. Publish the public PGP key.
5. Store the base64 secret key and passphrase as the PGP secrets.
6. Move `Versions.library` and README coordinates to the release version.
7. Run the `Publish Central` workflow from the release tag or commit.

## GitHub Packages, JitPack, and Release Jars

Maven Central is the canonical channel. Three additional channels distribute the
same artifacts for convenience and are documented for consumers in `README.md`.

The `.github/workflows/release.yml` workflow runs on every `v*` tag push (and can
be dispatched manually against an existing tag). It reuses the same validation as
Maven Central, then:

1. Builds all publishable modules with
   `./mill __.publishM2Local --m-2-repo-path "$GITHUB_WORKSPACE/staging-m2"`.
2. Generates `.sha1`/`.md5` checksums and uploads the full Maven layout to
   **GitHub Packages** (`https://maven.pkg.github.com/<owner>/<repo>`) via HTTP
   `PUT`. Re-runs are idempotent: an existing version returns `409` and is
   skipped.
3. Attaches every `*.jar` (main, sources, javadoc) to the **GitHub Release** for
   the tag, creating the release with auto-generated notes if it does not exist.

The workflow needs no extra secrets: it authenticates with the built-in
`GITHUB_TOKEN` and the `contents: write` / `packages: write` permissions declared
in the workflow. It refuses `-SNAPSHOT` versions like the Central workflow does.
Note that GitHub Packages requires consumers to authenticate even for public
packages.

**JitPack** builds on demand from `jitpack.yml`, which installs Temurin 21 via
SDKMAN (the default JitPack image is Java 8) and runs `./mill __.publishM2Local`.
No workflow or secret is involved; the first request for a tag triggers the
build. Bump the pinned Temurin version in `jitpack.yml` if SDKMAN delists it.

## Compatibility Policy

From `1.0.0` the published modules follow [Semantic Versioning](https://semver.org):

- **Major** — breaking changes to the public API of `core`, `client`,
  `backend-zio`, or `backend-okhttp`.
- **Minor** — backward-compatible additions, including new typed endpoints and
  models as the client fills out more of the Gitea API `1.26.2` contract.
  `1.0.0` is an API-stability commitment, not a coverage commitment.
- **Patch** — bug fixes, documentation, and build/publishing fixes.

Every release must pass `./mill compatibility.check`. A change that alters the
`api-snapshot/` baseline is by definition a public-API change: it is allowed only
in a minor release (additive) or a major release (breaking), and the baseline is
refreshed with `./mill compatibility.writeSnapshot`.

`./mill compatibility.check` compares the current published module JVM public
signatures against the checked-in `api-snapshot/` baseline. The baseline covers
`core`, `client`, `backend-zio`, and `backend-okhttp`, excluding implementation
classes under `internal` and generated anonymous codec classes.
