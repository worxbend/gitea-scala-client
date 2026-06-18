package io.worxbend.gitea4s.http

enum PullRequestFileWhitespace(val queryValue: String):
  case IgnoreAll extends PullRequestFileWhitespace("ignore-all")
  case IgnoreChange extends PullRequestFileWhitespace("ignore-change")
  case IgnoreEol extends PullRequestFileWhitespace("ignore-eol")
  case ShowAll extends PullRequestFileWhitespace("show-all")

final case class PullRequestFilesParams(
    skipTo: Option[String] = None,
    whitespace: Option[PullRequestFileWhitespace] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object PullRequestFilesParams:
  val default: PullRequestFilesParams = PullRequestFilesParams()
