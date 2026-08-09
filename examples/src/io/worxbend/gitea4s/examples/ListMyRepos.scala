package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.RepoListParams
import zio.{ZIO, ZIOAppDefault}

object ListMyRepos extends ZIOAppDefault:
  def run =
    ExampleSupport.runExample("Listing repositories") { client =>
      for
        user <- client.users.me
        login <- ZIO
          .fromOption(user.login)
          .orElseFail(GiteaError.DecodeError("GET /user response did not include login", ""))
        repositories <- client.repos
          .list(login, RepoListParams(limit = Some(25)))
          .take(25)
          .runCollect
      yield s"Repositories for $login: ${repositories.size}" +:
        repositories.map(repository => s"- ${ExampleSupport.repositoryName(repository)}")
    }
