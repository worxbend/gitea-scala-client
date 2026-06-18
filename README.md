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
  `GITEA_PAGE_SIZE`, `GITEA_TIMEOUT`, and `GITEA_MAX_RETRIES`; it also supports
  Typesafe config under the `gitea4s` path
- Retry: read-only requests honor `GiteaConfig.maxRetries` for transport failures,
  `429` rate limits, and selected `5xx` responses without retrying write requests by default

Useful commands:

```bash
./mill __.compile
./mill __.test
./mill it.test
./mill examples.run
```

`examples.run` is hermetic by default. If `GITEA_URL` is present with either
`GITEA_TOKEN` or both `GITEA_USERNAME` and `GITEA_PASSWORD`, it also builds the live
ZIO backend and calls `GET /user`. Token auth has precedence when both token and
username/password variables are set. Config validation errors mention variable names but
not credential values.

Config source precedence is explicit: programmatic `GiteaConfig` values are preferred,
then environment loading, then Typesafe config. The environment retry knob is
`GITEA_MAX_RETRIES`; the Typesafe config key is `gitea4s.max-retries`. Both accept
zero or a positive integer.

Integration tests are opt-in and live under the `it` module. By default, `./mill __.test`
and `./mill it.test` do not call external services; the live tests are reported as ignored.
Set both variables below to run the current live read-only smoke checks through
`ZioGiteaBackend`:

```bash
GITEA_URL=https://gitea.example \
GITEA_TOKEN=... \
./mill it.test
```

The current integration slice calls `GET /user` and streams the authenticated user's
repositories with a page size of one. Invalid live config or API failures fail the tests
when both integration variables are present.

Example Typesafe config:

```hocon
gitea4s {
  url = "https://gitea.example"
  token = "..."
  page-size = 50
  timeout = 30s
  max-retries = 2
}
```

The rewrite is still in progress. Implemented APIs are covered by hermetic stub-backed tests;
additional examples and publishing polish remain planned work.
