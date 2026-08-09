package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.http.{PullRequestListParams, PullRequestListState}
import zio.{ZIO, ZIOAppDefault}

object ListPullRequests extends ZIOAppDefault:
  private val ownerEnv = "GITEA_OWNER"
  private val repoEnv = "GITEA_REPO"

  private val params =
    PullRequestListParams(
      state = Some(PullRequestListState.All),
      limit = Some(25)
    )

  def run =
    ExampleSupport.runExample("Listing pull requests", repositoryHint) { client =>
      repositoryFromEnv match
        case None => ZIO.succeed(Seq(repositoryHint))
        case Some((owner, repo)) =>
          client.pulls
            .list(owner, repo, params)
            .take(25)
            .runCollect
            .map { pullRequests =>
              s"Pull requests for $owner/$repo: ${pullRequests.size}" +:
                pullRequests.map(pullRequest => s"- ${ExampleSupport.pullRequestSummary(pullRequest)}")
            }
    }

  private def repositoryFromEnv: Option[(String, String)] =
    for
      owner <- ExampleSupport.optionalEnv(ownerEnv)
      repo <- ExampleSupport.optionalEnv(repoEnv)
    yield (owner, repo)

  private def repositoryHint: String =
    s"Set $ownerEnv and $repoEnv to list repository pull requests."
