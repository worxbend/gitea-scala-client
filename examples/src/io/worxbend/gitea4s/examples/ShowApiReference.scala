package io.worxbend.gitea4s.examples

object ShowApiReference extends ExampleApp:
  def run =
    ExampleSupport.runExample("Reading the current user") { client =>
      client.users.me.map(user => Seq(s"Authenticated as ${ExampleSupport.userName(user)}"))
    }
