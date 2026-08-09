package io.worxbend.gitea4s.examples

import zio.ZIO

object ListBranchesAndTags extends ExampleApp:
  private val ownerEnv = "GITEA_OWNER"
  private val repoEnv = "GITEA_REPO"

  def run =
    ExampleSupport.runExample("Listing branches and tags", repositoryHint) { client =>
      repositoryFromEnv match
        case None => ZIO.succeed(Seq(repositoryHint))
        case Some((owner, repo)) =>
          for
            branches <- client.repos.branches(owner, repo).take(25).runCollect
            tags <- client.repos.tags(owner, repo).take(25).runCollect
          yield (s"Branches for $owner/$repo: ${branches.size}" +:
            branches.map(branch => s"- ${ExampleSupport.branchSummary(branch)}")) ++
            (s"Tags for $owner/$repo: ${tags.size}" +:
              tags.map(tag => s"- ${ExampleSupport.tagSummary(tag)}"))
    }

  private def repositoryFromEnv: Option[(String, String)] =
    for
      owner <- ExampleSupport.optionalEnv(ownerEnv)
      repo <- ExampleSupport.optionalEnv(repoEnv)
    yield (owner, repo)

  private def repositoryHint: String =
    s"Set $ownerEnv and $repoEnv to list repository branches and tags."
