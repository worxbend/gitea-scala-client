package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.GiteaClient
import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.{PullRequestListParams, PullRequestListState}
import zio.{Console, ZIO, ZIOAppDefault}

object ListPullRequests extends ZIOAppDefault:
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
            val params =
              PullRequestListParams(
                state = Some(PullRequestListState.All),
                limit = Some(25)
              )

            val program =
              ZIO.serviceWithZIO[GiteaClient] { client =>
                client.pullRequests(owner, repo, params).take(25).runCollect
              }

            Console.printLine(ExampleSupport.referenceLine) *>
              program
                .provideLayer(ZioGiteaBackend.configured(config))
                .foldZIO(
                  error => Console.printLineError(s"Listing pull requests failed: ${ExampleSupport.describeFailure(error)}") *> ZIO.fail(error),
                  pullRequests =>
                    Console.printLine(s"Pull requests for $owner/$repo: ${pullRequests.size}") *>
                      ZIO.foreachDiscard(pullRequests)(pullRequest =>
                        Console.printLine(s"- ${ExampleSupport.pullRequestSummary(pullRequest)}")
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
    s"Set $ownerEnv and $repoEnv to list repository pull requests."
