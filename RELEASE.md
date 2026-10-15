# Release Process

This project publishes to Maven Central through Mill's Sonatype Central Portal
support. These notes define local release readiness checks and the tag-triggered
Central publishing path. The project is at `2.0.0`.

## Preconditions

- Use Java 21.
- Keep `./mill` as the build entrypoint.
- Keep the supported Scala version aligned across `build.mill`, CI, README
  coordinates, and this document.
- Keep the Mill version aligned between the `build.mill` `//| mill-version`
  directive and the checked-in `mill` launcher's `DEFAULT_MILL_VERSION`.
- Do not require live Gitea credentials for default validation. Live integration
  tests must remain opt-in through `GITEA_URL` and `GITEA_TOKEN`.
- Configure the four repository secrets listed below before publishing.
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

`core`, `client`, `backend-zio`, and `backend-okhttp` publish together under
`com.worxbend`. Push a matching `v<version>` tag to run both
`.github/workflows/publish-central.yml` and `.github/workflows/release.yml`.
To retry an existing release, choose that tag in the **Run workflow** ref selector.
Branches and snapshot versions are rejected.

Both workflows call the full CI workflow without publishing credentials. The
publishing job checks out the validated SHA and checks the tag, all four module
versions, POM coordinates, and internal dependency coordinates before uploading.
Examples and integration-test modules are excluded from the explicit publish list.

Required GitHub repository Actions secrets:

```text
MILL_SONATYPE_USERNAME
MILL_SONATYPE_PASSWORD
MILL_PGP_SECRET_BASE64
MILL_PGP_PASSPHRASE
```

The Sonatype values are a Central Portal user-token pair. The PGP secret is a
base64-encoded armored private key; its public key must be on a supported
keyserver. Verify that the token can publish to the `com.worxbend` namespace.

The Central workflow signs all four libraries and automatically releases them.
For a manual staging run, select the release tag and set its `release` input to
`false`; finish publishing in Central Portal. Public artifact verification runs
only for automatic releases. The workflow allows up to 20 minutes for Central
validation, then downloads every POM and main/source/javadoc jar and compares
its SHA-256 with the local build.

## GitHub Packages, JitPack, and Release Jars

The `Release` workflow publishes the same four libraries with Mill's
`MavenPublishModule/publishAll`, using the built-in `GITHUB_TOKEN` and
`packages: write`. No additional package credentials are needed. It downloads and
verifies every uploaded POM and jar, then attaches the twelve main/source/javadoc
jars to the GitHub Release, creating that release if necessary.

GitHub Packages requires consumers to authenticate even for public packages.
Publication failures are reported; inspect a partially uploaded version before
retrying, since release versions are immutable.

**JitPack** builds on demand from `jitpack.yml`, which installs Temurin 21 via
SDKMAN (the default JitPack image is Java 8), then compiles, tests, and runs
`./mill __.publishM2Local`. No workflow or secret is involved; the first request
for a tag triggers the build. Bump the pinned Temurin version in `jitpack.yml`
if SDKMAN delists it.

The test step matters here more than it looks: JitPack exists to serve
pre-release commits, so it is the channel most likely to be asked for a broken
one, and whatever it builds is what a consumer resolves. `compatibility.check`
is deliberately *not* run there — it would fail every commit whose
`api-snapshot/` baseline has not been regenerated yet, which is exactly the
traffic this channel is for.

## Compatibility Policy

From `1.0.0` the published modules follow [Semantic Versioning](https://semver.org):

- **Major** — breaking changes to the public API of `core`, `client`,
  `backend-zio`, or `backend-okhttp`.
- **Minor** — backward-compatible additions, including new typed endpoints and
  models as the client fills out more of the Gitea API `1.27.3` contract.
  `1.0.0` is an API-stability commitment, not a coverage commitment.
- **Patch** — bug fixes, documentation, and build/publishing fixes.

Every release must pass `./mill compatibility.check`. A change that alters the
`api-snapshot/` baseline is by definition a public-API change: it is allowed only
in a minor release (additive) or a major release (breaking), and the baseline is
refreshed with `./mill compatibility.writeSnapshot`.

`./mill compatibility.check` compares the current JVM public signatures against
the checked-in `api-snapshot/` baseline for `core`, `client`, `backend-zio`, and
`backend-okhttp`. It detects unreviewed surface changes, not compatibility with
the v1.0.0 release. Version `2.0.0` releases the intentional current-contract
API changes and moves Maven coordinates to `com.worxbend`.
