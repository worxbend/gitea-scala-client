package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object PullReviewCommentReplySpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  def spec =
    suite("pull review comment replies")(
      test("posts only a reply body and decodes the created comment") {
        val built = GiteaRequests.repoCreatePullReviewCommentReply(config, "a b", "repo/one", 12, 42, "Looks good")
        val request = built.request
        val requestBody = request.body match
          case StringBody(value, _, _) => value
          case other => other.toString
        val success = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"id":43,"body":"Looks good"}""", StatusCode.Created)
        )
        val invalid = BackendStub.synchronous.whenAnyRequest.thenRespond(
          ResponseStub.adjust("""{"message":"invalid reply"}""", StatusCode.UnprocessableEntity)
        )
        val created = built.decode(request.send(success))
        val rejected = built.decode(request.send(invalid))

        assertTrue(
          built.endpoint == GiteaEndpoints.repoCreatePullReviewCommentReply,
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/a%20b/repo%2Fone/pulls/12/comments/42/replies",
          request.header("Authorization").contains("token secret"),
          requestBody == """{"body":"Looks good"}""",
          !built.retryable,
          created.map(_.id) == Right(Some(43L)),
          rejected.isLeft
        )
      },
      test("replies through the versioned client") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenAnyRequest.thenRespond(ResponseStub.adjust("""{"id":43}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        client.pulls.replyToReviewComment("owner", "repo", 12, 42, "Thanks").map { comment =>
          assertTrue(comment.id.contains(43L))
        }
      }
    )
