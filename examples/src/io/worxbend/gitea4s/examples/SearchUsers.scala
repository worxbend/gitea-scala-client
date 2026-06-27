package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.UserSearchParams
import zio.{Console, ZIO, ZIOAppDefault}

object SearchUsers extends ZIOAppDefault:
  private val queryEnv = "GITEA_USER_QUERY"

  def run =
    ExampleSupport.liveConfigFromEnv match
      case Right(None) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLine(ExampleSupport.credentialsHint) *>
          Console.printLine(queryHint)
      case Left(error) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)
      case Right(Some(config)) =>
        sys.env.get(queryEnv).map(_.trim).filter(_.nonEmpty) match
          case None =>
            Console.printLine(ExampleSupport.referenceLine) *>
              Console.printLine(queryHint)
          case Some(query) =>
            val params = UserSearchParams(q = Some(query), limit = Some(25))
            val program =
              ZIO.serviceWithZIO[GiteaClient] { client =>
                client.users.search(params).take(25).runCollect
              }

            Console.printLine(ExampleSupport.referenceLine) *>
              program
                .provideLayer(ZioGiteaBackend.configured(config))
                .foldZIO(
                  error => Console.printLineError(s"Searching users failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
                  users =>
                    Console.printLine(s"Users matching '$query': ${users.size}") *>
                      ZIO.foreachDiscard(users)(user =>
                        Console.printLine(s"- ${ExampleSupport.userName(user)}")
                      )
                )

  private def queryHint: String =
    s"Set $queryEnv to search users."
