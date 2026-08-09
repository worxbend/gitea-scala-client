package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{GiteaErrorPayload, Page, TopicNames, User}
import sttp.client4.Response
import sttp.model.StatusCode
import zio.Chunk
import zio.json.*

import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZonedDateTime}
import scala.util.Try

object GiteaResponseMapper:
  /** How much server-supplied text is kept on the returned error.
    *
    * The body is genuinely useful — Gitea puts validation detail in it — but a
    * `GiteaError` is a value users log, and without a ceiling a single failed
    * request could put a multi-megabyte payload into a log line. Real 422
    * payloads run to a few kilobytes.
    *
    * Bounds both fields a `GiteaError` carries: the body and the message
    * lifted out of it. Capping only the body would leave the ceiling defeated
    * by moving the payload inside the JSON.
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
    // Decoding straight into a Chunk rather than via List: zio-json builds a
    // List through a ListBuffer, and Chunk.fromIterable then walks all n cons
    // cells a second time into a builder, because List has no known size. The
    // Chunk decoder drives a ChunkBuilder directly and reports identical error
    // spans, both going through the same JsonDecoder.builder helper.
    decodeJson[Chunk[A]](response)

  def decodePage[A: JsonDecoder](
      response: Response[String],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, Page[A]] =
    decodeJson[Chunk[A]](response).map(toPage(response, page, pageSize, _))

  def decodeTopicNamesPage(
      response: Response[String],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, Page[String]] =
    decodeJson[TopicNames](response).map { value =>
      toPage(response, page, pageSize, Chunk.fromIterable(value.topics.getOrElse(Nil)))
    }

  def decodeUserSearchPage(
      response: Response[String],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, Page[User]] =
    decodeJson[UserSearchResults](response).map { value =>
      toPage(response, page, pageSize, value.data.getOrElse(Chunk.empty))
    }

  /** Assembles the `Page` around whatever the decoder extracted.
    *
    * The three page decoders differ only in how they get from the body to a
    * `Chunk`; each then read `x-total-count` and hand-built the same five
    * fields. Taking the values here rather than a count is the point: the
    * received size is derived once, next to the header it is compared against,
    * so no call site can pass `pageSize` where the number of items received is
    * required — which is exactly the bug `hasNextPage` documents at length
    * below.
    */
  private def toPage[A](
      response: Response[String],
      page: Int,
      pageSize: Int,
      values: Chunk[A]
  ): Page[A] =
    val totalCount = longHeader(response, "x-total-count")
    Page(
      data = values,
      totalCount = totalCount,
      page = page,
      pageSize = pageSize,
      hasNext = hasNextPage(response, page, values.size, totalCount)
    )

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
      case 429 => GiteaError.RateLimited(retryAt(response), body)
      // Every status without a dedicated case above lands here, not only 5xx.
      // The previous `case status if status >= 500` arm was followed by an
      // identical unguarded arm, so it never changed the outcome.
      case status => GiteaError.ServerError(status, body)

  /** When the server says it will accept another request, if it says at all.
    *
    * `Retry-After` is checked first: it is the standard header for 429 and 503,
    * and the one nginx, HAProxy and Cloudflare actually send. Gitea itself has
    * no built-in API rate limiter, so in practice a 429 comes from exactly
    * those front-ends. `x-ratelimit-reset` is the fallback.
    *
    * The value is reported exactly as the server gave it, including one that is
    * implausibly far ahead — a proxy reporting the reset in milliseconds sends
    * something that is a valid epoch *second* thousands of years out. Deciding
    * how long to actually wait is the executor's job, and it caps the wait; a
    * decoder has no clock to reason about "how far ahead" with.
    */
  private[gitea4s] def retryAt(response: Response[String]): Option[Instant] =
    retryAfter(response).orElse(rateLimitReset(response))

  private def retryAfter(response: Response[String]): Option[Instant] =
    response.header("retry-after").flatMap { raw =>
      val value = raw.trim
      // RFC 9110 allows either a delay in seconds or an HTTP-date. The
      // delay form has to be anchored to some "now" to become the absolute
      // instant `GiteaError.RateLimited` carries, and a decoder has no
      // effectful clock, so it uses the wall clock. In production that is the
      // same clock ZIO reads, so the resulting wait is right; under a test
      // clock the two differ, which is why the executor's own cap — not this
      // value — is what actually bounds the sleep.
      value.toLongOption
        .flatMap(seconds => Try(Instant.now().plusSeconds(seconds)).toOption)
        .orElse(
          Try(ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant).toOption
        )
    }

  /** The server's explanation of a failure, bounded like the body it came from.
    *
    * Every source here is server-controlled, so all three go through
    * `truncate`. Capping only the body left the ceiling trivially defeated: a
    * response of `{"message": "<4 MB>"}` put the whole payload on
    * `GiteaError.message` while `body` was dutifully clipped to 8 KiB.
    */
  private def errorMessage(response: Response[String]): String =
    truncate(
      response.body.fromJson[GiteaErrorPayload].toOption.flatMap(_.message)
        .orElse(response.header("message"))
        .getOrElse(response.statusText)
    )

  private def truncate(body: String): String =
    if body.length <= errorBodyLimit then body else body.take(errorBodyLimit)

  private def bytesResponseAsString(response: Response[Array[Byte]]): Response[String] =
    // Decode only as much of a binary error body as will be kept. Without the
    // cap a failed archive download materialised a second full copy of the
    // payload as a UTF-16 String purely to read a status and a short message.
    val bytes = response.body
    val kept = if bytes.length <= errorBodyLimit then bytes else bytes.take(errorBodyLimit)
    response.copy(body = String(kept, StandardCharsets.UTF_8))

  /** Whether another page exists, judged from what actually arrived.
    *
    * The count that matters is the number of items received, not the number
    * requested. Gitea clamps `limit` to its `MAX_RESPONSE_ITEMS` setting
    * (default 50), so asking for 100 and comparing `page * 100` against a total
    * of 200 concluded "no more pages" after two pages of 50 — silently dropping
    * half the collection with no error. Endpoints that send a `Link` header
    * were protected by the first disjunct; the ones that do not, such as issue
    * comments and repository topics, were not.
    *
    * `page * received` is a deliberate under-estimate of how much has been
    * seen: it assumes every earlier page was as small as this one. On a final
    * partial page that can read as "there may be more", costing one extra
    * request that comes back empty and ends the stream through the empty-page
    * guard in `Pagination`. Over-fetching one page is recoverable; silently
    * truncating a collection is not.
    *
    * `received >= pageSize` is deliberately not used instead: under the very
    * clamping this exists to fix, 50 >= 100 is false.
    *
    * A response carrying neither a `Link` header nor a total count says nothing
    * about further pages, and is treated as the end of the collection — the
    * behaviour such an endpoint has always had. Continuing on no evidence would
    * add a request to every such stream, and against a server that keeps
    * answering with a full page it would never terminate at all.
    */
  private def hasNextPage(
      response: Response[String],
      page: Int,
      received: Int,
      totalCount: Option[Long]
  ): Boolean =
    response.header("link").exists(_.contains("""rel="next"""")) ||
      (received > 0 && totalCount.exists(total => page.toLong * received.toLong < total))

  private def rateLimitReset(response: Response[String]): Option[Instant] =
    longHeader(response, "x-ratelimit-reset")
      .orElse(longHeader(response, "x-rate-limit-reset"))
      .flatMap(epochSeconds => Try(Instant.ofEpochSecond(epochSeconds)).toOption)

  private def longHeader(response: Response[String], name: String): Option[Long] =
    response.header(name).flatMap(value => Try(value.toLong).toOption)

  private final case class UserSearchResults(
      data: Option[Chunk[User]] = None,
      ok: Option[Boolean] = None
  )

  private object UserSearchResults:
    given JsonDecoder[UserSearchResults] = DeriveJsonDecoder.gen[UserSearchResults]
