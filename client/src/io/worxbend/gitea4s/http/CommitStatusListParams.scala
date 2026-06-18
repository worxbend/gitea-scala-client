package io.worxbend.gitea4s.http

enum CommitStatusSort(val queryValue: String):
  case Oldest extends CommitStatusSort("oldest")
  case RecentUpdate extends CommitStatusSort("recentupdate")
  case LeastUpdate extends CommitStatusSort("leastupdate")
  case LeastIndex extends CommitStatusSort("leastindex")
  case HighestIndex extends CommitStatusSort("highestindex")

enum CommitStatusListState(val queryValue: String):
  case Pending extends CommitStatusListState("pending")
  case Success extends CommitStatusListState("success")
  case Error extends CommitStatusListState("error")
  case Failure extends CommitStatusListState("failure")
  case Warning extends CommitStatusListState("warning")

final case class CommitStatusListParams(
    sort: Option[CommitStatusSort] = None,
    state: Option[CommitStatusListState] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object CommitStatusListParams:
  val default: CommitStatusListParams = CommitStatusListParams()
