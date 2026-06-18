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
- Optional backend: `backend-okhttp` exposes `OkHttpGiteaBackend.live`, token/basic/anonymous
  convenience layers, and caller-owned `okhttp3.OkHttpClient` support through sttp's
  async `OkHttpFutureBackend` adapted to ZIO
- Config: `GiteaConfig` supports programmatic constructors plus hermetic environment
  parsing for `GITEA_URL`, `GITEA_TOKEN`, `GITEA_USERNAME`, `GITEA_PASSWORD`,
  `GITEA_PAGE_SIZE`, and `GITEA_TIMEOUT`

Useful commands:

```bash
./mill __.compile
./mill __.test
./mill examples.run
```

`examples.run` is hermetic by default. If `GITEA_URL` is present with either
`GITEA_TOKEN` or both `GITEA_USERNAME` and `GITEA_PASSWORD`, it also builds the live
ZIO backend and calls `GET /user`. Token auth has precedence when both token and
username/password variables are set. Config validation errors mention variable names but
not credential values.

The rewrite is still in progress. Implemented APIs are covered by hermetic stub-backed tests;
retry, integration tests, additional examples, and publishing polish remain planned work.
