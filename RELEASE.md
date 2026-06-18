# Release Process

This project is not publishing to Maven Central yet. These notes define the
local release readiness checks for the pre-`1.0.0` snapshot line.

## Preconditions

- Use Java 21.
- Keep `./mill` as the build entrypoint.
- Keep the supported Scala version aligned across `build.mill`, CI, README
  coordinates, and this document.
- Do not require live Gitea credentials for default validation. Live integration
  tests must remain opt-in through `GITEA_URL` and `GITEA_TOKEN`.

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

## Compatibility Policy

Before `1.0.0`, minor versions may add, rename, or reshape typed endpoints and
models as the client converges on the Gitea API `1.26.2` contract. Patch
versions should be limited to bug fixes, documentation updates, and build or
publishing fixes.
