package io.worxbend.gitea4s.http

enum PullRequestUpdateStyle(val queryValue: String):
  case Merge extends PullRequestUpdateStyle("merge")
  case Rebase extends PullRequestUpdateStyle("rebase")
