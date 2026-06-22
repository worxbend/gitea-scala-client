package io.worxbend.gitea4s

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{
  ArchiveParams,
  CombinedStatusParams,
  CommitNoteParams,
  CommitStatusListParams,
  CommitStatusListState,
  CommitStatusSort,
  ContentsParams,
  GitTreeParams,
  GiteaRequests,
  IssueCommentListParams,
  IssueListParams,
  IssueTrackedTimeListParams,
  NotificationListParams,
  PullRequestCommitsParams,
  PullRequestDiffType,
  PullRequestFilesParams,
  PullRequestListParams,
  PullRequestUpdateStyle,
  ReleaseListParams,
  RepoListParams,
  RepositoryCommentListParams,
  SingleCommitParams,
  UserSearchParams
}
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.{
  AddTimeOption,
  Auth,
  BranchProtection,
  CommitDiffType,
  CommitStatusState,
  CreateIssue,
  CreatePullRequestOption,
  CreatePullReviewOptions,
  CreateStatusOption,
  DismissPullReviewOptions,
  EditDeadlineOption,
  EditIssue,
  EditIssueComment,
  EditPullRequestOption,
  EditReactionOption,
  IssueMeta,
  LanguageStatistics,
  LockIssueOption,
  MergePullRequestMethod,
  MergePullRequestOption,
  PullReviewRequestOptions,
  PullReviewState,
  RepoCollaboratorPermission,
  SubmitPullReviewOptions,
  TagProtection,
  Team,
  TeamPermission
}
import sttp.capabilities.Effect
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Header, Method, RequestMetadata, StatusCode}
import sttp.monad.MonadError
import zio.{Chunk, Ref, Task, ZIO}
import zio.test.*

import java.time.{Duration, Instant}

object GiteaClientSpec extends ZIOSpecDefault:
  private val config =
    GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret")).copy(pageSize = 1)

  private def taskStub =
    BackendStub[Task](new RIOMonadAsyncError[Any])

  private def stringResponse(
      body: String,
      status: StatusCode = StatusCode.Ok,
      headers: List[Header] = Nil
  ): Response[String] =
    Response(
      body = body,
      code = status,
      statusText = "",
      headers = headers,
      history = Nil,
      request = RequestMetadata(Method.GET, uri"https://gitea.example/api/v1/user", Nil)
    )

  private final class ScriptedBackend[B](responses: Ref[List[Task[Response[B]]]]) extends Backend[Task]:
    private val taskMonad = new RIOMonadAsyncError[Any]

    override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
      responses.modify {
        case next :: rest =>
          (next.map(_.asInstanceOf[Response[T]]), rest)
        case Nil =>
          (ZIO.fail(IllegalStateException("scripted backend has no remaining responses")), Nil)
      }.flatten

    override def close(): Task[Unit] =
      ZIO.unit

    override def monad: MonadError[Task] =
      taskMonad

  def spec =
    suite("GiteaClient")(
      test("loads the current user through the UsersApi") {
        val backend =
          taskStub.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"id":42,"login":"octo"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.me.map(user => user.id -> user.login))(
          Assertion.equalTo(Some(42L) -> Some("octo"))
        )
      },
      test("loads a user and repository through overloaded get methods") {
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "alice")))
            .thenRespond(ResponseStub.adjust("""{"id":7,"login":"alice"}"""))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "project")))
            .thenRespond(ResponseStub.adjust("""{"id":11,"name":"project"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          user <- client.get("alice")
          repo <- client.get("alice", "project")
        yield assertTrue(
          user.login.contains("alice"),
          repo.name.contains("project")
        )
      },
      test("checks repository pin capacity through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "new_pin_allowed"))
          }.thenRespond(ResponseStub.adjust("""{"issues":true,"pull_requests":false}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.newIssuePinsAllowed("owner", "repo").map(value => value.issues -> value.pullRequests))(
          Assertion.equalTo(Some(true) -> Some(false))
        )
      },
      test("loads a repository commit through the ReposApi commit method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "abc123")) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """{"sha":"abc123","commit":{"message":"Add single commit facade test"},"stats":{"total":3}}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.commit("alice", "api", "abc123").map(commit => commit.sha -> commit.commit.flatMap(_.message)))(
          Assertion.equalTo(Some("abc123") -> Some("Add single commit facade test"))
        )
      },
      test("forwards single commit query parameters through the ReposApi commit method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "abc123")) &&
              request.uri.paramsMap.get("stat").contains("true") &&
              request.uri.paramsMap.get("verification").contains("false") &&
              request.uri.paramsMap.get("files").contains("true")
          }.thenRespond(ResponseStub.adjust("""{"sha":"abc123","files":[{"filename":"README.md","status":"modified"}]}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client
            .commit(
              "alice",
              "api",
              "abc123",
              SingleCommitParams(stat = Some(true), verification = Some(false), files = Some(true))
            )
            .map(_.files.flatMap(_.headOption.flatMap(_.filename)))
        )(Assertion.equalTo(Some("README.md")))
      },
      test("propagates single commit documented 404 and 422 errors through the facade") {
        val notFoundBody = """{"message":"commit not found"}"""
        val validationBody = """{"message":"validation failed"}"""
        val notFoundBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "missing-sha"))
          ).thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val validationBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "bad-sha"))
          ).thenRespond(ResponseStub.adjust(validationBody, StatusCode.UnprocessableEntity))
        val notFoundClient = GiteaClient.fromBackend(config, notFoundBackend)
        val validationClient = GiteaClient.fromBackend(config, validationBackend)

        for
          notFound <- notFoundClient.commit("alice", "api", "missing-sha").either
          validation <- validationClient.commit("alice", "api", "bad-sha").either
        yield assertTrue(
          notFound == Left(GiteaError.NotFound("commit not found", notFoundBody)),
          validation == Left(GiteaError.UnprocessableEntity("validation failed", validationBody))
        )
      },
      test("propagates single commit transport failures through the facade") {
        val failure = RuntimeException("connection refused")
        val backend = taskStub.whenAnyRequest.thenThrow(failure)
        val client = GiteaClient.fromBackend(config, backend)

        client.commit("alice", "api", "abc123").either.map { result =>
          assertTrue(
            result.left.exists {
              case GiteaError.TransportError(cause) => cause eq failure
              case _ => false
            }
          )
        }
      },
      test("retries single commit lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "abc123"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"sha":"abc123","commit":{"message":"Retried commit"}}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.commit("alice", "api", "abc123").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          commit <- fiber.join
        yield assertTrue(
          commit.sha.contains("abc123"),
          commit.commit.flatMap(_.message).contains("Retried commit")
        )
      },
      test("loads a repository commit diff or patch as raw text through the ReposApi") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val patch = "From 0000000000000000000000000000000000000000 Mon Sep 17 00:00:00 2001\n"
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString == "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/commits/abc%2Fdef%20123.patch" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "git",
                "commits",
                "abc/def 123.patch"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("text/plain") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321")
          }.thenRespond(ResponseStub.adjust(patch))
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(client.commitDiffOrPatch("space owner", "repo/slash", "abc/def 123", CommitDiffType.patch))(
          Assertion.equalTo(patch)
        )
      },
      test("propagates commit diff or patch documented 404 errors through the facade") {
        val body = """{"message":"commit diff not found"}"""
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "missing-sha.diff"))
          ).thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.commitDiffOrPatch("alice", "api", "missing-sha", CommitDiffType.diff).either)(
          Assertion.equalTo(Left(GiteaError.NotFound("commit diff not found", body)))
        )
      },
      test("retries commit diff or patch lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(stringResponse("diff --git a/README.md b/README.md\n"))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.commitDiffOrPatch("alice", "api", "abc123", CommitDiffType.diff).fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          diff <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          diff.startsWith("diff --git"),
          remaining.isEmpty
        )
      },
      test("loads a repository commit note through the ReposApi as a read-only GET") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/notes/abc%2Fdef%20123?verification=false&files=true" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "git",
                "notes",
                "abc/def 123"
              ) &&
              request.uri.paramsMap.get("verification").contains("false") &&
              request.uri.paramsMap.get("files").contains("true") &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """{"message":"Reviewed-by: Octo","commit":{"sha":"abc123","commit":{"message":"Commit with note"}}}"""
            )
          )
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(
          client
            .commitNote(
              "space owner",
              "repo/slash",
              "abc/def 123",
              CommitNoteParams(verification = Some(false), files = Some(true))
            )
            .map(note => note.message -> note.commit.flatMap(_.sha) -> note.commit.flatMap(_.commit.flatMap(_.message)))
        )(
          Assertion.equalTo(Some("Reviewed-by: Octo") -> Some("abc123") -> Some("Commit with note"))
        )
      },
      test("retries commit note lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(
                stringResponse(
                  """{"message":"Retried note","commit":{"sha":"abc123","commit":{"message":"Retried commit note"}}}"""
                )
              )
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.commitNote("alice", "api", "abc123").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          note <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          note.message.contains("Retried note"),
          note.commit.flatMap(_.sha).contains("abc123"),
          remaining.isEmpty
        )
      },
      test("loads a Git tree through the ReposApi gitTree method") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/trees/abc%2Fdef%20123?recursive=true&page=2&per_page=50" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "git",
                "trees",
                "abc/def 123"
              ) &&
              request.uri.paramsMap.get("recursive").contains("true") &&
              request.uri.paramsMap.get("page").contains("2") &&
              request.uri.paramsMap.get("per_page").contains("50") &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """{"page":2,"sha":"abc123","total_count":1,"tree":[{"mode":"100644","path":"README.md","sha":"file-sha","size":123,"type":"blob","url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/blobs/file-sha"}],"truncated":false,"url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/trees/abc123"}"""
            )
          )
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(
          client
            .gitTree(
              "space owner",
              "repo/slash",
              "abc/def 123",
              GitTreeParams(recursive = Some(true), page = Some(2), perPage = Some(50))
            )
            .map(tree => tree.page -> tree.sha -> tree.totalCount -> tree.tree.flatMap(_.headOption.map(_.path)))
        )(
          Assertion.equalTo(Some(2L) -> Some("abc123") -> Some(1L) -> Some(Some("README.md")))
        )
      },
      test("omits Git tree query parameters when facade params are omitted") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString == "https://gitea.example/api/v1/repos/alice/api/git/trees/abc123" &&
              request.uri.paramsMap.isEmpty &&
              !request.uri.paramsMap.contains("recursive") &&
              !request.uri.paramsMap.contains("page") &&
              !request.uri.paramsMap.contains("per_page") &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """{"sha":"abc123","tree":[{"mode":"100644","path":"README.md","sha":"file-sha","size":123,"type":"blob"}],"truncated":false}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client
            .gitTree("alice", "api", "abc123")
            .map(tree => tree.sha -> tree.tree.flatMap(_.headOption.flatMap(_.path)) -> tree.truncated)
        )(
          Assertion.equalTo(Some("abc123") -> Some("README.md") -> Some(false))
        )
      },
      test("propagates Git tree documented 400 and 404 errors through the facade") {
        val badBody = """{"message":"invalid tree sha"}"""
        val notFoundBody = """{"message":"tree not found"}"""
        val badBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "trees", "bad-sha"))
          ).thenRespond(ResponseStub.adjust(badBody, StatusCode.BadRequest))
        val notFoundBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "trees", "missing-sha"))
          ).thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val badClient = GiteaClient.fromBackend(config, badBackend)
        val notFoundClient = GiteaClient.fromBackend(config, notFoundBackend)

        for
          bad <- badClient.gitTree("alice", "api", "bad-sha", GitTreeParams.default).either
          notFound <- notFoundClient.gitTree("alice", "api", "missing-sha", GitTreeParams.default).either
        yield assertTrue(
          bad == Left(GiteaError.BadRequest("invalid tree sha", badBody)),
          notFound == Left(GiteaError.NotFound("tree not found", notFoundBody))
        )
      },
      test("retries Git tree lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(
                stringResponse(
                  """{"sha":"abc123","tree":[{"path":"README.md","type":"blob","sha":"file-sha"}],"truncated":false}"""
                )
              )
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.gitTree("alice", "api", "abc123", GitTreeParams.default).fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          tree <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          tree.sha.contains("abc123"),
          tree.tree.flatMap(_.headOption.flatMap(_.path)).contains("README.md"),
          remaining.isEmpty
        )
      },
      test("loads a Git blob through the ReposApi gitBlob method") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/blobs/blob%2Fabc%20123" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "git",
                "blobs",
                "blob/abc 123"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """{"content":"SGVsbG8K","encoding":"base64","lfs_oid":"oid123","lfs_size":1024,"sha":"blob123","size":6,"url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/blobs/blob123"}"""
            )
          )
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(
          client
            .gitBlob("space owner", "repo/slash", "blob/abc 123")
            .map(blob => blob.content -> blob.encoding -> blob.lfsOid -> blob.lfsSize -> blob.sha -> blob.size)
        )(
          Assertion.equalTo(
            Some("SGVsbG8K") -> Some("base64") -> Some("oid123") -> Some(1024L) -> Some("blob123") -> Some(6L)
          )
        )
      },
      test("propagates Git blob documented 400 and 404 errors through the facade") {
        val badBody = """{"message":"invalid blob sha"}"""
        val notFoundBody = """{"message":"blob not found"}"""
        val badBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "blobs", "bad-sha"))
          ).thenRespond(ResponseStub.adjust(badBody, StatusCode.BadRequest))
        val notFoundBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "blobs", "missing-sha"))
          ).thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val badClient = GiteaClient.fromBackend(config, badBackend)
        val notFoundClient = GiteaClient.fromBackend(config, notFoundBackend)

        for
          bad <- badClient.gitBlob("alice", "api", "bad-sha").either
          notFound <- notFoundClient.gitBlob("alice", "api", "missing-sha").either
        yield assertTrue(
          bad == Left(GiteaError.BadRequest("invalid blob sha", badBody)),
          notFound == Left(GiteaError.NotFound("blob not found", notFoundBody))
        )
      },
      test("retries Git blob lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(
                stringResponse(
                  """{"content":"UmV0cmllZAo=","encoding":"base64","sha":"blob123","size":8}"""
                )
              )
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.gitBlob("alice", "api", "blob123").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          blob <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          blob.content.contains("UmV0cmllZAo="),
          blob.sha.contains("blob123"),
          remaining.isEmpty
        )
      },
      test("loads an annotated Git tag through the ReposApi annotatedTag method") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/tags/tag%2Fabc%20123" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "git",
                "tags",
                "tag/abc 123"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """{"message":"Release 1.0\n","object":{"sha":"commit123","type":"commit","url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/commits/commit123"},"sha":"tag123","tag":"v1.0.0","tagger":{"date":"2026-06-01T12:34:56Z","email":"tagger@example.com","name":"Tagger"},"url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/tags/tag123","verification":{"verified":true,"reason":"gpg","signature":"sig","payload":"payload","signer":{"name":"Tagger","email":"tagger@example.com","username":"tagger"}}}"""
            )
          )
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(
          client
            .annotatedTag("space owner", "repo/slash", "tag/abc 123")
            .map(tag =>
              tag.message ->
                tag.gitObject.flatMap(_.sha) ->
                tag.gitObject.flatMap(_.`type`) ->
                tag.tag ->
                tag.tagger.flatMap(_.name) ->
                tag.verification.flatMap(_.verified) ->
                tag.verification.flatMap(_.signer.flatMap(_.username))
            )
        )(
          Assertion.equalTo(
            Some("Release 1.0\n") ->
              Some("commit123") ->
              Some("commit") ->
              Some("v1.0.0") ->
              Some("Tagger") ->
              Some(true) ->
              Some("tagger")
          )
        )
      },
      test("propagates annotated Git tag documented 400 and 404 errors through the facade") {
        val badBody = """{"message":"invalid tag ref"}"""
        val notFoundBody = """{"message":"tag not found"}"""
        val badBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "tags", "bad-sha"))
          ).thenRespond(ResponseStub.adjust(badBody, StatusCode.BadRequest))
        val notFoundBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "tags", "missing-sha"))
          ).thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val badClient = GiteaClient.fromBackend(config, badBackend)
        val notFoundClient = GiteaClient.fromBackend(config, notFoundBackend)

        for
          bad <- badClient.annotatedTag("alice", "api", "bad-sha").either
          notFound <- notFoundClient.annotatedTag("alice", "api", "missing-sha").either
        yield assertTrue(
          bad == Left(GiteaError.BadRequest("invalid tag ref", badBody)),
          notFound == Left(GiteaError.NotFound("tag not found", notFoundBody))
        )
      },
      test("retries annotated Git tag lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(
                stringResponse(
                  """{"message":"Retried tag","object":{"sha":"commit123","type":"commit"},"sha":"tag123","tag":"v1.0.0"}"""
                )
              )
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.annotatedTag("alice", "api", "tag123").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          tag <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          tag.message.contains("Retried tag"),
          tag.gitObject.flatMap(_.sha).contains("commit123"),
          tag.tag.contains("v1.0.0"),
          remaining.isEmpty
        )
      },
      test("loads all Git refs through the ReposApi gitRefs method") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/refs" &&
              request.uri.path == List("api", "v1", "repos", "space owner", "repo/slash", "git", "refs") &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """[{"ref":"refs/heads/main","url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/refs/heads/main","object":{"sha":"abc123","type":"commit","url":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/commits/abc123"}}]"""
            )
          )
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(
          client
            .gitRefs("space owner", "repo/slash")
            .map(refs =>
              refs.map(ref => ref.ref -> ref.gitObject.flatMap(_.sha) -> ref.gitObject.flatMap(_.`type`))
            )
        )(
          Assertion.equalTo(Chunk(Some("refs/heads/main") -> Some("abc123") -> Some("commit")))
        )
      },
      test("loads filtered Git refs through the ReposApi gitRefs method with an encoded slash ref") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/refs/heads%2Fmain" &&
              request.uri.path ==
                List("api", "v1", "repos", "space owner", "repo/slash", "git", "refs", "heads/main") &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """[{"ref":"refs/heads/main","object":{"sha":"abc123","type":"commit"}}]"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client
            .gitRefs("space owner", "repo/slash", "heads/main")
            .map(refs => refs.map(ref => ref.ref -> ref.gitObject.flatMap(_.sha)))
        )(
          Assertion.equalTo(Chunk(Some("refs/heads/main") -> Some("abc123")))
        )
      },
      test("propagates Git refs documented 404 errors through the facade") {
        val allRefsBody = """{"message":"repository not found"}"""
        val filteredRefsBody = """{"message":"reference not found"}"""
        val allRefsBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "missing", "git", "refs"))
          ).thenRespond(ResponseStub.adjust(allRefsBody, StatusCode.NotFound))
        val filteredRefsBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "refs", "heads/main"))
          ).thenRespond(ResponseStub.adjust(filteredRefsBody, StatusCode.NotFound))
        val allRefsClient = GiteaClient.fromBackend(config, allRefsBackend)
        val filteredRefsClient = GiteaClient.fromBackend(config, filteredRefsBackend)

        for
          allRefs <- allRefsClient.gitRefs("alice", "missing").either
          filteredRefs <- filteredRefsClient.gitRefs("alice", "api", "heads/main").either
        yield assertTrue(
          allRefs == Left(GiteaError.NotFound("repository not found", allRefsBody)),
          filteredRefs == Left(GiteaError.NotFound("reference not found", filteredRefsBody))
        )
      },
      test("retries Git refs lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(
                stringResponse(
                  """[{"ref":"refs/heads/main","object":{"sha":"abc123","type":"commit"}}]"""
                )
              )
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.gitRefs("alice", "api", "heads/main").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          refs <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          refs.map(_.ref) == Chunk(Some("refs/heads/main")),
          refs.headOption.flatMap(_.gitObject.flatMap(_.sha)).contains("abc123"),
          remaining.isEmpty
        )
      },
      test("lists repository root contents through the ReposApi contents method") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/contents" &&
              request.uri.path == List("api", "v1", "repos", "space owner", "repo/slash", "contents") &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """[{"name":"README.md","path":"README.md","sha":"abc123","size":42,"type":"file","encoding":"base64","content":"IyBSZWFkbWU=","_links":{"self":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/contents/README.md","git":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/blobs/abc123","html":"https://gitea.example/space%20owner/repo%2Fslash/src/branch/main/README.md"}}]"""
            )
          )
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(
          client
            .contents("space owner", "repo/slash", ContentsParams.default)
            .map(contents => contents.map(item => item.name -> item.path -> item.content -> item.links.flatMap(_.self)))
        )(
          Assertion.equalTo(
            Chunk(
              Some("README.md") -> Some("README.md") -> Some("IyBSZWFkbWU=") ->
                Some("https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/contents/README.md")
            )
          )
        )
      },
      test("loads repository contents for a slash-containing filepath") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/contents/docs%2Freadme.md" &&
              request.uri.path ==
                List("api", "v1", "repos", "space owner", "repo/slash", "contents", "docs/readme.md") &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(
            ResponseStub.adjust(
              """{"name":"readme.md","path":"docs/readme.md","sha":"def456","size":128,"type":"file","encoding":"base64","content":"SGVsbG8=","download_url":"https://gitea.example/space%20owner/repo%2Fslash/raw/branch/main/docs/readme.md","_links":{"self":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/contents/docs/readme.md","git":"https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/blobs/def456","html":"https://gitea.example/space%20owner/repo%2Fslash/src/branch/main/docs/readme.md"}}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client
            .contents("space owner", "repo/slash", "docs/readme.md", ContentsParams.default)
            .map(content => content.name -> content.path -> content.downloadUrl -> content.links.flatMap(_.git))
        )(
          Assertion.equalTo(
            Some("readme.md") -> Some("docs/readme.md") ->
              Some("https://gitea.example/space%20owner/repo%2Fslash/raw/branch/main/docs/readme.md") ->
              Some("https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/git/blobs/def456")
          )
        )
      },
      test("forwards repository contents ref query parameters through the ReposApi contents methods") {
        val listBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "contents")) &&
              request.uri.paramsMap.get("ref").contains("release/1.0")
          }.thenRespond(ResponseStub.adjust("""[{"name":"docs","path":"docs","type":"dir"}]"""))
        val fileBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "contents", "docs/readme.md")) &&
              request.uri.paramsMap.get("ref").contains("release/1.0")
          }.thenRespond(ResponseStub.adjust("""{"name":"readme.md","path":"docs/readme.md","type":"file"}"""))
        val listClient = GiteaClient.fromBackend(config, listBackend)
        val fileClient = GiteaClient.fromBackend(config, fileBackend)
        val params = ContentsParams(ref = Some("release/1.0"))

        for
          rootContents <- listClient.contents("alice", "api", params)
          fileContent <- fileClient.contents("alice", "api", "docs/readme.md", params)
        yield assertTrue(
          rootContents.map(_.path) == Chunk(Some("docs")),
          fileContent.path.contains("docs/readme.md")
        )
      },
      test("propagates repository contents documented 404 errors through the facade") {
        val rootBody = """{"message":"repository contents not found"}"""
        val fileBody = """{"message":"file contents not found"}"""
        val rootBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "missing", "contents"))
          ).thenRespond(ResponseStub.adjust(rootBody, StatusCode.NotFound))
        val fileBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "contents", "docs/readme.md"))
          ).thenRespond(ResponseStub.adjust(fileBody, StatusCode.NotFound))
        val rootClient = GiteaClient.fromBackend(config, rootBackend)
        val fileClient = GiteaClient.fromBackend(config, fileBackend)

        for
          rootContents <- rootClient.contents("alice", "missing", ContentsParams.default).either
          fileContents <- fileClient.contents("alice", "api", "docs/readme.md", ContentsParams.default).either
        yield assertTrue(
          rootContents == Left(GiteaError.NotFound("repository contents not found", rootBody)),
          fileContents == Left(GiteaError.NotFound("file contents not found", fileBody))
        )
      },
      test("retries repository contents lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(
                stringResponse(
                  """{"name":"readme.md","path":"docs/readme.md","sha":"def456","type":"file","content":"SGVsbG8="}"""
                )
              )
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.contents("alice", "api", "docs/readme.md", ContentsParams.default).fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          content <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          content.path.contains("docs/readme.md"),
          content.content.contains("SGVsbG8="),
          remaining.isEmpty
        )
      },
      test("downloads raw and media repository files as bytes through the ReposApi facade") {
        val rawBytes = Array[Byte](0, 1, 2, -1, 65, 10)
        val mediaBytes = Array[Byte](10, 20, 30, -1)
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val rawBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/raw/docs%2Freadme.md?ref=release/1.0" &&
              request.uri.path ==
                List("api", "v1", "repos", "space owner", "repo/slash", "raw", "docs/readme.md") &&
              request.uri.paramsMap.get("ref").contains("release/1.0") &&
              request.header("Accept").contains("application/octet-stream") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(ResponseStub.adjust(rawBytes))
        val mediaBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/media/docs%2Freadme.md?ref=release/1.0" &&
              request.uri.path ==
                List("api", "v1", "repos", "space owner", "repo/slash", "media", "docs/readme.md") &&
              request.uri.paramsMap.get("ref").contains("release/1.0") &&
              request.header("Accept").contains("application/octet-stream") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321") &&
              request.header("Content-Type").isEmpty &&
              request.body == NoBody
          }.thenRespond(ResponseStub.adjust(mediaBytes))
        val rawClient = GiteaClient.fromBackend(facadeConfig, rawBackend)
        val mediaClient = GiteaClient.fromBackend(facadeConfig, mediaBackend)
        val params = ContentsParams(ref = Some("release/1.0"))

        for
          raw <- rawClient.rawFile("space owner", "repo/slash", "docs/readme.md", params)
          media <- mediaClient.mediaFile("space owner", "repo/slash", "docs/readme.md", params)
        yield assertTrue(
          raw == Chunk.fromArray(rawBytes),
          media == Chunk.fromArray(mediaBytes)
        )
      },
      test("propagates raw and media repository file documented 404 errors through the facade") {
        val rawBody = """{"message":"raw file not found"}"""
        val mediaBody = """{"message":"media file not found"}"""
        val rawBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "raw", "docs/readme.md"))
          ).thenRespond(
            ResponseStub.adjust(rawBody.getBytes(java.nio.charset.StandardCharsets.UTF_8), StatusCode.NotFound)
          )
        val mediaBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "media", "docs/readme.md"))
          ).thenRespond(
            ResponseStub.adjust(mediaBody.getBytes(java.nio.charset.StandardCharsets.UTF_8), StatusCode.NotFound)
          )
        val rawClient = GiteaClient.fromBackend(config, rawBackend)
        val mediaClient = GiteaClient.fromBackend(config, mediaBackend)

        for
          raw <- rawClient.rawFile("alice", "api", "docs/readme.md").either
          media <- mediaClient.mediaFile("alice", "api", "docs/readme.md").either
        yield assertTrue(
          raw == Left(GiteaError.NotFound("raw file not found", rawBody)),
          media == Left(GiteaError.NotFound("media file not found", mediaBody))
        )
      },
      test("retries raw and media repository file downloads because they are read-only GET requests") {
        val rawBytes = Array[Byte](0, 1, 2, -1, 65, 10)
        val mediaBytes = Array[Byte](10, 20, 30, -1)
        val unavailable = """{"message":"temporarily unavailable"}""".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        val rawBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "raw", "docs/readme.md")) &&
              request.header("Accept").contains("application/octet-stream")
          }.thenRespondCyclic(
            ResponseStub.adjust(unavailable, StatusCode.ServiceUnavailable),
            ResponseStub.adjust(rawBytes)
          )
        val mediaBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "media", "docs/readme.md")) &&
              request.header("Accept").contains("application/octet-stream")
          }.thenRespondCyclic(
            ResponseStub.adjust(unavailable, StatusCode.ServiceUnavailable),
            ResponseStub.adjust(mediaBytes)
          )
        val rawClient = GiteaClient.fromBackend(config.copy(maxRetries = 1), rawBackend)
        val mediaClient = GiteaClient.fromBackend(config.copy(maxRetries = 1), mediaBackend)

        for
          rawFiber <- rawClient.rawFile("alice", "api", "docs/readme.md").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          raw <- rawFiber.join
          mediaFiber <- mediaClient.mediaFile("alice", "api", "docs/readme.md").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          media <- mediaFiber.join
        yield assertTrue(
          raw == Chunk.fromArray(rawBytes),
          media == Chunk.fromArray(mediaBytes)
        )
      },
      test("downloads archive path values as bytes through the ReposApi facade") {
        val zipBytes = Array[Byte](80, 75, 3, 4, 20, 0)
        val tarBytes = Array[Byte](31, -117, 8, 0, 0, 0, 0, 0)
        val pathBytes = Array[Byte](80, 75, 6, 6)
        val zipBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "myrepo", "archive", "main.zip")) &&
              request.uri.paramsSeq.isEmpty &&
              request.header("Accept").contains("application/octet-stream") &&
              request.body == NoBody
          }.thenRespond(ResponseStub.adjust(zipBytes))
        val tarBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "myrepo", "archive", "v1.0.0.tar.gz")) &&
              request.uri.paramsSeq.isEmpty &&
              request.header("Accept").contains("application/octet-stream") &&
              request.body == NoBody
          }.thenRespond(ResponseStub.adjust(tarBytes))
        val pathBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "myrepo", "archive", "main.zip")) &&
              request.uri.paramsSeq == Seq("path" -> "src", "path" -> "docs/readme.md") &&
              request.header("Accept").contains("application/octet-stream") &&
              request.body == NoBody
          }.thenRespond(ResponseStub.adjust(pathBytes))
        val zipClient = GiteaClient.fromBackend(config, zipBackend)
        val tarClient = GiteaClient.fromBackend(config, tarBackend)
        val pathClient = GiteaClient.fromBackend(config, pathBackend)

        for
          zip <- zipClient.archive("alice", "myrepo", "main.zip")
          tar <- tarClient.archive("alice", "myrepo", "v1.0.0.tar.gz")
          path <- pathClient.archive(
            "alice",
            "myrepo",
            "main.zip",
            ArchiveParams(path = Chunk("src", "docs/readme.md"))
          )
        yield assertTrue(
          zip == Chunk.fromArray(zipBytes),
          tar == Chunk.fromArray(tarBytes),
          path == Chunk.fromArray(pathBytes)
        )
      },
      test("propagates archive documented 404 error through the facade") {
        val body = """{"message":"archive not found"}"""
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "archive", "main.zip"))
          ).thenRespond(
            ResponseStub.adjust(body.getBytes(java.nio.charset.StandardCharsets.UTF_8), StatusCode.NotFound)
          )
        val client = GiteaClient.fromBackend(config, backend)

        for result <- client.archive("alice", "api", "main.zip").either
        yield assertTrue(result == Left(GiteaError.NotFound("archive not found", body)))
      },
      test("retries archive download because it is a read-only GET request") {
        val bytes = Array[Byte](80, 75, 3, 4, 20, 0)
        val unavailable = """{"message":"temporarily unavailable"}""".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "archive", "main.zip")) &&
              request.header("Accept").contains("application/octet-stream")
          }.thenRespondCyclic(
            ResponseStub.adjust(unavailable, StatusCode.ServiceUnavailable),
            ResponseStub.adjust(bytes)
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.archive("alice", "api", "main.zip").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          result <- fiber.join
        yield assertTrue(result == Chunk.fromArray(bytes))
      },
      test("loads a repository commit through a stub without live environment credentials") {
        val hermeticConfig =
          GiteaConfig.default(uri"https://gitea.example", Auth.Anonymous).copy(pageSize = 1)
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "git", "commits", "abc123")) &&
              request.header("Authorization").isEmpty
          }.thenRespond(ResponseStub.adjust("""{"sha":"abc123"}"""))
        val client = GiteaClient.fromBackend(hermeticConfig, backend)

        assertZIO(client.commit("alice", "api", "abc123").map(_.sha))(
          Assertion.equalTo(Some("abc123"))
        )
      },
      test("loads an issue through the IssuesApi get method") {
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "owner", "repo", "issues", "7")))
            .thenRespond(ResponseStub.adjust("""{"id":17,"number":7,"title":"tracked"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.get("owner", "repo", 7).map(issue => issue.id -> issue.number -> issue.title))(
          Assertion.equalTo(Some(17L) -> Some(7L) -> Some("tracked"))
        )
      },
      test("lists pinned issues through the IssuesApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "pinned"))
          }.thenRespond(ResponseStub.adjust("""[{"id":17,"number":7,"title":"pinned"}]"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.pinned("owner", "repo").map(_.headOption.flatMap(_.title)))(
          Assertion.equalTo(Some("pinned"))
        )
      },
      test("creates an issue through the IssuesApi create method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.POST &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""title":"Created"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":18,"number":8,"title":"Created"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.create("owner", "repo", CreateIssue(title = "Created")).map(_.number))(
          Assertion.equalTo(Some(8L))
        )
      },
      test("deletes an issue through the IssuesApi delete method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.DELETE &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8"))
          }.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.delete("owner", "repo", 8).either)(
          Assertion.equalTo(Right(()))
        )
      },
      test("manages issue pins through the IssuesApi pin methods") {
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "pin"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "pin", "2"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "pin"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          pinned <- client.pin("owner", "repo", 8).either
          moved <- client.movePin("owner", "repo", 8, 2).either
          unpinned <- client.unpin("owner", "repo", 8).either
        yield assertTrue(
          pinned == Right(()),
          moved == Right(()),
          unpinned == Right(())
        )
      },
      test("edits an issue through the IssuesApi edit method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.PATCH &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""title":"Retitle"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":18,"number":8,"title":"Retitle"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.edit("owner", "repo", 8, EditIssue(title = Some("Retitle"))).map(_.title))(
          Assertion.equalTo(Some("Retitle"))
        )
      },
      test("closes an issue through the IssuesApi close helper") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.PATCH &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""state":"closed"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":18,"number":8,"state":"closed"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.close("owner", "repo", 8).map(_.state.map(_.jsonValue)))(
          Assertion.equalTo(Some("closed"))
        )
      },
      test("adds an issue comment through the IssuesApi comment method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.POST &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "comments")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""body":"Looks good"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"id":30,"body":"Looks good"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.comment("owner", "repo", 8, "Looks good").map(comment => comment.id -> comment.body))(
          Assertion.equalTo(Some(30L) -> Some("Looks good"))
        )
      },
      test("lists, loads, edits, and deletes issue comments through the IssuesApi comment methods") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "comments")) &&
                request.uri.paramsMap.get("since").contains("2026-06-01T00:00:00Z")
            }
            .thenRespond(ResponseStub.adjust("""[{"id":30,"body":"First"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments")) &&
                request.uri.paramsMap.contains("page")
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":30,"body":"First repo"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":31,"body":"Second repo"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments", "30"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":30,"body":"First"}"""))
            .whenRequestMatches { request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments", "30")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""body":"Updated"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":30,"body":"Updated"}"""))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments", "30"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          issueComments <- client.comments(
            "owner",
            "repo",
            8,
            IssueCommentListParams(since = Some(Instant.parse("2026-06-01T00:00:00Z")))
          )
          repoComments <- client
            .repositoryComments("owner", "repo", RepositoryCommentListParams(limit = Some(1)))
            .runCollect
          loaded <- client.comment("owner", "repo", 30)
          edited <- client.editComment("owner", "repo", 30, EditIssueComment("Updated"))
          deleted <- client.deleteComment("owner", "repo", 30).either
        yield assertTrue(
          issueComments.map(_.body) == Chunk(Some("First")),
          repoComments.map(_.id) == Chunk(Some(30L), Some(31L)),
          loaded.body.contains("First"),
          edited.body.contains("Updated"),
          deleted == Right(())
        )
      },
      test("manages issue and comment reactions through the IssuesApi methods") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "reactions"))
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"content":"+1","user":{"id":1,"login":"alice"}}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"content":"heart","user":{"id":2,"login":"bob"}}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "reactions")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""content":"+1"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"content":"+1"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "reactions")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""content":"+1"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments", "30", "reactions"))
            }
            .thenRespond(ResponseStub.adjust("""[{"content":"eyes","user":{"id":3,"login":"carol"}}]"""))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments", "30", "reactions")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""content":"eyes"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"content":"eyes"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "comments", "30", "reactions")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""content":"eyes"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          issueReactions <- client.reactions("owner", "repo", 8).runCollect
          issueReaction <- client.react("owner", "repo", 8, EditReactionOption("+1"))
          issueReactionDeleted <- client.deleteReaction("owner", "repo", 8, EditReactionOption("+1")).either
          commentReactions <- client.commentReactions("owner", "repo", 30)
          commentReaction <- client.reactToComment("owner", "repo", 30, EditReactionOption("eyes"))
          commentReactionDeleted <- client.deleteCommentReaction(
            "owner",
            "repo",
            30,
            EditReactionOption("eyes")
          ).either
        yield assertTrue(
          issueReactions.map(_.content) == Chunk(Some("+1"), Some("heart")),
          issueReaction.content.contains("+1"),
          issueReactionDeleted == Right(()),
          commentReactions.map(_.content) == Chunk(Some("eyes")),
          commentReaction.content.contains("eyes"),
          commentReactionDeleted == Right(())
        )
      },
      test("manages issue subscriptions through the IssuesApi methods") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "subscriptions"))
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"alice"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":2,"login":"bob"}]""", StatusCode.Ok, headers)
            )
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "subscriptions", "check"))
            }
            .thenRespond(ResponseStub.adjust("""{"subscribed":true,"ignored":false}"""))
            .whenRequestMatches { request =>
              request.method == Method.PUT &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "subscriptions", "alice"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "subscriptions", "alice"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Ok))
        val client = GiteaClient.fromBackend(config, backend)

        for
          subscribers <- client.subscribers("owner", "repo", 8).runCollect
          watch <- client.subscription("owner", "repo", 8)
          subscribed <- client.subscribe("owner", "repo", 8, "alice").either
          unsubscribed <- client.unsubscribe("owner", "repo", 8, "alice").either
        yield assertTrue(
          subscribers.map(_.login) == Chunk(Some("alice"), Some("bob")),
          watch.subscribed.contains(true),
          subscribed == Right(()),
          unsubscribed == Right(())
        )
      },
      test("manages issue tracked times through the IssuesApi methods") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "times")) &&
                request.uri.paramsMap.get("user").contains("octo")
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":44,"time":3600,"user_name":"octo"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":45,"time":1800,"user_name":"octo"}]""", StatusCode.Ok, headers)
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "times")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""time":900""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":46,"time":900,"user_name":"octo"}"""))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "times", "44"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "times"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          times <- client
            .trackedTimes("owner", "repo", 8, IssueTrackedTimeListParams(user = Some("octo")))
            .runCollect
          added <- client.addTrackedTime("owner", "repo", 8, AddTimeOption(time = 900L))
          deleted <- client.deleteTrackedTime("owner", "repo", 8, 44).either
          reset <- client.resetTrackedTime("owner", "repo", 8).either
        yield assertTrue(
          times.map(_.id) == Chunk(Some(44L), Some(45L)),
          times.map(_.time) == Chunk(Some(3600L), Some(1800L)),
          added.time.contains(900L),
          deleted == Right(()),
          reset == Right(())
        )
      },
      test("manages issue stopwatches through the IssuesApi and UsersApi methods") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("user", "stopwatches"))
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"issue_index":8,"repo_name":"repo","seconds":60}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"issue_index":9,"repo_name":"repo","seconds":120}]""", StatusCode.Ok, headers)
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "stopwatch", "start"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "stopwatch", "stop"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "stopwatch", "delete"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          stopwatches <- client.stopwatches.runCollect
          started <- client.startStopwatch("owner", "repo", 8).either
          stopped <- client.stopStopwatch("owner", "repo", 8).either
          deleted <- client.deleteStopwatch("owner", "repo", 8).either
        yield assertTrue(
          stopwatches.map(_.issueIndex) == Chunk(Some(8L), Some(9L)),
          stopwatches.map(_.seconds) == Chunk(Some(60L), Some(120L)),
          started == Right(()),
          stopped == Right(()),
          deleted == Right(())
        )
      },
      test("manages issue labels through the IssuesApi label methods") {
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels"))
            }
            .thenRespond(ResponseStub.adjust("""[{"id":1,"name":"kind/api"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.PUT &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""labels":[1,2]""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""[{"id":1,"name":"kind/api"},{"id":2,"name":"status/ready"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""labels":[3]""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""[{"id":3,"name":"priority/high"}]"""))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels", "3"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "labels"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          existing <- client.labels("owner", "repo", 8)
          replaced <- client.replaceLabels("owner", "repo", 8, Chunk(1L, 2L))
          added <- client.addLabels("owner", "repo", 8, Chunk(3L))
          removed <- client.removeLabel("owner", "repo", 8, 3).either
          cleared <- client.clearLabels("owner", "repo", 8).either
        yield assertTrue(
          existing.map(_.name) == Chunk(Some("kind/api")),
          replaced.map(_.id) == Chunk(Some(1L), Some(2L)),
          added.map(_.name) == Chunk(Some("priority/high")),
          removed == Right(()),
          cleared == Right(())
        )
      },
      test("locks and unlocks an issue through the IssuesApi lock methods") {
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.PUT &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "lock")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""lock_reason":"resolved"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "lock"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
        val client = GiteaClient.fromBackend(config, backend)

        for
          locked <- client.lock("owner", "repo", 8, LockIssueOption(lockReason = Some("resolved"))).either
          unlocked <- client.unlock("owner", "repo", 8).either
        yield assertTrue(
          locked == Right(()),
          unlocked == Right(())
        )
      },
      test("edits an issue deadline through the IssuesApi deadline method") {
        val due = Instant.parse("2026-07-03T00:00:00Z")
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.POST &&
              request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "deadline")) &&
              (request.body match
                case StringBody(body, _, _) => body.contains(""""due_date":"2026-07-03T00:00:00Z"""")
                case _ => false)
          }.thenRespond(ResponseStub.adjust("""{"due_date":"2026-07-03T00:00:00Z"}""", StatusCode.Created))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client.editDeadline("owner", "repo", 8, EditDeadlineOption(dueDate = Some(due))).map(_.dueDate)
        )(Assertion.equalTo(Some(due)))
      },
      test("manages issue blocking relationships through the IssuesApi methods") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "blocks"))
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":13,"number":13,"title":"Blocked first"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":14,"number":14,"title":"Blocked second"}]""", StatusCode.Ok, headers)
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "blocks")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""index":13""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":8,"number":8,"title":"Root"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "blocks")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""index":13""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":8,"number":8,"title":"Root"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          blocked <- client.blocks("owner", "repo", 8).runCollect
          afterBlock <- client.block("owner", "repo", 8, IssueMeta(index = 13L))
          afterUnblock <- client.unblock("owner", "repo", 8, IssueMeta(index = 13L))
        yield assertTrue(
          blocked.map(_.number) == Chunk(Some(13L), Some(14L)),
          afterBlock.number.contains(8L),
          afterUnblock.title.contains("Root")
        )
      },
      test("manages issue dependencies through the IssuesApi methods") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "dependencies"))
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":21,"number":21,"title":"Dependency first"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":22,"number":22,"title":"Dependency second"}]""", StatusCode.Ok, headers)
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "dependencies")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""index":21""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":8,"number":8,"title":"Root"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "owner", "repo", "issues", "8", "dependencies")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""index":21""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":8,"number":8,"title":"Root"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          dependencies <- client.dependencies("owner", "repo", 8).runCollect
          afterAdd <- client.addDependency("owner", "repo", 8, IssueMeta(index = 21L))
          afterRemove <- client.removeDependency("owner", "repo", 8, IssueMeta(index = 21L))
        yield assertTrue(
          dependencies.map(_.number) == Chunk(Some(21L), Some(22L)),
          afterAdd.number.contains(8L),
          afterRemove.title.contains("Root")
        )
      },
      test("loads an organization through the OrgsApi facade") {
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform")))
            .thenRespond(ResponseStub.adjust("""{"id":9,"name":"platform","full_name":"Platform Team"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.orgs.get("platform").map(org => org.id -> org.name -> org.fullName))(
          Assertion.equalTo(Some(9L) -> Some("platform") -> Some("Platform Team"))
        )
      },
      test("streams organization members across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "members")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"alice"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":2,"login":"bob"}]""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.orgs.members("platform").runCollect.map { users =>
          assertTrue(users.map(_.login) == Chunk(Some("alice"), Some("bob")))
        }
      },
      test("streams public organization members across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "public_members")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"alice"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":2,"login":"bob"}]""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.orgs.publicMembers("platform").runCollect.map { users =>
          assertTrue(users.map(_.login) == Chunk(Some("alice"), Some("bob")))
        }
      },
      test("streams organization repositories across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("orgs", "platform", "repos")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":10,"name":"api"}]""", StatusCode.Ok, headers),
              ResponseStub.adjust("""[{"id":11,"name":"client"}]""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.orgs.repos("platform").runCollect.map { repos =>
          assertTrue(repos.map(_.name) == Chunk(Some("api"), Some("client")))
        }
      },
      test("returns decode failures as GiteaError") {
        val backend =
          taskStub.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"id":"not-a-number"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.me.either)(
          Assertion.isLeft(Assertion.isSubtype[GiteaError.DecodeError](Assertion.anything))
        )
      },
      test("maps backend failures to transport errors") {
        val failure = RuntimeException("connection refused")
        val backend = taskStub.whenAnyRequest.thenThrow(failure)
        val client = GiteaClient.fromBackend(config, backend)

        client.me.either.map { result =>
          assertTrue(
            result.left.exists {
              case GiteaError.TransportError(cause) => cause eq failure
              case _ => false
            }
          )
        }
      },
      test("retries transport failures for read-only requests") {
        val failure = RuntimeException("connection reset")

        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.fail(failure),
              ZIO.succeed(stringResponse("""{"id":42,"login":"retry"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.me.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          user <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          user.login.contains("retry"),
          remaining.isEmpty
        )
      },
      test("retries selected 5xx responses for read-only requests") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(stringResponse("""{"id":43,"login":"server-retry"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.me.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          user <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          user.login.contains("server-retry"),
          remaining.isEmpty
        )
      },
      test("uses rate-limit reset headers for retry delay") {
        val now = Instant.parse("2030-01-01T00:00:00Z")
        val resetAt = now.plusSeconds(2)

        for
          _ <- TestClock.setTime(now)
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"rate limited"}""",
                  StatusCode.TooManyRequests,
                  List(Header("x-ratelimit-reset", resetAt.getEpochSecond.toString))
                )
              ),
              ZIO.succeed(stringResponse("""{"id":44,"login":"limited"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.me.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          early <- fiber.poll
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          user <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          early.isEmpty,
          user.login.contains("limited"),
          remaining.isEmpty
        )
      },
      test("does not retry requests marked non-retryable") {
        val failure = RuntimeException("connection reset")

        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.fail(failure),
              ZIO.succeed(stringResponse("""{"id":45,"login":"unused"}"""))
            )
          )
          executor = GiteaRequestExecutor(ScriptedBackend(responses), maxRetries = 2)
          result <- executor.send(GiteaRequests.currentUser(config).copy(retryable = false)).either
          remaining <- responses.get
        yield assertTrue(
          result.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          remaining.size == 1
        )
      },
      test("streams all issues from multiple pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenAnyRequest.thenRespondCyclic(
            ResponseStub.adjust("""[{"id":1,"number":1,"title":"first"}]""", StatusCode.Ok, headers),
            ResponseStub.adjust("""[{"id":2,"number":2,"title":"second"}]""", StatusCode.Ok, headers)
          )
        val client = GiteaClient.fromBackend(config, backend)

        client.list("owner", "repo", IssueListParams.default).runCollect.map { issues =>
          assertTrue(issues.map(_.number) == Chunk(Some(1L), Some(2L)))
        }
      },
      test("streams user repositories and collects repository topics across pages") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "alice", "repos")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":10,"name":"api"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":11,"name":"client"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "topics")))
            .thenRespondCyclic(
              ResponseStub.adjust("""{"topics":["scala"]}""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""{"topics":["zio"]}""", StatusCode.Ok, twoPageHeaders)
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          repos <- client.list("alice", RepoListParams.default).runCollect
          topics <- client.topics("alice", "api")
        yield assertTrue(
          repos.map(_.name) == Chunk(Some("api"), Some("client")),
          topics == Chunk("scala", "zio")
        )
      },
      test("streams repository branches and tags across pages") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "branches")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"name":"main"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"name":"release"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "tags")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"name":"v1.0.0"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"name":"v1.1.0"}]""", StatusCode.Ok, twoPageHeaders)
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          branches <- client.branches("alice", "api").runCollect
          tags <- client.tags("alice", "api").runCollect
        yield assertTrue(
          branches.map(_.name) == Chunk(Some("main"), Some("release")),
          tags.map(_.name) == Chunk(Some("v1.0.0"), Some("v1.1.0"))
        )
      },
      test("loads repository language statistics through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "languages")) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("Content-Type").isEmpty
          }.thenRespond(ResponseStub.adjust("""{"Scala":1234,"Java":55}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.languages("alice", "api"))(
          Assertion.equalTo(LanguageStatistics(Map("Scala" -> 1234L, "Java" -> 55L)))
        )
      },
      test("propagates repository language documented not-found errors through the facade") {
        val body = """{"message":"repository not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "missing", "languages"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.languages("alice", "missing").either)(
          Assertion.equalTo(Left(GiteaError.NotFound("repository not found", body)))
        )
      },
      test("retries repository language lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "languages"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"Scala":321,"TypeScript":12}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.languages("alice", "api").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          languages <- fiber.join
        yield assertTrue(
          languages == LanguageStatistics(Map("Scala" -> 321L, "TypeScript" -> 12L))
        )
      },
      test("loads a single repository tag through the ReposApi tag method") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tags", "v1.0.0")) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """{"id":"abc123","name":"v1.0.0","message":"First stable","commit":{"sha":"commit123","url":"https://gitea.example/api/v1/repos/alice/api/git/commits/commit123"}}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.tag("alice", "api", "v1.0.0").map(tag => tag.id -> tag.name -> tag.message))(
          Assertion.equalTo(Some("abc123") -> Some("v1.0.0") -> Some("First stable"))
        )
      },
      test("routes slash-containing repository tags as one facade path segment") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/tags/release%2Fcandidate" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "tags",
                "release/candidate"
              ) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(ResponseStub.adjust("""{"id":"tag123","name":"release/candidate"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client.tag("space owner", "repo/slash", "release/candidate").map(_.name)
        )(Assertion.equalTo(Some("release/candidate")))
      },
      test("propagates repository tag documented not-found errors through the facade") {
        val body = """{"message":"tag not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tags", "missing"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.tag("alice", "api", "missing").either)(
          Assertion.equalTo(Left(GiteaError.NotFound("tag not found", body)))
        )
      },
      test("retries repository tag lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tags", "v1.0.0"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"id":"tag123","name":"v1.0.0","message":"Retried tag"}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.tag("alice", "api", "v1.0.0").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          tag <- fiber.join
        yield assertTrue(
          tag.id.contains("tag123"),
          tag.message.contains("Retried tag")
        )
      },
      test("loads repository tag protections as a non-paginated list through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tag_protections")) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("Content-Type").isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """[{"id":7,"name_pattern":"v*","whitelist_teams":["Maintainers"],"whitelist_usernames":["octo"]}]"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.tagProtections("alice", "api"))(
          Assertion.equalTo(
            Chunk(
              TagProtection(
                id = Some(7L),
                namePattern = Some("v*"),
                whitelistTeams = Some(List("Maintainers")),
                whitelistUsernames = Some(List("octo"))
              )
            )
          )
        )
      },
      test("loads a repository tag protection through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tag_protections", "7")) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """{"id":7,"name_pattern":"v*","created_at":"2026-06-01T00:00:00Z","updated_at":"2026-06-02T00:00:00Z"}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.tagProtection("alice", "api", 7))(
          Assertion.equalTo(
            TagProtection(
              createdAt = Some(Instant.parse("2026-06-01T00:00:00Z")),
              id = Some(7L),
              namePattern = Some("v*"),
              updatedAt = Some(Instant.parse("2026-06-02T00:00:00Z"))
            )
          )
        )
      },
      test("propagates repository tag-protection documented not-found errors through the facade") {
        val body = """{"message":"tag protection not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tag_protections", "7"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.tagProtection("alice", "api", 7).either)(
          Assertion.equalTo(Left(GiteaError.NotFound("tag protection not found", body)))
        )
      },
      test("retries repository tag-protection requests because they are read-only GETs") {
        val listBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tag_protections")) &&
              request.uri.paramsMap.isEmpty
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""[{"id":7,"name_pattern":"v*"}]""")
          )
        val detailBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "tag_protections", "7")) &&
              request.uri.paramsMap.isEmpty
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"id":7,"name_pattern":"v*"}""")
          )
        val listClient = GiteaClient.fromBackend(config.copy(maxRetries = 1), listBackend)
        val detailClient = GiteaClient.fromBackend(config.copy(maxRetries = 1), detailBackend)

        for
          listFiber <- listClient.tagProtections("alice", "api").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          protections <- listFiber.join
          detailFiber <- detailClient.tagProtection("alice", "api", 7).fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          protection <- detailFiber.join
        yield assertTrue(
          protections.map(_.id) == Chunk(Some(7L)),
          protections.headOption.flatMap(_.namePattern).contains("v*"),
          protection.id.contains(7L),
          protection.namePattern.contains("v*")
        )
      },
      test("loads repository branch protections as a non-paginated list through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "branch_protections")) &&
              request.uri.paramsMap.isEmpty &&
              !request.uri.paramsMap.contains("page") &&
              !request.uri.paramsMap.contains("limit") &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("Content-Type").isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """[{"rule_name":"main","required_approvals":2,"status_check_contexts":["ci/test"]}]"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.branchProtections("alice", "api"))(
          Assertion.equalTo(
            Chunk(
              BranchProtection(
                ruleName = Some("main"),
                requiredApprovals = Some(2L),
                statusCheckContexts = Some(List("ci/test"))
              )
            )
          )
        )
      },
      test("loads a repository branch protection with a slash-containing name through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/branch_protections/release%2F2026" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "branch_protections",
                "release/2026"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Content-Type").isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """{"rule_name":"release/2026","enable_push":true,"created_at":"2026-06-01T00:00:00Z","updated_at":"2026-06-02T00:00:00Z"}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.branchProtection("space owner", "repo/slash", "release/2026"))(
          Assertion.equalTo(
            BranchProtection(
              ruleName = Some("release/2026"),
              enablePush = Some(true),
              createdAt = Some(Instant.parse("2026-06-01T00:00:00Z")),
              updatedAt = Some(Instant.parse("2026-06-02T00:00:00Z"))
            )
          )
        )
      },
      test("propagates repository branch-protection documented not-found errors through the facade") {
        val body = """{"message":"branch protection not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "branch_protections", "release/2026"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.branchProtection("alice", "api", "release/2026").either)(
          Assertion.equalTo(Left(GiteaError.NotFound("branch protection not found", body)))
        )
      },
      test("retries repository branch-protection requests because they are read-only GETs") {
        val listBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "branch_protections")) &&
              request.uri.paramsMap.isEmpty
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""[{"rule_name":"main","required_approvals":2}]""")
          )
        val detailBackend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "branch_protections", "release/2026")) &&
              request.uri.paramsMap.isEmpty
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"rule_name":"release/2026","required_approvals":1}""")
          )
        val listClient = GiteaClient.fromBackend(config.copy(maxRetries = 1), listBackend)
        val detailClient = GiteaClient.fromBackend(config.copy(maxRetries = 1), detailBackend)

        for
          listFiber <- listClient.branchProtections("alice", "api").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          protections <- listFiber.join
          detailFiber <- detailClient.branchProtection("alice", "api", "release/2026").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          protection <- detailFiber.join
        yield assertTrue(
          protections.map(_.ruleName) == Chunk(Some("main")),
          protections.headOption.flatMap(_.requiredApprovals).contains(2L),
          protection.ruleName.contains("release/2026"),
          protection.requiredApprovals.contains(1L)
        )
      },
      test("loads and streams commit statuses through the ReposApi methods") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val listParams = CommitStatusListParams(
          sort = Some(CommitStatusSort.HighestIndex),
          state = Some(CommitStatusListState.Success)
        )
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "commits", "main", "status")) &&
                request.uri.paramsMap.get("page").contains("1") &&
                request.uri.paramsMap.get("limit").contains("1")
            }
            .thenRespond(
              ResponseStub.adjust(
                """{"sha":"abc123","state":"success","total_count":2,"statuses":[{"context":"ci","status":"success"}]}"""
              )
            )
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "commits", "main", "statuses")) &&
                request.uri.paramsMap.get("sort").contains("highestindex") &&
                request.uri.paramsMap.get("state").contains("success")
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"context":"ci","status":"success"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"context":"lint","status":"success"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "statuses", "abc123")) &&
                request.uri.paramsMap.get("sort").contains("highestindex") &&
                request.uri.paramsMap.get("state").contains("success")
            }
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"context":"build","status":"success"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"context":"docs","status":"success"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "statuses", "abc123")) &&
                (request.body match
                  case StringBody(body, _, _) =>
                    body.contains(""""state":"success"""") &&
                      body.contains(""""target_url":"https://ci.example/build/1"""")
                  case _ => false)
            }
            .thenRespond(
              ResponseStub.adjust(
                """{"context":"ci","status":"success","target_url":"https://ci.example/build/1"}""",
                StatusCode.Created
              )
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          combined <- client.combinedStatusByRef("alice", "api", "main")
          byRef <- client.statusesByRef("alice", "api", "main", listParams).runCollect
          bySha <- client.statuses("alice", "api", "abc123", listParams).runCollect
          created <- client.createStatus(
            "alice",
            "api",
            "abc123",
            CreateStatusOption(
              context = Some("ci"),
              state = Some(CommitStatusState.Success),
              targetUrl = Some("https://ci.example/build/1")
            )
          )
        yield assertTrue(
          combined.sha.contains("abc123"),
          combined.state.contains(CommitStatusState.Success),
          byRef.map(_.context) == Chunk(Some("ci"), Some("lint")),
          bySha.map(_.context) == Chunk(Some("build"), Some("docs")),
          created.state.contains(CommitStatusState.Success),
          created.targetUrl.contains("https://ci.example/build/1")
        )
      },
      test("sends custom combined commit status pagination through the ReposApi method") {
        val backend =
          taskStub
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "commits", "main", "status")) &&
                request.uri.paramsMap.get("page").contains("3") &&
                request.uri.paramsMap.get("limit").contains("7")
            }
            .thenRespond(
              ResponseStub.adjust(
                """{"sha":"abc123","state":"success","total_count":7,"statuses":[{"context":"page-3","status":"success"}]}"""
              )
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          combined <- client.combinedStatusByRef(
            "alice",
            "api",
            "main",
            CombinedStatusParams(page = Some(3), limit = Some(7))
          )
        yield assertTrue(
          combined.sha.contains("abc123"),
          combined.totalCount.contains(7L),
          combined.statuses.exists(_.exists(_.context.contains("page-3")))
        )
      },
      test("propagates commit status decode and transport errors through the facade") {
        val decodeBackend =
          taskStub.whenAnyRequest.thenRespond(ResponseStub.adjust("""{"sha":"abc123","state":"unknown"}"""))
        val failure = RuntimeException("connection refused")
        val transportBackend = taskStub.whenAnyRequest.thenThrow(failure)
        val decodeClient = GiteaClient.fromBackend(config, decodeBackend)
        val transportClient = GiteaClient.fromBackend(config, transportBackend)

        for
          decodeResult <- decodeClient.combinedStatusByRef("alice", "api", "main").either
          transportResult <- transportClient.statusesByRef("alice", "api", "main").runCollect.either
        yield assertTrue(
          decodeResult.left.exists(_.isInstanceOf[GiteaError.DecodeError]),
          transportResult.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          }
        )
      },
      test("does not retry create status because it is a non-retryable write") {
        val failure = RuntimeException("connection reset")

        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.fail(failure),
              ZIO.succeed(stringResponse("""{"context":"unused","status":"success"}""", StatusCode.Created))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 2), ScriptedBackend(responses))
          result <- client
            .createStatus(
              "alice",
              "api",
              "abc123",
              CreateStatusOption(context = Some("ci"), state = Some(CommitStatusState.Success))
            )
            .either
          remaining <- responses.get
        yield assertTrue(
          result.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          remaining.size == 1
        )
      },
      test("loads and streams repository releases") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "releases")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"tag_name":"v1.0.0"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":2,"tag_name":"v1.1.0"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "releases", "2")))
            .thenRespond(ResponseStub.adjust("""{"id":2,"tag_name":"v1.1.0","name":"Second"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        for
          releases <- client.releases("alice", "api").runCollect
          release <- client.release("alice", "api", 2)
        yield assertTrue(
          releases.map(_.tagName) == Chunk(Some("v1.0.0"), Some("v1.1.0")),
          release.id.contains(2L),
          release.name.contains("Second")
        )
      },
      test("starts release-list streams at page 1 while preserving filters and page size") {
        val twoPageHeaders = List(Header("x-total-count", "4"))
        val params =
          ReleaseListParams(draft = Some(true), preRelease = Some(false), page = Some(7), limit = Some(2))

        for
          seenQueries <- Ref.make(Vector.empty[Map[String, String]])
          responses <- Ref.make(
            List[Response[String]](
              stringResponse("""[{"id":10,"tag_name":"v2.0.0-rc1"}]""", StatusCode.Ok, twoPageHeaders),
              stringResponse("""[{"id":11,"tag_name":"v2.0.0-rc2"}]""", StatusCode.Ok, twoPageHeaders)
            )
          )
          backend = new Backend[Task]:
            private val taskMonad = new RIOMonadAsyncError[Any]

            override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
              for
                _ <- ZIO
                  .unless(
                    request.method == Method.GET &&
                      request.uri.path.endsWith(List("repos", "alice", "api", "releases"))
                  )(ZIO.fail(IllegalStateException(s"unexpected request: ${request.method} ${request.uri}")))
                _ <- seenQueries.update(_ :+ request.uri.paramsMap)
                response <- responses.modify {
                  case next :: rest => (ZIO.succeed(next.asInstanceOf[Response[T]]), rest)
                  case Nil => (ZIO.fail(IllegalStateException("no release-list response left")), Nil)
                }.flatten
              yield response

            override def close(): Task[Unit] =
              ZIO.unit

            override def monad: MonadError[Task] =
              taskMonad
          client = GiteaClient.fromBackend(config, backend)
          releases <- client.releases("alice", "api", params).runCollect
          queries <- seenQueries.get
        yield assertTrue(
          releases.map(_.tagName) == Chunk(Some("v2.0.0-rc1"), Some("v2.0.0-rc2")),
          queries.headOption.exists { first =>
            first.get("page").contains("1") &&
            first.get("limit").contains("2") &&
            first.get("draft").contains("true") &&
            first.get("pre-release").contains("false")
          },
          queries.map(_("page")) == Vector("1", "2"),
          queries.forall(_.get("draft").contains("true")),
          queries.forall(_.get("pre-release").contains("false")),
          queries.forall(_.get("limit").contains("2")),
          !queries.exists(_.get("page").contains("7"))
        )
      },
      test("propagates release-list documented not-found errors through the facade") {
        val body = """{"message":"repository not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "missing", "releases"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.releases("alice", "missing").runCollect.either)(
          Assertion.equalTo(Left(GiteaError.NotFound("repository not found", body)))
        )
      },
      test("retries release-list streaming because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""[{"id":2,"tag_name":"v1.1.0","name":"Retried release"}]""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.releases("alice", "api").runCollect.fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          releases <- fiber.join
        yield assertTrue(
          releases.map(_.id) == Chunk(Some(2L)),
          releases.headOption.flatMap(_.name).contains("Retried release")
        )
      },
      test("loads the latest repository release through the ReleasesApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "latest")) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(ResponseStub.adjust("""{"id":3,"tag_name":"v2.0.0","name":"Latest stable"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.latestRelease("alice", "api").map(release => release.id -> release.name))(
          Assertion.equalTo(Some(3L) -> Some("Latest stable"))
        )
      },
      test("propagates latest-release documented not-found errors through the facade") {
        val body = """{"message":"latest release not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "latest"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.latestRelease("alice", "api").either)(
          Assertion.equalTo(Left(GiteaError.NotFound("latest release not found", body)))
        )
      },
      test("retries latest-release lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "latest"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"id":3,"tag_name":"v2.0.0","name":"Retried latest release"}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.latestRelease("alice", "api").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          release <- fiber.join
        yield assertTrue(
          release.id.contains(3L),
          release.name.contains("Retried latest release")
        )
      },
      test("loads a repository release by tag through the ReleasesApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "tags", "v1.0.0")) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(ResponseStub.adjust("""{"id":2,"tag_name":"v1.0.0","name":"First stable"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.releaseByTag("alice", "api", "v1.0.0").map(release => release.id -> release.name))(
          Assertion.equalTo(Some(2L) -> Some("First stable"))
        )
      },
      test("routes slash-containing release tags as one facade path segment") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/releases/tags/release%2Fcandidate" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "releases",
                "tags",
                "release/candidate"
              ) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(ResponseStub.adjust("""{"id":88,"tag_name":"release/candidate","name":"Candidate"}"""))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(
          client.releaseByTag("space owner", "repo/slash", "release/candidate").map(_.tagName)
        )(Assertion.equalTo(Some("release/candidate")))
      },
      test("propagates release-by-tag documented not-found errors through the facade") {
        val body = """{"message":"release not found"}"""
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "tags", "missing"))
          }.thenRespond(ResponseStub.adjust(body, StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.releaseByTag("alice", "api", "missing").either)(
          Assertion.equalTo(Left(GiteaError.NotFound("release not found", body)))
        )
      },
      test("retries release-by-tag lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "tags", "v1.0.0"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"id":2,"tag_name":"v1.0.0","name":"Retried release"}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.releaseByTag("alice", "api", "v1.0.0").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          release <- fiber.join
        yield assertTrue(
          release.id.contains(2L),
          release.name.contains("Retried release")
        )
      },
      test("loads repository release assets through the ReleasesApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "2", "assets")) &&
              request.uri.paramsMap.isEmpty
          }.thenRespond(
            ResponseStub.adjust(
              """[{"id":901,"name":"gitea4s.jar","download_count":7},{"id":902,"name":"gitea4s-sources.jar"}]"""
            )
          )
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "releases", "2", "assets", "901")) &&
                request.uri.paramsMap.isEmpty
            }
            .thenRespond(
              ResponseStub.adjust("""{"id":901,"name":"gitea4s.jar","browser_download_url":"https://gitea.example/dl"}""")
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          assets <- client.releaseAssets("alice", "api", releaseId = 2)
          asset <- client.releaseAsset("alice", "api", releaseId = 2, assetId = 901)
        yield assertTrue(
          assets.map(_.name) == Chunk(Some("gitea4s.jar"), Some("gitea4s-sources.jar")),
          assets.headOption.flatMap(_.downloadCount).contains(7L),
          asset.id.contains(901L),
          asset.browserDownloadUrl.contains("https://gitea.example/dl")
        )
      },
      test("propagates release asset documented not-found errors through the facade") {
        val listBody = """{"message":"release not found"}"""
        val assetBody = """{"message":"asset not found"}"""
        val listBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "404", "assets"))
          }.thenRespond(ResponseStub.adjust(listBody, StatusCode.NotFound))
        val assetBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "2", "assets", "404"))
          }.thenRespond(ResponseStub.adjust(assetBody, StatusCode.NotFound))
        val listClient = GiteaClient.fromBackend(config, listBackend)
        val assetClient = GiteaClient.fromBackend(config, assetBackend)

        for
          listResult <- listClient.releaseAssets("alice", "api", releaseId = 404).either
          assetResult <- assetClient.releaseAsset("alice", "api", releaseId = 2, assetId = 404).either
        yield assertTrue(
          listResult == Left(GiteaError.NotFound("release not found", listBody)),
          assetResult == Left(GiteaError.NotFound("asset not found", assetBody))
        )
      },
      test("retries release asset lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "releases", "2", "assets", "901"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"id":901,"name":"gitea4s.jar"}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.releaseAsset("alice", "api", releaseId = 2, assetId = 901).fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          asset <- fiber.join
        yield assertTrue(
          asset.id.contains(901L),
          asset.name.contains("gitea4s.jar")
        )
      },
      test("streams repository collaborators from page 1 with the configured page size") {
        val twoPageHeaders = List(Header("x-total-count", "2"))

        for
          seenQueries <- Ref.make(Vector.empty[Map[String, String]])
          responses <- Ref.make(
            List[Response[String]](
              stringResponse("""[{"id":42,"login":"octo"}]""", StatusCode.Ok, twoPageHeaders),
              stringResponse("""[{"id":43,"login":"mona"}]""", StatusCode.Ok, twoPageHeaders)
            )
          )
          backend = new Backend[Task]:
            private val taskMonad = new RIOMonadAsyncError[Any]

            override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
              for
                _ <- ZIO
                  .unless(
                    request.method == Method.GET &&
                      request.uri.path.endsWith(List("repos", "alice", "api", "collaborators"))
                  )(ZIO.fail(IllegalStateException(s"unexpected request: ${request.method} ${request.uri}")))
                _ <- seenQueries.update(_ :+ request.uri.paramsMap)
                response <- responses.modify {
                  case next :: rest => (ZIO.succeed(next.asInstanceOf[Response[T]]), rest)
                  case Nil          => (ZIO.fail(IllegalStateException("no collaborator response left")), Nil)
                }.flatten
              yield response

            override def close(): Task[Unit] =
              ZIO.unit

            override def monad: MonadError[Task] =
              taskMonad
          client = GiteaClient.fromBackend(config, backend)
          collaborators <- client.collaborators("alice", "api").runCollect
          queries <- seenQueries.get
        yield assertTrue(
          collaborators.map(_.login) == Chunk(Some("octo"), Some("mona")),
          queries.map(_("page")) == Vector("1", "2"),
          queries.forall(_.get("limit").contains("1"))
        )
      },
      test("checks collaborator membership and loads collaborator permissions through the ReposApi") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/collaborators/space%20user%2Fslash" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "collaborators",
                "space user/slash"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret")
          }.thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "space owner", "repo/slash", "collaborators", "missing"))
            }
            .thenRespond(ResponseStub.adjust("""{"message":"not found"}""", StatusCode.NotFound))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.toString ==
                  "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/collaborators/space%20user%2Fslash/permission" &&
                request.uri.path == List(
                  "api",
                  "v1",
                  "repos",
                  "space owner",
                  "repo/slash",
                  "collaborators",
                  "space user/slash",
                  "permission"
                ) &&
                request.uri.paramsMap.isEmpty
            }
            .thenRespond(
              ResponseStub.adjust("""{"permission":"write","role_name":"Developer","user":{"id":42,"login":"octo"}}""")
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          isCollaborator <- client.isCollaborator("space owner", "repo/slash", "space user/slash")
          missing <- client.isCollaborator("space owner", "repo/slash", "missing")
          permission <- client.collaboratorPermission("space owner", "repo/slash", "space user/slash")
        yield assertTrue(
          isCollaborator,
          !missing,
          permission == RepoCollaboratorPermission(
            permission = Some("write"),
            roleName = Some("Developer"),
            user = Some(io.worxbend.gitea4s.model.User(id = Some(42L), login = Some("octo")))
          )
        )
      },
      test("propagates collaborator documented errors through the facade") {
        val notFoundBody = """{"message":"repository not found"}"""
        val validationBody = """{"message":"invalid collaborator"}"""
        val forbiddenBody = """{"message":"forbidden"}"""
        val listBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "missing", "collaborators"))
          }.thenRespond(ResponseStub.adjust(notFoundBody, StatusCode.NotFound))
        val checkBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "collaborators", "bad-user"))
          }.thenRespond(ResponseStub.adjust(validationBody, StatusCode.UnprocessableEntity))
        val permissionBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "collaborators", "octo", "permission"))
          }.thenRespond(ResponseStub.adjust(forbiddenBody, StatusCode.Forbidden))
        val listClient = GiteaClient.fromBackend(config, listBackend)
        val checkClient = GiteaClient.fromBackend(config, checkBackend)
        val permissionClient = GiteaClient.fromBackend(config, permissionBackend)

        for
          listResult <- listClient.collaborators("alice", "missing").runCollect.either
          checkResult <- checkClient.isCollaborator("alice", "api", "bad-user").either
          permissionResult <- permissionClient.collaboratorPermission("alice", "api", "octo").either
        yield assertTrue(
          listResult == Left(GiteaError.NotFound("repository not found", notFoundBody)),
          checkResult == Left(GiteaError.UnprocessableEntity("invalid collaborator", validationBody)),
          permissionResult == Left(GiteaError.Forbidden("forbidden", forbiddenBody))
        )
      },
      test("retries collaborator permission lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "collaborators", "octo", "permission"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"permission":"read","role_name":"Reader","user":{"login":"octo"}}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.collaboratorPermission("alice", "api", "octo").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          permission <- fiber.join
        yield assertTrue(
          permission.permission.contains("read"),
          permission.roleName.contains("Reader"),
          permission.user.flatMap(_.login).contains("octo")
        )
      },
      test("streams repository teams from page 1 with the configured page size") {
        val twoPageHeaders = List(Header("x-total-count", "2"))

        for
          seenQueries <- Ref.make(Vector.empty[Map[String, String]])
          responses <- Ref.make(
            List[Response[String]](
              stringResponse("""[{"id":12,"name":"reviewers","permission":"read"}]""", StatusCode.Ok, twoPageHeaders),
              stringResponse("""[{"id":13,"name":"maintainers","permission":"write"}]""", StatusCode.Ok, twoPageHeaders)
            )
          )
          backend = new Backend[Task]:
            private val taskMonad = new RIOMonadAsyncError[Any]

            override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
              for
                _ <- ZIO
                  .unless(
                    request.method == Method.GET &&
                      request.uri.path.endsWith(List("repos", "alice", "api", "teams"))
                  )(ZIO.fail(IllegalStateException(s"unexpected request: ${request.method} ${request.uri}")))
                _ <- seenQueries.update(_ :+ request.uri.paramsMap)
                response <- responses.modify {
                  case next :: rest => (ZIO.succeed(next.asInstanceOf[Response[T]]), rest)
                  case Nil          => (ZIO.fail(IllegalStateException("no team response left")), Nil)
                }.flatten
              yield response

            override def close(): Task[Unit] =
              ZIO.unit

            override def monad: MonadError[Task] =
              taskMonad
          client = GiteaClient.fromBackend(config, backend)
          teams <- client.teams("alice", "api").runCollect
          queries <- seenQueries.get
        yield assertTrue(
          teams.map(_.name) == Chunk(Some("reviewers"), Some("maintainers")),
          teams.map(_.permission) == Chunk(Some(TeamPermission.Read), Some(TeamPermission.Write)),
          queries.map(_("page")) == Vector("1", "2"),
          queries.forall(_.get("limit").contains("1"))
        )
      },
      test("loads a repository team with path-safe team name encoding") {
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString ==
                "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/teams/space%20team%2Fslash" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "teams",
                "space team/slash"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret")
          }.thenRespond(
            ResponseStub.adjust(
              """{"id":12,"name":"space team/slash","description":"Repository reviewers","permission":"read"}"""
            )
          )
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.team("space owner", "repo/slash", "space team/slash"))(
          Assertion.equalTo(
            Team(
              id = Some(12L),
              name = Some("space team/slash"),
              description = Some("Repository reviewers"),
              permission = Some(TeamPermission.Read)
            )
          )
        )
      },
      test("propagates repository team documented errors through the facade") {
        val listBody = """{"message":"repository not found"}"""
        val teamBody = """{"message":"team not found"}"""
        val methodBody = """{"message":"team assignment unavailable"}"""
        val listBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "missing", "teams"))
          }.thenRespond(ResponseStub.adjust(listBody, StatusCode.NotFound))
        val teamBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "teams", "missing-team"))
          }.thenRespond(ResponseStub.adjust(teamBody, StatusCode.NotFound))
        val methodBackend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "teams", "blocked-team"))
          }.thenRespond(ResponseStub.adjust(methodBody, StatusCode.MethodNotAllowed))
        val listClient = GiteaClient.fromBackend(config, listBackend)
        val teamClient = GiteaClient.fromBackend(config, teamBackend)
        val methodClient = GiteaClient.fromBackend(config, methodBackend)

        for
          listResult <- listClient.teams("alice", "missing").runCollect.either
          teamResult <- teamClient.team("alice", "api", "missing-team").either
          methodResult <- methodClient.team("alice", "api", "blocked-team").either
        yield assertTrue(
          listResult == Left(GiteaError.NotFound("repository not found", listBody)),
          teamResult == Left(GiteaError.NotFound("team not found", teamBody)),
          methodResult == Left(GiteaError.MethodNotAllowed("team assignment unavailable", methodBody))
        )
      },
      test("retries repository team lookup because it is a read-only GET") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "teams", "maintainers"))
          ).thenRespondCyclic(
            ResponseStub.adjust("""{"message":"temporarily unavailable"}""", StatusCode.ServiceUnavailable),
            ResponseStub.adjust("""{"id":13,"name":"maintainers","permission":"write"}""")
          )
        val client = GiteaClient.fromBackend(config.copy(maxRetries = 1), backend)

        for
          fiber <- client.team("alice", "api", "maintainers").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          team <- fiber.join
        yield assertTrue(
          team.id.contains(13L),
          team.name.contains("maintainers"),
          team.permission.contains(TeamPermission.Write)
        )
      },
      test("loads and streams repository pull requests") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "pulls"))
          )
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"id":1,"number":1,"title":"First","state":"open"}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"id":2,"number":2,"title":"Second","state":"closed"}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls")) &&
                (request.body match
                  case StringBody(body, _, _) =>
                    body.contains(""""base":"main"""") &&
                      body.contains(""""head":"feature"""") &&
                      body.contains(""""title":"Created PR"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":5,"number":5,"title":"Created PR","state":"open"}""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2")) &&
                (request.body match
                  case StringBody(body, _, _) =>
                    body.contains(""""content_version":7""") &&
                      body.contains(""""title":"Retitled PR"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""{"id":2,"number":2,"title":"Retitled PR","state":"open"}"""))
            .whenRequestMatches(request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2"))
            )
            .thenRespond(ResponseStub.adjust("""{"id":2,"number":2,"title":"Second","state":"closed"}"""))
            .whenRequestMatches(request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "merge"))
            )
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches(request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "3", "merge"))
            )
            .thenRespond(ResponseStub.adjust("", StatusCode.NotFound))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "merge")) &&
                (request.body match
                  case StringBody(body, _, _) =>
                    body.contains(""""Do":"squash"""") &&
                      body.contains(""""MergeTitleField":"Merge typed facade"""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Ok))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "merge"))
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "update")) &&
                request.uri.paramsMap.get("style").contains("merge") &&
                request.body == NoBody
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Ok))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "update")) &&
                request.uri.paramsMap.get("style").contains("rebase") &&
                request.body == NoBody
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.Ok))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "requested_reviewers")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""reviewers":["reviewer"]""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("""[{"id":12,"state":"REQUEST_REVIEW"}]""", StatusCode.Created))
            .whenRequestMatches { request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "requested_reviewers")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""reviewers":["reviewer"]""")
                  case _ => false)
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":12,"state":"COMMENT","body":"Pending notes"}"""))
            .whenRequestMatches { request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews"))
            }
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"id":10,"state":"APPROVED","body":"Looks good"}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"id":11,"state":"REQUEST_CHANGES","body":"Please update docs"}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews", "10"))
            }
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","body":"Looks good"}"""))
            .whenRequestMatches(request =>
              request.method == Method.GET &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews", "10"))
            )
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","body":"Looks good"}"""))
            .whenRequestMatches(
              _.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews", "10", "dismissals"))
            )
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","dismissed":true}"""))
            .whenRequestMatches(
              _.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews", "10", "undismissals"))
            )
            .thenRespond(ResponseStub.adjust("""{"id":10,"state":"APPROVED","dismissed":false}"""))
            .whenRequestMatches(request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews", "10"))
            )
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches(
              _.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "reviews", "10", "comments"))
            )
            .thenRespond(ResponseStub.adjust("""[{"id":12,"body":"Nit","path":"README.md","position":3}]"""))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "comments", "12", "resolve")) &&
                request.body == NoBody
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches { request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "comments", "12", "unresolve")) &&
                request.body == NoBody
            }
            .thenRespond(ResponseStub.adjust("", StatusCode.NoContent))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2.patch")))
            .thenRespond(ResponseStub.adjust("From 0000000000000000000000000000000000000000 Mon Sep 17 00:00:00 2001"))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls", "pinned")))
            .thenRespond(ResponseStub.adjust("""[{"id":3,"number":3,"title":"Pinned","state":"open"}]"""))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls", "main", "feature")))
            .thenRespond(ResponseStub.adjust("""{"id":4,"number":4,"title":"By branch","state":"open"}"""))
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "files")))
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"filename":"src/Main.scala","status":"modified","additions":4}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"filename":"README.md","status":"added","additions":12}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
            .whenRequestMatches(_.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "commits")))
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"sha":"abc123","commit":{"message":"First commit"}}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"sha":"def456","commit":{"message":"Second commit"}}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          pullRequests <- client.pullRequests("alice", "api", PullRequestListParams.default).runCollect
          pullRequest <- client.pullRequest("alice", "api", 2)
          createdPullRequest <- client.createPullRequest(
            "alice",
            "api",
            CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("Created PR"))
          )
          editedPullRequest <- client.editPullRequest(
            "alice",
            "api",
            2,
            EditPullRequestOption(contentVersion = Some(7L), title = Some("Retitled PR"))
          )
          merged <- client.pullRequestIsMerged("alice", "api", 2)
          notMerged <- client.pullRequestIsMerged("alice", "api", 3)
          mergePullRequest <- client.mergePullRequest(
            "alice",
            "api",
            2,
            MergePullRequestOption(
              mergeMethod = MergePullRequestMethod.Squash,
              mergeTitleField = Some("Merge typed facade")
            )
          ).either
          canceledAutoMerge <- client.cancelScheduledAutoMerge("alice", "api", 2).either
          updatedByMerge <- client.updatePullRequest("alice", "api", 2, PullRequestUpdateStyle.Merge).either
          updatedByRebase <- client.updatePullRequest("alice", "api", 2, PullRequestUpdateStyle.Rebase).either
          requestedReviews <- client.requestPullReviews(
            "alice",
            "api",
            2,
            PullReviewRequestOptions(reviewers = Some(List("reviewer")))
          )
          canceledReviewRequests <- client.cancelPullReviewRequests(
            "alice",
            "api",
            2,
            PullReviewRequestOptions(reviewers = Some(List("reviewer")))
          ).either
          reviews <- client.pullRequestReviews("alice", "api", 2).runCollect
          createdReview <- client.createPullRequestReview(
            "alice",
            "api",
            2,
            CreatePullReviewOptions(body = Some("Pending notes"), event = Some(PullReviewState.Comment))
          )
          review <- client.pullRequestReview("alice", "api", 2, 10)
          submittedReview <- client.submitPullRequestReview(
            "alice",
            "api",
            2,
            10,
            SubmitPullReviewOptions(body = Some("Looks good"), event = Some(PullReviewState.Approved))
          )
          dismissedReview <- client.dismissPullRequestReview(
            "alice",
            "api",
            2,
            10,
            DismissPullReviewOptions(message = Some("outdated"))
          )
          undismissedReview <- client.undismissPullRequestReview("alice", "api", 2, 10)
          reviewComments <- client.pullRequestReviewComments("alice", "api", 2, 10)
          resolvedComment <- client.resolvePullRequestReviewComment("alice", "api", 12).either
          unresolvedComment <- client.unresolvePullRequestReviewComment("alice", "api", 12).either
          deleteReview <- client.deletePullRequestReview("alice", "api", 2, 10)
          pullRequestPatch <- client.pullRequestDiffOrPatch("alice", "api", 2, PullRequestDiffType.Patch)
          pinnedPullRequests <- client.pinnedPullRequests("alice", "api")
          pullRequestByBaseHead <- client.pullRequestByBaseHead("alice", "api", "main", "feature")
          changedFiles <- client.pullRequestFiles("alice", "api", 2, PullRequestFilesParams.default).runCollect
          commits <- client.pullRequestCommits("alice", "api", 2, PullRequestCommitsParams.default).runCollect
        yield assertTrue(
          pullRequests.map(_.number) == Chunk(Some(1L), Some(2L)),
          pullRequest.id.contains(2L),
          pullRequest.title.contains("Second"),
          createdPullRequest.number.contains(5L),
          createdPullRequest.title.contains("Created PR"),
          editedPullRequest.number.contains(2L),
          editedPullRequest.title.contains("Retitled PR"),
          merged,
          !notMerged,
          mergePullRequest == Right(()),
          canceledAutoMerge == Right(()),
          updatedByMerge == Right(()),
          updatedByRebase == Right(()),
          requestedReviews.map(_.state) == Chunk(Some(PullReviewState.RequestReview)),
          canceledReviewRequests == Right(()),
          reviews.map(_.state) == Chunk(Some(PullReviewState.Approved), Some(PullReviewState.RequestChanges)),
          createdReview.state.contains(PullReviewState.Comment),
          review.body.contains("Looks good"),
          submittedReview.state.contains(PullReviewState.Approved),
          dismissedReview.dismissed.contains(true),
          undismissedReview.dismissed.contains(false),
          reviewComments.map(_.path) == Chunk(Some("README.md")),
          resolvedComment == Right(()),
          unresolvedComment == Right(()),
          deleteReview == (),
          pullRequestPatch.startsWith("From 0000000000000000000000000000000000000000"),
          pinnedPullRequests.map(_.number) == Chunk(Some(3L)),
          pullRequestByBaseHead.number.contains(4L),
          pullRequestByBaseHead.title.contains("By branch"),
          changedFiles.map(_.filename) == Chunk(Some("src/Main.scala"), Some("README.md")),
          commits.map(_.sha) == Chunk(Some("abc123"), Some("def456"))
        )
      },
      test("loads the pull request associated with a commit through the PullRequestsApi") {
        val facadeConfig = config.copy(userAgent = Some("gitea4s-test"), otp = Some("654321"))
        val backend =
          taskStub.whenRequestMatches { request =>
            request.method == Method.GET &&
              request.uri.toString == "https://gitea.example/api/v1/repos/space%20owner/repo%2Fslash/commits/abc%2Fdef%20123/pull" &&
              request.uri.path == List(
                "api",
                "v1",
                "repos",
                "space owner",
                "repo/slash",
                "commits",
                "abc/def 123",
                "pull"
              ) &&
              request.uri.paramsMap.isEmpty &&
              request.header("Accept").contains("application/json") &&
              request.header("Authorization").contains("token secret") &&
              request.header("User-Agent").contains("gitea4s-test") &&
              request.header("X-Gitea-OTP").contains("654321")
          }.thenRespond(ResponseStub.adjust("""{"id":7,"number":7,"title":"Commit PR","state":"open"}"""))
        val client = GiteaClient.fromBackend(facadeConfig, backend)

        assertZIO(client.commitPullRequest("space owner", "repo/slash", "abc/def 123").map(pr => pr.id -> pr.title))(
          Assertion.equalTo(Some(7L) -> Some("Commit PR"))
        )
      },
      test("propagates commit pull-request 404 errors through the facade") {
        val backend =
          taskStub.whenRequestMatches(request =>
            request.method == Method.GET &&
              request.uri.path.endsWith(List("repos", "alice", "api", "commits", "missing-sha", "pull"))
          ).thenRespond(ResponseStub.adjust("""{"message":"pull request not found"}""", StatusCode.NotFound))
        val client = GiteaClient.fromBackend(config, backend)

        assertZIO(client.commitPullRequest("alice", "api", "missing-sha").either)(
          Assertion.equalTo(Left(GiteaError.NotFound("pull request not found", """{"message":"pull request not found"}""")))
        )
      },
      test("retries commit pull-request lookup because it is a read-only GET") {
        for
          responses <- Ref.make(
            List[Task[Response[String]]](
              ZIO.succeed(
                stringResponse(
                  """{"message":"temporarily unavailable"}""",
                  StatusCode.ServiceUnavailable
                )
              ),
              ZIO.succeed(stringResponse("""{"id":8,"number":8,"title":"Retried commit PR","state":"open"}"""))
            )
          )
          client = GiteaClient.fromBackend(config.copy(maxRetries = 1), ScriptedBackend(responses))
          fiber <- client.commitPullRequest("alice", "api", "abc123").fork
          _ <- TestClock.adjust(Duration.ofSeconds(1))
          pullRequest <- fiber.join
          remaining <- responses.get
        yield assertTrue(
          pullRequest.number.contains(8L),
          pullRequest.title.contains("Retried commit PR"),
          remaining.isEmpty
        )
      },
      test("propagates pull-request create and edit write errors through the facade") {
        val createBody = CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("New PR"))
        val editBody = EditPullRequestOption(contentVersion = Some(7L), title = Some("Retitled PR"))
        val backend =
          taskStub
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls")) &&
                (request.body match
                  case StringBody(body, _, _) => body.contains(""""title":"New PR"""")
                  case _ => false)
            )
            .thenRespond(ResponseStub.adjust("""{"message":"create forbidden"}""", StatusCode.Forbidden))
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "missing", "pulls"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"repository not found"}""", StatusCode.NotFound))
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "conflict", "pulls"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"pull request already exists"}""", StatusCode.Conflict))
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "invalid", "pulls"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"invalid pull request"}""", StatusCode.UnprocessableEntity))
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "archived", "pulls"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"repository is archived"}""", StatusCode(423)))
            .whenRequestMatches(request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"edit forbidden"}""", StatusCode.Forbidden))
            .whenRequestMatches(request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "3"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"pull request not found"}""", StatusCode.NotFound))
            .whenRequestMatches(request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "4"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"edit conflict"}""", StatusCode.Conflict))
            .whenRequestMatches(request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "5"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"stale content"}""", StatusCode(412)))
            .whenRequestMatches(request =>
              request.method == Method.PATCH &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "6"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"invalid edit"}""", StatusCode.UnprocessableEntity))
        val client = GiteaClient.fromBackend(config, backend)

        for
          createForbidden <- client.createPullRequest("alice", "api", createBody).either
          createNotFound <- client.createPullRequest("alice", "missing", createBody).either
          createConflict <- client.createPullRequest("alice", "conflict", createBody).either
          createInvalid <- client.createPullRequest("alice", "invalid", createBody).either
          createLocked <- client.createPullRequest("alice", "archived", createBody).either
          editForbidden <- client.editPullRequest("alice", "api", 2, editBody).either
          editNotFound <- client.editPullRequest("alice", "api", 3, editBody).either
          editConflict <- client.editPullRequest("alice", "api", 4, editBody).either
          editStale <- client.editPullRequest("alice", "api", 5, editBody).either
          editInvalid <- client.editPullRequest("alice", "api", 6, editBody).either
        yield assertTrue(
          createForbidden.left.exists(_.isInstanceOf[GiteaError.Forbidden]),
          createNotFound.left.exists(_.isInstanceOf[GiteaError.NotFound]),
          createConflict.left.exists(_.isInstanceOf[GiteaError.Conflict]),
          createInvalid == Left(GiteaError.UnprocessableEntity("invalid pull request", """{"message":"invalid pull request"}""")),
          createLocked == Left(GiteaError.Locked("repository is archived", """{"message":"repository is archived"}""")),
          editForbidden.left.exists(_.isInstanceOf[GiteaError.Forbidden]),
          editNotFound.left.exists(_.isInstanceOf[GiteaError.NotFound]),
          editConflict.left.exists(_.isInstanceOf[GiteaError.Conflict]),
          editStale == Left(GiteaError.PreconditionFailed("stale content", """{"message":"stale content"}""")),
          editInvalid == Left(GiteaError.UnprocessableEntity("invalid edit", """{"message":"invalid edit"}"""))
        )
      },
      test("propagates pull-request merge and update write errors through the facade") {
        val backend =
          taskStub
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "2", "merge"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"merge forbidden"}""", StatusCode.Forbidden))
            .whenRequestMatches(request =>
              request.method == Method.DELETE &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "3", "merge"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"pull request not found"}""", StatusCode.NotFound))
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "4", "update"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"pull request has conflicts"}""", StatusCode.Conflict))
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "5", "merge"))
            )
            .thenRespond(
              ResponseStub.adjust("""{"message":"merge method is not allowed"}""", StatusCode(405))
            )
            .whenRequestMatches(request =>
              request.method == Method.POST &&
                request.uri.path.endsWith(List("repos", "alice", "api", "pulls", "6", "update"))
            )
            .thenRespond(ResponseStub.adjust("""{"message":"repository is archived"}""", StatusCode(423)))
        val client = GiteaClient.fromBackend(config, backend)

        for
          forbidden <- client
            .mergePullRequest("alice", "api", 2, MergePullRequestOption(MergePullRequestMethod.Merge))
            .either
          notFound <- client.cancelScheduledAutoMerge("alice", "api", 3).either
          conflict <- client.updatePullRequest("alice", "api", 4, PullRequestUpdateStyle.Merge).either
          methodNotAllowed <- client
            .mergePullRequest("alice", "api", 5, MergePullRequestOption(MergePullRequestMethod.Rebase))
            .either
          locked <- client.updatePullRequest("alice", "api", 6, PullRequestUpdateStyle.Rebase).either
        yield assertTrue(
          forbidden.left.exists(_.isInstanceOf[GiteaError.Forbidden]),
          notFound.left.exists(_.isInstanceOf[GiteaError.NotFound]),
          conflict.left.exists(_.isInstanceOf[GiteaError.Conflict]),
          methodNotAllowed == Left(
            GiteaError.MethodNotAllowed(
              "merge method is not allowed",
              """{"message":"merge method is not allowed"}"""
            )
          ),
          locked == Left(GiteaError.Locked("repository is archived", """{"message":"repository is archived"}"""))
        )
      },
      test("does not retry pull-request merge and update write requests") {
        val failure = RuntimeException("connection reset")

        def clientWithTwoResponses =
          Ref.make(
            List[Task[Response[String]]](
              ZIO.fail(failure),
              ZIO.succeed(stringResponse("", StatusCode.Ok))
            )
          ).map { responses =>
            (GiteaClient.fromBackend(config.copy(maxRetries = 2), ScriptedBackend(responses)), responses)
          }

        for
          mergeSetup <- clientWithTwoResponses
          (mergeClient, mergeResponses) = mergeSetup
          mergeResult <- mergeClient
            .mergePullRequest("alice", "api", 2, MergePullRequestOption(MergePullRequestMethod.Merge))
            .either
          mergeRemaining <- mergeResponses.get
          createSetup <- clientWithTwoResponses
          (createClient, createResponses) = createSetup
          createResult <- createClient
            .createPullRequest(
              "alice",
              "api",
              CreatePullRequestOption(base = Some("main"), head = Some("feature"), title = Some("Created PR"))
            )
            .either
          createRemaining <- createResponses.get
          editSetup <- clientWithTwoResponses
          (editClient, editResponses) = editSetup
          editResult <- editClient
            .editPullRequest(
              "alice",
              "api",
              2,
              EditPullRequestOption(contentVersion = Some(7L), title = Some("Retitled PR"))
            )
            .either
          editRemaining <- editResponses.get
          cancelSetup <- clientWithTwoResponses
          (cancelClient, cancelResponses) = cancelSetup
          cancelResult <- cancelClient.cancelScheduledAutoMerge("alice", "api", 2).either
          cancelRemaining <- cancelResponses.get
          updateSetup <- clientWithTwoResponses
          (updateClient, updateResponses) = updateSetup
          updateResult <- updateClient.updatePullRequest("alice", "api", 2, PullRequestUpdateStyle.Rebase).either
          updateRemaining <- updateResponses.get
        yield assertTrue(
          mergeResult.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          mergeRemaining.size == 1,
          createResult.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          createRemaining.size == 1,
          editResult.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          editRemaining.size == 1,
          cancelResult.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          cancelRemaining.size == 1,
          updateResult.left.exists {
            case GiteaError.TransportError(cause) => cause eq failure
            case _ => false
          },
          updateRemaining.size == 1
        )
      },
      test("loads notification count, streams notification threads, and fetches a single thread") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("notifications")))
            .thenRespondCyclic(
              ResponseStub.adjust(
                """[{"id":1,"unread":true,"subject":{"title":"First","state":"open","type":"Issue"}}]""",
                StatusCode.Ok,
                twoPageHeaders
              ),
              ResponseStub.adjust(
                """[{"id":2,"unread":false,"subject":{"title":"Second","state":"closed","type":"Pull"}}]""",
                StatusCode.Ok,
                twoPageHeaders
              )
            )
            .whenRequestMatches(_.uri.path.endsWith(List("notifications", "new")))
            .thenRespond(ResponseStub.adjust("""{"new":5}"""))
            .whenRequestMatches(_.uri.path.endsWith(List("notifications", "threads", "2")))
            .thenRespond(
              ResponseStub.adjust("""{"id":2,"unread":false,"subject":{"title":"Second","type":"Pull"}}""")
            )
        val client = GiteaClient.fromBackend(config, backend)

        for
          threads <- client.notificationThreads(NotificationListParams.default).runCollect
          count <- client.unreadNotificationCount
          thread <- client.notificationThread("2")
        yield assertTrue(
          threads.map(_.id) == Chunk(Some(1L), Some(2L)),
          count.unread.contains(5L),
          thread.subject.flatMap(_.title).contains("Second")
        )
      },
      test("streams followers and following users across paginated endpoints") {
        val twoPageHeaders = List(Header("x-total-count", "2"))
        val onePageHeaders = List(Header("x-total-count", "1"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "alice", "followers")))
            .thenRespondCyclic(
              ResponseStub.adjust("""[{"id":1,"login":"bob"}]""", StatusCode.Ok, twoPageHeaders),
              ResponseStub.adjust("""[{"id":2,"login":"carol"}]""", StatusCode.Ok, twoPageHeaders)
            )
            .whenRequestMatches(_.uri.path.endsWith(List("users", "alice", "following")))
            .thenRespond(ResponseStub.adjust("""[{"id":3,"login":"dave"}]""", StatusCode.Ok, onePageHeaders))
        val client = GiteaClient.fromBackend(config, backend)

        for
          followers <- client.followers("alice").runCollect
          following <- client.following("alice").runCollect
        yield assertTrue(
          followers.map(_.login) == Chunk(Some("bob"), Some("carol")),
          following.map(_.login) == Chunk(Some("dave"))
        )
      },
      test("streams user search results across pages") {
        val headers = List(Header("x-total-count", "2"))
        val backend =
          taskStub.whenRequestMatches(_.uri.path.endsWith(List("users", "search")))
            .thenRespondCyclic(
              ResponseStub.adjust("""{"ok":true,"data":[{"id":1,"login":"alice"}]}""", StatusCode.Ok, headers),
              ResponseStub.adjust("""{"ok":true,"data":[{"id":2,"login":"alicia"}]}""", StatusCode.Ok, headers)
            )
        val client = GiteaClient.fromBackend(config, backend)

        client.search(UserSearchParams(q = Some("ali"))).runCollect.map { users =>
          assertTrue(users.map(_.login) == Chunk(Some("alice"), Some("alicia")))
        }
      }
    )
