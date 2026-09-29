package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Auth
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object GeneralSettingsSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  private def respond(body: String, code: StatusCode = StatusCode.Ok) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, code))

  def spec =
    suite("public settings reads")(
      test("all routes are authenticated retryable GETs without bodies or queries") {
        val requests = List(
          GiteaRequests.generalAPISettings(config).request -> "api",
          GiteaRequests.generalAttachmentSettings(config).request -> "attachment",
          GiteaRequests.generalRepositorySettings(config).request -> "repository",
          GiteaRequests.generalUISettings(config).request -> "ui"
        )
        assertTrue(requests.forall { case (request, resource) =>
          request.method == Method.GET && request.body == NoBody &&
          request.header("Authorization").contains("token secret") &&
          request.uri.toString == s"https://gitea.example/root/api/v1/settings/$resource"
        })
      },
      test("decodes each response shape and maps errors") {
        val api = GiteaRequests.generalAPISettings(config)
        val attachment = GiteaRequests.generalAttachmentSettings(config)
        val repos = GiteaRequests.generalRepositorySettings(config)
        val ui = GiteaRequests.generalUISettings(config)
        val apiValue = api.decode(api.request.send(respond("""{"default_paging_num":50}""")))
        val attachmentValue = attachment.decode(attachment.request.send(respond("""{"enabled":false,"max_files":0}""")))
        val repoValue = repos.decode(repos.request.send(respond("""{"lfs_disabled":true}""")))
        val uiValue = ui.decode(ui.request.send(respond("""{"allowed_reactions":["+1"],"custom_emojis":[]}""")))
        val missing = api.decode(api.request.send(respond("", StatusCode.NotFound)))
        val malformed = ui.decode(ui.request.send(respond("<html>error</html>")))

        assertTrue(
          api.retryable, attachment.retryable, repos.retryable, ui.retryable,
          apiValue.map(_.defaultPagingNum) == Right(Some(50L)),
          attachmentValue.map(_.enabled) == Right(Some(false)),
          repoValue.map(_.lfsDisabled) == Right(Some(true)),
          uiValue.map(_.customEmojis) == Right(Some(Nil)),
          missing.left.exists(_.isInstanceOf[GiteaError.NotFound]),
          malformed.isLeft
        )
      },
      test("the ordinary client exposes the settings namespace") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.uri.path.endsWith(List("settings", "api")))
          .thenRespond(ResponseStub.adjust("""{"max_response_items":100}"""))
          .whenRequestMatches(_.uri.path.endsWith(List("settings", "attachment")))
          .thenRespond(ResponseStub.adjust("""{"enabled":true}"""))
          .whenRequestMatches(_.uri.path.endsWith(List("settings", "repository")))
          .thenRespond(ResponseStub.adjust("""{"mirrors_disabled":false}"""))
          .whenRequestMatches(_.uri.path.endsWith(List("settings", "ui")))
          .thenRespond(ResponseStub.adjust("""{"default_theme":"auto"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          api <- client.settings.api
          attachments <- client.settings.attachments
          repositories <- client.settings.repositories
          ui <- client.settings.ui
        yield assertTrue(
          api.maxResponseItems.contains(100L), attachments.enabled.contains(true),
          repositories.mirrorsDisabled.contains(false), ui.defaultTheme.contains("auto")
        )
      }
    )
