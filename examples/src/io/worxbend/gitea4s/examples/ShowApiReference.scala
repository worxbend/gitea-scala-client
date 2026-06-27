package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import zio.{Console, ZIO, ZIOAppDefault}

object ShowApiReference extends ZIOAppDefault:
  def run =
    ExampleSupport.liveConfigFromEnv match
      case Right(None) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLine(ExampleSupport.credentialsHint)
      case Left(error) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)
      case Right(Some(config)) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          ZIO.serviceWithZIO[io.worxbend.gitea4s.GiteaClient](_.users.me)
            .provideLayer(ZioGiteaBackend.configured(config))
            .foldZIO(
              error => Console.printLineError(s"GET /user failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
              user => Console.printLine(s"Authenticated as ${user.login.getOrElse("<unknown>")}")
            )
