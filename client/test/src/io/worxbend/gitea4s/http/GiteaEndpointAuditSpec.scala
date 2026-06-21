package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.model.{
  Auth,
  CommitDiffType,
  CommitStatusState,
  CreatePullRequestOption,
  CreatePullReviewOptions,
  CreateStatusOption,
  DismissPullReviewOptions,
  EditPullRequestOption,
  MergePullRequestMethod,
  MergePullRequestOption,
  PullReviewRequestOptions,
  PullReviewState,
  SubmitPullReviewOptions
}
import sttp.client4.*
import zio.Chunk
import zio.test.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

object GiteaEndpointAuditSpec extends ZIOSpecDefault:
  private val config =
    GiteaConfig.default(uri"https://gitea.example", Auth.Token("secret"))

  private val pullReviewLifecycleRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoPullReviews(config, "owner", "repo", index = 12),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoPullReview(config, "owner", "repo", index = 12, id = 34),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.deletePullReview(config, "owner", "repo", index = 12, id = 34),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoPullReviewComments(config, "owner", "repo", index = 12, id = 34),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.createPullReviewRequests(
        config,
        "owner",
        "repo",
        index = 12,
        PullReviewRequestOptions(reviewers = Some(List("octo")))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.deletePullReviewRequests(
        config,
        "owner",
        "repo",
        index = 12,
        PullReviewRequestOptions(teamReviewers = Some(List("reviewers")))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.createPullReview(
        config,
        "owner",
        "repo",
        index = 12,
        CreatePullReviewOptions(body = Some("review"), event = Some(PullReviewState.Comment))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.submitPullReview(
        config,
        "owner",
        "repo",
        index = 12,
        id = 34,
        SubmitPullReviewOptions(body = Some("approved"), event = Some(PullReviewState.Approved))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.dismissPullReview(
        config,
        "owner",
        "repo",
        index = 12,
        id = 34,
        DismissPullReviewOptions(message = Some("stale"))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.undismissPullReview(config, "owner", "repo", index = 12, id = 34),
      noBodyLifecyclePost = true
    ),
    AuditedRequest(
      request = GiteaRequests.resolvePullReviewComment(config, "owner", "repo", id = 56),
      noBodyLifecyclePost = true
    ),
    AuditedRequest(
      request = GiteaRequests.unresolvePullReviewComment(config, "owner", "repo", id = 56),
      noBodyLifecyclePost = true
    )
  )

  private val commitStatusRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoCombinedStatusByRef(
        config,
        "owner",
        "repo",
        ref = "main",
        CombinedStatusParams(page = Some(2), limit = Some(10))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoStatusesByRef(
        config,
        "owner",
        "repo",
        ref = "main",
        CommitStatusListParams(
          sort = Some(CommitStatusSort.HighestIndex),
          state = Some(CommitStatusListState.Success),
          page = Some(2),
          limit = Some(10)
        )
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoStatuses(
        config,
        "owner",
        "repo",
        sha = "abc123",
        CommitStatusListParams(
          sort = Some(CommitStatusSort.Oldest),
          state = Some(CommitStatusListState.Pending),
          page = Some(3),
          limit = Some(11)
        )
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.createStatus(
        config,
        "owner",
        "repo",
        sha = "abc123",
        CreateStatusOption(context = Some("ci"), state = Some(CommitStatusState.Success))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val commitPullRequestRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoCommitPullRequest(config, "owner", "repo", sha = "abc123"),
      noBodyLifecyclePost = false
    )
  )

  private val singleCommitRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoSingleCommit(
        config,
        "owner",
        "repo",
        sha = "abc123",
        SingleCommitParams(stat = Some(true), verification = Some(false), files = Some(true))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val commitNoteRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoCommitNote(
        config,
        "owner",
        "repo",
        sha = "abc123",
        CommitNoteParams(verification = Some(false), files = Some(true))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val gitTreeRequests = List(
    AuditedRequest(
      request = GiteaRequests.gitTree(
        config,
        "owner",
        "repo",
        sha = "abc123",
        GitTreeParams(recursive = Some(true), page = Some(2), perPage = Some(50))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val gitBlobRequests = List(
    AuditedRequest(
      request = GiteaRequests.gitBlob(config, "owner", "repo", sha = "abc123"),
      noBodyLifecyclePost = false
    )
  )

  private val annotatedTagRequests = List(
    AuditedRequest(
      request = GiteaRequests.annotatedTag(config, "owner", "repo", sha = "abc123"),
      noBodyLifecyclePost = false
    )
  )

  private val gitRefEndpoints = List(
    GiteaEndpoints.repoListAllGitRefs,
    GiteaEndpoints.repoListGitRefs
  )

  private val contentsRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoContentsList(config, "owner", "repo", ContentsParams(ref = Some("main"))),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoContents(
        config,
        "owner",
        "repo",
        filepath = "docs/readme.md",
        ContentsParams(ref = Some("main"))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val rawFileRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoRawFile(
        config,
        "owner",
        "repo",
        filepath = "docs/readme.md",
        ContentsParams(ref = Some("main"))
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoMediaFile(
        config,
        "owner",
        "repo",
        filepath = "docs/readme.md",
        ContentsParams(ref = Some("main"))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val archiveRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoGetArchive(config, "owner", "repo", archive = "main.zip"),
      noBodyLifecyclePost = false,
      expectedQueryParams = Nil
    ),
    AuditedRequest(
      request = GiteaRequests.repoGetArchive(config, "owner", "repo", archive = "v1.0.0.tar.gz"),
      noBodyLifecyclePost = false,
      expectedQueryParams = Nil
    ),
    AuditedRequest(
      request = GiteaRequests.repoGetArchive(
        config,
        "owner",
        "repo",
        archive = "main.zip",
        ArchiveParams(path = Chunk("src", "docs/readme.md"))
      ),
      noBodyLifecyclePost = false,
      expectedQueryParams = Seq("path" -> "src", "path" -> "docs/readme.md")
    )
  )

  private val releaseAssetRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoReleaseAssets(config, "owner", "repo", releaseId = 77),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.repoReleaseAsset(config, "owner", "repo", releaseId = 77, attachmentId = 901),
      noBodyLifecyclePost = false
    )
  )

  private val releaseByTagRequests = List(
    AuditedRequest(
      request = GiteaRequests.repoReleaseByTag(config, "owner", "repo", tag = "release/candidate"),
      noBodyLifecyclePost = false
    )
  )

  private val commitDiffOrPatchRequests = List(
    AuditedRequest(
      request =
        GiteaRequests.repoCommitDiffOrPatch(config, "owner", "repo", sha = "abc123", diffType = CommitDiffType.diff),
      noBodyLifecyclePost = false
    )
  )

  private val pathEnumAudits = List(
    PathEnumAudit(
      endpoint = GiteaEndpoints.repoDownloadCommitDiffOrPatch,
      parameterName = "diffType",
      localValues = CommitDiffType.values.map(_.pathValue).toList
    ),
    PathEnumAudit(
      endpoint = GiteaEndpoints.repoDownloadPullDiffOrPatch,
      parameterName = "diffType",
      localValues = PullRequestDiffType.values.map(_.pathValue).toList
    )
  )

  private val pullRequestMergeUpdateRequests = List(
    AuditedRequest(
      request = GiteaRequests.mergePullRequest(
        config,
        "owner",
        "repo",
        index = 12,
        MergePullRequestOption(mergeMethod = MergePullRequestMethod.Merge)
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.cancelScheduledAutoMerge(config, "owner", "repo", index = 12),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.updatePullRequest(config, "owner", "repo", index = 12, PullRequestUpdateStyle.Rebase),
      noBodyLifecyclePost = true
    )
  )

  private val pullRequestCreateEditRequests = List(
    AuditedRequest(
      request = GiteaRequests.createPullRequest(
        config,
        "owner",
        "repo",
        CreatePullRequestOption(
          base = Some("main"),
          head = Some("feature"),
          title = Some("Add feature")
        )
      ),
      noBodyLifecyclePost = false
    ),
    AuditedRequest(
      request = GiteaRequests.editPullRequest(
        config,
        "owner",
        "repo",
        index = 12,
        EditPullRequestOption(title = Some("Update title"))
      ),
      noBodyLifecyclePost = false
    )
  )

  private val expectedNonSuccessResponseLabels = Map(
    "repoCreatePullRequest" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("409", "#/responses/error"),
      GiteaResponseLabel("422", "#/responses/validationError"),
      GiteaResponseLabel("423", "#/responses/repoArchivedError")
    ),
    "repoEditPullRequest" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("409", "#/responses/error"),
      GiteaResponseLabel("412", "#/responses/error"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoResolvePullReviewComment" -> List(
      GiteaResponseLabel("400", "#/responses/validationError"),
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoUnresolvePullReviewComment" -> List(
      GiteaResponseLabel("400", "#/responses/validationError"),
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoMergePullRequest" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("405", "#/responses/empty"),
      GiteaResponseLabel("409", "#/responses/error"),
      GiteaResponseLabel("423", "#/responses/repoArchivedError")
    ),
    "repoCancelScheduledAutoMerge" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("423", "#/responses/repoArchivedError")
    ),
    "repoUpdatePullRequest" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("409", "#/responses/error"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoCreatePullReviewRequests" -> List(
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoDeletePullReviewRequests" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoListPullReviews" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoCreatePullReview" -> List(
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoGetPullReview" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoSubmitPullReview" -> List(
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoDeletePullReview" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetPullReviewComments" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoDismissPullReview" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoUnDismissPullReview" -> List(
      GiteaResponseLabel("403", "#/responses/forbidden"),
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoGetCombinedStatusByRef" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoListStatusesByRef" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoListStatuses" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoCreateStatus" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetCommitPullRequest" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetSingleCommit" -> List(
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "repoGetNote" -> List(
      GiteaResponseLabel("404", "#/responses/notFound"),
      GiteaResponseLabel("422", "#/responses/validationError")
    ),
    "GetTree" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "GetBlob" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "GetAnnotatedTag" -> List(
      GiteaResponseLabel("400", "#/responses/error"),
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoListAllGitRefs" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoListGitRefs" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetContentsList" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetContents" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetRawFile" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetRawFileOrLFS" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetArchive" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoListReleaseAttachments" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetReleaseAttachment" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoGetReleaseByTag" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    ),
    "repoDownloadCommitDiffOrPatch" -> List(
      GiteaResponseLabel("404", "#/responses/notFound")
    )
  )

  def spec =
    suite("Gitea endpoint metadata audit")(
      test("pull-review lifecycle metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = pullReviewLifecycleRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("commit-status metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = commitStatusRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("commit pull-request metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = commitPullRequestRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("single commit metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = singleCommitRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("commit note metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = commitNoteRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("git tree metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = gitTreeRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("git blob metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = gitBlobRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("annotated Git tag metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = annotatedTagRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("git refs metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = gitRefEndpoints.flatMap(auditEndpoint(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("repository contents metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = contentsRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("repository raw file metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = rawFileRequests.flatMap(auditRawFile(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("repository archive metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = archiveRequests.flatMap(auditArchive(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("repository release asset metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = releaseAssetRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("repository release by tag metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = releaseByTagRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("commit diff or patch metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = commitDiffOrPatchRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("pull-request merge/update metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = pullRequestMergeUpdateRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("pull-request create/edit metadata matches plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = pullRequestCreateEditRequests.flatMap(audit(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("commit-status query enums match plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures =
          List(
            compareSwagger(
              "repoListStatusesByRef sort enum",
              CommitStatusSort.values.map(_.queryValue).toList,
              swagger.parameterEnumValues(
                "/repos/{owner}/{repo}/commits/{ref}/statuses",
                "GET",
                "sort"
              )
            ),
            compareSwagger(
              "repoListStatusesByRef state enum",
              CommitStatusListState.values.map(_.queryValue).toList,
              swagger.parameterEnumValues(
                "/repos/{owner}/{repo}/commits/{ref}/statuses",
                "GET",
                "state"
              )
            ),
            compareSwagger(
              "repoListStatuses sort enum",
              CommitStatusSort.values.map(_.queryValue).toList,
              swagger.parameterEnumValues(
                "/repos/{owner}/{repo}/statuses/{sha}",
                "GET",
                "sort"
              )
            ),
            compareSwagger(
              "repoListStatuses state enum",
              CommitStatusListState.values.map(_.queryValue).toList,
              swagger.parameterEnumValues(
                "/repos/{owner}/{repo}/statuses/{sha}",
                "GET",
                "state"
              )
            )
          ).flatten

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("pull-request update query enums match plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures =
          compareSwagger(
            "repoUpdatePullRequest style enum",
            PullRequestUpdateStyle.values.map(_.queryValue).toList,
            swagger.parameterEnumValues(
              "/repos/{owner}/{repo}/pulls/{index}/update",
              "POST",
              "style"
            )
          )

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      },
      test("path enum values match plugin-redoc-2.yaml") {
        val swagger = SwaggerAudit.load()
        val failures = pathEnumAudits.flatMap(auditPathEnum(swagger, _))

        assertTrue(failures.isEmpty) ?? failures.mkString("\n")
      }
    )

  private def auditEndpoint(swagger: SwaggerAudit, endpoint: GiteaEndpoint): List[String] =
    val expectedNonSuccessResponses = expectedNonSuccessResponseLabels.get(endpoint.operationId)
    swagger.operation(endpoint.path, endpoint.method) match
      case Left(message) => List(s"${endpoint.operationId}: $message")
      case Right(operation) =>
        val requiredPathParameterNames =
          endpoint.parameters.collect {
            case GiteaParameter(name, "path", true) => name
          }
        val optionalQueryParameterNames =
          endpoint.parameters.collect {
            case GiteaParameter(name, "query", false) => name
          }
        val endpointHasBody = endpoint.parameters.exists(_.in == "body")
        val expectedRetryable = endpoint.method.equalsIgnoreCase("GET") || endpoint.method.equalsIgnoreCase("HEAD")

        List(
          compare("operationId", endpoint.operationId, operation.operationId),
          compare("method", endpoint.method.toUpperCase, operation.method),
          compare("path", endpoint.path, operation.path),
          compare("required path parameters", requiredPathParameterNames, operation.requiredPathParameters),
          compare("optional query parameters", optionalQueryParameterNames, operation.optionalQueryParameters),
          compare("success response labels", List(endpoint.response), operation.successResponseLabels),
          expectedNonSuccessResponses.fold(
            Some("non-2xx response label lookup failed: no expected labels registered for audited endpoint")
          )(expected => compare("non-2xx response labels", expected, operation.nonSuccessResponses)),
          compare("endpoint body presence", endpointHasBody, operation.hasRequestBody),
          compare("retryable", GiteaRequest.isReadOnly(endpoint), expectedRetryable)
        ).flatten.map(message => s"${endpoint.operationId}: $message")

  private def audit(swagger: SwaggerAudit, audited: AuditedRequest): List[String] =
    val endpoint = audited.request.endpoint
    val expectedNonSuccessResponses = expectedNonSuccessResponseLabels.get(endpoint.operationId)
    swagger.operation(endpoint.path, endpoint.method) match
      case Left(message) => List(s"${endpoint.operationId}: $message")
      case Right(operation) =>
        val requiredPathParameterNames =
          endpoint.parameters.collect {
            case GiteaParameter(name, "path", true) => name
          }
        val optionalQueryParameterNames =
          endpoint.parameters.collect {
            case GiteaParameter(name, "query", false) => name
          }
        val endpointHasBody = endpoint.parameters.exists(_.in == "body")
        val requestHasBody = audited.request.request.body != NoBody
        val expectedRetryable = endpoint.method.equalsIgnoreCase("GET") || endpoint.method.equalsIgnoreCase("HEAD")

        List(
          compare("operationId", endpoint.operationId, operation.operationId),
          compare("method", endpoint.method.toUpperCase, operation.method),
          compare("path", endpoint.path, operation.path),
          compare("required path parameters", requiredPathParameterNames, operation.requiredPathParameters),
          compare("optional query parameters", optionalQueryParameterNames, operation.optionalQueryParameters),
          compare("success response labels", List(endpoint.response), operation.successResponseLabels),
          expectedNonSuccessResponses.fold(
            Some("non-2xx response label lookup failed: no expected labels registered for audited endpoint")
          )(expected => compare("non-2xx response labels", expected, operation.nonSuccessResponses)),
          compare("endpoint body presence", endpointHasBody, operation.hasRequestBody),
          compare("request body presence", requestHasBody, operation.hasRequestBody),
          compare("retryable", audited.request.retryable, expectedRetryable),
          Option.when(audited.noBodyLifecyclePost && requestHasBody)("no-body lifecycle POST emitted a request body"),
          Option.when(audited.noBodyLifecyclePost && audited.request.request.header("Content-Type").nonEmpty)(
            "no-body lifecycle POST emitted Content-Type"
          )
        ).flatten.map(message => s"${endpoint.operationId}: $message")

  private def auditRawFile(swagger: SwaggerAudit, audited: AuditedRequest): List[String] =
    val endpoint = audited.request.endpoint
    val metadataFailures = audit(swagger, audited)
    val octetStreamFailures =
      swagger.operation(endpoint.path, endpoint.method) match
        case Left(message) => List(s"${endpoint.operationId}: $message")
        case Right(operation) =>
          val expectedProduces = List("application/octet-stream")
          List(
            compare("produces", operation.produces, expectedProduces),
            compare("request Accept", audited.request.request.header("Accept"), Some(expectedProduces.head))
          ).flatten.map(message => s"${endpoint.operationId}: $message")

    metadataFailures ++ octetStreamFailures

  private def auditArchive(swagger: SwaggerAudit, audited: AuditedRequest): List[String] =
    val endpoint = audited.request.endpoint
    val metadataFailures = audit(swagger, audited)
    val requestFailures =
      swagger.operation(endpoint.path, endpoint.method) match
        case Left(message) => List(s"${endpoint.operationId}: $message")
        case Right(operation) =>
          List(
            compare("produces", operation.produces, List("application/json")),
            compare("request Accept", audited.request.request.header("Accept"), Some("application/octet-stream")),
            compare("request query parameters", audited.request.request.uri.paramsSeq, audited.expectedQueryParams)
          ).flatten.map(message => s"${endpoint.operationId}: $message")

    metadataFailures ++ requestFailures

  private def compare[A](label: String, actual: A, expected: A): Option[String] =
    Option.when(actual != expected)(s"$label actual=$actual expected=$expected")

  private def compareSwagger[A](label: String, actual: A, expected: Either[String, A]): List[String] =
    expected.fold(
      message => List(s"$label: $message"),
      value => compare(label, actual, value).toList
    )

  private def auditPathEnum(swagger: SwaggerAudit, audited: PathEnumAudit): List[String] =
    compareSwagger(
      s"${audited.endpoint.operationId} ${audited.parameterName} path enum",
      audited.localValues,
      swagger.pathParameterEnumValues(
        audited.endpoint.path,
        audited.endpoint.method,
        audited.parameterName
      )
    )

  private final case class AuditedRequest(
      request: GiteaRequest[?],
      noBodyLifecyclePost: Boolean,
      expectedQueryParams: Seq[(String, String)] = Nil
  )

  private final case class PathEnumAudit(
      endpoint: GiteaEndpoint,
      parameterName: String,
      localValues: List[String]
  )

  private final case class SwaggerOperation(
      path: String,
      method: String,
      operationId: String,
      requiredPathParameters: List[String],
      optionalQueryParameters: List[String],
      successResponseLabels: List[String],
      nonSuccessResponses: List[GiteaResponseLabel],
      produces: List[String],
      hasRequestBody: Boolean
  )

  private final case class GiteaResponseLabel(status: String, label: String)

  private final class SwaggerAudit(lines: Vector[String]):
    def operation(path: String, method: String): Either[String, SwaggerOperation] =
      for
        pathIndex <- findPath(path)
        methodIndex <- findMethod(pathIndex, method)
        methodBlock = blockAfter(methodIndex, minimumIndent = 6)
        operationId <- findValue(methodBlock, "operationId", s"${method.toUpperCase} $path")
        parameters = parseParameters(methodBlock)
        responses = parseResponseLabels(methodBlock)
      yield SwaggerOperation(
        path = path,
        method = method.toUpperCase,
        operationId = operationId,
        requiredPathParameters = parameters.collect {
          case SwaggerParameter(name, "path", true, _) => name
        },
        optionalQueryParameters = parameters.collect {
          case SwaggerParameter(name, "query", false, _) => name
        },
        successResponseLabels = responses.collect {
          case GiteaResponseLabel(status, label) if status.startsWith("2") => label
        },
        nonSuccessResponses = responses.filterNot(_.status.startsWith("2")),
        produces = parseProduces(methodBlock),
        hasRequestBody = parameters.exists(_.in == "body")
      )

    def parameterEnumValues(path: String, method: String, name: String): Either[String, List[String]] =
      parameterValues(path, method, name, in = None)

    def pathParameterEnumValues(path: String, method: String, name: String): Either[String, List[String]] =
      parameterValues(path, method, name, in = Some("path"))

    private def parameterValues(
        path: String,
        method: String,
        name: String,
        in: Option[String]
    ): Either[String, List[String]] =
      for
        pathIndex <- findPath(path)
        methodIndex <- findMethod(pathIndex, method)
        methodBlock = blockAfter(methodIndex, minimumIndent = 6)
        parameters = parseParameters(methodBlock)
        parameter <- parameters
          .find(parameter => parameter.name == name && in.forall(_ == parameter.in))
          .toRight {
            val available = parameters.map(parameter => s"${parameter.name}:${parameter.in}").mkString(", ")
            val location = in.fold("")(value => s" $value")
            s"Swagger parameter lookup failed: parameter '$name'$location not found in ${method.toUpperCase} $path; available parameters: [$available]"
          }
      yield parameter.enumValues

    private def findPath(path: String): Either[String, Int] =
      lines.indexWhere(_.trim == s"$path:") match
        case -1    => Left(s"Swagger path lookup failed: path not found in plugin-redoc-2.yaml: $path")
        case index => Right(index)

    private def findMethod(pathIndex: Int, method: String): Either[String, Int] =
      val needle = s"${method.toLowerCase}:"
      val path = lines(pathIndex).trim.stripSuffix(":")
      val nextPathIndex = lines.indexWhere(line => line.startsWith("  /") && line.trim.endsWith(":"), pathIndex + 1)
      val end = if nextPathIndex == -1 then lines.length else nextPathIndex
      val index = lines.indexWhere(_.trim == needle, pathIndex + 1)
      Option
        .when(index != -1 && index < end)(index)
        .toRight(s"Swagger method lookup failed: method ${method.toUpperCase} not found for path $path")

    private def blockAfter(index: Int, minimumIndent: Int): Vector[String] =
      lines
        .drop(index + 1)
        .takeWhile(line => line.trim.isEmpty || indentation(line) >= minimumIndent)

    private def findValue(block: Vector[String], key: String, context: String): Either[String, String] =
      block
        .find(_.trim.startsWith(s"$key: "))
        .map(_.trim.stripPrefix(s"$key: ").trim)
        .toRight(s"Swagger operation lookup failed: $key not found for $context")

    private def parseParameters(block: Vector[String]): List[SwaggerParameter] =
      val parameterLines = section(block, "parameters:", "responses:")
      parameterEntries(parameterLines).flatMap { entry =>
        for
          name <- entryValue(entry, "name")
          in <- entryValue(entry, "in")
        yield SwaggerParameter(
          name = name,
          in = in,
          required = entryValue(entry, "required").contains("true"),
          enumValues = enumValues(entry)
        )
      }

    private def parseResponseLabels(block: Vector[String]): List[GiteaResponseLabel] =
      val responseLines = block.dropWhile(_.trim != "responses:").drop(1)
      responseEntries(responseLines).flatMap {
        case (status, entry) =>
          responseLabel(entry).map(label => GiteaResponseLabel(status, label))
      }

    private def parseProduces(block: Vector[String]): List[String] =
      block
        .dropWhile(_.trim != "produces:")
        .drop(1)
        .takeWhile(_.trim.startsWith("- "))
        .map(_.trim.stripPrefix("- ").trim)
        .toList

    private def section(block: Vector[String], start: String, end: String): Vector[String] =
      block.dropWhile(_.trim != start).drop(1).takeWhile(_.trim != end)

    private def parameterEntries(parameterLines: Vector[String]): List[Vector[String]] =
      entries(parameterLines, line => line.startsWith(" " * 8 + "- "))

    private def responseEntries(responseLines: Vector[String]): List[(String, Vector[String])] =
      val grouped = entries(responseLines, line => responseStatus(line).nonEmpty)
      grouped.flatMap(entry => responseStatus(entry.headOption.getOrElse("")).map(_ -> entry))

    private def entries(rawLines: Vector[String], entryStart: String => Boolean): List[Vector[String]] =
      rawLines.foldLeft(List.empty[Vector[String]]) {
        case (entries, line) if entryStart(line) => entries :+ Vector(line)
        case (Nil, _)                            => Nil
        case (entries, line)                     => entries.init :+ (entries.last :+ line)
      }

    private def responseStatus(line: String): Option[String] =
      val trimmed = line.trim
      Option.when(trimmed.startsWith("'") && trimmed.endsWith(":"))(trimmed.stripPrefix("'").stripSuffix("':"))

    private def entryValue(entry: Vector[String], key: String): Option[String] =
      entry
        .find(_.trim.startsWith(s"$key:"))
        .map(_.trim.stripPrefix(s"$key:").trim.stripPrefix("'").stripSuffix("'"))
        .orElse {
          entry
            .find(_.trim.startsWith(s"- $key:"))
            .map(_.trim.stripPrefix(s"- $key:").trim.stripPrefix("'").stripSuffix("'"))
        }

    private def responseLabel(entry: Vector[String]): Option[String] =
      entryValue(entry, "$ref")
        .orElse(responseSchemaType(entry).map(schemaType => s"type:$schemaType"))
        .orElse(entryValue(entry, "description").map(description => s"description: $description"))

    private def responseSchemaType(entry: Vector[String]): Option[String] =
      entry
        .find(_.trim.startsWith("type:"))
        .map(_.trim.stripPrefix("type:").trim)

    private def enumValues(entry: Vector[String]): List[String] =
      entry
        .dropWhile { line =>
          val trimmed = line.trim
          trimmed != "enum:" && trimmed != "- enum:"
        }
        .drop(1)
        .takeWhile(line => line.trim.startsWith("- "))
        .map(_.trim.stripPrefix("- ").trim)
        .toList

    private def indentation(line: String): Int =
      line.takeWhile(_ == ' ').length

  private object SwaggerAudit:
    def load(): SwaggerAudit =
      val path = swaggerPath()
      val content = Files.readString(path, StandardCharsets.UTF_8)
      SwaggerAudit(content.linesIterator.toVector)

    private def swaggerPath(): Path =
      val candidates = Iterator
        .iterate(Paths.get("").toAbsolutePath)(_.getParent)
        .takeWhile(_ != null)
        .map(_.resolve("plugin-redoc-2.yaml"))
        .toList

      candidates.find(Files.isRegularFile(_)).getOrElse(Paths.get("plugin-redoc-2.yaml").toAbsolutePath)

  private final case class SwaggerParameter(name: String, in: String, required: Boolean, enumValues: List[String])
