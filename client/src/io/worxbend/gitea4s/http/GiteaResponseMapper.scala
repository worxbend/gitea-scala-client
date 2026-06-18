package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{GiteaErrorPayload, Page, TopicNames, User}
import sttp.client4.Response
import zio.Chunk
import zio.json.*

import java.time.Instant
import scala.util.Try

object GiteaResponseMapper:
  def decodeJson[A: JsonDecoder](response: Response[String]): Either[GiteaError, A] =
    if response.isSuccess then
      response.body.fromJson[A].left.map(message => GiteaError.DecodeError(message, response.body))
    else Left(toError(response))

  def decodeUnit(response: Response[String]): Either[GiteaError, Unit] =
    if response.isSuccess then Right(()) else Left(toError(response))

  def decodeString(response: Response[String]): Either[GiteaError, String] =
    if response.isSuccess then Right(response.body) else Left(toError(response))

  def decodeChunk[A: JsonDecoder](response: Response[String]): Either[GiteaError, Chunk[A]] =
    decodeJson[List[A]](response).map(Chunk.fromIterable)

  def decodePage[A: JsonDecoder](
      response: Response[String],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, Page[A]] =
    decodeJson[List[A]](response).map { values =>
      val totalCount = longHeader(response, "x-total-count")
      Page(
        data = Chunk.fromIterable(values),
        totalCount = totalCount,
        page = page,
        pageSize = pageSize,
        hasNext = hasNextPage(response, page, pageSize, totalCount)
      )
    }

  def decodeTopicNamesPage(
      response: Response[String],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, Page[String]] =
    decodeJson[TopicNames](response).map { value =>
      val totalCount = longHeader(response, "x-total-count")
      Page(
        data = Chunk.fromIterable(value.topics.getOrElse(Nil)),
        totalCount = totalCount,
        page = page,
        pageSize = pageSize,
        hasNext = hasNextPage(response, page, pageSize, totalCount)
      )
    }

  def decodeUserSearchPage(
      response: Response[String],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, Page[User]] =
    decodeJson[UserSearchResults](response).map { value =>
      val totalCount = longHeader(response, "x-total-count")
      Page(
        data = Chunk.fromIterable(value.data.getOrElse(Nil)),
        totalCount = totalCount,
        page = page,
        pageSize = pageSize,
        hasNext = hasNextPage(response, page, pageSize, totalCount)
      )
    }

  def toError(response: Response[String]): GiteaError =
    val body = response.body
    val message = errorMessage(response)

    response.code.code match
      case 400 => GiteaError.BadRequest(message, body)
      case 401 => GiteaError.Unauthorized(message, body)
      case 403 => GiteaError.Forbidden(message, body)
      case 404 => GiteaError.NotFound(message, body)
      case 409 => GiteaError.Conflict(message, body)
      case 422 => GiteaError.UnprocessableEntity(message, body)
      case 429 => GiteaError.RateLimited(rateLimitReset(response), body)
      case status if status >= 500 => GiteaError.ServerError(status, body)
      case status => GiteaError.ServerError(status, body)

  private def errorMessage(response: Response[String]): String =
    response.body.fromJson[GiteaErrorPayload].toOption.flatMap(_.message)
      .orElse(response.header("message"))
      .getOrElse(response.statusText)

  private def hasNextPage(
      response: Response[String],
      page: Int,
      pageSize: Int,
      totalCount: Option[Long]
  ): Boolean =
    response.header("link").exists(_.contains("""rel="next"""")) ||
      totalCount.exists(total => page.toLong * pageSize.toLong < total)

  private def rateLimitReset(response: Response[String]): Option[Instant] =
    longHeader(response, "x-ratelimit-reset")
      .orElse(longHeader(response, "x-rate-limit-reset"))
      .flatMap(epochSeconds => Try(Instant.ofEpochSecond(epochSeconds)).toOption)

  private def longHeader(response: Response[String], name: String): Option[Long] =
    response.header(name).flatMap(value => Try(value.toLong).toOption)

  private final case class UserSearchResults(
      data: Option[List[User]] = None,
      ok: Option[Boolean] = None
  )

  private object UserSearchResults:
    given JsonDecoder[UserSearchResults] = DeriveJsonDecoder.gen[UserSearchResults]
