package io.worxbend.gitea4s.model

import zio.json.*
import zio.test.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

object ApiReferenceSpec extends ZIOSpecDefault:
  def spec =
    suite("ApiReference")(
      test("records the version of the contract actually vendored here") {
        // Previously this compared the constant against a retyped copy of
        // itself, so refreshing `plugin-redoc-2.yaml` to a newer Gitea would
        // ship a banner naming the old version with the build still green.
        // Reading the spec is the only way this assertion can fail for the
        // reason it exists.
        assertTrue(
          ApiReference.gitea1262.version == swaggerVersion(),
          ApiReference.gitea1262.document == "plugin-redoc-2.yaml",
          // The document it names must be the one that was read.
          Files.isRegularFile(swaggerPath())
        )
      },
      test("round-trips through zio-json") {
        val decoded = ApiReference.gitea1262.toJson.fromJson[ApiReference]

        assertTrue(decoded == Right(ApiReference.gitea1262))
      }
    )

  /** The `info.version` of the vendored OpenAPI document. */
  private def swaggerVersion(): String =
    val lines = Files.readString(swaggerPath(), StandardCharsets.UTF_8).linesIterator
    lines
      .map(_.trim)
      .collectFirst { case line if line.startsWith("version:") => line.stripPrefix("version:").trim }
      .getOrElse(throw IllegalStateException("plugin-redoc-2.yaml declares no version"))

  /** Walks up from the working directory, as the other spec readers do, so this
    * runs from any module.
    */
  private def swaggerPath(): Path =
    Iterator
      .iterate(Paths.get("").toAbsolutePath)(_.getParent)
      .takeWhile(_ != null)
      .map(_.resolve("plugin-redoc-2.yaml"))
      .find(Files.isRegularFile(_))
      .getOrElse(Paths.get("plugin-redoc-2.yaml").toAbsolutePath)
