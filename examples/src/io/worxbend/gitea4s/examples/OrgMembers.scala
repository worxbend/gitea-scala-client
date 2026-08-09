package io.worxbend.gitea4s.examples

import zio.{ZIO, ZIOAppDefault}

object OrgMembers extends ZIOAppDefault:
  private val orgEnv = "GITEA_ORG"

  def run =
    ExampleSupport.runExample("Listing organization members", orgHint) { client =>
      ExampleSupport.optionalEnv(orgEnv) match
        case None => ZIO.succeed(Seq(orgHint))
        case Some(org) =>
          client.orgs
            .members(org)
            .take(25)
            .runCollect
            .map { members =>
              s"Members of $org: ${members.size}" +:
                members.map(member => s"- ${ExampleSupport.userName(member)}")
            }
    }

  private def orgHint: String =
    s"Set $orgEnv to list organization members."
