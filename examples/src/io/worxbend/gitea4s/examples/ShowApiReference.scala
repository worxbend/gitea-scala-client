package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.model.ApiReference

object ShowApiReference:
  def main(args: Array[String]): Unit =
    println(s"gitea4s targets Gitea API ${ApiReference.gitea1262.version}")
