package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Auth, BranchProtectionSettings, CreateBranchProtectionOption, EditBranchProtectionOption, UpdateBranchProtectionPriorities}
import sttp.client4.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.{BackendStub, ResponseStub}
import sttp.model.{Method, StatusCode}
import zio.Task
import zio.test.*

object BranchProtectionSpec extends ZIOSpecDefault:
  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))

  private def respond(body: String, code: StatusCode = StatusCode.Ok) =
    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, code))

  private def stringBody(request: Request[?]): String =
    request.body match
      case StringBody(value, _, _) => value
      case other => other.toString

  def spec =
    suite("branch protection")(
      test("creates a rule with flattened typed settings and decodes new response fields") {
        val body = CreateBranchProtectionOption(
          BranchProtectionSettings(enableBypassAllowlist = Some(false), bypassAllowlistTeams = Some(Nil)),
          branchName = Some("release/*"), ruleName = Some("release")
        )
        val built = GiteaRequests.createBranchProtection(config, "team org", "repo", body)
        val request = built.request
        val created = built.decode(request.send(respond(
          """{"rule_name":"release","enable_bypass_allowlist":false,"bypass_allowlist_teams":[]}""", StatusCode.Created
        )))
        val login = built.decode(request.send(respond("<html>login</html>")))

        assertTrue(
          request.method == Method.POST,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/team%20org/repo/branch_protections",
          request.header("Authorization").contains("token secret"),
          request.header("Content-Type").exists(_.startsWith("application/json")),
          stringBody(request) == """{"bypass_allowlist_teams":[],"enable_bypass_allowlist":false,"branch_name":"release/*","rule_name":"release"}""",
          created.map(_.bypassAllowlistTeams) == Right(Some(Nil)),
          created.map(_.enableBypassAllowlist) == Right(Some(false)),
          login.isLeft,
          !built.retryable
        )
      },
      test("edits one escaped rule; rejects an unexpected success status") {
        val built = GiteaRequests.editBranchProtection(
          config, "owner", "repo", "release/*", EditBranchProtectionOption(
            BranchProtectionSettings(requiredApprovals = Some(2L), enableBypassAllowlist = Some(true))
          )
        )
        val request = built.request
        val edited = built.decode(request.send(respond("""{"rule_name":"release","required_approvals":2}""")))
        val wrongStatus = built.decode(request.send(respond("{}", StatusCode.Created)))
        val archived = built.decode(request.send(respond("""{"message":"archived"}""", StatusCode.Locked)))

        assertTrue(
          request.method == Method.PATCH,
          request.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branch_protections/release%2F*",
          stringBody(request) == """{"enable_bypass_allowlist":true,"required_approvals":2}""",
          edited.map(_.requiredApprovals) == Right(Some(2L)),
          wrongStatus.isLeft,
          archived.left.exists(_.isInstanceOf[GiteaError.Locked]),
          !built.retryable
        )
      },
      test("reorders and deletes rules only on 204") {
        val priority = GiteaRequests.updateBranchProtectionPriorities(config, "owner", "repo", UpdateBranchProtectionPriorities(List(3L, 1L)))
        val deleted = GiteaRequests.deleteBranchProtection(config, "owner", "repo", "release/*")
        val priorityRequest = priority.request
        val deleteRequest = deleted.request
        val updated = priority.decode(priorityRequest.send(respond("", StatusCode.NoContent)))
        val wrongStatus = priority.decode(priorityRequest.send(respond("<html>login</html>")))
        val removed = deleted.decode(deleteRequest.send(respond("", StatusCode.NoContent)))
        val missing = deleted.decode(deleteRequest.send(respond("", StatusCode.NotFound)))

        assertTrue(
          priorityRequest.method == Method.POST,
          priorityRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branch_protections/priority",
          stringBody(priorityRequest) == """{"ids":[3,1]}""",
          updated == Right(()),
          wrongStatus.isLeft,
          !priority.retryable,
          deleteRequest.method == Method.DELETE,
          deleteRequest.body == NoBody,
          removed == Right(()),
          missing.left.exists(_.isInstanceOf[GiteaError.NotFound]),
          !deleted.retryable
        )
      },
      test("list and detail preserve bypass allowlist fields") {
        val list = GiteaRequests.repoBranchProtections(config, "owner", "repo")
        val get = GiteaRequests.repoBranchProtection(config, "owner", "repo", "release/*")
        val listRequest = list.request
        val getRequest = get.request
        val listed = list.decode(listRequest.send(respond("""[{"rule_name":"release","enable_bypass_allowlist":true}]""")))
        val found = get.decode(getRequest.send(respond("""{"rule_name":"release","bypass_allowlist_usernames":["alice"]}""")))

        assertTrue(
          listRequest.method == Method.GET,
          getRequest.uri.toString == "https://gitea.example/root/api/v1/repos/owner/repo/branch_protections/release%2F*",
          listed.map(_.head.enableBypassAllowlist) == Right(Some(true)),
          found.map(_.bypassAllowlistUsernames) == Right(Some(List("alice")))
        )
      },
      test("ordinary client exposes branch protection writes") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.method == Method.POST)
          .thenRespond(ResponseStub.adjust("""{"rule_name":"release","enable_bypass_allowlist":true}""", StatusCode.Created))
          .whenRequestMatches(_.method == Method.PATCH)
          .thenRespond(ResponseStub.adjust("""{"rule_name":"release"}"""))
        val client = GiteaClient.fromBackend(config, backend)
        for
          created <- client.repos.createBranchProtection("owner", "repo", CreateBranchProtectionOption())
          edited <- client.repos.editBranchProtection("owner", "repo", "release", EditBranchProtectionOption())
        yield assertTrue(created.enableBypassAllowlist.contains(true), edited.ruleName.contains("release"))
      },
      test("ordinary client reads preserve bypass allowlists") {
        val backend = BackendStub[Task](new RIOMonadAsyncError[Any])
          .whenRequestMatches(_.uri.path.endsWith(List("branch_protections", "release")))
          .thenRespond(ResponseStub.adjust("""{"rule_name":"release","bypass_allowlist_usernames":["alice"]}"""))
          .whenRequestMatches(_.uri.path.endsWith(List("branch_protections")))
          .thenRespond(ResponseStub.adjust("""[{"rule_name":"release","enable_bypass_allowlist":false}]"""))
        val client = GiteaClient.fromBackend(config, backend)
        for
          rules <- client.repos.branchProtections("owner", "repo")
          rule <- client.repos.branchProtection("owner", "repo", "release")
        yield assertTrue(
          rules.head.enableBypassAllowlist.contains(false),
          rule.bypassAllowlistUsernames.contains(List("alice"))
        )
      }
    )
