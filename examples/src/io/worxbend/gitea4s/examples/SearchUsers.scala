package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.http.UserSearchParams
import zio.{ZIO, ZIOAppDefault}

object SearchUsers extends ZIOAppDefault:
  private val queryEnv = "GITEA_USER_QUERY"

  def run =
    ExampleSupport.runExample("Searching users", queryHint) { client =>
      ExampleSupport.optionalEnv(queryEnv) match
        case None => ZIO.succeed(Seq(queryHint))
        case Some(query) =>
          client.users
            .search(UserSearchParams(q = Some(query), limit = Some(25)))
            .take(25)
            .runCollect
            .map { users =>
              s"Users matching '$query': ${users.size}" +:
                users.map(user => s"- ${ExampleSupport.userName(user)}")
            }
    }

  private def queryHint: String =
    s"Set $queryEnv to search users."
