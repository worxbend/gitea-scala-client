# Release Process

This project has Maven Central publishing groundwork through Mill's Sonatype
Central Portal support. These notes define local release readiness checks and
the manual Central publishing path for the pre-`1.0.0` line.

## Preconditions

- Use Java 21.
- Keep `./mill` as the build entrypoint.
- Keep the supported Scala version aligned across `build.mill`, CI, README
  coordinates, and this document.
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
4. Regenerate local source and javadoc jars with Mill.
5. Publish to the local Maven repository with `./mill __.publishM2Local`.
6. Verify a sample application can resolve the local coordinates.

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

## Compatibility Policy

Before `1.0.0`, minor versions may add, rename, or reshape typed endpoints and
models as the client converges on the Gitea API `1.26.2` contract. Patch
versions should be limited to bug fixes, documentation updates, and build or
publishing fixes.
