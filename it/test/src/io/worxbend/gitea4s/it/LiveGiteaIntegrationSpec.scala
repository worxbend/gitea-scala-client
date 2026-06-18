package io.worxbend.gitea4s.it

import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.RepoListParams
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
      }
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

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).map(_.trim).filter(_.nonEmpty)
