package io.worxbend.gitea4s.model

import zio.json.*

enum IssueState(val jsonValue: String):
  case Open extends IssueState("open")
  case Closed extends IssueState("closed")

object IssueState:
  private val byJsonValue = IssueState.values.map(state => state.jsonValue -> state).toMap

  def fromString(value: String): Either[String, IssueState] =
    byJsonValue.get(value).toRight(s"Unknown issue state: $value")

  given JsonCodec[IssueState] =
    summon[JsonCodec[String]].transformOrFail(fromString, _.jsonValue)

enum ObjectFormatName(val jsonValue: String):
  case Sha1 extends ObjectFormatName("sha1")
  case Sha256 extends ObjectFormatName("sha256")

object ObjectFormatName:
  private val byJsonValue = ObjectFormatName.values.map(format => format.jsonValue -> format).toMap

  def fromString(value: String): Either[String, ObjectFormatName] =
    byJsonValue.get(value).toRight(s"Unknown object format name: $value")

  given JsonCodec[ObjectFormatName] =
    summon[JsonCodec[String]].transformOrFail(fromString, _.jsonValue)

enum TeamPermission(val jsonValue: String):
  case None extends TeamPermission("none")
  case Read extends TeamPermission("read")
  case Write extends TeamPermission("write")
  case Admin extends TeamPermission("admin")
  case Owner extends TeamPermission("owner")

object TeamPermission:
  private val byJsonValue = TeamPermission.values.map(permission => permission.jsonValue -> permission).toMap

  def fromString(value: String): Either[String, TeamPermission] =
    byJsonValue.get(value).toRight(s"Unknown team permission: $value")

  given JsonCodec[TeamPermission] =
    summon[JsonCodec[String]].transformOrFail(fromString, _.jsonValue)
