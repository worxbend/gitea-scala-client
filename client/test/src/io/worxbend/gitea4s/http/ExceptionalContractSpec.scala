package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.model.{AttachmentUpload, Auth}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.{Chunk, Task}
import zio.test.*

object ExceptionalContractSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))
  private val upload = AttachmentUpload("report.txt", Chunk[Byte](1, 2, 3))

  private def respond(body: String, code: StatusCode = StatusCode.Created) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, code))

  private def multipartParts(request: Request[?]): Seq[sttp.model.Part[BasicBodyPart]] =
    request.body match
      case BasicMultipartBody(parts) => parts
      case _ => Nil

  private def attachmentBytes(request: Request[?]): Option[Chunk[Byte]] =
    multipartParts(request).headOption.flatMap { part =>
      part.body match
        case ByteArrayBody(bytes, _) => Some(Chunk.fromArray(bytes))
        case _ => None
    }

  def spec =
    suite("multipart and redirected artifact operations")(
      test("issue attachment has the correct part, filename, URI, and 201 response") {
        val built = GiteaRequests.issueCreateIssueAttachment(config, "space owner", "repo", 12L, upload, Some("report"))
        val request = built.request
        val parts = multipartParts(request)
        val partNames = parts.map(_.name)
        val fileNames = parts.flatMap(_.fileName)
        val bytes = attachmentBytes(request)
        val decoded = built.decode(request.send(respond("""{"id":15,"name":"report.txt"}""")))
        val invalid = built.decode(request.send(respond("", StatusCode.PayloadTooLarge)))

        assertTrue(
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/space%20owner/repo/issues/12/assets?name=report",
          request.header("Authorization").contains("token secret"),
          parts.size == 1,
          partNames == Seq("attachment"),
          fileNames == Seq("report.txt"),
          bytes.contains(upload.content),
          decoded.map(_.id) == Right(Some(15L)),
          invalid.isLeft,
          !built.retryable
        )
      },
      test("comment and release uploads use their distinct paths") {
        val comment = GiteaRequests.issueCreateIssueCommentAttachment(config, "owner", "repo", 8L, upload)
        val release = GiteaRequests.repoCreateReleaseAttachment(config, "owner", "repo", 9L, upload)
        val commentRequest = comment.request
        val releaseRequest = release.request
        val createdComment = comment.decode(commentRequest.send(respond("""{"id":8}""")))
        val createdRelease = release.decode(releaseRequest.send(respond("""{"id":9}""")))
        val commentNames = multipartParts(commentRequest).flatMap(_.fileName)
        val releaseNames = multipartParts(releaseRequest).flatMap(_.fileName)

        assertTrue(
          commentRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/issues/comments/8/assets",
          releaseRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/releases/9/assets",
          commentNames == Seq("report.txt"),
          releaseNames == Seq("report.txt"),
          attachmentBytes(commentRequest).contains(upload.content),
          attachmentBytes(releaseRequest).contains(upload.content),
          createdComment.map(_.id) == Right(Some(8L)),
          createdRelease.map(_.id) == Right(Some(9L))
        )
      },
      test("artifact download preserves binary bytes and does not invent a JSON response") {
        val built = GiteaRequests.downloadArtifact(config, "space owner", "repo", "id/slash")
        val request = built.request
        val bytes = Array[Byte](0, 1, -1, 80)
        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(bytes))
        val decoded = built.decode(request.send(backend))

        assertTrue(
          request.method == Method.GET,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/space%20owner/repo/actions/artifacts/id%2Fslash/zip",
          request.header("Accept").contains("application/octet-stream"),
          built.retryable,
          decoded == Right(Chunk.fromArray(bytes))
        )
      },
      test("ordinary client reaches all three attachment routes") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenAnyRequest.thenRespond(ResponseStub.adjust("""{"id":3}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)
        for
          issue <- client.issues.createIssueAttachment("owner", "repo", 1L, upload)
          comment <- client.issues.createCommentAttachment("owner", "repo", 2L, upload)
          release <- client.releases.createAttachment("owner", "repo", 3L, upload)
        yield assertTrue(issue.id.contains(3L), comment.id.contains(3L), release.id.contains(3L))
      },
      test("generated namespaces execute through the ordinary client") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenAnyRequest.thenRespond(ResponseStub.adjust("[]"))
        val client = GiteaClient.fromBackend(config, backend)
        for
          cron <- client.admin.adminCronList()
          templates <- client.catalog.listGitignoresTemplates()
          packages <- client.packages.listPackages("owner")
          teams <- client.teams.orgListTeamMembers(1L)
        yield assertTrue(cron.isEmpty, templates.isEmpty, packages.isEmpty, teams.isEmpty)
      },
      test("compare endpoint switches its response format with the output query") {
        val json = GeneratedReposRequests.repoCompareDiff(config, "owner", "repo", "main...head")
        val diff = GeneratedReposRequests.repoCompareDiff(config, "owner", "repo", "main...head",
          Some(io.worxbend.gitea4s.model.contract.CompareOutput.Diff))
        val jsonRequest = json.request
        val diffRequest = diff.request
        val jsonResult = json.decode(jsonRequest.send(respond("{}", StatusCode.Ok)))
        val diffResult = diff.decode(diffRequest.send(respond("diff --git a b\n", StatusCode.Ok)))
        val jsonAccept = jsonRequest.header("Accept")
        val diffAccept = diffRequest.header("Accept")
        val diffQuery = diffRequest.uri.paramsSeq

        assertTrue(
          jsonAccept.contains("application/json"),
          diffAccept.contains("text/plain"),
          diffQuery == Seq("output" -> "diff"),
          jsonResult.exists(_.isLeft),
          diffResult == Right(Right("diff --git a b\n"))
        )
      }
    )
