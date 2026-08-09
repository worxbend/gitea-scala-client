package io.worxbend.gitea4s.examples

import zio.ZIOAppDefault

object ShowApiReference extends ZIOAppDefault:
  def run =
    ExampleSupport.runExample("GET /user") { client =>
      client.users.me.map(user => Seq(s"Authenticated as ${user.login.getOrElse("<unknown>")}"))
    }
