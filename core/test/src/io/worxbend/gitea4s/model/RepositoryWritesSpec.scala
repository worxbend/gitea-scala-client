package io.worxbend.gitea4s.model

import zio.json.*
import zio.Chunk
import zio.test.*

object RepositoryWritesSpec extends ZIOSpecDefault:
  def spec =
    suite("repository write models")(
      test("encodes repository creation using v1.27.3 field names and enum values") {
        val body = CreateRepoOption(
          name = "scala-client",
          autoInit = Some(true),
          isPrivate = Some(true),
          objectFormatName = Some(ObjectFormatName.Sha256),
          trustModel = Some(RepoTrustModel.CollaboratorCommitter)
        )
        val json = """{"name":"scala-client","auto_init":true,"object_format_name":"sha256","private":true,"trust_model":"collaboratorcommitter"}"""

        assertTrue(body.toJson == json, json.fromJson[CreateRepoOption] == Right(body))
      },
      test("writes fork and branch source fields exactly") {
        val fork = CreateForkOption(name = Some("copy"), organization = Some("team"))
        val branch = CreateBranchRepoOption("release/new", oldRefName = Some("refs/heads/main"))

        assertTrue(
          fork.toJson == """{"name":"copy","organization":"team"}""",
          branch.toJson == """{"new_branch_name":"release/new","old_ref_name":"refs/heads/main"}""",
          branch.toJson.fromJson[CreateBranchRepoOption] == Right(branch)
        )
      },
      test("serializes credentials for the write but redacts them from diagnostics") {
        val body = EditRepoOption(
          name = Some("new-name"),
          hasActions = Some(false),
          externalTracker = Some(ExternalTracker(url = Some("https://issues.example"))),
          internalTracker = Some(InternalTracker(enableIssueDependencies = Some(true))),
          mirrorPassword = Some("sensitive-password"),
          mirrorToken = Some("sensitive-token")
        )
        val json = body.toJson

        assertTrue(
          json.contains("\"mirror_password\":\"sensitive-password\""),
          json.contains("\"mirror_token\":\"sensitive-token\""),
          json.contains("\"has_actions\":false"),
          json.contains("\"external_tracker\":{\"external_tracker_url\":\"https://issues.example\"}"),
          json.fromJson[EditRepoOption] == Right(body),
          !body.toString.contains("sensitive-password"),
          !body.toString.contains("sensitive-token")
        )
      },
      test("rejects unknown write-only trust models") {
        assertTrue(
          RepoTrustModel.fromString("unknown").isLeft,
          """{"name":"new","trust_model":"unknown"}""".fromJson[CreateRepoOption].isLeft
        )
      },
      test("encodes transfer target and team IDs") {
        val body = TransferRepoOption("new-owner", Some(Chunk(1L, 2L)))
        val json = """{"new_owner":"new-owner","team_ids":[1,2]}"""

        assertTrue(body.toJson == json, json.fromJson[TransferRepoOption] == Right(body))
      }
    )
