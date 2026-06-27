package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import zio.{Console, ZIO, ZIOAppDefault}

object ListBranchesAndTags extends ZIOAppDefault:
  private val ownerEnv = "GITEA_OWNER"
  private val repoEnv = "GITEA_REPO"

  def run =
    ExampleSupport.liveConfigFromEnv match
      case Right(None) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLine(ExampleSupport.credentialsHint) *>
          Console.printLine(repositoryHint)
      case Left(error) =>
        Console.printLine(ExampleSupport.referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)
      case Right(Some(config)) =>
        repositoryFromEnv match
          case None =>
            Console.printLine(ExampleSupport.referenceLine) *>
              Console.printLine(repositoryHint)
          case Some((owner, repo)) =>
            val program =
              ZIO.serviceWithZIO[GiteaClient] { client =>
                for
                  branches <- client.repos.branches(owner, repo).take(25).runCollect
                  tags <- client.repos.tags(owner, repo).take(25).runCollect
                yield (branches, tags)
              }

            Console.printLine(ExampleSupport.referenceLine) *>
              program
                .provideLayer(ZioGiteaBackend.configured(config))
                .foldZIO(
                  error => Console.printLineError(s"Listing branches and tags failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
                  (branches, tags) =>
                    Console.printLine(s"Branches for $owner/$repo: ${branches.size}") *>
                      ZIO.foreachDiscard(branches)(branch =>
                        Console.printLine(s"- ${ExampleSupport.branchSummary(branch)}")
                      ) *>
                      Console.printLine(s"Tags for $owner/$repo: ${tags.size}") *>
                      ZIO.foreachDiscard(tags)(tag =>
                        Console.printLine(s"- ${ExampleSupport.tagSummary(tag)}")
                      )
                )

  private def repositoryFromEnv: Option[(String, String)] =
    for
      owner <- nonBlankEnv(ownerEnv)
      repo <- nonBlankEnv(repoEnv)
    yield (owner, repo)

  private def nonBlankEnv(name: String): Option[String] =
    sys.env.get(name).map(_.trim).filter(_.nonEmpty)

  private def repositoryHint: String =
    s"Set $ownerEnv and $repoEnv to list repository branches and tags."
