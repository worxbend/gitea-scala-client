package io.worxbend.gitea4s.model

import zio.json.*

enum IssueState(val jsonValue: String):
  case Open extends IssueState("open")
  case Closed extends IssueState("closed")

object IssueState:
  private val json = JsonValueLookup(IssueState.values, "issue state", _.jsonValue)

  def fromString(value: String): Either[String, IssueState] = json.fromString(value)

  given JsonCodec[IssueState] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[IssueState]] = json.lenientOptionDecoder

enum ObjectFormatName(val jsonValue: String):
  case Sha1 extends ObjectFormatName("sha1")
  case Sha256 extends ObjectFormatName("sha256")

object ObjectFormatName:
  private val json = JsonValueLookup(ObjectFormatName.values, "object format name", _.jsonValue)

  def fromString(value: String): Either[String, ObjectFormatName] = json.fromString(value)

  given JsonCodec[ObjectFormatName] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[ObjectFormatName]] = json.lenientOptionDecoder

enum TeamPermission(val jsonValue: String):
  case None extends TeamPermission("none")
  case Read extends TeamPermission("read")
  case Write extends TeamPermission("write")
  case Admin extends TeamPermission("admin")
  case Owner extends TeamPermission("owner")

object TeamPermission:
  private val json = JsonValueLookup(TeamPermission.values, "team permission", _.jsonValue)

  def fromString(value: String): Either[String, TeamPermission] = json.fromString(value)

  given JsonCodec[TeamPermission] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[TeamPermission]] = json.lenientOptionDecoder

enum NotificationSubjectState(val jsonValue: String):
  case Open extends NotificationSubjectState("open")
  case Closed extends NotificationSubjectState("closed")
  case Merged extends NotificationSubjectState("merged")

object NotificationSubjectState:
  private val json = JsonValueLookup(NotificationSubjectState.values, "notification subject state", _.jsonValue)

  def fromString(value: String): Either[String, NotificationSubjectState] = json.fromString(value)

  given JsonCodec[NotificationSubjectState] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[NotificationSubjectState]] = json.lenientOptionDecoder

enum NotificationSubjectType(val jsonValue: String, val queryValue: String):
  case Issue extends NotificationSubjectType("Issue", "issue")
  case Pull extends NotificationSubjectType("Pull", "pull")
  case Commit extends NotificationSubjectType("Commit", "commit")
  case Repository extends NotificationSubjectType("Repository", "repository")

object NotificationSubjectType:
  private val json = JsonValueLookup(NotificationSubjectType.values, "notification subject type", _.jsonValue)

  def fromString(value: String): Either[String, NotificationSubjectType] = json.fromString(value)

  given JsonCodec[NotificationSubjectType] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[NotificationSubjectType]] = json.lenientOptionDecoder

enum PullReviewState(val jsonValue: String):
  case Approved extends PullReviewState("APPROVED")
  case Pending extends PullReviewState("PENDING")
  case Comment extends PullReviewState("COMMENT")
  case RequestChanges extends PullReviewState("REQUEST_CHANGES")
  case RequestReview extends PullReviewState("REQUEST_REVIEW")

object PullReviewState:
  private val json = JsonValueLookup(PullReviewState.values, "pull review state", _.jsonValue)

  def fromString(value: String): Either[String, PullReviewState] = json.fromString(value)

  given JsonCodec[PullReviewState] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[PullReviewState]] = json.lenientOptionDecoder

enum MergePullRequestMethod(val jsonValue: String):
  case Merge extends MergePullRequestMethod("merge")
  case Rebase extends MergePullRequestMethod("rebase")
  case RebaseMerge extends MergePullRequestMethod("rebase-merge")
  case Squash extends MergePullRequestMethod("squash")
  case FastForwardOnly extends MergePullRequestMethod("fast-forward-only")
  case ManuallyMerged extends MergePullRequestMethod("manually-merged")

object MergePullRequestMethod:
  private val json = JsonValueLookup(MergePullRequestMethod.values, "merge pull request method", _.jsonValue)

  def fromString(value: String): Either[String, MergePullRequestMethod] = json.fromString(value)

  given JsonCodec[MergePullRequestMethod] = json.codec

  /** Unknown values read as `None`; see [[JsonValueLookup.lenientOptionDecoder]]. */
  given JsonDecoder[Option[MergePullRequestMethod]] = json.lenientOptionDecoder
