package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClientV1273, GiteaConfig}
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Auth, CreateTagProtectionOption, EditTagProtectionOption}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object TagProtectionWritesSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  private def respond(body: String, code: StatusCode = StatusCode.Ok) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, code))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("tag protection writes")(
      test("creates a protection with typed allowlists and requires 201") {
        val built = GiteaRequests.createTagProtection(
          config, "team org", "repo", CreateTagProtectionOption(
            namePattern = Some("v*"), whitelistTeams = Some(List("release")), whitelistUsernames = Some(Nil)
          )
        )
        val request = built.request
        val created = built.decode(request.send(respond("""{"id":12,"name_pattern":"v*"}""", StatusCode.Created)))
        val invalid = built.decode(request.send(respond("""{"message":"invalid"}""", StatusCode.UnprocessableEntity)))
        val login = built.decode(request.send(respond("""{"id":12}""")))

        assertTrue(
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/team%20org/repo/tag_protections",
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          stringBody(request) == """{"name_pattern":"v*","whitelist_teams":["release"],"whitelist_usernames":[]}""",
          !built.retryable,
          created.map(_.id) == Right(Some(12L)),
          invalid.left.exists(_.isInstanceOf[GiteaError.UnprocessableEntity]),
          login.isLeft
        )
      },
      test("edits an existing protection and keeps explicitly empty allowlists") {
        val built = GiteaRequests.editTagProtection(
          config, "owner", "repo", 12L, EditTagProtectionOption(whitelistUsernames = Some(Nil))
        )
        val request = built.request
        val edited = built.decode(request.send(respond("""{"id":12,"whitelist_usernames":[]}""")))
        val archived = built.decode(request.send(respond("""{"message":"archived"}""", StatusCode.Locked)))

        assertTrue(
          request.method == Method.PATCH,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/tag_protections/12",
          stringBody(request) == """{"whitelist_usernames":[]}""",
          edited.map(_.whitelistUsernames) == Right(Some(Nil)),
          archived.left.exists(_.isInstanceOf[GiteaError.Locked]),
          !built.retryable
        )
      },
      test("deletes a protection only on 204") {
        val built = GiteaRequests.deleteTagProtection(config, "owner", "repo", 12L)
        val request = built.request
        val deleted = built.decode(request.send(respond("", StatusCode.NoContent)))
        val login = built.decode(request.send(respond("<html>login</html>")))
        val missing = built.decode(request.send(respond("", StatusCode.NotFound)))

        assertTrue(
          request.method == Method.DELETE,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/tag_protections/12",
          request.body == NoBody,
          !built.retryable,
          deleted == Right(()),
          login.isLeft,
          missing.left.exists(_.isInstanceOf[GiteaError.NotFound])
        )
      },
      test("versioned facade reaches all three writes") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.method == Method.POST)
          .thenRespond(ResponseStub.adjust("""{"id":12}""", StatusCode.Created))
          .whenRequestMatches(_.method == Method.PATCH)
          .thenRespond(ResponseStub.adjust("""{"id":12}"""))
          .whenRequestMatches(_.method == Method.DELETE)
          .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClientV1273.fromBackend(config, backend)

        for
          created <- client.repos.createTagProtection("owner", "repo", CreateTagProtectionOption())
          edited <- client.repos.editTagProtection("owner", "repo", 12L, EditTagProtectionOption())
          deleted <- client.repos.deleteTagProtection("owner", "repo", 12L)
        yield assertTrue(created.id.contains(12L), edited.id.contains(12L), deleted == ())
      }
    )
