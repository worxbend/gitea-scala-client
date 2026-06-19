package io.worxbend.gitea4s.it

import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.{ContentsParams, RepoListParams}
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig, GiteaConfigError}
import zio.ZIO
import zio.test.*

object LiveGiteaIntegrationSpec extends ZIOSpecDefault:
  def spec =
    suite("LiveGiteaIntegrationSpec")(
      test("calls GET /user when live credentials are configured") {
        withLiveClient { client =>
          client.me.map { user =>
            assertTrue(user.login.exists(_.nonEmpty))
          }
        }
      },
      test("streams the authenticated user's repositories through the live backend") {
        withLiveClient { client =>
          for
            user <- client.me
            username <- ZIO.fromOption(user.login).orElseFail("GET /user response did not include login")
            repos <- client.list(username, RepoListParams(limit = Some(1))).take(1).runCollect
          yield assertTrue(repos.length <= 1)
        }
      },
      test("loads a slash-containing Git ref through the live backend") {
        withLiveClient { client =>
          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            ref <- liveEnv(Env.ref)
            refs <- client.gitRefs(owner, repo, ref)
          yield assertTrue(refs.exists(_.ref.exists(_.endsWith(ref))))
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.ref)(nonEmptyValue),
      test("loads an annotated Git tag through the live backend") {
        withLiveClient { client =>
          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            sha <- liveEnv(Env.annotatedTagSha)
            tag <- client.annotatedTag(owner, repo, sha)
          yield assertTrue(tag.sha.contains(sha))
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.annotatedTagSha)(nonEmptyValue),
      test("loads repository contents for a configured filepath through the live backend") {
        withLiveClient { client =>
          val params = nonBlank(sys.env.toMap, Env.contentsRef) match
            case Some(ref) => ContentsParams(ref = Some(ref))
            case None      => ContentsParams.default

          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            filepath <- liveEnv(Env.contentsFilepath)
            contents <- client.contents(owner, repo, filepath, params)
          yield assertTrue(contents != null)
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.contentsFilepath)(nonEmptyValue)
    ) @@ TestAspect.ifEnv(GiteaConfig.Env.url)((value: String) => value.trim.nonEmpty) @@
      TestAspect.ifEnv(GiteaConfig.Env.token)((value: String) => value.trim.nonEmpty)

  private def withLiveClient(
      run: GiteaClient => ZIO[Any, Any, TestResult]
  ): ZIO[Any, Any, TestResult] =
    liveConfig match
      case Right(None) => ZIO.succeed(assertTrue(true))
      case Left(error) => ZIO.fail(error.message)
      case Right(Some(config)) =>
        ZIO.serviceWithZIO[GiteaClient](run).provideLayer(ZioGiteaBackend.configured(config))

  private def liveConfig: Either[GiteaConfigError, Option[GiteaConfig]] =
    val env = sys.env.toMap
    if nonBlank(env, GiteaConfig.Env.url).isEmpty || nonBlank(env, GiteaConfig.Env.token).isEmpty then Right(None)
    else GiteaConfig.fromEnv(env).map(Some(_))

  private def liveEnv(name: String): ZIO[Any, String, String] =
    ZIO.fromOption(nonBlank(sys.env.toMap, name)).orElseFail(s"$name must be configured for this live test")

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).map(_.trim).filter(_.nonEmpty)

  private def nonEmptyValue(value: String): Boolean =
    value.trim.nonEmpty

  private object Env:
    val owner = "GITEA_OWNER"
    val repo = "GITEA_REPO"
    val ref = "GITEA_REF"
    val annotatedTagSha = "GITEA_ANNOTATED_TAG_SHA"
    val contentsFilepath = "GITEA_CONTENTS_FILEPATH"
    val contentsRef = "GITEA_CONTENTS_REF"
