package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{GiteaErrorPayload, Page, TopicNames, User}
import sttp.client4.Response
import sttp.model.StatusCode
import zio.Chunk
import zio.json.*

import java.nio.charset.StandardCharsets
import java.time.Instant
import scala.util.Try

object GiteaResponseMapper:
  /** How much of a failing response body is kept on the returned error.
    *
    * The body is genuinely useful — Gitea puts validation detail in it — but a
    * `GiteaError` is a value users log, and without a ceiling a single failed
    * request could put a multi-megabyte payload into a log line. Real 422
    * payloads run to a few kilobytes.
    */
  private val errorBodyLimit: Int = 8 * 1024

  def decodeJson[A: JsonDecoder](response: Response[String]): Either[GiteaError, A] =
    if response.isSuccess then
      response.body.fromJson[A].left.map(message => GiteaError.DecodeError(message, truncate(response.body)))
    else Left(toError(response))

  def decodeUnit(response: Response[String]): Either[GiteaError, Unit] =
    if response.isSuccess then Right(()) else Left(toError(response))

  def decodeString(response: Response[String]): Either[GiteaError, String] =
    if response.isSuccess then Right(response.body) else Left(toError(response))

  def decodeBytes(response: Response[Array[Byte]]): Either[GiteaError, Chunk[Byte]] =
    if response.isSuccess then Right(Chunk.fromArray(response.body))
    else Left(toError(bytesResponseAsString(response)))

  /** Answers a 204/404 membership question, and nothing else.
    *
    * Only 204 means yes and only 404 means no. An unexpected 2xx used to be
    * read as yes, which is the wrong direction to fail for a question callers
    * use as a permission gate: an identity-aware proxy whose session has
    * lapsed answers `200 OK` with an HTML login page, and that must not read as
    * "yes, this user is a collaborator".
    *
    * The remaining ambiguity is upstream's and cannot be resolved here: Gitea
    * returns 404 both for "not a collaborator" and for "repository not visible
    * to you".
    */
  def decodeNoContentOrNotFoundBoolean(response: Response[String]): Either[GiteaError, Boolean] =
    response.code match
      case StatusCode.NoContent => Right(true)
      case StatusCode.NotFound => Right(false)
      case _ => Left(toError(response))

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
    val body = truncate(response.body)
    val message = errorMessage(response)

    response.code.code match
      case 400 => GiteaError.BadRequest(message, body)
      case 401 => GiteaError.Unauthorized(message, body)
      case 403 => GiteaError.Forbidden(message, body)
      case 404 => GiteaError.NotFound(message, body)
      case 405 => GiteaError.MethodNotAllowed(message, body)
      case 409 => GiteaError.Conflict(message, body)
      case 412 => GiteaError.PreconditionFailed(message, body)
      case 422 => GiteaError.UnprocessableEntity(message, body)
      case 423 => GiteaError.Locked(message, body)
      case 429 => GiteaError.RateLimited(rateLimitReset(response), body)
      // Every status without a dedicated case above lands here, not only 5xx.
      // The previous `case status if status >= 500` arm was followed by an
      // identical unguarded arm, so it never changed the outcome.
      case status => GiteaError.ServerError(status, body)

  private def errorMessage(response: Response[String]): String =
    response.body.fromJson[GiteaErrorPayload].toOption.flatMap(_.message)
      .orElse(response.header("message"))
      .getOrElse(response.statusText)

  private def truncate(body: String): String =
    if body.length <= errorBodyLimit then body else body.take(errorBodyLimit)

  private def bytesResponseAsString(response: Response[Array[Byte]]): Response[String] =
    // Decode only as much of a binary error body as will be kept. Without the
    // cap a failed archive download materialised a second full copy of the
    // payload as a UTF-16 String purely to read a status and a short message.
    val bytes = response.body
    val kept = if bytes.length <= errorBodyLimit then bytes else bytes.take(errorBodyLimit)
    response.copy(body = String(kept, StandardCharsets.UTF_8))

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
