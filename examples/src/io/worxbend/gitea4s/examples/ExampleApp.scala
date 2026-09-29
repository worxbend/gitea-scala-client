package io.worxbend.gitea4s.examples

import zio.{Runtime, ZIOAppDefault, ZLayer}

/** Examples report failures through `ExampleSupport` and keep a non-zero exit code.
  * Disable the default loggers to avoid reporting the same failure twice.
  */
private[examples] trait ExampleApp extends ZIOAppDefault:
  override val bootstrap: ZLayer[Any, Nothing, Unit] = Runtime.removeDefaultLoggers
