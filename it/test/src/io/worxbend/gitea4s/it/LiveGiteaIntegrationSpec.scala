package io.worxbend.gitea4s.it

import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.http.{ArchiveParams, ContentsParams, RepoListParams}
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig, GiteaConfigError}
import zio.{Chunk, ZIO}
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
        TestAspect.ifEnv(Env.contentsFilepath)(nonEmptyValue),
      test("downloads a configured raw repository file through the live backend") {
        withLiveClient { client =>
          val params = nonBlank(sys.env.toMap, Env.rawRef) match
            case Some(ref) => ContentsParams(ref = Some(ref))
            case None      => ContentsParams.default

          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            filepath <- liveEnv(Env.rawFilepath)
            bytes <- client.rawFile(owner, repo, filepath, params)
          yield assertTrue(bytes.nonEmpty)
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.rawFilepath)(nonEmptyValue),
      test("downloads a configured repository archive through the live backend") {
        withLiveClient { client =>
          val params = archiveParams(sys.env.toMap)

          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            archive <- liveEnv(Env.archive)
            bytes <- client.archive(owner, repo, archive, params)
          yield assertTrue(bytes.nonEmpty)
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.archive)(nonEmptyValue),
      test("loads configured release metadata through the live backend") {
        withLiveClient { client =>
          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            releaseId <- liveEnvLong(Env.releaseId)
            release <- client.release(owner, repo, releaseId)
          yield assertTrue(release.id.contains(releaseId))
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.releaseId)(nonEmptyValue),
      test("loads configured release metadata by tag through the live backend") {
        withLiveClient { client =>
          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            tag <- liveEnv(Env.releaseTag)
            release <- client.releaseByTag(owner, repo, tag)
          yield assertTrue(release.tagName.contains(tag))
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.releaseTag)(nonEmptyValue),
      test("loads configured release asset metadata list through the live backend") {
        withLiveClient { client =>
          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            releaseId <- liveEnvLong(Env.releaseId)
            expectedAssetId <- optionalLiveEnvLong(Env.releaseAssetId)
            assets <- client.releaseAssets(owner, repo, releaseId)
          yield expectedAssetId match
            case Some(assetId) => assertTrue(assets.exists(_.id.contains(assetId)))
            case None          => assertTrue(assets.forall(_.id.forall(_ > 0L)))
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.releaseId)(nonEmptyValue),
      test("loads configured release asset metadata through the live backend") {
        withLiveClient { client =>
          for
            owner <- liveEnv(Env.owner)
            repo <- liveEnv(Env.repo)
            releaseId <- liveEnvLong(Env.releaseId)
            assetId <- liveEnvLong(Env.releaseAssetId)
            asset <- client.releaseAsset(owner, repo, releaseId, assetId)
          yield assertTrue(asset.id.contains(assetId))
        }
      } @@ TestAspect.ifEnv(Env.owner)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.repo)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.releaseId)(nonEmptyValue) @@
        TestAspect.ifEnv(Env.releaseAssetId)(nonEmptyValue)
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

  private def liveEnvLong(name: String): ZIO[Any, String, Long] =
    liveEnv(name).flatMap { value =>
      ZIO.fromOption(value.toLongOption).orElseFail(s"$name must be a whole number for this live test")
    }

  private def optionalLiveEnvLong(name: String): ZIO[Any, String, Option[Long]] =
    nonBlank(sys.env.toMap, name) match
      case Some(value) =>
        ZIO.fromOption(value.toLongOption).map(Some(_)).orElseFail(s"$name must be a whole number for this live test")
      case None => ZIO.succeed(None)

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).map(_.trim).filter(_.nonEmpty)

  private def archiveParams(env: Map[String, String]): ArchiveParams =
    val paths = nonBlank(env, Env.archivePaths)
      .map(_.split(Env.archivePathsDelimiter).toIndexedSeq.map(_.trim).filter(_.nonEmpty))
      .getOrElse(IndexedSeq.empty)

    if paths.isEmpty then ArchiveParams.default
    else ArchiveParams(path = Chunk.fromIterable(paths))

  private def nonEmptyValue(value: String): Boolean =
    value.trim.nonEmpty

  private object Env:
    val owner = "GITEA_OWNER"
    val repo = "GITEA_REPO"
    val ref = "GITEA_REF"
    val annotatedTagSha = "GITEA_ANNOTATED_TAG_SHA"
    val contentsFilepath = "GITEA_CONTENTS_FILEPATH"
    val contentsRef = "GITEA_CONTENTS_REF"
    val rawFilepath = "GITEA_RAW_FILEPATH"
    val rawRef = "GITEA_RAW_REF"
    val archive = "GITEA_ARCHIVE"
    val archivePaths = "GITEA_ARCHIVE_PATHS"
    val archivePathsDelimiter = ","
    val releaseId = "GITEA_RELEASE_ID"
    val releaseTag = "GITEA_RELEASE_TAG"
    val releaseAssetId = "GITEA_RELEASE_ASSET_ID"
