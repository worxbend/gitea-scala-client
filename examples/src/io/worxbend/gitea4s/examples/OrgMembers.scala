package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import zio.{Console, ZIO, ZIOAppDefault}

object OrgMembers extends ZIOAppDefault:
  private val orgEnv = "GITEA_ORG"

  def run =
    ExampleSupport.liveConfigFromEnv match
      case Right(None) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLine(ExampleSupport.credentialsHint) *>
          Console.printLine(s"Set $orgEnv to list organization members.")
      case Left(error) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)
      case Right(Some(config)) =>
        sys.env.get(orgEnv).map(_.trim).filter(_.nonEmpty) match
          case None =>
            Console.printLine(ExampleSupport.referenceLine) *>
              Console.printLine(s"Set $orgEnv to list organization members.")
          case Some(org) =>
            val program =
              ZIO.serviceWithZIO[GiteaClient] { client =>
                client.orgs.members(org).take(25).runCollect
              }

            Console.printLine(ExampleSupport.referenceLine) *>
              program
                .provideLayer(ZioGiteaBackend.configured(config))
                .foldZIO(
                  error => Console.printLineError(s"Listing organization members failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
                  members =>
                    Console.printLine(s"Members of $org: ${members.size}") *>
                      ZIO.foreachDiscard(members)(member =>
                        Console.printLine(s"- ${ExampleSupport.userName(member)}")
                      )
                )
