package io.worxbend.gitea4s.examples

import zio.ZIO

object ListReleases extends ExampleApp:
  private val ownerEnv = "GITEA_OWNER"
  private val repoEnv = "GITEA_REPO"

  def run =
    ExampleSupport.runExample("Listing releases", repositoryHint) { client =>
      repositoryFromEnv match
        case None => ZIO.succeed(Seq(repositoryHint))
        case Some((owner, repo)) =>
          client.releases
            .list(owner, repo)
            .take(25)
            .runCollect
            .map { releases =>
              s"Releases for $owner/$repo: ${releases.size}" +:
                releases.map(release => s"- ${ExampleSupport.releaseSummary(release)}")
            }
    }

  private def repositoryFromEnv: Option[(String, String)] =
    for
      owner <- ExampleSupport.optionalEnv(ownerEnv)
      repo <- ExampleSupport.optionalEnv(repoEnv)
    yield (owner, repo)

  private def repositoryHint: String =
    s"Set $ownerEnv and $repoEnv to list repository releases."
