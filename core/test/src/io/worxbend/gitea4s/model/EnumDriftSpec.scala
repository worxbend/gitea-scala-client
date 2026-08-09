package io.worxbend.gitea4s.model

import zio.Chunk
import zio.json.*
import zio.test.*

/** Forward compatibility with a Gitea that has learned a new enum value.
  *
  * Gitea adds enum members between minor versions. Before these tests, one
  * unrecognised string anywhere in a page failed the decode of the entire page
  * — `GiteaResponseMapper` decodes a page as a single `Chunk[A]` — so a caller
  * lost every other item on it, and the `ZStream` above it then failed, losing
  * every later page too.
  */
object EnumDriftSpec extends ZIOSpecDefault:
  def spec =
    suite("unknown enum values")(
      test("a page survives an item carrying an unknown value") {
        // The valid first thread is the point: it used to be discarded along
        // with the one the client could not fully understand.
        val page =
          """[{"id":1,"unread":true,"subject":{"title":"a","state":"open"}},
             {"id":2,"unread":false,"subject":{"title":"b","state":"quantum-superposition"}}]"""

        page.fromJson[Chunk[NotificationThread]] match
          case Left(error) => assertNever(s"page failed to decode: $error")
          case Right(threads) =>
            assertTrue(
              threads.size == 2,
              threads.head.subject.flatMap(_.state).contains(NotificationSubjectState.Open),
              // Unreadable, so absent — never a wrong value, and never fatal.
              threads(1).subject.flatMap(_.state).isEmpty,
              // Everything the client *can* read is still read.
              threads(1).subject.flatMap(_.title).contains("b")
            )
      },
      test("each read-position enum tolerates an unknown value") {
        val issue = """{"id":1,"state":"archived"}""".fromJson[Issue]
        val review = """{"id":1,"state":"RESCINDED"}""".fromJson[PullReview]
        val team = """{"id":1,"permission":"superuser"}""".fromJson[Team]
        val status = """{"id":1,"status":"cancelled"}""".fromJson[CommitStatus]
        val repo = """{"id":1,"object_format_name":"sha512"}""".fromJson[Repository]

        assertTrue(
          issue.map(_.state) == Right(None),
          review.map(_.state) == Right(None),
          team.map(_.permission) == Right(None),
          status.map(_.state) == Right(None),
          repo.map(_.objectFormatName) == Right(None)
        )
      },
      test("a known value still decodes, and absence is still absence") {
        assertTrue(
          """{"id":1,"state":"closed"}""".fromJson[Issue].map(_.state) == Right(Some(IssueState.Closed)),
          """{"id":1}""".fromJson[Issue].map(_.state) == Right(None),
          """{"id":1,"state":null}""".fromJson[Issue].map(_.state) == Right(None)
        )
      },
      test("write positions stay strict") {
        // Sending a value the server will reject is worth failing on. These are
        // request bodies, so leniency would only hide a caller's mistake.
        assertTrue(
          MergePullRequestMethod.fromString("teleport").isLeft,
          IssueState.fromString("archived").isLeft,
          """{"Do":"teleport"}""".fromJson[MergePullRequestOption].isLeft
        )
      }
    )
