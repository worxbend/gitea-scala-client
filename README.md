# gitea4s

Scala 3 client library for the Gitea API, built with Mill.

Current checkpoint:

- Build tool: Mill `1.1.6` through the checked-in `./mill` launcher
- Package root: `io.worxbend.gitea4s`
- JVM target: Java 21
- API reference: local `plugin-redoc-2.yaml` for Gitea API `1.26.2`

Useful commands:

```bash
./mill __.compile
./mill __.test
./mill examples.run
```

The implementation is in the early rewrite skeleton stage. Core models and codecs are next.
