# gitea4s

Scala 3 client library for the Gitea API, built with Mill.

Current checkpoint:

- Build tool: Mill `1.1.6` through the checked-in `./mill` launcher
- Package root: `io.worxbend.gitea4s`
- JVM target: Java 21
- API reference: local `plugin-redoc-2.yaml` for Gitea API `1.26.2`
- Implemented surface: typed core models/codecs plus read-only users, organizations,
  repositories, issues, releases, pull requests, and notifications through a ZIO client API
- Live backend: `backend-zio` exposes `ZioGiteaBackend.live`, token/basic/anonymous
  convenience layers, and custom `java.net.http.HttpClient` support

Useful commands:

```bash
./mill __.compile
./mill __.test
./mill examples.run
```

`examples.run` is hermetic by default. If `GITEA_URL` and `GITEA_TOKEN` are present, it
also builds the live ZIO backend and calls `GET /user`.

The rewrite is still in progress. Implemented APIs are covered by hermetic stub-backed tests;
config sources, retry, integration tests, optional OkHttp wiring, and publishing polish remain
planned work.
