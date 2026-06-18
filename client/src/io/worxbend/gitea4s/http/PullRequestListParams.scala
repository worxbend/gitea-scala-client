package io.worxbend.gitea4s.http

import zio.Chunk

enum PullRequestListState(val queryValue: String):
  case Open extends PullRequestListState("open")
  case Closed extends PullRequestListState("closed")
  case All extends PullRequestListState("all")

enum PullRequestSort(val queryValue: String):
  case Oldest extends PullRequestSort("oldest")
  case RecentUpdate extends PullRequestSort("recentupdate")
  case RecentClose extends PullRequestSort("recentclose")
  case LeastUpdate extends PullRequestSort("leastupdate")
  case MostComment extends PullRequestSort("mostcomment")
  case LeastComment extends PullRequestSort("leastcomment")
  case Priority extends PullRequestSort("priority")

final case class PullRequestListParams(
    baseBranch: Option[String] = None,
    state: Option[PullRequestListState] = None,
    sort: Option[PullRequestSort] = None,
    milestone: Option[Long] = None,
    labels: Chunk[Long] = Chunk.empty,
    poster: Option[String] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object PullRequestListParams:
  val default: PullRequestListParams = PullRequestListParams()
