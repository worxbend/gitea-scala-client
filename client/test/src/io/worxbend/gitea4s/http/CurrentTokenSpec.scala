package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClientV1273, GiteaConfig}
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object CurrentTokenSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret"))

  def spec =
    suite("current token operations")(
      test("builds an authenticated metadata request and decodes the response") {
        val built = GiteaRequests.currentToken(config)
        val request = built.request
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"id":42,"name":"automation","user":{"login":"alice"}}""")
        )
        val result = built.decode(request.send(backend))

        assertTrue(
          built.endpoint == GiteaEndpoints.getCurrentToken,
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/api/v1/token",
          request.header("Authorization").contains("token secret"),
          built.retryable,
          result.map(_.name) == Right(Some("automation"))
        )
      },
      test("revokes the token only on successful responses") {
        val built = GiteaRequests.deleteCurrentToken(config)
        val request = built.request
        val success = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val failure = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"forbidden"}""", StatusCode.Forbidden)
        )
        val loginPage = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("<html>login</html>"))
        val successResult = built.decode(request.send(success))
        val failureResult = built.decode(request.send(failure))
        val loginResult = built.decode(request.send(loginPage))

        assertTrue(
          built.endpoint == GiteaEndpoints.deleteCurrentToken,
          request.method == Method.DELETE,
          request.uri.toString == "https://gitea.example/api/v1/token",
          request.header("Authorization").contains("token secret"),
          !built.retryable,
          successResult == Right(()),
          failureResult.isLeft,
          loginResult.isLeft
        )
      },
      test("exposes token operations through the versioned facade") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.method == Method.GET)
          .thenRespond(ResponseStub.adjust("""{"id":42,"name":"automation"}"""))
          .whenRequestMatches(_.method == Method.DELETE)
          .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClientV1273.fromBackend(config, backend)

        for
          token <- client.tokens.current
          _ <- client.tokens.revokeCurrent
        yield assertTrue(token.id.contains(42L))
      }
    )
