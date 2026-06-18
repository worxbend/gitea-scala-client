# gitea4s

Scala 3 client library for the Gitea API, built with Mill.

Current checkpoint:

- Build tool: Mill `1.1.6` through the checked-in `./mill` launcher
- Package root: `io.worxbend.gitea4s`
- JVM target: Java 21
- API reference: local `plugin-redoc-2.yaml` for Gitea API `1.26.2`
- Implemented surface: typed core models/codecs plus read-only users, organizations,
  repositories, issues, releases, pull requests, and notifications through a ZIO client API

Useful commands:

```bash
./mill __.compile
./mill __.test
./mill examples.run
```

The rewrite is still in progress. Implemented APIs are covered by hermetic stub-backed tests;
live backend, config, retry, integration tests, and publishing polish remain planned work.
