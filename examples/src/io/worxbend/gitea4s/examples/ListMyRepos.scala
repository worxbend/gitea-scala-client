package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.RepoListParams
import zio.{Console, ZIO, ZIOAppDefault}

object ListMyRepos extends ZIOAppDefault:
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
        val program =
          ZIO.serviceWithZIO[GiteaClient] { client =>
            for
              user <- client.me
              login <- ZIO
                .fromOption(user.login)
                .orElseFail(GiteaError.DecodeError("GET /user response did not include login", ""))
              repositories <- client
                .list(login, RepoListParams(limit = Some(25)))
                .take(25)
                .runCollect
            yield (login, repositories)
          }

        Console.printLine(ExampleSupport.referenceLine) *>
          program
            .provideLayer(ZioGiteaBackend.configured(config))
            .foldZIO(
              error => Console.printLineError(s"Listing repositories failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
              (login, repositories) =>
                Console.printLine(s"Repositories for $login: ${repositories.size}") *>
                  ZIO.foreachDiscard(repositories)(repository =>
                    Console.printLine(s"- ${ExampleSupport.repositoryName(repository)}")
                  )
            )
