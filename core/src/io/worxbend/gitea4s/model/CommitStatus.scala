package io.worxbend.gitea4s.model

import java.time.Instant

import zio.json.*

enum CommitStatusState(val jsonValue: String):
  case Pending extends CommitStatusState("pending")
  case Success extends CommitStatusState("success")
  case Error extends CommitStatusState("error")
  case Failure extends CommitStatusState("failure")
  case Warning extends CommitStatusState("warning")
  case Skipped extends CommitStatusState("skipped")

object CommitStatusState:
  private val json = JsonValueLookup(CommitStatusState.values, "commit status state", _.jsonValue)

  def fromString(value: String): Either[String, CommitStatusState] = json.fromString(value)

  given JsonCodec[CommitStatusState] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[CommitStatusState]] = json.lenientOptionDecoder

final case class CommitStatus(
    context: Option[String] = None,
    @jsonField("created_at") createdAt: Option[Instant] = None,
    creator: Option[User] = None,
    description: Option[String] = None,
    id: Option[Long] = None,
    @jsonField("status") state: Option[CommitStatusState] = None,
    @jsonField("target_url") targetUrl: Option[String] = None,
    @jsonField("updated_at") updatedAt: Option[Instant] = None,
    url: Option[String] = None
)

object CommitStatus:
  given JsonCodec[CommitStatus] = DeriveJsonCodec.gen[CommitStatus]

final case class CombinedStatus(
    @jsonField("commit_url") commitUrl: Option[String] = None,
    repository: Option[Repository] = None,
    sha: Option[String] = None,
    state: Option[CommitStatusState] = None,
    statuses: Option[List[CommitStatus]] = None,
    @jsonField("total_count") totalCount: Option[Long] = None,
    url: Option[String] = None
)

object CombinedStatus:
  given JsonCodec[CombinedStatus] = DeriveJsonCodec.gen[CombinedStatus]

final case class CreateStatusOption(
    context: Option[String] = None,
    description: Option[String] = None,
    state: Option[CommitStatusState] = None,
    @jsonField("target_url") targetUrl: Option[String] = None
)

object CreateStatusOption:
  given JsonCodec[CreateStatusOption] = DeriveJsonCodec.gen[CreateStatusOption]
