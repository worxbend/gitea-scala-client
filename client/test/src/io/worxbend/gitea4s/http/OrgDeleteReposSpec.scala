package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object OrgDeleteReposSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  def spec =
    suite("delete organization repositories")(
      test("accepts only the documented 202 and 204 statuses") {
        val built = GiteaRequests.orgDeleteRepos(config, "space org")
        val request = built.request
        val accepted = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.Accepted))
        val noContent = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val loginPage = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("<html>login</html>"))
        val denied = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.Forbidden))
        val acceptedResult = built.decode(request.send(accepted))
        val noContentResult = built.decode(request.send(noContent))
        val loginResult = built.decode(request.send(loginPage))
        val deniedResult = built.decode(request.send(denied))

        assertTrue(
          built.endpoint == GiteaEndpoints.orgDeleteRepos,
          request.method == Method.DELETE,
          request.uri.toString == "https://gitea.example/root/api/v1/orgs/space%20org/repos",
          request.header("Authorization").contains("token secret"),
          request.body == NoBody,
          !built.retryable,
          acceptedResult == Right(()),
          noContentResult == Right(()),
          loginResult.isLeft,
          deniedResult.isLeft
        )
      },
      test("the versioned facade exposes explicit bulk deletion") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenAnyRequest.thenRespond(ResponseStub.adjust("", StatusCode.Accepted))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.orgs.deleteAllRepositories("example"))(Assertion.equalTo(()))
      }
    )
