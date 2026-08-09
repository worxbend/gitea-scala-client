package io.worxbend.gitea4s.model

import io.worxbend.gitea4s.error.GiteaError
import zio.Chunk
import zio.json.*
import zio.json.ast.Json
import zio.test.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import java.time.Instant

object CoreModelsSpec extends ZIOSpecDefault:
  private final case class SchemaFieldChecklist(
      swaggerDefinition: String,
      jsonFields: Set[String]
  )

  private final case class DynamicMapSchemaChecklist(
      swaggerDefinition: String,
      valueSchema: String,
      sampleKeys: Set[String]
  )

  /** The field names each schema declares in `plugin-redoc-2.yaml`.
    *
    * Wire field names used to live in three places: the vendored spec, the
    * `@jsonField` annotations on the models, and a hand-typed copy of the spec
    * in this file. The test compared the second copy against the third and
    * never against the first, so two copies that were wrong in the same way —
    * or a Gitea release that moved on — passed green.
    *
    * Reading the spec removes the third copy. Scoped to the `definitions:`
    * block on purpose: `Attachment` is declared both there and again under
    * `responses:`, and an unscoped search finds the wrong one.
    */
  private lazy val swaggerDefinitionFields: Map[String, Set[String]] =
    val lines = Files.readString(swaggerPath(), StandardCharsets.UTF_8).linesIterator.toVector
    val start = lines.indexWhere(_ == "definitions:")
    val end =
      lines.indexWhere(line => line.nonEmpty && !line.head.isWhitespace, start + 1) match
        case -1 => lines.length
        case index => index

    definitionsIn(lines.slice(start + 1, end))

  private def definitionsIn(block: Vector[String]): Map[String, Set[String]] =
    // Indentation is the whole grammar here: a definition name sits at two
    // spaces, `properties:` at four, and each field name at six.
    val definitionStarts =
      block.indices.filter(index => indentation(block(index)) == 2 && block(index).trim.endsWith(":"))

    definitionStarts.map { index =>
      val name = block(index).trim.dropRight(1)
      val body = block.drop(index + 1).takeWhile(line => line.isBlank || indentation(line) >= 4)
      name -> fieldNamesIn(body)
    }.toMap

  private def fieldNamesIn(definitionBody: Vector[String]): Set[String] =
    definitionBody
      .dropWhile(line => indentation(line) != 4 || line.trim != "properties:")
      .drop(1)
      .takeWhile(line => line.isBlank || indentation(line) >= 6)
      .filter(line => indentation(line) == 6 && line.trim.endsWith(":"))
      .map(_.trim.dropRight(1))
      .toSet

  private def indentation(line: String): Int =
    line.takeWhile(_ == ' ').length

  /** Finds the vendored spec by walking up from the working directory, the
    * same way `GiteaEndpointAuditSpec` does, so the suite runs from any module.
    */
  private def swaggerPath(): Path =
    Iterator
      .iterate(Paths.get("").toAbsolutePath)(_.getParent)
      .takeWhile(_ != null)
      .map(_.resolve("plugin-redoc-2.yaml"))
      .find(Files.isRegularFile(_))
      .getOrElse(Paths.get("plugin-redoc-2.yaml").toAbsolutePath)

  private val schemaFieldChecklist = List(
    SchemaFieldChecklist(
      "Attachment",
      Set("browser_download_url", "created_at", "download_count", "id", "name", "size", "uuid")
    ),
    SchemaFieldChecklist(
      "RepoCollaboratorPermission",
      Set("permission", "role_name", "user")
    ),
    SchemaFieldChecklist(
      "Team",
      Set(
        "can_create_org_repo",
        "description",
        "id",
        "includes_all_repositories",
        "name",
        "organization",
        "permission",
        "units",
        "units_map"
      )
    ),
    SchemaFieldChecklist(
      "TagProtection",
      Set(
        "created_at",
        "id",
        "name_pattern",
        "updated_at",
        "whitelist_teams",
        "whitelist_usernames"
      )
    ),
    SchemaFieldChecklist(
      "BranchProtection",
      Set(
        "approvals_whitelist_teams",
        "approvals_whitelist_username",
        "block_admin_merge_override",
        "block_on_official_review_requests",
        "block_on_outdated_branch",
        "block_on_rejected_reviews",
        "branch_name",
        "created_at",
        "dismiss_stale_approvals",
        "enable_approvals_whitelist",
        "enable_force_push",
        "enable_force_push_allowlist",
        "enable_merge_whitelist",
        "enable_push",
        "enable_push_whitelist",
        "enable_status_check",
        "force_push_allowlist_deploy_keys",
        "force_push_allowlist_teams",
        "force_push_allowlist_usernames",
        "ignore_stale_approvals",
        "merge_whitelist_teams",
        "merge_whitelist_usernames",
        "priority",
        "protected_file_patterns",
        "push_whitelist_deploy_keys",
        "push_whitelist_teams",
        "push_whitelist_usernames",
        "require_signed_commits",
        "required_approvals",
        "rule_name",
        "status_check_contexts",
        "unprotected_file_patterns",
        "updated_at"
      )
    ),
    SchemaFieldChecklist("Reference", Set("object", "ref", "url")),
    SchemaFieldChecklist("GitObject", Set("sha", "type", "url")),
    SchemaFieldChecklist(
      "AnnotatedTag",
      Set("message", "object", "sha", "tag", "tagger", "url", "verification")
    ),
    SchemaFieldChecklist("AnnotatedTagObject", Set("sha", "type", "url")),
    SchemaFieldChecklist(
      "GitBlobResponse",
      Set("content", "encoding", "lfs_oid", "lfs_size", "sha", "size", "url")
    ),
    SchemaFieldChecklist(
      "ContentsResponse",
      Set(
        "_links",
        "content",
        "download_url",
        "encoding",
        "git_url",
        "html_url",
        "last_author_date",
        "last_commit_message",
        "last_commit_sha",
        "last_committer_date",
        "lfs_oid",
        "lfs_size",
        "name",
        "path",
        "sha",
        "size",
        "submodule_git_url",
        "target",
        "type",
        "url"
      )
    ),
    SchemaFieldChecklist(
      "FileLinksResponse",
      Set("git", "html", "self")
    )
  )

  private val schemaFieldEncodedFixtures = Map(
    "Attachment" ->
      ReleaseAsset(
        browserDownloadUrl = Some("https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar"),
        createdAt = Some(Instant.parse("2026-06-19T10:00:00Z")),
        downloadCount = Some(42L),
        id = Some(500L),
        name = Some("gitea4s.jar"),
        size = Some(4096L),
        uuid = Some("asset-uuid-500")
      ).toJson,
    "RepoCollaboratorPermission" ->
      RepoCollaboratorPermission(
        permission = Some("write"),
        roleName = Some("Developer"),
        user = Some(User(id = Some(42L), login = Some("octo")))
      ).toJson,
    "Team" ->
      Team(
        canCreateOrgRepo = Some(true),
        description = Some("Repository maintainers"),
        id = Some(77L),
        includesAllRepositories = Some(false),
        name = Some("maintainers"),
        organization = Some(Organization(id = Some(12L), name = Some("octo"))),
        permission = Some(TeamPermission.Write),
        units = Some(List("repo.code", "repo.issues", "repo.pulls")),
        unitsMap = Some(
          Map(
            "repo.code" -> "read",
            "repo.issues" -> "write",
            "repo.pulls" -> "owner"
          )
        )
      ).toJson,
    "TagProtection" ->
      TagProtection(
        createdAt = Some(Instant.parse("2026-06-20T10:00:00Z")),
        id = Some(64L),
        namePattern = Some("release/*"),
        updatedAt = Some(Instant.parse("2026-06-20T11:00:00Z")),
        whitelistTeams = Some(List("release-managers", "maintainers")),
        whitelistUsernames = Some(List("octo", "release-bot"))
      ).toJson,
    "BranchProtection" ->
      BranchProtection(
        approvalsWhitelistTeams = Some(List("maintainers", "qa")),
        approvalsWhitelistUsernames = Some(List("octo", "reviewer")),
        blockAdminMergeOverride = Some(true),
        blockOnOfficialReviewRequests = Some(true),
        blockOnOutdatedBranch = Some(true),
        blockOnRejectedReviews = Some(true),
        branchName = Some("release/2026"),
        createdAt = Some(Instant.parse("2026-06-21T10:00:00Z")),
        dismissStaleApprovals = Some(true),
        enableApprovalsWhitelist = Some(true),
        enableForcePush = Some(false),
        enableForcePushAllowlist = Some(true),
        enableMergeWhitelist = Some(true),
        enablePush = Some(true),
        enablePushWhitelist = Some(true),
        enableStatusCheck = Some(true),
        forcePushAllowlistDeployKeys = Some(false),
        forcePushAllowlistTeams = Some(List("release-managers")),
        forcePushAllowlistUsernames = Some(List("release-bot")),
        ignoreStaleApprovals = Some(false),
        mergeWhitelistTeams = Some(List("maintainers")),
        mergeWhitelistUsernames = Some(List("octo")),
        priority = Some(25L),
        protectedFilePatterns = Some("src/**"),
        pushWhitelistDeployKeys = Some(true),
        pushWhitelistTeams = Some(List("maintainers", "qa")),
        pushWhitelistUsernames = Some(List("octo", "release-bot")),
        requireSignedCommits = Some(true),
        requiredApprovals = Some(2L),
        ruleName = Some("release/*"),
        statusCheckContexts = Some(List("ci/mill", "security/scan")),
        unprotectedFilePatterns = Some("docs/**"),
        updatedAt = Some(Instant.parse("2026-06-21T11:00:00Z"))
      ).toJson,
    "Reference" ->
      Reference(
        gitObject = Some(
          GitObject(
            sha = Some("abc123"),
            `type` = Some("commit"),
            url = Some("https://gitea.example/git/abc123")
          )
        ),
        ref = Some("refs/heads/main"),
        url = Some("https://gitea.example/git/refs/heads/main")
      ).toJson,
    "GitObject" ->
      GitObject(
        sha = Some("abc123"),
        `type` = Some("commit"),
        url = Some("https://gitea.example/git/abc123")
      ).toJson,
    "AnnotatedTag" ->
      AnnotatedTag(
        message = Some("Release v0.1.0"),
        gitObject = Some(
          AnnotatedTagObject(
            sha = Some("abc123"),
            `type` = Some("commit"),
            url = Some("https://gitea.example/git/commits/abc123")
          )
        ),
        sha = Some("tag123"),
        tag = Some("v0.1.0"),
        tagger = Some(CommitUser(name = Some("Octo Maintainer"), email = Some("octo@example.test"))),
        url = Some("https://gitea.example/git/tags/tag123"),
        verification = Some(PayloadCommitVerification(verified = Some(true)))
      ).toJson,
    "AnnotatedTagObject" ->
      AnnotatedTagObject(
        sha = Some("abc123"),
        `type` = Some("commit"),
        url = Some("https://gitea.example/git/commits/abc123")
      ).toJson,
    "GitBlobResponse" ->
      GitBlobResponse(
        content = Some("SGVsbG8sIEdpdGVhIQ=="),
        encoding = Some("base64"),
        lfsOid = Some("sha256:0123456789abcdef"),
        lfsSize = Some(4096L),
        sha = Some("blob123"),
        size = Some(13L),
        url = Some("https://gitea.example/git/blobs/blob123")
      ).toJson,
    "ContentsResponse" ->
      ContentsResponse(
        links = Some(
          FileLinksResponse(
            git = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
            html = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md"),
            self = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md")
          )
        ),
        content = Some("SGVsbG8sIGNvbnRlbnRzIQ=="),
        downloadUrl = Some("https://gitea.example/octo/gitea4s/raw/branch/main/docs/readme.md"),
        encoding = Some("base64"),
        gitUrl = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
        htmlUrl = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md"),
        lastAuthorDate = Some(Instant.parse("2026-06-18T12:00:00Z")),
        lastCommitMessage = Some("Add contents docs"),
        lastCommitSha = Some("commit123"),
        lastCommitterDate = Some(Instant.parse("2026-06-18T12:01:00Z")),
        lfsOid = Some("sha256:0123456789abcdef"),
        lfsSize = Some(4096L),
        name = Some("readme.md"),
        path = Some("docs/readme.md"),
        sha = Some("blob123"),
        size = Some(16L),
        submoduleGitUrl = Some("https://gitea.example/octo/submodule.git"),
        target = Some("../README.md"),
        `type` = Some("file"),
        url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md")
      ).toJson,
    "FileLinksResponse" ->
      FileLinksResponse(
        git = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
        html = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md"),
        self = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md")
      ).toJson
  )

  private val dynamicMapSchemaChecklist = List(
    DynamicMapSchemaChecklist(
      "LanguageStatistics",
      "additionalProperties integer int64",
      Set("Scala", "Java")
    )
  )

  private val dynamicMapEncodedFixtures = Map(
    "LanguageStatistics" ->
      LanguageStatistics(
        Map(
          "Scala" -> 1234L,
          "Java" -> 55L
        )
      ).toJson
  )

  private def topLevelJsonFields(json: String): Either[String, Set[String]] =
    json.fromJson[Json].flatMap {
      case obj: Json.Obj => Right(obj.keys.toList.toSet)
      case other         => Left(s"expected object JSON, got ${other.getClass.getSimpleName}")
    }

  def spec =
    suite("Core models")(
      test("records Swagger field checklist for schema-traced response models") {
        // Every definition the checklist claims to trace must exist in the
        // vendored spec, and the checklist must account for every field that
        // definition declares. Read from the spec rather than from a copy of
        // it, so a Gitea release that adds a field fails here instead of
        // passing unnoticed.
        val missingDefinitions =
          schemaFieldChecklist
            .map(_.swaggerDefinition)
            .filterNot(swaggerDefinitionFields.contains)
            .sorted
            .map(definition => s"$definition is not declared in plugin-redoc-2.yaml")
        val missingChecklistFields =
          schemaFieldChecklist.flatMap { checklist =>
            swaggerDefinitionFields.get(checklist.swaggerDefinition).toList.flatMap { declaredFields =>
              val missing = declaredFields.diff(checklist.jsonFields)

              Option.when(missing.nonEmpty)(
                s"${checklist.swaggerDefinition} checklist missing ${missing.toList.sorted.mkString(", ")}"
              )
            }
          }
        val missingEncodedFields =
          schemaFieldChecklist.flatMap { checklist =>
            schemaFieldEncodedFixtures.get(checklist.swaggerDefinition) match
              case None =>
                Some(s"${checklist.swaggerDefinition} fixture is missing")
              case Some(json) =>
                topLevelJsonFields(json) match
                  case Left(error) =>
                    Some(s"${checklist.swaggerDefinition} fixture could not be inspected: $error")
                  case Right(actualFields) =>
                    val missing = checklist.jsonFields.diff(actualFields)

                    Option.when(missing.nonEmpty)(
                      s"${checklist.swaggerDefinition} encoded JSON missing ${missing.toList.sorted.mkString(", ")}"
                    )
        }

        assertTrue(
          missingDefinitions == Nil,
          missingChecklistFields == Nil,
          missingEncodedFields == Nil
        )
      },
      test("records Swagger checklist for dynamic map-shaped response models") {
        val missingFixtures =
          dynamicMapSchemaChecklist.flatMap { checklist =>
            dynamicMapEncodedFixtures.get(checklist.swaggerDefinition) match
              case None =>
                Some(s"${checklist.swaggerDefinition} fixture is missing")
              case Some(json) =>
                topLevelJsonFields(json) match
                  case Left(error) =>
                    Some(s"${checklist.swaggerDefinition} fixture could not be inspected: $error")
                  case Right(actualFields) =>
                    val missing = checklist.sampleKeys.diff(actualFields)
                    val wrapperFieldLeaked = actualFields.contains("bytesByLanguage")
                    val valueSchemaRecorded = checklist.valueSchema == "additionalProperties integer int64"

                    Option.when(missing.nonEmpty || wrapperFieldLeaked || !valueSchemaRecorded)(
                      s"${checklist.swaggerDefinition} dynamic map checklist failed"
                    )
          }

        assertTrue(missingFixtures == Nil)
      },
      test("decodes user and organization fields from schema JSON names") {
        val userJson =
          """{
            |  "id": 42,
            |  "login": "octo",
            |  "login_name": "octo-login",
            |  "full_name": "Octo User",
            |  "email": "octo@example.test",
            |  "avatar_url": "https://gitea.example/avatars/42",
            |  "html_url": "https://gitea.example/octo",
            |  "is_admin": true,
            |  "followers_count": 7,
            |  "following_count": 3,
            |  "starred_repos_count": 11,
            |  "created": "2026-05-20T10:15:30Z",
            |  "last_login": "2026-06-01T08:00:00Z"
            |}""".stripMargin

        val organizationJson =
          """{
            |  "id": 9,
            |  "name": "platform",
            |  "username": "platform",
            |  "full_name": "Platform Team",
            |  "avatar_url": "https://gitea.example/avatars/org/9",
            |  "repo_admin_change_team_access": true
            |}""".stripMargin

        val user = userJson.fromJson[User]
        val organization = organizationJson.fromJson[Organization]

        assertTrue(
          user.map(_.id) == Right(Some(42L)),
          user.map(_.loginName) == Right(Some("octo-login")),
          user.map(_.isAdmin) == Right(Some(true)),
          user.map(_.created) == Right(Some(Instant.parse("2026-05-20T10:15:30Z"))),
          organization.map(_.fullName) == Right(Some("Platform Team")),
          organization.map(_.repoAdminChangeTeamAccess) == Right(Some(true))
        )
      },
      test("decodes repository permissions, object format, and private flag") {
        val json =
          """{
            |  "id": 100,
            |  "owner": { "id": 42, "login": "octo" },
            |  "name": "gitea4s",
            |  "full_name": "octo/gitea4s",
            |  "private": true,
            |  "fork": false,
            |  "html_url": "https://gitea.example/octo/gitea4s",
            |  "clone_url": "https://gitea.example/octo/gitea4s.git",
            |  "ssh_url": "git@gitea.example:octo/gitea4s.git",
            |  "default_branch": "main",
            |  "stars_count": 5,
            |  "forks_count": 2,
            |  "watchers_count": 6,
            |  "open_issues_count": 1,
            |  "permissions": { "admin": true, "pull": true, "push": true },
            |  "topics": ["scala", "zio"],
            |  "object_format_name": "sha256",
            |  "created_at": "2026-05-20T10:15:30Z",
            |  "updated_at": "2026-06-01T08:00:00Z"
            |}""".stripMargin

        val repository = json.fromJson[Repository]

        assertTrue(
          repository.map(_.fullName) == Right(Some("octo/gitea4s")),
          repository.map(_.isPrivate) == Right(Some(true)),
          repository.map(_.owner.flatMap(_.login)) == Right(Some("octo")),
          repository.map(_.permissions.flatMap(_.admin)) == Right(Some(true)),
          repository.map(_.topics) == Right(Some(List("scala", "zio"))),
          repository.map(_.objectFormatName) == Right(Some(ObjectFormatName.Sha256))
        )
      },
      test("decodes language statistics as a map-shaped JSON object") {
        val json =
          """{
            |  "Scala": 1234,
            |  "Java": 55
            |}""".stripMargin

        assertTrue(
          json.fromJson[LanguageStatistics] ==
            Right(LanguageStatistics(Map("Scala" -> 1234L, "Java" -> 55L)))
        )
      },
      test("encodes and round-trips language statistics without a wrapper field") {
        val payload = LanguageStatistics(Map("Scala" -> 1234L, "Java" -> 55L))
        val json = payload.toJson

        assertTrue(
          json.fromJson[LanguageStatistics] == Right(payload),
          topLevelJsonFields(json) == Right(Set("Scala", "Java")),
          json.contains(""""Scala":1234"""),
          json.contains(""""Java":55"""),
          !json.contains("bytesByLanguage")
        )
      },
      test("handles empty language statistics maps as empty JSON objects") {
        val payload = LanguageStatistics()

        assertTrue(
          "{}".fromJson[LanguageStatistics] == Right(payload),
          payload.toJson == "{}"
        )
      },
      test("reuses one Page codec per element codec instead of re-deriving it") {
        // The given takes a type parameter, so the compiler cannot cache it in
        // a lazy val the way it does for every other model codec here; without
        // memoisation each summon rebuilt the whole derivation.
        val first = summon[JsonCodec[Page[Int]]]
        val second = summon[JsonCodec[Page[Int]]]
        val page = Page(Chunk(1, 2), totalCount = Some(2L), page = 1, pageSize = 50, hasNext = false)

        assertTrue(
          first eq second,
          page.toJson.fromJson[Page[Int]] == Right(page)
        )
      },
      test("rejects non-numeric language byte counts") {
        val stringValue = """{"Scala":"1234"}"""
        val booleanValue = """{"Scala":true}"""
        val objectValue = """{"Scala":{"bytes":1234}}"""

        assertTrue(
          stringValue.fromJson[LanguageStatistics].isLeft,
          booleanValue.fromJson[LanguageStatistics].isLeft,
          objectValue.fromJson[LanguageStatistics].isLeft
        )
      },
      test("decodes and round-trips repository collaborator permission response shape") {
        val json =
          """{
            |  "permission": "write",
            |  "role_name": "Developer",
            |  "user": { "id": 42, "login": "octo" }
            |}""".stripMargin
        val decoded = json.fromJson[RepoCollaboratorPermission]
        val payload =
          RepoCollaboratorPermission(
            permission = Some("write"),
            roleName = Some("Developer"),
            user = Some(User(id = Some(42L), login = Some("octo")))
          )

        assertTrue(
          decoded == Right(payload),
          decoded.map(_.toJson) ==
            Right("""{"permission":"write","role_name":"Developer","user":{"id":42,"login":"octo"}}"""),
          !payload.toJson.contains("roleName")
        )
      },
      test("decodes and round-trips repository team response shape") {
        val json =
          """{
            |  "can_create_org_repo": true,
            |  "description": "Repository maintainers",
            |  "id": 77,
            |  "includes_all_repositories": false,
            |  "name": "maintainers",
            |  "organization": { "id": 12, "name": "octo", "full_name": "Octo Org" },
            |  "permission": "write",
            |  "units": ["repo.code", "repo.issues", "repo.pulls"],
            |  "units_map": {
            |    "repo.code": "read",
            |    "repo.issues": "write",
            |    "repo.pulls": "owner"
            |  }
            |}""".stripMargin
        val payload =
          Team(
            canCreateOrgRepo = Some(true),
            description = Some("Repository maintainers"),
            id = Some(77L),
            includesAllRepositories = Some(false),
            name = Some("maintainers"),
            organization = Some(
              Organization(id = Some(12L), name = Some("octo"), fullName = Some("Octo Org"))
            ),
            permission = Some(TeamPermission.Write),
            units = Some(List("repo.code", "repo.issues", "repo.pulls")),
            unitsMap = Some(
              Map(
                "repo.code" -> "read",
                "repo.issues" -> "write",
                "repo.pulls" -> "owner"
              )
            )
          )
        val decoded = json.fromJson[Team]
        val encoded = payload.toJson

        assertTrue(
          decoded == Right(payload),
          encoded.fromJson[Team] == Right(payload),
          encoded.contains(""""can_create_org_repo":true"""),
          encoded.contains(""""includes_all_repositories":false"""),
          encoded.contains(""""units_map""""),
          !encoded.contains("canCreateOrgRepo"),
          !encoded.contains("includesAllRepositories"),
          !encoded.contains("unitsMap")
        )
      },
      test("decodes repository team lists through the TeamList response shape") {
        val json =
          """[
            |  {
            |    "id": 77,
            |    "name": "maintainers",
            |    "permission": "write",
            |    "units": ["repo.code", "repo.issues"],
            |    "units_map": { "repo.code": "read", "repo.issues": "write" }
            |  },
            |  {
            |    "id": 78,
            |    "name": "triage",
            |    "permission": "read",
            |    "can_create_org_repo": false,
            |    "includes_all_repositories": true
            |  }
            |]""".stripMargin
        val expected =
          List(
            Team(
              id = Some(77L),
              name = Some("maintainers"),
              permission = Some(TeamPermission.Write),
              units = Some(List("repo.code", "repo.issues")),
              unitsMap = Some(Map("repo.code" -> "read", "repo.issues" -> "write"))
            ),
            Team(
              id = Some(78L),
              name = Some("triage"),
              permission = Some(TeamPermission.Read),
              canCreateOrgRepo = Some(false),
              includesAllRepositories = Some(true)
            )
          )
        val encoded = expected.toJson

        assertTrue(
          json.fromJson[List[Team]] == Right(expected),
          encoded.fromJson[List[Team]] == Right(expected),
          encoded.contains(""""units_map"""")
        )
      },
      test("decodes tag protection response shape from Swagger JSON names") {
        val json =
          """{
            |  "created_at": "2026-06-20T10:00:00Z",
            |  "id": 64,
            |  "name_pattern": "release/*",
            |  "updated_at": "2026-06-20T11:00:00Z",
            |  "whitelist_teams": ["release-managers", "maintainers"],
            |  "whitelist_usernames": ["octo", "release-bot"]
            |}""".stripMargin

        val tagProtection = json.fromJson[TagProtection]

        assertTrue(
          tagProtection.map(_.createdAt) == Right(Some(Instant.parse("2026-06-20T10:00:00Z"))),
          tagProtection.map(_.id) == Right(Some(64L)),
          tagProtection.map(_.namePattern) == Right(Some("release/*")),
          tagProtection.map(_.updatedAt) == Right(Some(Instant.parse("2026-06-20T11:00:00Z"))),
          tagProtection.map(_.whitelistTeams) == Right(Some(List("release-managers", "maintainers"))),
          tagProtection.map(_.whitelistUsernames) == Right(Some(List("octo", "release-bot")))
        )
      },
      test("round-trips tag protection response shape without losing Swagger JSON names") {
        val payload =
          TagProtection(
            createdAt = Some(Instant.parse("2026-06-20T10:00:00Z")),
            id = Some(64L),
            namePattern = Some("release/*"),
            updatedAt = Some(Instant.parse("2026-06-20T11:00:00Z")),
            whitelistTeams = Some(List("release-managers", "maintainers")),
            whitelistUsernames = Some(List("octo", "release-bot"))
          )
        val json = payload.toJson

        assertTrue(
          json.fromJson[TagProtection] == Right(payload),
          json.contains(""""created_at":"2026-06-20T10:00:00Z""""),
          json.contains(""""name_pattern":"release/*""""),
          json.contains(""""updated_at":"2026-06-20T11:00:00Z""""),
          json.contains(""""whitelist_teams":["release-managers","maintainers"]"""),
          json.contains(""""whitelist_usernames":["octo","release-bot"]"""),
          !json.contains("createdAt"),
          !json.contains("namePattern"),
          !json.contains("updatedAt"),
          !json.contains("whitelistTeams"),
          !json.contains("whitelistUsernames"),
          TagProtection(id = Some(64L), namePattern = Some("release/*")).toJson ==
            """{"id":64,"name_pattern":"release/*"}""",
          TagProtection().toJson == "{}"
        )
      },
      test("decodes tag protection list responses as non-paginated TagProtectionList arrays") {
        val json =
          """[
            |  {
            |    "created_at": "2026-06-20T10:00:00Z",
            |    "id": 64,
            |    "name_pattern": "release/*",
            |    "updated_at": "2026-06-20T11:00:00Z",
            |    "whitelist_teams": ["release-managers"],
            |    "whitelist_usernames": ["octo"]
            |  },
            |  {
            |    "id": 65,
            |    "name_pattern": "v*"
            |  }
            |]""".stripMargin
        val expected =
          List(
            TagProtection(
              createdAt = Some(Instant.parse("2026-06-20T10:00:00Z")),
              id = Some(64L),
              namePattern = Some("release/*"),
              updatedAt = Some(Instant.parse("2026-06-20T11:00:00Z")),
              whitelistTeams = Some(List("release-managers")),
              whitelistUsernames = Some(List("octo"))
            ),
            TagProtection(
              id = Some(65L),
              namePattern = Some("v*")
            )
          )
        val encoded = expected.toJson

        assertTrue(
          json.fromJson[List[TagProtection]] == Right(expected),
          encoded.fromJson[List[TagProtection]] == Right(expected),
          encoded.contains(""""name_pattern":"release/*""""),
          encoded.contains(""""name_pattern":"v*"""")
        )
      },
      test("decodes branch protection response shape from Swagger JSON names") {
        val json =
          """{
            |  "approvals_whitelist_teams": ["maintainers", "qa"],
            |  "approvals_whitelist_username": ["octo", "reviewer"],
            |  "block_admin_merge_override": true,
            |  "block_on_official_review_requests": true,
            |  "block_on_outdated_branch": true,
            |  "block_on_rejected_reviews": true,
            |  "branch_name": "release/2026",
            |  "created_at": "2026-06-21T10:00:00Z",
            |  "dismiss_stale_approvals": true,
            |  "enable_approvals_whitelist": true,
            |  "enable_force_push": false,
            |  "enable_force_push_allowlist": true,
            |  "enable_merge_whitelist": true,
            |  "enable_push": true,
            |  "enable_push_whitelist": true,
            |  "enable_status_check": true,
            |  "force_push_allowlist_deploy_keys": false,
            |  "force_push_allowlist_teams": ["release-managers"],
            |  "force_push_allowlist_usernames": ["release-bot"],
            |  "ignore_stale_approvals": false,
            |  "merge_whitelist_teams": ["maintainers"],
            |  "merge_whitelist_usernames": ["octo"],
            |  "priority": 25,
            |  "protected_file_patterns": "src/**",
            |  "push_whitelist_deploy_keys": true,
            |  "push_whitelist_teams": ["maintainers", "qa"],
            |  "push_whitelist_usernames": ["octo", "release-bot"],
            |  "require_signed_commits": true,
            |  "required_approvals": 2,
            |  "rule_name": "release/*",
            |  "status_check_contexts": ["ci/mill", "security/scan"],
            |  "unprotected_file_patterns": "docs/**",
            |  "updated_at": "2026-06-21T11:00:00Z"
            |}""".stripMargin

        val branchProtection = json.fromJson[BranchProtection]

        assertTrue(
          branchProtection.map(_.approvalsWhitelistTeams) == Right(Some(List("maintainers", "qa"))),
          branchProtection.map(_.approvalsWhitelistUsernames) == Right(Some(List("octo", "reviewer"))),
          branchProtection.map(_.blockAdminMergeOverride) == Right(Some(true)),
          branchProtection.map(_.blockOnOfficialReviewRequests) == Right(Some(true)),
          branchProtection.map(_.blockOnOutdatedBranch) == Right(Some(true)),
          branchProtection.map(_.blockOnRejectedReviews) == Right(Some(true)),
          branchProtection.map(_.branchName) == Right(Some("release/2026")),
          branchProtection.map(_.createdAt) == Right(Some(Instant.parse("2026-06-21T10:00:00Z"))),
          branchProtection.map(_.dismissStaleApprovals) == Right(Some(true)),
          branchProtection.map(_.enableApprovalsWhitelist) == Right(Some(true)),
          branchProtection.map(_.enableForcePush) == Right(Some(false)),
          branchProtection.map(_.enableForcePushAllowlist) == Right(Some(true)),
          branchProtection.map(_.enableMergeWhitelist) == Right(Some(true)),
          branchProtection.map(_.enablePush) == Right(Some(true)),
          branchProtection.map(_.enablePushWhitelist) == Right(Some(true)),
          branchProtection.map(_.enableStatusCheck) == Right(Some(true)),
          branchProtection.map(_.forcePushAllowlistDeployKeys) == Right(Some(false)),
          branchProtection.map(_.forcePushAllowlistTeams) == Right(Some(List("release-managers"))),
          branchProtection.map(_.forcePushAllowlistUsernames) == Right(Some(List("release-bot"))),
          branchProtection.map(_.ignoreStaleApprovals) == Right(Some(false)),
          branchProtection.map(_.mergeWhitelistTeams) == Right(Some(List("maintainers"))),
          branchProtection.map(_.mergeWhitelistUsernames) == Right(Some(List("octo"))),
          branchProtection.map(_.priority) == Right(Some(25L)),
          branchProtection.map(_.protectedFilePatterns) == Right(Some("src/**")),
          branchProtection.map(_.pushWhitelistDeployKeys) == Right(Some(true)),
          branchProtection.map(_.pushWhitelistTeams) == Right(Some(List("maintainers", "qa"))),
          branchProtection.map(_.pushWhitelistUsernames) == Right(Some(List("octo", "release-bot"))),
          branchProtection.map(_.requireSignedCommits) == Right(Some(true)),
          branchProtection.map(_.requiredApprovals) == Right(Some(2L)),
          branchProtection.map(_.ruleName) == Right(Some("release/*")),
          branchProtection.map(_.statusCheckContexts) == Right(Some(List("ci/mill", "security/scan"))),
          branchProtection.map(_.unprotectedFilePatterns) == Right(Some("docs/**")),
          branchProtection.map(_.updatedAt) == Right(Some(Instant.parse("2026-06-21T11:00:00Z")))
        )
      },
      test("round-trips branch protection response shape without losing Swagger JSON names") {
        val payload =
          BranchProtection(
            approvalsWhitelistTeams = Some(List("maintainers", "qa")),
            approvalsWhitelistUsernames = Some(List("octo", "reviewer")),
            blockAdminMergeOverride = Some(true),
            blockOnOfficialReviewRequests = Some(true),
            blockOnOutdatedBranch = Some(true),
            blockOnRejectedReviews = Some(true),
            branchName = Some("release/2026"),
            createdAt = Some(Instant.parse("2026-06-21T10:00:00Z")),
            dismissStaleApprovals = Some(true),
            enableApprovalsWhitelist = Some(true),
            enableForcePush = Some(false),
            enableForcePushAllowlist = Some(true),
            enableMergeWhitelist = Some(true),
            enablePush = Some(true),
            enablePushWhitelist = Some(true),
            enableStatusCheck = Some(true),
            forcePushAllowlistDeployKeys = Some(false),
            forcePushAllowlistTeams = Some(List("release-managers")),
            forcePushAllowlistUsernames = Some(List("release-bot")),
            ignoreStaleApprovals = Some(false),
            mergeWhitelistTeams = Some(List("maintainers")),
            mergeWhitelistUsernames = Some(List("octo")),
            priority = Some(25L),
            protectedFilePatterns = Some("src/**"),
            pushWhitelistDeployKeys = Some(true),
            pushWhitelistTeams = Some(List("maintainers", "qa")),
            pushWhitelistUsernames = Some(List("octo", "release-bot")),
            requireSignedCommits = Some(true),
            requiredApprovals = Some(2L),
            ruleName = Some("release/*"),
            statusCheckContexts = Some(List("ci/mill", "security/scan")),
            unprotectedFilePatterns = Some("docs/**"),
            updatedAt = Some(Instant.parse("2026-06-21T11:00:00Z"))
          )
        val json = payload.toJson

        assertTrue(
          json.fromJson[BranchProtection] == Right(payload),
          json.contains(""""approvals_whitelist_teams":["maintainers","qa"]"""),
          json.contains(""""approvals_whitelist_username":["octo","reviewer"]"""),
          json.contains(""""block_admin_merge_override":true"""),
          json.contains(""""block_on_official_review_requests":true"""),
          json.contains(""""block_on_outdated_branch":true"""),
          json.contains(""""block_on_rejected_reviews":true"""),
          json.contains(""""branch_name":"release/2026""""),
          json.contains(""""created_at":"2026-06-21T10:00:00Z""""),
          json.contains(""""dismiss_stale_approvals":true"""),
          json.contains(""""enable_approvals_whitelist":true"""),
          json.contains(""""enable_force_push":false"""),
          json.contains(""""enable_force_push_allowlist":true"""),
          json.contains(""""enable_merge_whitelist":true"""),
          json.contains(""""enable_push":true"""),
          json.contains(""""enable_push_whitelist":true"""),
          json.contains(""""enable_status_check":true"""),
          json.contains(""""force_push_allowlist_deploy_keys":false"""),
          json.contains(""""force_push_allowlist_teams":["release-managers"]"""),
          json.contains(""""force_push_allowlist_usernames":["release-bot"]"""),
          json.contains(""""ignore_stale_approvals":false"""),
          json.contains(""""merge_whitelist_teams":["maintainers"]"""),
          json.contains(""""merge_whitelist_usernames":["octo"]"""),
          json.contains(""""priority":25"""),
          json.contains(""""protected_file_patterns":"src/**""""),
          json.contains(""""push_whitelist_deploy_keys":true"""),
          json.contains(""""push_whitelist_teams":["maintainers","qa"]"""),
          json.contains(""""push_whitelist_usernames":["octo","release-bot"]"""),
          json.contains(""""require_signed_commits":true"""),
          json.contains(""""required_approvals":2"""),
          json.contains(""""rule_name":"release/*""""),
          json.contains(""""status_check_contexts":["ci/mill","security/scan"]"""),
          json.contains(""""unprotected_file_patterns":"docs/**""""),
          json.contains(""""updated_at":"2026-06-21T11:00:00Z""""),
          !json.contains("approvalsWhitelistTeams"),
          !json.contains("approvalsWhitelistUsernames"),
          !json.contains("requiredApprovals"),
          !json.contains("statusCheckContexts"),
          BranchProtection(ruleName = Some("main"), requiredApprovals = Some(1L)).toJson ==
            """{"required_approvals":1,"rule_name":"main"}""",
          BranchProtection().toJson == "{}"
        )
      },
      test("decodes branch protection list responses as non-paginated BranchProtectionList arrays") {
        val json =
          """[
            |  {
            |    "rule_name": "main",
            |    "required_approvals": 2,
            |    "status_check_contexts": ["ci/mill"]
            |  },
            |  {
            |    "branch_name": "release/2026",
            |    "priority": 25,
            |    "enable_push": true
            |  }
            |]""".stripMargin
        val expected =
          List(
            BranchProtection(
              ruleName = Some("main"),
              requiredApprovals = Some(2L),
              statusCheckContexts = Some(List("ci/mill"))
            ),
            BranchProtection(
              branchName = Some("release/2026"),
              priority = Some(25L),
              enablePush = Some(true)
            )
          )
        val encoded = expected.toJson

        assertTrue(
          json.fromJson[List[BranchProtection]] == Right(expected),
          encoded.fromJson[List[BranchProtection]] == Right(expected),
          encoded.contains(""""rule_name":"main""""),
          encoded.contains(""""branch_name":"release/2026"""")
        )
      },
      test("keeps branch protection fields optional and ignores unknown response metadata") {
        val minimalJson =
          """{
            |  "rule_name": "main",
            |  "future_server_field": "ignored"
            |}""".stripMargin

        assertTrue(
          "{}".fromJson[BranchProtection] == Right(BranchProtection()),
          minimalJson.fromJson[BranchProtection] == Right(BranchProtection(ruleName = Some("main")))
        )
      },
      test("decodes topic names response shape") {
        val json =
          """{
            |  "topics": ["scala", "zio", "gitea"]
            |}""".stripMargin

        val topics = json.fromJson[TopicNames]

        assertTrue(
          topics.map(_.topics) == Right(Some(List("scala", "zio", "gitea")))
        )
      },
      test("decodes new issue pins allowed response shape") {
        val json =
          """{
            |  "issues": true,
            |  "pull_requests": false
            |}""".stripMargin

        val allowed = json.fromJson[NewIssuePinsAllowed]

        assertTrue(
          allowed == Right(NewIssuePinsAllowed(issues = Some(true), pullRequests = Some(false))),
          allowed.map(_.toJson) == Right("""{"issues":true,"pull_requests":false}""")
        )
      },
      test("decodes notification count, subject, and thread payloads") {
        val countJson =
          """{
            |  "new": 3
            |}""".stripMargin

        val threadJson =
          """{
            |  "id": 900,
            |  "pinned": true,
            |  "repository": { "id": 100, "name": "gitea4s", "full_name": "octo/gitea4s" },
            |  "subject": {
            |    "html_url": "https://gitea.example/octo/gitea4s/issues/12",
            |    "latest_comment_html_url": "https://gitea.example/octo/gitea4s/issues/12#comment-300",
            |    "latest_comment_url": "https://gitea.example/api/v1/repos/octo/gitea4s/issues/comments/300",
            |    "state": "merged",
            |    "title": "Add notification support",
            |    "type": "Pull",
            |    "url": "https://gitea.example/api/v1/repos/octo/gitea4s/pulls/12"
            |  },
            |  "unread": true,
            |  "updated_at": "2026-06-18T06:00:00Z",
            |  "url": "https://gitea.example/api/v1/notifications/threads/900"
            |}""".stripMargin

        val count = countJson.fromJson[NotificationCount]
        val thread = threadJson.fromJson[NotificationThread]

        assertTrue(
          count == Right(NotificationCount(unread = Some(3L))),
          thread.map(_.id) == Right(Some(900L)),
          thread.map(_.repository.flatMap(_.fullName)) == Right(Some("octo/gitea4s")),
          thread.map(_.subject.flatMap(_.state)) == Right(Some(NotificationSubjectState.Merged)),
          thread.map(_.subject.flatMap(_.subjectType)) == Right(Some(NotificationSubjectType.Pull)),
          thread.map(_.updatedAt) == Right(Some(Instant.parse("2026-06-18T06:00:00Z")))
        )
      },
      test("decodes release asset metadata from the Attachment response schema") {
        val json =
          """{
            |  "browser_download_url": "https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar",
            |  "created_at": "2026-06-19T10:00:00Z",
            |  "download_count": 42,
            |  "id": 500,
            |  "name": "gitea4s.jar",
            |  "size": 4096,
            |  "uuid": "asset-uuid-500"
            |}""".stripMargin

        val asset = json.fromJson[ReleaseAsset]

        assertTrue(
          asset.map(_.browserDownloadUrl) ==
            Right(Some("https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar")),
          asset.map(_.createdAt) == Right(Some(Instant.parse("2026-06-19T10:00:00Z"))),
          asset.map(_.downloadCount) == Right(Some(42L)),
          asset.map(_.id) == Right(Some(500L)),
          asset.map(_.name) == Right(Some("gitea4s.jar")),
          asset.map(_.size) == Right(Some(4096L)),
          asset.map(_.uuid) == Right(Some("asset-uuid-500"))
        )
      },
      test("round-trips release asset metadata without losing Attachment JSON names") {
        val payload = ReleaseAsset(
          browserDownloadUrl = Some("https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar"),
          createdAt = Some(Instant.parse("2026-06-19T10:00:00Z")),
          downloadCount = Some(42L),
          id = Some(500L),
          name = Some("gitea4s.jar"),
          size = Some(4096L),
          uuid = Some("asset-uuid-500")
        )
        val json = payload.toJson

        assertTrue(
          json.fromJson[ReleaseAsset] == Right(payload),
          json.contains(""""browser_download_url":"https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar""""),
          json.contains(""""created_at":"2026-06-19T10:00:00Z""""),
          json.contains(""""download_count":42"""),
          !json.contains("browserDownloadUrl"),
          !json.contains("createdAt"),
          !json.contains("downloadCount"),
          ReleaseAsset(id = Some(500L), name = Some("gitea4s.jar")).toJson ==
            """{"id":500,"name":"gitea4s.jar"}""",
          ReleaseAsset().toJson == "{}"
        )
      },
      test("decodes release asset list responses as non-paginated AttachmentList arrays") {
        val json =
          """[
            |  {
            |    "browser_download_url": "https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar",
            |    "created_at": "2026-06-19T10:00:00Z",
            |    "download_count": 42,
            |    "id": 500,
            |    "name": "gitea4s.jar",
            |    "size": 4096,
            |    "uuid": "asset-uuid-500"
            |  },
            |  {
            |    "browser_download_url": "https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s-sources.jar",
            |    "created_at": "2026-06-19T10:05:00Z",
            |    "download_count": 7,
            |    "id": 501,
            |    "name": "gitea4s-sources.jar",
            |    "size": 1024,
            |    "uuid": "asset-uuid-501"
            |  }
            |]""".stripMargin

        val expected = List(
          ReleaseAsset(
            browserDownloadUrl = Some("https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s.jar"),
            createdAt = Some(Instant.parse("2026-06-19T10:00:00Z")),
            downloadCount = Some(42L),
            id = Some(500L),
            name = Some("gitea4s.jar"),
            size = Some(4096L),
            uuid = Some("asset-uuid-500")
          ),
          ReleaseAsset(
            browserDownloadUrl =
              Some("https://gitea.example/octo/gitea4s/releases/download/v0.1.0/gitea4s-sources.jar"),
            createdAt = Some(Instant.parse("2026-06-19T10:05:00Z")),
            downloadCount = Some(7L),
            id = Some(501L),
            name = Some("gitea4s-sources.jar"),
            size = Some(1024L),
            uuid = Some("asset-uuid-501")
          )
        )
        val encoded = expected.toJson

        assertTrue(
          json.fromJson[List[ReleaseAsset]] == Right(expected),
          encoded.fromJson[List[ReleaseAsset]] == Right(expected),
          encoded.contains(""""name":"gitea4s.jar""""),
          encoded.contains(""""name":"gitea4s-sources.jar"""")
        )
      },
      test("decodes issue, label, milestone, and comment payloads") {
        val issueJson =
          """{
            |  "id": 200,
            |  "number": 12,
            |  "title": "Implement models",
            |  "body": "First slice",
            |  "state": "open",
            |  "user": { "id": 42, "login": "octo" },
            |  "labels": [{ "id": 1, "name": "kind/api", "color": "0e8a16" }],
            |  "milestone": {
            |    "id": 3,
            |    "title": "v0.1",
            |    "state": "open",
            |    "open_issues": 8,
            |    "closed_issues": 5,
            |    "due_on": "2026-07-01T00:00:00Z"
            |  },
            |  "repository": { "id": 100, "owner": "octo", "name": "gitea4s", "full_name": "octo/gitea4s" },
            |  "created_at": "2026-05-20T10:15:30Z",
            |  "updated_at": "2026-06-01T08:00:00Z"
            |}""".stripMargin

        val commentJson =
          """{
            |  "id": 300,
            |  "body": "Looks good",
            |  "user": { "id": 43, "login": "reviewer" },
            |  "issue_url": "https://gitea.example/api/v1/repos/octo/gitea4s/issues/12",
            |  "html_url": "https://gitea.example/octo/gitea4s/issues/12#comment-300",
            |  "created_at": "2026-06-01T09:00:00Z"
            |}""".stripMargin

        val issue = issueJson.fromJson[Issue]
        val comment = commentJson.fromJson[Comment]

        assertTrue(
          issue.map(_.state) == Right(Some(IssueState.Open)),
          issue.map(_.labels.flatMap(_.headOption.flatMap(_.name))) == Right(Some("kind/api")),
          issue.map(_.milestone.flatMap(_.dueOn)) == Right(Some(Instant.parse("2026-07-01T00:00:00Z"))),
          issue.map(_.repository.flatMap(_.fullName)) == Right(Some("octo/gitea4s")),
          comment.map(_.user.flatMap(_.login)) == Right(Some("reviewer")),
          comment.map(_.issueUrl) == Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/issues/12"))
        )
      },
      test("round-trips create issue request payloads using schema JSON names") {
        val payload = CreateIssue(
          title = "Implement write path",
          assignee = Some("octo"),
          assignees = Some(List("octo", "reviewer")),
          body = Some("First POST slice"),
          closed = Some(false),
          dueDate = Some(Instant.parse("2026-07-01T00:00:00Z")),
          labels = Some(List(1L, 2L)),
          milestone = Some(3L),
          ref = Some("main")
        )

        val decoded = payload.toJson.fromJson[CreateIssue]

        assertTrue(
          payload.toJson.contains(""""due_date":"2026-07-01T00:00:00Z""""),
          decoded == Right(payload)
        )
      },
      test("round-trips edit issue request payloads using schema JSON names") {
        val payload = EditIssue(
          title = Some("Retitle issue"),
          body = Some("Updated body"),
          contentVersion = Some(12L),
          dueDate = Some(Instant.parse("2026-07-02T00:00:00Z")),
          milestone = Some(4L),
          ref = Some("main"),
          state = Some(IssueState.Closed),
          unsetDueDate = Some(false)
        )

        val decoded = payload.toJson.fromJson[EditIssue]

        assertTrue(
          payload.toJson.contains(""""content_version":12"""),
          payload.toJson.contains(""""due_date":"2026-07-02T00:00:00Z""""),
          payload.toJson.contains(""""unset_due_date":false"""),
          payload.toJson.contains(""""state":"closed""""),
          decoded == Right(payload)
        )
      },
      test("round-trips create issue comment request payload") {
        val payload = CreateIssueComment(body = "Looks good")
        val decoded = payload.toJson.fromJson[CreateIssueComment]

        assertTrue(
          payload.toJson == """{"body":"Looks good"}""",
          decoded == Right(payload)
        )
      },
      test("round-trips edit issue comment request payload") {
        val payload = EditIssueComment(body = "Updated comment")
        val decoded = payload.toJson.fromJson[EditIssueComment]

        assertTrue(
          payload.toJson == """{"body":"Updated comment"}""",
          decoded == Right(payload)
        )
      },
      test("decodes reactions and round-trips reaction request payload") {
        val reactionJson =
          """{
            |  "content": "+1",
            |  "created_at": "2026-06-18T09:00:00Z",
            |  "user": { "id": 42, "login": "octo" }
            |}""".stripMargin
        val payload = EditReactionOption(content = "+1")

        val reaction = reactionJson.fromJson[Reaction]

        assertTrue(
          reaction.map(_.content) == Right(Some("+1")),
          reaction.map(_.createdAt) == Right(Some(Instant.parse("2026-06-18T09:00:00Z"))),
          reaction.map(_.user.flatMap(_.login)) == Right(Some("octo")),
          payload.toJson == """{"content":"+1"}""",
          payload.toJson.fromJson[EditReactionOption] == Right(payload)
        )
      },
      test("decodes watch info using schema JSON names") {
        val watchJson =
          """{
            |  "created_at": "2026-06-18T10:00:00Z",
            |  "ignored": false,
            |  "reason": "subscribed",
            |  "repository_url": "https://gitea.example/api/v1/repos/owner/repo",
            |  "subscribed": true,
            |  "url": "https://gitea.example/api/v1/repos/owner/repo/subscription"
            |}""".stripMargin

        val watch = watchJson.fromJson[WatchInfo]

        assertTrue(
          watch.map(_.createdAt) == Right(Some(Instant.parse("2026-06-18T10:00:00Z"))),
          watch.map(_.repositoryUrl) == Right(Some("https://gitea.example/api/v1/repos/owner/repo")),
          watch.map(_.subscribed) == Right(Some(true)),
          watch.map(_.ignored) == Right(Some(false))
        )
      },
      test("decodes tracked time and round-trips add time request payload") {
        val trackedTimeJson =
          """{
            |  "created": "2026-06-18T10:15:00Z",
            |  "id": 44,
            |  "issue": { "id": 200, "number": 12, "title": "Implement models" },
            |  "issue_id": 200,
            |  "time": 3600,
            |  "user_id": 42,
            |  "user_name": "octo"
            |}""".stripMargin
        val payload = AddTimeOption(
          time = 1800L,
          created = Some(Instant.parse("2026-06-18T10:00:00Z")),
          userName = Some("octo")
        )

        val trackedTime = trackedTimeJson.fromJson[TrackedTime]

        assertTrue(
          trackedTime.map(_.id) == Right(Some(44L)),
          trackedTime.map(_.issue.flatMap(_.number)) == Right(Some(12L)),
          trackedTime.map(_.issueId) == Right(Some(200L)),
          trackedTime.map(_.time) == Right(Some(3600L)),
          trackedTime.map(_.userName) == Right(Some("octo")),
          payload.toJson.contains(""""time":1800"""),
          payload.toJson.contains(""""created":"2026-06-18T10:00:00Z""""),
          payload.toJson.contains(""""user_name":"octo""""),
          payload.toJson.fromJson[AddTimeOption] == Right(payload)
        )
      },
      test("decodes stopwatch payloads using schema JSON names") {
        val stopwatchJson =
          """{
            |  "created": "2026-06-18T10:30:00Z",
            |  "duration": "1h2m3s",
            |  "issue_index": 12,
            |  "issue_title": "Implement stopwatch support",
            |  "repo_name": "gitea4s",
            |  "repo_owner_name": "worxbend",
            |  "seconds": 3723
            |}""".stripMargin

        val stopwatch = stopwatchJson.fromJson[StopWatch]

        assertTrue(
          stopwatch.map(_.created) == Right(Some(Instant.parse("2026-06-18T10:30:00Z"))),
          stopwatch.map(_.duration) == Right(Some("1h2m3s")),
          stopwatch.map(_.issueIndex) == Right(Some(12L)),
          stopwatch.map(_.issueTitle) == Right(Some("Implement stopwatch support")),
          stopwatch.map(_.repoName) == Right(Some("gitea4s")),
          stopwatch.map(_.repoOwnerName) == Right(Some("worxbend")),
          stopwatch.map(_.seconds) == Right(Some(3723L))
        )
      },
      test("decodes commit status and combined status payloads using schema JSON names") {
        val commitStatusJson =
          """{
            |  "context": "ci/mill",
            |  "created_at": "2026-06-18T11:00:00Z",
            |  "creator": { "id": 42, "login": "octo" },
            |  "description": "Mill tests passed",
            |  "id": 700,
            |  "status": "success",
            |  "target_url": "https://ci.example/builds/700",
            |  "updated_at": "2026-06-18T11:05:00Z",
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/statuses/abc123"
            |}""".stripMargin
        val combinedStatusJson =
          """{
            |  "commit_url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123",
            |  "repository": { "id": 100, "name": "gitea4s", "full_name": "octo/gitea4s" },
            |  "sha": "abc123",
            |  "state": "warning",
            |  "statuses": [{
            |    "context": "ci/mill",
            |    "id": 700,
            |    "status": "success",
            |    "target_url": "https://ci.example/builds/700"
            |  }],
            |  "total_count": 1,
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/commits/abc123/status"
            |}""".stripMargin

        val commitStatus = commitStatusJson.fromJson[CommitStatus]
        val combinedStatus = combinedStatusJson.fromJson[CombinedStatus]

        assertTrue(
          commitStatus.map(_.id) == Right(Some(700L)),
          commitStatus.map(_.state) == Right(Some(CommitStatusState.Success)),
          commitStatus.map(_.createdAt) == Right(Some(Instant.parse("2026-06-18T11:00:00Z"))),
          commitStatus.map(_.updatedAt) == Right(Some(Instant.parse("2026-06-18T11:05:00Z"))),
          commitStatus.map(_.targetUrl) == Right(Some("https://ci.example/builds/700")),
          commitStatus.map(_.creator.flatMap(_.login)) == Right(Some("octo")),
          combinedStatus.map(_.commitUrl) ==
            Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123")),
          combinedStatus.map(_.repository.flatMap(_.fullName)) == Right(Some("octo/gitea4s")),
          combinedStatus.map(_.state) == Right(Some(CommitStatusState.Warning)),
          combinedStatus.map(_.statuses.flatMap(_.headOption).flatMap(_.state)) ==
            Right(Some(CommitStatusState.Success)),
          combinedStatus.map(_.totalCount) == Right(Some(1L))
        )
      },
      test("decodes note payloads with the documented commit shape") {
        val noteJson =
          """{
            |  "commit": {
            |    "sha": "abc123",
            |    "created": "2026-06-18T12:00:00Z",
            |    "html_url": "https://gitea.example/octo/gitea4s/commit/abc123",
            |    "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123",
            |    "author": { "id": 42, "login": "octo" },
            |    "committer": { "id": 43, "login": "maintainer" },
            |    "commit": {
            |      "message": "Implement commit notes",
            |      "author": { "name": "Octo", "email": "octo@example.test", "date": "2026-06-18T12:00:00Z" },
            |      "committer": { "name": "Maintainer", "email": "maintainer@example.test", "date": "2026-06-18T12:01:00Z" },
            |      "tree": { "sha": "tree123", "created": "2026-06-18T12:00:00Z" },
            |      "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123",
            |      "verification": { "verified": true, "reason": "gpg" }
            |    },
            |    "files": [{ "filename": "src/Note.scala", "status": "added" }],
            |    "parents": [{ "sha": "parent123", "created": "2026-06-18T11:00:00Z" }],
            |    "stats": { "additions": 14, "deletions": 0, "total": 14 }
            |  },
            |  "message": "Reviewed-by: Maintainer"
            |}""".stripMargin

        val note = noteJson.fromJson[Note]

        assertTrue(
          note.map(_.message) == Right(Some("Reviewed-by: Maintainer")),
          note.map(_.commit.flatMap(_.sha)) == Right(Some("abc123")),
          note.map(_.commit.flatMap(_.created)) == Right(Some(Instant.parse("2026-06-18T12:00:00Z"))),
          note.map(_.commit.flatMap(_.htmlUrl)) ==
            Right(Some("https://gitea.example/octo/gitea4s/commit/abc123")),
          note.map(_.commit.flatMap(_.author).flatMap(_.login)) == Right(Some("octo")),
          note.map(_.commit.flatMap(_.commit).flatMap(_.message)) == Right(Some("Implement commit notes")),
          note.map(_.commit.flatMap(_.commit).flatMap(_.verification).flatMap(_.verified)) == Right(Some(true)),
          note.map(_.commit.flatMap(_.files).flatMap(_.headOption).flatMap(_.filename)) ==
            Right(Some("src/Note.scala")),
          note.map(_.commit.flatMap(_.parents).flatMap(_.headOption).flatMap(_.sha)) == Right(Some("parent123")),
          note.map(_.commit.flatMap(_.stats).flatMap(_.total)) == Right(Some(14L))
        )
      },
      test("round-trips note optional fields without losing schema JSON names") {
        val payload = Note(
          commit = Some(
            Commit(
              commit = Some(
                RepoCommit(
                  message = Some("Implement commit notes"),
                  tree = Some(CommitMeta(sha = Some("tree123"))),
                  verification = Some(PayloadCommitVerification(verified = Some(false), reason = Some("unsigned")))
                )
              ),
              created = Some(Instant.parse("2026-06-18T12:00:00Z")),
              files = Some(List(CommitAffectedFile(filename = Some("src/Note.scala"), status = Some("added")))),
              htmlUrl = Some("https://gitea.example/octo/gitea4s/commit/abc123"),
              parents = Some(List(CommitMeta(sha = Some("parent123")))),
              sha = Some("abc123"),
              stats = Some(CommitStats(additions = Some(14L), deletions = Some(0L), total = Some(14L)))
            )
          ),
          message = Some("Reviewed-by: Maintainer")
        )
        val json = payload.toJson

        assertTrue(
          json.contains(""""html_url":"https://gitea.example/octo/gitea4s/commit/abc123""""),
          json.contains(""""files":[{"filename":"src/Note.scala","status":"added"}]"""),
          json.contains(""""parents":[{"sha":"parent123"}]"""),
          json.contains(""""stats":{"additions":14,"deletions":0,"total":14}"""),
          json.contains(""""verification":{"verified":false,"reason":"unsigned"}"""),
          json.fromJson[Note] == Right(payload),
          Note(message = Some("Reviewed-by: Maintainer")).toJson == """{"message":"Reviewed-by: Maintainer"}""",
          Note().toJson == "{}"
        )
      },
      test("decodes annotated tag payloads from schema JSON names") {
        val json =
          """{
            |  "message": "Release v0.1.0",
            |  "object": {
            |    "sha": "abc123",
            |    "type": "commit",
            |    "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123"
            |  },
            |  "sha": "tag123",
            |  "tag": "v0.1.0",
            |  "tagger": {
            |    "name": "Octo Maintainer",
            |    "email": "octo@example.test",
            |    "date": "2026-06-18T12:00:00Z"
            |  },
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/tags/tag123",
            |  "verification": {
            |    "verified": true,
            |    "reason": "gpg",
            |    "signature": "-----BEGIN PGP SIGNATURE-----",
            |    "payload": "object abc123",
            |    "signer": {
            |      "name": "Octo Maintainer",
            |      "email": "octo@example.test",
            |      "username": "octo"
            |    }
            |  }
            |}""".stripMargin

        val tag = json.fromJson[AnnotatedTag]

        assertTrue(
          tag.map(_.message) == Right(Some("Release v0.1.0")),
          tag.map(_.gitObject.flatMap(_.sha)) == Right(Some("abc123")),
          tag.map(_.gitObject.flatMap(_.`type`)) == Right(Some("commit")),
          tag.map(_.sha) == Right(Some("tag123")),
          tag.map(_.tag) == Right(Some("v0.1.0")),
          tag.map(_.tagger.flatMap(_.name)) == Right(Some("Octo Maintainer")),
          tag.map(_.tagger.flatMap(_.date)) == Right(Some("2026-06-18T12:00:00Z")),
          tag.map(_.verification.flatMap(_.verified)) == Right(Some(true)),
          tag.map(_.verification.flatMap(_.signer).flatMap(_.username)) == Right(Some("octo"))
        )
      },
      test("round-trips annotated tag optional fields without losing object mapping") {
        val payload = AnnotatedTag(
          message = Some("Release v0.1.0"),
          gitObject = Some(
            AnnotatedTagObject(
              sha = Some("abc123"),
              `type` = Some("commit"),
              url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123")
            )
          ),
          sha = Some("tag123"),
          tag = Some("v0.1.0"),
          tagger = Some(
            CommitUser(
              date = Some("2026-06-18T12:00:00Z"),
              email = Some("octo@example.test"),
              name = Some("Octo Maintainer")
            )
          ),
          url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/tags/tag123"),
          verification = Some(
            PayloadCommitVerification(
              verified = Some(false),
              reason = Some("unsigned"),
              signer = Some(PayloadUser(name = Some("Octo Maintainer"), username = Some("octo")))
            )
          )
        )
        val json = payload.toJson

        assertTrue(
          json.contains(""""object":{"""),
          json.contains(""""tagger":{"""),
          json.contains(""""verification":{"""),
          !json.contains("gitObject"),
          json.fromJson[AnnotatedTag] == Right(payload),
          AnnotatedTag().toJson == "{}"
        )
      },
      test("decodes git tree response from schema JSON names") {
        val json =
          """{
            |  "page": 1,
            |  "sha": "tree123",
            |  "total_count": 2,
            |  "tree": [
            |    {
            |      "mode": "100644",
            |      "path": "README.md",
            |      "sha": "blob123",
            |      "size": 128,
            |      "type": "blob",
            |      "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"
            |    },
            |    {
            |      "mode": "040000",
            |      "path": "src",
            |      "sha": "subtree123",
            |      "type": "tree",
            |      "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/subtree123"
            |    }
            |  ],
            |  "truncated": false,
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree123"
            |}""".stripMargin

        val tree = json.fromJson[GitTreeResponse]

        assertTrue(
          tree.map(_.page) == Right(Some(1L)),
          tree.map(_.sha) == Right(Some("tree123")),
          tree.map(_.totalCount) == Right(Some(2L)),
          tree.map(_.tree.flatMap(_.headOption).flatMap(_.path)) == Right(Some("README.md")),
          tree.map(_.tree.flatMap(_.headOption).flatMap(_.`type`)) == Right(Some("blob")),
          tree.map(_.tree.flatMap(_.headOption).flatMap(_.size)) == Right(Some(128L)),
          tree.map(_.tree.flatMap(_.drop(1).headOption).flatMap(_.size)) == Right(None),
          tree.map(_.tree.flatMap(_.drop(1).headOption).flatMap(_.`type`)) == Right(Some("tree")),
          tree.map(_.truncated) == Right(Some(false)),
          tree.map(_.url) == Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree123"))
        )
      },
      test("round-trips git tree response without losing total_count wire name") {
        val payload = GitTreeResponse(
          page = Some(2L),
          sha = Some("tree456"),
          totalCount = Some(1L),
          tree = Some(
            List(
              GitEntry(
                mode = Some("120000"),
                path = Some("current"),
                sha = Some("link123"),
                size = Some(7L),
                `type` = Some("symlink"),
                url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/link123")
              )
            )
          ),
          truncated = Some(true),
          url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree456")
        )
        val json = payload.toJson

        assertTrue(
          json.contains(""""total_count":1"""),
          json.contains(""""tree":[{"mode":"120000","path":"current","sha":"link123","size":7,"type":"symlink""""),
          json.fromJson[GitTreeResponse] == Right(payload),
          GitTreeResponse(sha = Some("tree456")).toJson == """{"sha":"tree456"}""",
          GitTreeResponse().toJson == "{}"
        )
      },
      test("decodes git blob response with all documented fields") {
        val json =
          """{
            |  "content": "SGVsbG8sIEdpdGVhIQ==",
            |  "encoding": "base64",
            |  "lfs_oid": "sha256:0123456789abcdef",
            |  "lfs_size": 4096,
            |  "sha": "blob123",
            |  "size": 13,
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"
            |}""".stripMargin

        val blob = json.fromJson[GitBlobResponse]

        assertTrue(
          blob.map(_.content) == Right(Some("SGVsbG8sIEdpdGVhIQ==")),
          blob.map(_.encoding) == Right(Some("base64")),
          blob.map(_.lfsOid) == Right(Some("sha256:0123456789abcdef")),
          blob.map(_.lfsSize) == Right(Some(4096L)),
          blob.map(_.sha) == Right(Some("blob123")),
          blob.map(_.size) == Right(Some(13L)),
          blob.map(_.url) == Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"))
        )
      },
      test("round-trips git blob response without decoding content") {
        val payload = GitBlobResponse(
          content = Some("SGVsbG8sIEdpdGVhIQ=="),
          encoding = Some("base64"),
          lfsOid = Some("sha256:0123456789abcdef"),
          lfsSize = Some(4096L),
          sha = Some("blob123"),
          size = Some(13L),
          url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123")
        )
        val json = payload.toJson

        assertTrue(
          json.fromJson[GitBlobResponse] == Right(payload),
          json.contains(""""content":"SGVsbG8sIEdpdGVhIQ==""""),
          GitBlobResponse(sha = Some("blob123")).toJson == """{"sha":"blob123"}""",
          GitBlobResponse().toJson == "{}"
        )
      },
      test("uses schema LFS JSON names for git blob response") {
        val payload = GitBlobResponse(
          lfsOid = Some("sha256:fedcba9876543210"),
          lfsSize = Some(8192L)
        )
        val json = payload.toJson

        assertTrue(
          json == """{"lfs_oid":"sha256:fedcba9876543210","lfs_size":8192}""",
          json.fromJson[GitBlobResponse].map(_.lfsOid) == Right(Some("sha256:fedcba9876543210")),
          json.fromJson[GitBlobResponse].map(_.lfsSize) == Right(Some(8192L)),
          !json.contains("lfsOid"),
          !json.contains("lfsSize")
        )
      },
      test("decodes and round-trips a repository contents response") {
        val json =
          """{
            |  "_links": {
            |    "git": "https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123",
            |    "html": "https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md",
            |    "self": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md"
            |  },
            |  "content": "SGVsbG8sIGNvbnRlbnRzIQ==",
            |  "download_url": "https://gitea.example/octo/gitea4s/raw/branch/main/docs/readme.md",
            |  "encoding": "base64",
            |  "git_url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123",
            |  "html_url": "https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md",
            |  "last_author_date": "2026-06-18T12:00:00Z",
            |  "last_commit_message": "Add contents docs",
            |  "last_commit_sha": "commit123",
            |  "last_committer_date": "2026-06-18T12:01:00Z",
            |  "lfs_oid": "sha256:0123456789abcdef",
            |  "lfs_size": 4096,
            |  "name": "readme.md",
            |  "path": "docs/readme.md",
            |  "sha": "blob123",
            |  "size": 16,
            |  "submodule_git_url": "https://gitea.example/octo/submodule.git",
            |  "target": "../README.md",
            |  "type": "file",
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md"
            |}""".stripMargin

        val expected = ContentsResponse(
          links = Some(
            FileLinksResponse(
              git = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
              html = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md"),
              self = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md")
            )
          ),
          content = Some("SGVsbG8sIGNvbnRlbnRzIQ=="),
          downloadUrl = Some("https://gitea.example/octo/gitea4s/raw/branch/main/docs/readme.md"),
          encoding = Some("base64"),
          gitUrl = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
          htmlUrl = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs/readme.md"),
          lastAuthorDate = Some(Instant.parse("2026-06-18T12:00:00Z")),
          lastCommitMessage = Some("Add contents docs"),
          lastCommitSha = Some("commit123"),
          lastCommitterDate = Some(Instant.parse("2026-06-18T12:01:00Z")),
          lfsOid = Some("sha256:0123456789abcdef"),
          lfsSize = Some(4096L),
          name = Some("readme.md"),
          path = Some("docs/readme.md"),
          sha = Some("blob123"),
          size = Some(16L),
          submoduleGitUrl = Some("https://gitea.example/octo/submodule.git"),
          target = Some("../README.md"),
          `type` = Some("file"),
          url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs/readme.md")
        )
        val encoded = expected.toJson

        assertTrue(
          json.fromJson[ContentsResponse] == Right(expected),
          encoded.fromJson[ContentsResponse] == Right(expected),
          encoded.contains(""""_links":{"""),
          encoded.contains(""""content":"SGVsbG8sIGNvbnRlbnRzIQ==""""),
          encoded.contains(""""download_url":"https://gitea.example/octo/gitea4s/raw/branch/main/docs/readme.md""""),
          encoded.contains(""""last_author_date":"2026-06-18T12:00:00Z""""),
          encoded.contains(""""last_commit_sha":"commit123""""),
          encoded.contains(""""last_committer_date":"2026-06-18T12:01:00Z""""),
          encoded.contains(""""lfs_oid":"sha256:0123456789abcdef""""),
          encoded.contains(""""submodule_git_url":"https://gitea.example/octo/submodule.git""""),
          !encoded.contains("downloadUrl"),
          !encoded.contains("lastCommitSha"),
          !encoded.contains("submoduleGitUrl")
        )
      },
      test("decodes and round-trips a repository contents list response") {
        val json =
          """[
            |  {
            |    "_links": {
            |      "git": "https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree123",
            |      "html": "https://gitea.example/octo/gitea4s/src/branch/main/docs",
            |      "self": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs"
            |    },
            |    "content": null,
            |    "download_url": "",
            |    "encoding": null,
            |    "git_url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree123",
            |    "html_url": "https://gitea.example/octo/gitea4s/src/branch/main/docs",
            |    "name": "docs",
            |    "path": "docs",
            |    "sha": "tree123",
            |    "size": 0,
            |    "type": "dir",
            |    "url": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs"
            |  },
            |  {
            |    "_links": {
            |      "git": "https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123",
            |      "html": "https://gitea.example/octo/gitea4s/src/branch/main/README.md",
            |      "self": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/README.md"
            |    },
            |    "content": "UmVhZG1lIGNvbnRlbnQ=",
            |    "download_url": "https://gitea.example/octo/gitea4s/raw/branch/main/README.md",
            |    "encoding": "base64",
            |    "git_url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123",
            |    "html_url": "https://gitea.example/octo/gitea4s/src/branch/main/README.md",
            |    "name": "README.md",
            |    "path": "README.md",
            |    "sha": "blob123",
            |    "size": 14,
            |    "type": "file",
            |    "url": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/README.md"
            |  }
            |]""".stripMargin

        val expected = List(
          ContentsResponse(
            links = Some(
              FileLinksResponse(
                git = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree123"),
                html = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs"),
                self = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs")
              )
            ),
            downloadUrl = Some(""),
            gitUrl = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/trees/tree123"),
            htmlUrl = Some("https://gitea.example/octo/gitea4s/src/branch/main/docs"),
            name = Some("docs"),
            path = Some("docs"),
            sha = Some("tree123"),
            size = Some(0L),
            `type` = Some("dir"),
            url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/docs")
          ),
          ContentsResponse(
            links = Some(
              FileLinksResponse(
                git = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
                html = Some("https://gitea.example/octo/gitea4s/src/branch/main/README.md"),
                self = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/README.md")
              )
            ),
            content = Some("UmVhZG1lIGNvbnRlbnQ="),
            downloadUrl = Some("https://gitea.example/octo/gitea4s/raw/branch/main/README.md"),
            encoding = Some("base64"),
            gitUrl = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/blobs/blob123"),
            htmlUrl = Some("https://gitea.example/octo/gitea4s/src/branch/main/README.md"),
            name = Some("README.md"),
            path = Some("README.md"),
            sha = Some("blob123"),
            size = Some(14L),
            `type` = Some("file"),
            url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/README.md")
          )
        )
        val encoded = expected.toJson

        assertTrue(
          json.fromJson[List[ContentsResponse]] == Right(expected),
          encoded.fromJson[List[ContentsResponse]] == Right(expected),
          encoded.contains(""""content":"UmVhZG1lIGNvbnRlbnQ=""""),
          encoded.contains(""""type":"dir""""),
          encoded.contains(""""type":"file"""")
        )
      },
      test("decodes git reference response with nested git object fields") {
        val json =
          """{
            |  "ref": "refs/heads/main",
            |  "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/refs/heads/main",
            |  "object": {
            |    "sha": "abc123",
            |    "type": "commit",
            |    "url": "https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123"
            |  }
            |}""".stripMargin

        val reference = json.fromJson[Reference]

        assertTrue(
          reference.map(_.ref) == Right(Some("refs/heads/main")),
          reference.map(_.url) ==
            Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/refs/heads/main")),
          reference.map(_.gitObject.flatMap(_.sha)) == Right(Some("abc123")),
          reference.map(_.gitObject.flatMap(_.`type`)) == Right(Some("commit")),
          reference.map(_.gitObject.flatMap(_.url)) ==
            Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123"))
        )
      },
      test("round-trips git reference response without losing object wire name") {
        val payload = Reference(
          gitObject = Some(
            GitObject(
              sha = Some("tag123"),
              `type` = Some("tag"),
              url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/tags/tag123")
            )
          ),
          ref = Some("refs/tags/v0.1.0"),
          url = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/refs/tags/v0.1.0")
        )
        val json = payload.toJson

        assertTrue(
          json.fromJson[Reference] == Right(payload),
          json.contains(""""ref":"refs/tags/v0.1.0""""),
          json.contains(""""object":{"sha":"tag123","type":"tag""""),
          !json.contains("gitObject"),
          Reference(ref = Some("refs/heads/main")).toJson == """{"ref":"refs/heads/main"}""",
          Reference().toJson == "{}"
        )
      },
      test("round-trips commit status request and response payloads with state/status JSON fields") {
        val create = CreateStatusOption(
          context = Some("ci/mill"),
          description = Some("Mill tests passed"),
          state = Some(CommitStatusState.Success),
          targetUrl = Some("https://ci.example/builds/700")
        )
        val returned = CommitStatus(
          context = Some("ci/mill"),
          id = Some(700L),
          state = Some(CommitStatusState.Success),
          targetUrl = Some("https://ci.example/builds/700")
        )
        val combined = CombinedStatus(
          commitUrl = Some("https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123"),
          sha = Some("abc123"),
          state = Some(CommitStatusState.Success),
          statuses = Some(List(returned)),
          totalCount = Some(1L)
        )

        assertTrue(
          create.toJson ==
            """{"context":"ci/mill","description":"Mill tests passed","state":"success","target_url":"https://ci.example/builds/700"}""",
          create.toJson.fromJson[CreateStatusOption] == Right(create),
          returned.toJson ==
            """{"context":"ci/mill","id":700,"status":"success","target_url":"https://ci.example/builds/700"}""",
          returned.toJson.fromJson[CommitStatus] == Right(returned),
          combined.toJson.contains(""""commit_url":"https://gitea.example/api/v1/repos/octo/gitea4s/git/commits/abc123""""),
          combined.toJson.contains(""""total_count":1"""),
          combined.toJson.fromJson[CombinedStatus] == Right(combined)
        )
      },
      test("round-trips issue meta payloads for dependency and blocking requests") {
        val sameRepo = IssueMeta(index = 13L)
        val crossRepo = IssueMeta(index = 21L, owner = Some("other-owner"), repo = Some("other-repo"))

        assertTrue(
          sameRepo.toJson == """{"index":13}""",
          sameRepo.toJson.fromJson[IssueMeta] == Right(sameRepo),
          crossRepo.toJson == """{"index":21,"owner":"other-owner","repo":"other-repo"}""",
          crossRepo.toJson.fromJson[IssueMeta] == Right(crossRepo)
        )
      },
      test("round-trips issue labels request payload") {
        val payload = IssueLabelsOption(labels = List(1L, 2L, 3L))
        val decoded = payload.toJson.fromJson[IssueLabelsOption]

        assertTrue(
          payload.toJson == """{"labels":[1,2,3]}""",
          decoded == Right(payload)
        )
      },
      test("round-trips issue lock request payload using schema JSON names") {
        val payload = LockIssueOption(lockReason = Some("resolved"))
        val decoded = payload.toJson.fromJson[LockIssueOption]

        assertTrue(
          payload.toJson == """{"lock_reason":"resolved"}""",
          decoded == Right(payload)
        )
      },
      test("round-trips issue deadline payloads using schema JSON names") {
        val due = Instant.parse("2026-07-03T00:00:00Z")
        val edit = EditDeadlineOption(dueDate = Some(due))
        val deadlineJson =
          """{
            |  "due_date": "2026-07-03T00:00:00Z"
            |}""".stripMargin

        val decodedDeadline = deadlineJson.fromJson[IssueDeadline]

        assertTrue(
          edit.toJson == """{"due_date":"2026-07-03T00:00:00Z"}""",
          edit.toJson.fromJson[EditDeadlineOption] == Right(edit),
          EditDeadlineOption(dueDate = None).toJson == """{"due_date":null}""",
          """{"due_date":null}""".fromJson[IssueDeadline] == Right(IssueDeadline(dueDate = None)),
          decodedDeadline == Right(IssueDeadline(dueDate = Some(due)))
        )
      },
      test("round-trips pull review request payloads using schema JSON names") {
        val payload = PullReviewRequestOptions(
          reviewers = Some(List("alice", "bob")),
          teamReviewers = Some(List("maintainers"))
        )
        val decoded = payload.toJson.fromJson[PullReviewRequestOptions]

        assertTrue(
          payload.toJson == """{"reviewers":["alice","bob"],"team_reviewers":["maintainers"]}""",
          decoded == Right(payload)
        )
      },
      test("round-trips create pull request option using schema JSON names") {
        val payload = CreatePullRequestOption(
          allowMaintainerEdit = Some(true),
          assignee = Some("alice"),
          assignees = Some(List("alice", "bob")),
          base = Some("main"),
          body = Some("Ready for review"),
          dueDate = Some(Instant.parse("2026-07-04T00:00:00Z")),
          head = Some("alice:feature/pr-create"),
          labels = Some(List(10L, 11L)),
          milestone = Some(12L),
          reviewers = Some(List("reviewer")),
          teamReviewers = Some(List("maintainers")),
          title = Some("Add pull request create API")
        )
        val decoded = payload.toJson.fromJson[CreatePullRequestOption]

        assertTrue(
          payload.toJson ==
            """{"allow_maintainer_edit":true,"assignee":"alice","assignees":["alice","bob"],"base":"main","body":"Ready for review","due_date":"2026-07-04T00:00:00Z","head":"alice:feature/pr-create","labels":[10,11],"milestone":12,"reviewers":["reviewer"],"team_reviewers":["maintainers"],"title":"Add pull request create API"}""",
          decoded == Right(payload)
        )
      },
      test("round-trips edit pull request option using schema JSON names") {
        val payload = EditPullRequestOption(
          allowMaintainerEdit = Some(false),
          assignee = Some("bob"),
          assignees = Some(List("bob", "carol")),
          base = Some("release/1.0"),
          body = Some("Updated description"),
          contentVersion = Some(9L),
          dueDate = Some(Instant.parse("2026-07-05T00:00:00Z")),
          labels = Some(List(20L, 21L)),
          milestone = Some(22L),
          state = Some(IssueState.Closed),
          title = Some("Retitle pull request"),
          unsetDueDate = Some(false)
        )
        val decoded = payload.toJson.fromJson[EditPullRequestOption]

        assertTrue(
          payload.toJson ==
            """{"allow_maintainer_edit":false,"assignee":"bob","assignees":["bob","carol"],"base":"release/1.0","body":"Updated description","content_version":9,"due_date":"2026-07-05T00:00:00Z","labels":[20,21],"milestone":22,"state":"closed","title":"Retitle pull request","unset_due_date":false}""",
          decoded == Right(payload)
        )
      },
      test("round-trips merge pull request option using schema JSON names") {
        val payload = MergePullRequestOption(
          mergeMethod = MergePullRequestMethod.RebaseMerge,
          mergeCommitId = Some("abc123"),
          mergeMessageField = Some("Merge pull request"),
          mergeTitleField = Some("PR title"),
          deleteBranchAfterMerge = Some(true),
          forceMerge = Some(false),
          headCommitId = Some("def456"),
          mergeWhenChecksSucceed = Some(true)
        )
        val decoded = payload.toJson.fromJson[MergePullRequestOption]

        assertTrue(
          payload.toJson ==
            """{"Do":"rebase-merge","MergeCommitID":"abc123","MergeMessageField":"Merge pull request","MergeTitleField":"PR title","delete_branch_after_merge":true,"force_merge":false,"head_commit_id":"def456","merge_when_checks_succeed":true}""",
          decoded == Right(payload),
          MergePullRequestOption(MergePullRequestMethod.Merge).toJson == """{"Do":"merge"}""",
          MergePullRequestMethod.values.map(_.jsonValue).toList ==
            List("merge", "rebase", "rebase-merge", "squash", "fast-forward-only", "manually-merged")
        )
      },
      test("renders commit diff type path values") {
        assertTrue(
          CommitDiffType.diff.pathValue == "diff",
          CommitDiffType.patch.pathValue == "patch",
          CommitDiffType.values.map(_.pathValue).toList == List("diff", "patch")
        )
      },
      test("round-trips pull review write payloads using schema JSON names") {
        val comment = CreatePullReviewComment(
          body = Some("Use the shared helper here"),
          newPosition = Some(14L),
          oldPosition = Some(0L),
          path = Some("src/Main.scala")
        )
        val create = CreatePullReviewOptions(
          body = Some("Review summary"),
          comments = Some(List(comment)),
          commitId = Some("abc123"),
          event = Some(PullReviewState.Comment)
        )
        val submit = SubmitPullReviewOptions(
          body = Some("Approved"),
          event = Some(PullReviewState.Approved)
        )
        val dismiss = DismissPullReviewOptions(
          message = Some("Superseded by a newer review"),
          priors = Some(true)
        )

        assertTrue(
          comment.toJson ==
            """{"body":"Use the shared helper here","new_position":14,"old_position":0,"path":"src/Main.scala"}""",
          create.toJson ==
            """{"body":"Review summary","comments":[{"body":"Use the shared helper here","new_position":14,"old_position":0,"path":"src/Main.scala"}],"commit_id":"abc123","event":"COMMENT"}""",
          create.toJson.fromJson[CreatePullReviewOptions] == Right(create),
          submit.toJson == """{"body":"Approved","event":"APPROVED"}""",
          submit.toJson.fromJson[SubmitPullReviewOptions] == Right(submit),
          dismiss.toJson == """{"message":"Superseded by a newer review","priors":true}""",
          dismiss.toJson.fromJson[DismissPullReviewOptions] == Right(dismiss)
        )
      },
      test("decodes pull request, release, branch, and tag payloads") {
        val pullRequestJson =
          """{
            |  "id": 400,
            |  "number": 4,
            |  "title": "Add request layer",
            |  "state": "closed",
            |  "draft": false,
            |  "base": { "label": "octo:main", "ref": "main", "sha": "abc", "repo_id": 100 },
            |  "head": { "label": "octo:feature", "ref": "feature", "sha": "def", "repo_id": 101 },
            |  "merged": true,
            |  "merged_at": "2026-06-02T12:00:00Z",
            |  "merged_by": { "id": 42, "login": "octo" },
            |  "additions": 10,
            |  "deletions": 2,
            |  "changed_files": 3,
            |  "diff_url": "https://gitea.example/octo/gitea4s/pulls/4.diff"
            |}""".stripMargin

        val releaseJson =
          """{
            |  "id": 500,
            |  "author": { "id": 42, "login": "octo" },
            |  "name": "v0.1.0",
            |  "tag_name": "v0.1.0",
            |  "draft": false,
            |  "prerelease": true,
            |  "target_commitish": "main",
            |  "published_at": "2026-06-03T12:00:00Z"
            |}""".stripMargin

        val branchJson =
          """{
            |  "name": "main",
            |  "protected": true,
            |  "user_can_merge": true,
            |  "required_approvals": 2,
            |  "status_check_contexts": ["ci"],
            |  "commit": {
            |    "id": "abc123",
            |    "message": "Initial",
            |    "timestamp": "2026-06-01T08:00:00Z",
            |    "author": { "name": "Octo", "email": "octo@example.test", "username": "octo" },
            |    "verification": { "verified": true, "reason": "gpg" }
            |  }
            |}""".stripMargin

        val tagJson =
          """{
            |  "id": "refs/tags/v0.1.0",
            |  "name": "v0.1.0",
            |  "message": "First release",
            |  "commit": { "sha": "abc123", "created": "2026-06-01T08:00:00Z" },
            |  "tarball_url": "https://gitea.example/octo/gitea4s/archive/v0.1.0.tar.gz"
            |}""".stripMargin

        val changedFileJson =
          """{
            |  "additions": 10,
            |  "changes": 12,
            |  "contents_url": "https://gitea.example/api/v1/repos/octo/gitea4s/contents/src/Main.scala",
            |  "deletions": 2,
            |  "filename": "src/Main.scala",
            |  "html_url": "https://gitea.example/octo/gitea4s/pulls/1/files",
            |  "previous_filename": "src/OldMain.scala",
            |  "raw_url": "https://gitea.example/octo/gitea4s/raw/src/Main.scala",
            |  "status": "renamed"
            |}""".stripMargin

        val commitJson =
          """{
            |  "sha": "abc123",
            |  "created": "2026-06-01T08:00:00Z",
            |  "html_url": "https://gitea.example/octo/gitea4s/commit/abc123",
            |  "author": { "id": 42, "login": "octo" },
            |  "commit": {
            |    "message": "Implement pull request commits",
            |    "author": { "name": "Octo", "email": "octo@example.test", "date": "2026-06-01T08:00:00Z" },
            |    "tree": { "sha": "tree123" },
            |    "verification": { "verified": true, "reason": "gpg" }
            |  },
            |  "files": [{ "filename": "src/Main.scala", "status": "modified" }],
            |  "parents": [{ "sha": "parent123" }],
            |  "stats": { "additions": 10, "deletions": 2, "total": 12 }
            |}""".stripMargin

        val pullReviewJson =
          """{
            |  "id": 900,
            |  "body": "Looks good",
            |  "comments_count": 2,
            |  "commit_id": "abc123",
            |  "dismissed": false,
            |  "html_url": "https://gitea.example/octo/gitea4s/pulls/4#pullreview-900",
            |  "official": true,
            |  "pull_request_url": "https://gitea.example/octo/gitea4s/pulls/4",
            |  "stale": false,
            |  "state": "APPROVED",
            |  "submitted_at": "2026-06-04T12:00:00Z",
            |  "team": { "id": 10, "name": "maintainers", "permission": "write" },
            |  "updated_at": "2026-06-04T12:01:00Z",
            |  "user": { "id": 43, "login": "reviewer" }
            |}""".stripMargin

        val pullReviewCommentJson =
          """{
            |  "id": 901,
            |  "body": "Please rename this",
            |  "commit_id": "abc123",
            |  "created_at": "2026-06-04T12:02:00Z",
            |  "diff_hunk": "@@ -1,1 +1,1 @@",
            |  "html_url": "https://gitea.example/octo/gitea4s/pulls/4#discussion_r901",
            |  "original_commit_id": "def456",
            |  "original_position": 3,
            |  "path": "src/Main.scala",
            |  "position": 4,
            |  "pull_request_review_id": 900,
            |  "pull_request_url": "https://gitea.example/octo/gitea4s/pulls/4",
            |  "resolver": { "id": 44, "login": "resolver" },
            |  "updated_at": "2026-06-04T12:03:00Z",
            |  "user": { "id": 43, "login": "reviewer" }
            |}""".stripMargin

        val pullRequest = pullRequestJson.fromJson[PullRequest]
        val release = releaseJson.fromJson[Release]
        val branch = branchJson.fromJson[Branch]
        val tag = tagJson.fromJson[Tag]
        val changedFile = changedFileJson.fromJson[ChangedFile]
        val commit = commitJson.fromJson[Commit]
        val pullReview = pullReviewJson.fromJson[PullReview]
        val pullReviewComment = pullReviewCommentJson.fromJson[PullReviewComment]

        assertTrue(
          pullRequest.map(_.state) == Right(Some(IssueState.Closed)),
          pullRequest.map(_.base.flatMap(_.repoId)) == Right(Some(100L)),
          release.map(_.tagName) == Right(Some("v0.1.0")),
          release.map(_.publishedAt) == Right(Some(Instant.parse("2026-06-03T12:00:00Z"))),
          branch.map(_.isProtected) == Right(Some(true)),
          branch.map(_.commit.flatMap(_.verification.flatMap(_.verified))) == Right(Some(true)),
          tag.map(_.commit.flatMap(_.sha)) == Right(Some("abc123")),
          changedFile.map(_.contentsUrl) ==
            Right(Some("https://gitea.example/api/v1/repos/octo/gitea4s/contents/src/Main.scala")),
          changedFile.map(_.previousFilename) == Right(Some("src/OldMain.scala")),
          commit.map(_.commit.flatMap(_.message)) == Right(Some("Implement pull request commits")),
          commit.map(_.commit.flatMap(_.verification.flatMap(_.verified))) == Right(Some(true)),
          commit.map(_.files.flatMap(_.headOption).flatMap(_.filename)) == Right(Some("src/Main.scala")),
          commit.map(_.stats.flatMap(_.total)) == Right(Some(12L)),
          pullReview.map(_.state) == Right(Some(PullReviewState.Approved)),
          pullReview.map(_.commentsCount) == Right(Some(2L)),
          pullReview.map(_.commitId) == Right(Some("abc123")),
          pullReview.map(_.submittedAt) == Right(Some(Instant.parse("2026-06-04T12:00:00Z"))),
          pullReview.map(_.team.flatMap(_.permission)) == Right(Some(TeamPermission.Write)),
          pullReview.map(_.user.flatMap(_.login)) == Right(Some("reviewer")),
          pullReviewComment.map(_.commitId) == Right(Some("abc123")),
          pullReviewComment.map(_.originalCommitId) == Right(Some("def456")),
          pullReviewComment.map(_.originalPosition) == Right(Some(3L)),
          pullReviewComment.map(_.pullRequestReviewId) == Right(Some(900L)),
          pullReviewComment.map(_.resolver.flatMap(_.login)) == Right(Some("resolver")),
          pullReviewComment.map(_.updatedAt) == Right(Some(Instant.parse("2026-06-04T12:03:00Z")))
        )
      },
      test("round-trips representative models through zio-json") {
        val user = User(id = Some(42L), login = Some("octo"))
        val repository = Repository(
          id = Some(100L),
          owner = Some(user),
          name = Some("gitea4s"),
          isPrivate = Some(false),
          objectFormatName = Some(ObjectFormatName.Sha1)
        )
        val issue = Issue(
          id = Some(200L),
          number = Some(12L),
          title = Some("Implement models"),
          state = Some(IssueState.Open),
          labels = Some(List(Label(id = Some(1L), name = Some("kind/api"))))
        )
        val page = Page(
          data = Chunk(user),
          totalCount = Some(1L),
          page = 1,
          pageSize = 50,
          hasNext = false
        )

        assertTrue(
          user.toJson.fromJson[User] == Right(user),
          repository.toJson.fromJson[Repository] == Right(repository),
          issue.toJson.fromJson[Issue] == Right(issue),
          page.toJson.fromJson[Page[User]] == Right(page)
        )
      },
      test("rejects unknown closed-set enum values") {
        val issue = """{ "id": 1, "state": "paused" }""".fromJson[Issue]
        val repository = """{ "id": 1, "object_format_name": "md5" }""".fromJson[Repository]
        val team = """{ "id": 1, "permission": "maintain" }""".fromJson[Team]
        val notificationSubject =
          """{ "title": "Invalid", "state": "stale", "type": "Message" }""".fromJson[NotificationSubject]
        val pullReview = """{ "id": 1, "state": "STALE" }""".fromJson[PullReview]
        val commitStatus = """{ "id": 1, "status": "queued" }""".fromJson[CommitStatus]
        val createStatus = """{ "state": "queued" }""".fromJson[CreateStatusOption]
        val mergeOption = """{ "Do": "cherry-pick" }""".fromJson[MergePullRequestOption]

        assertTrue(
          issue.isLeft,
          repository.isLeft,
          team.isLeft,
          notificationSubject.isLeft,
          pullReview.isLeft,
          commitStatus.isLeft,
          createStatus.isLeft,
          mergeOption.isLeft
        )
      },
      test("models auth modes and core error ADT values") {
        val auth: Auth = Auth.Basic("octo", "secret")
        val error: GiteaError =
          GiteaError.RateLimited(Some(Instant.parse("2026-06-18T00:00:00Z")), "rate limited")
        val methodNotAllowed: GiteaError =
          GiteaError.MethodNotAllowed("merge method is not allowed", """{"message":"merge method is not allowed"}""")
        val preconditionFailed: GiteaError =
          GiteaError.PreconditionFailed("stale content", """{"message":"stale content"}""")
        val locked: GiteaError =
          GiteaError.Locked("repository is archived", """{"message":"repository is archived"}""")

        assertTrue(
          auth == Auth.Basic("octo", "secret"),
          error == GiteaError.RateLimited(Some(Instant.parse("2026-06-18T00:00:00Z")), "rate limited"),
          methodNotAllowed == GiteaError.MethodNotAllowed(
            "merge method is not allowed",
            """{"message":"merge method is not allowed"}"""
          ),
          preconditionFailed == GiteaError.PreconditionFailed("stale content", """{"message":"stale content"}"""),
          locked == GiteaError.Locked("repository is archived", """{"message":"repository is archived"}""")
        )
      },
      test("decodes Gitea error payload") {
        val decoded = """{ "message": "not found", "url": "https://docs.gitea.com/api" }"""
          .fromJson[GiteaErrorPayload]

        assertTrue(
          decoded == Right(GiteaErrorPayload(Some("not found"), Some("https://docs.gitea.com/api")))
        )
      }
    )
