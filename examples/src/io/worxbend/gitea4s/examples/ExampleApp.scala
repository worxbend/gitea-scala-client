package io.worxbend.gitea4s.examples

import zio.{Runtime, ZIOAppDefault, ZLayer}

/** The entry point every example extends.
  *
  * `ExampleSupport.runExample` prints a readable one-line failure and then
  * re-fails, which is what keeps the process exit code non-zero. But
  * `GiteaError` is not a `Throwable`, so `ZIOAppDefault`'s default handler
  * logged the same failure a second time as a multi-line `cause=` block with a
  * stack trace pointing into `ExampleSupport`. Every failure was reported
  * twice, and the second report was the less useful one.
  *
  * Removing the default loggers keeps the friendly line and the exit code and
  * drops the duplicate. It is deliberately not `.as(ExitCode.failure)`: that
  * would silence the failure channel as well, and a non-zero exit is exactly
  * what a CI step running these examples needs.
  */
private[examples] trait ExampleApp extends ZIOAppDefault:
  override val bootstrap: ZLayer[Any, Nothing, Unit] = Runtime.removeDefaultLoggers
