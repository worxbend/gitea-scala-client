package io.worxbend.gitea4s.model

import io.worxbend.gitea4s.error.GiteaError
import zio.Chunk
import zio.json.*
import zio.test.*

import java.time.Instant

object CoreModelsSpec extends ZIOSpecDefault:
  def spec =
    suite("Core models")(
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
        val notificationSubject =
          """{ "title": "Invalid", "state": "stale", "type": "Message" }""".fromJson[NotificationSubject]
        val pullReview = """{ "id": 1, "state": "STALE" }""".fromJson[PullReview]
        val commitStatus = """{ "id": 1, "status": "queued" }""".fromJson[CommitStatus]
        val createStatus = """{ "state": "queued" }""".fromJson[CreateStatusOption]
        val mergeOption = """{ "Do": "cherry-pick" }""".fromJson[MergePullRequestOption]

        assertTrue(
          issue.isLeft,
          repository.isLeft,
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
        val locked: GiteaError =
          GiteaError.Locked("repository is archived", """{"message":"repository is archived"}""")

        assertTrue(
          auth == Auth.Basic("octo", "secret"),
          error == GiteaError.RateLimited(Some(Instant.parse("2026-06-18T00:00:00Z")), "rate limited"),
          methodNotAllowed == GiteaError.MethodNotAllowed(
            "merge method is not allowed",
            """{"message":"merge method is not allowed"}"""
          ),
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
