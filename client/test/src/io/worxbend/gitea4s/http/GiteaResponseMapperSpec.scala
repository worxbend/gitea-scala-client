package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.StatusCode
import zio.{Chunk, Task}
import zio.test.*

import java.nio.charset.StandardCharsets

object GiteaResponseMapperSpec extends ZIOSpecDefault:
  def spec =
    suite("Gitea response mapper")(
      suite("binary bodies")(
        test("decodes successful byte bodies as chunks without string conversion") {
          val bytes = Array[Byte](0, 1, 2, -1, 65, 10)
          val response = rawBytes(bytes, StatusCode.Ok)

          assertTrue(
            GiteaResponseMapper.decodeBytes(response) == Right(Chunk.fromArray(bytes))
          )
        },
        test("maps non-2xx byte bodies through the normal GiteaError taxonomy") {
          val body = """{"message":"file missing"}"""
          val response = rawBytes(body.getBytes(StandardCharsets.UTF_8), StatusCode.NotFound)

          assertTrue(
            GiteaResponseMapper.decodeBytes(response) ==
              Left(GiteaError.NotFound("file missing", body))
          )
        },
        test("sends byte response requests through the shared request executor") {
          val bytes = Array[Byte](0, 1, 2, -1, 65, 10)
          val backend =
            BackendStub[Task](new RIOMonadAsyncError[Any])
              .whenAnyRequest
              .thenRespond(ResponseStub.adjust(bytes))
          val request =
            GiteaRequest.withBody[Chunk[Byte], Array[Byte]](
              endpoint = GiteaEndpoints.userGetCurrent,
              request = basicRequest
                .get(uri"https://gitea.example/api/v1/raw")
                .response(asByteArrayAlways),
              decode = GiteaResponseMapper.decodeBytes,
              retryable = true
            )

          new GiteaRequestExecutor(backend, maxRetries = 0).send(request).map { result =>
            assertTrue(result == Chunk.fromArray(bytes))
          }
        }
      ),
      suite("global resource-state failures")(
        test("maps 405 JSON error bodies to MethodNotAllowed with the decoded Gitea message") {
          val body = """{"message":"merge method is not allowed"}"""
          val response = raw(body, StatusCode.MethodNotAllowed)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.MethodNotAllowed("merge method is not allowed", body)
          )
        },
        test("maps 405 empty bodies to MethodNotAllowed using the HTTP status text") {
          val response = raw("", StatusCode.MethodNotAllowed)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.MethodNotAllowed("Method Not Allowed", "")
          )
        },
        test("maps 405 non-JSON bodies to MethodNotAllowed while preserving the raw body") {
          val body = "method is disabled for this repository"
          val response = raw(body, StatusCode.MethodNotAllowed)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.MethodNotAllowed("Method Not Allowed", body)
          )
        },
        test("maps 412 JSON error bodies to PreconditionFailed with the decoded Gitea message") {
          val body = """{"message":"stale content version"}"""
          val response = raw(body, StatusCode(412))

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.PreconditionFailed("stale content version", body)
          )
        },
        test("maps 412 empty bodies to PreconditionFailed using the HTTP status text") {
          val response = raw("", StatusCode(412))

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.PreconditionFailed("Precondition Failed", "")
          )
        },
        test("maps 412 non-JSON bodies to PreconditionFailed while preserving the raw body") {
          val body = "stale content version"
          val response = raw(body, StatusCode(412))

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.PreconditionFailed("Precondition Failed", body)
          )
        },
        test("maps 423 JSON error bodies to Locked with the decoded Gitea message") {
          val body = """{"message":"repository is archived"}"""
          val response = raw(body, StatusCode.Locked)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.Locked("repository is archived", body)
          )
        },
        test("maps 423 empty bodies to Locked using the HTTP status text") {
          val response = raw("", StatusCode.Locked)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.Locked("Locked", "")
          )
        },
        test("maps 423 non-JSON bodies to Locked while preserving the raw body") {
          val body = "resource is locked by another operation"
          val response = raw(body, StatusCode.Locked)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.Locked("Locked", body)
          )
        }
      ),
      suite("pagination signals")(
        test("keeps paging when the server returned fewer items than were requested") {
          // Gitea clamps `limit` to MAX_RESPONSE_ITEMS (50 by default). Judging
          // hasNext from the requested size concluded 2 * 100 >= 200 after two
          // pages and silently dropped half the collection.
          val page = decodeIntPage("[" + (1 to 50).mkString(",") + "]", Some(200), page = 2, pageSize = 100)

          assertTrue(page.map(_.hasNext) == Right(true))
        },
        test("stops at the end of a collection when the counts line up") {
          val page = decodeIntPage("[" + (1 to 50).mkString(",") + "]", Some(100), page = 2, pageSize = 50)

          assertTrue(page.map(_.hasNext) == Right(false))
        },
        test("stops on an empty page") {
          val page = decodeIntPage("[]", Some(200), page = 3, pageSize = 50)

          assertTrue(page.map(_.hasNext) == Right(false))
        },
        test("stops when the response carries no pagination metadata at all") {
          // Neither a Link header nor a total count is evidence of a further
          // page. Continuing on no evidence would add a request to every such
          // stream and would never terminate against a server that keeps
          // answering with a full page.
          val page = decodeIntPage("[1,2,3]", None, page = 1, pageSize = 3)

          assertTrue(page.map(_.hasNext) == Right(false))
        },
        test("trusts an explicit next link even when the counts suggest otherwise") {
          val response =
            ResponseStub(
              "[1,2]",
              StatusCode.Ok,
              Seq(sttp.model.Header("link", """<https://gitea.example/api/v1/x?page=2>; rel="next""""))
            )

          assertTrue(GiteaResponseMapper.decodePage[Int](response, 9, 50).map(_.hasNext) == Right(true))
        },
        test("reports the requested page size, not the received one") {
          // Page.pageSize documents the size that was asked for; only the
          // hasNext decision uses what actually arrived.
          val page = decodeIntPage("[1,2]", Some(200), page = 1, pageSize = 100)

          assertTrue(page.map(_.pageSize) == Right(100))
        }
      ),
      suite("retry timing signals")(
        test("honours Retry-After given as a delay in seconds") {
          val before = java.time.Instant.now()
          val error = GiteaResponseMapper.toError(withHeaders("", StatusCode.TooManyRequests, "retry-after" -> "60"))

          error match
            case GiteaError.RateLimited(Some(at), _) =>
              val delay = java.time.Duration.between(before, at).toSeconds
              assertTrue(delay >= 55L, delay <= 65L)
            case _ => assertTrue(false)
        },
        test("honours Retry-After given as an HTTP date") {
          val when = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC).plusMinutes(2)
          val header = java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(when)
          val error = GiteaResponseMapper.toError(withHeaders("", StatusCode.TooManyRequests, "retry-after" -> header))

          error match
            case GiteaError.RateLimited(Some(_), _) => assertTrue(true)
            case _ => assertTrue(false)
        },
        test("prefers Retry-After over x-ratelimit-reset") {
          val reset = java.time.Instant.now().plusSeconds(3600).getEpochSecond
          val error =
            GiteaResponseMapper.toError(
              withHeaders("", StatusCode.TooManyRequests, "retry-after" -> "30", "x-ratelimit-reset" -> reset.toString)
            )

          error match
            case GiteaError.RateLimited(Some(at), _) =>
              assertTrue(java.time.Duration.between(java.time.Instant.now(), at).toSeconds <= 60L)
            case _ => assertTrue(false)
        },
        test("reports an implausibly distant reset instant rather than second-guessing it") {
          // The decoder has no clock; GiteaRequestExecutorSpec covers the cap
          // that stops this becoming an unbounded sleep.
          val error =
            GiteaResponseMapper.toError(withHeaders("", StatusCode.TooManyRequests, "x-ratelimit-reset" -> "99999999999"))

          error match
            case GiteaError.RateLimited(Some(at), _) => assertTrue(at.getEpochSecond == 99999999999L)
            case _ => assertTrue(false)
        },
        test("ignores an unparseable reset header") {
          val error =
            GiteaResponseMapper.toError(withHeaders("", StatusCode.TooManyRequests, "x-ratelimit-reset" -> "soon"))

          assertTrue(error == GiteaError.RateLimited(None, ""))
        }
      ),
      suite("membership answers")(
        test("204 means yes and 404 means no") {
          assertTrue(
            GiteaResponseMapper.decodeNoContentOrNotFoundBoolean(raw("", StatusCode.NoContent)) == Right(true),
            GiteaResponseMapper.decodeNoContentOrNotFoundBoolean(raw("", StatusCode.NotFound)) == Right(false)
          )
        },
        test("an unexpected 200 is an error, not an affirmative answer") {
          // An identity-aware proxy whose session lapsed answers 200 with an
          // HTML login page. That must not read as "yes, they are a
          // collaborator" to a caller using this as a permission gate.
          val result = GiteaResponseMapper.decodeNoContentOrNotFoundBoolean(raw("<html>login</html>", StatusCode.Ok))

          assertTrue(result.isLeft)
        }
      ),
      suite("retained error bodies")(
        test("truncates an oversized error body") {
          val body = "x" * (100 * 1024)
          val error = GiteaResponseMapper.toError(raw(body, StatusCode.InternalServerError))

          assertTrue(error == GiteaError.ServerError(500, "x" * 8192))
        },
        test("leaves a normal error body intact") {
          val body = """{"message":"nope"}"""

          assertTrue(GiteaResponseMapper.toError(raw(body, StatusCode.NotFound)) == GiteaError.NotFound("nope", body))
        },
        test("truncates an oversized binary error body without decoding all of it") {
          val body = Array.fill[Byte](100 * 1024)('x'.toByte)
          val result = GiteaResponseMapper.decodeBytes(rawBytes(body, StatusCode.InternalServerError))

          assertTrue(result == Left(GiteaError.ServerError(500, "x" * 8192)))
        },
        test("truncates the body kept on a decode failure") {
          val body = "[" + ("\"not-an-int\"," * 5000).dropRight(1) + "]"
          val result = GiteaResponseMapper.decodeChunk[Int](ResponseStub(body, StatusCode.Ok))

          assertTrue(result.left.map(_.asInstanceOf[GiteaError.DecodeError].body.length) == Left(8192))
        }
      )
    )

  private def withHeaders(body: String, statusCode: StatusCode, headers: (String, String)*): Response[String] =
    ResponseStub(body, statusCode, headers.map(sttp.model.Header(_, _)))

  private def decodeIntPage(
      body: String,
      totalCount: Option[Long],
      page: Int,
      pageSize: Int
  ): Either[GiteaError, io.worxbend.gitea4s.model.Page[Int]] =
    val headers = totalCount.map(count => sttp.model.Header("x-total-count", count.toString)).toSeq
    GiteaResponseMapper.decodePage[Int](ResponseStub(body, StatusCode.Ok, headers), page, pageSize)

  private def raw(body: String, statusCode: StatusCode): Response[String] =
    ResponseStub(body, statusCode)

  private def rawBytes(body: Array[Byte], statusCode: StatusCode): Response[Array[Byte]] =
    ResponseStub(body, statusCode)
