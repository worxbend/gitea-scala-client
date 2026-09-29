package io.worxbend.gitea4s.model

import zio.json.*
import zio.test.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

object ApiReferenceSpec extends ZIOSpecDefault:
  def spec =
    suite("ApiReference")(
      test("records the versions of both vendored contracts") {
        // Both public references must name a document containing their version,
        // so an updated spec cannot silently leave the example banner behind.
        assertTrue(
          ApiReference.gitea1262.version == swaggerVersion(ApiReference.gitea1262.document),
          ApiReference.gitea1262.document == "plugin-redoc-2.yaml",
          ApiReference.gitea1273.version == swaggerVersion(ApiReference.gitea1273.document),
          ApiReference.gitea1273.document == "gitea-v1.27.3.yaml"
        )
      },
      test("round-trips through zio-json") {
        val decoded = ApiReference.gitea1273.toJson.fromJson[ApiReference]

        assertTrue(decoded == Right(ApiReference.gitea1273))
      }
    )

  /** The `info.version` of the vendored OpenAPI document. */
  private def swaggerVersion(document: String): String =
    val lines = Files.readString(swaggerPath(document), StandardCharsets.UTF_8).linesIterator
    lines
      .map(_.trim)
      .collectFirst { case line if line.startsWith("version:") => line.stripPrefix("version:").trim }
      .getOrElse(throw IllegalStateException(s"$document declares no version"))

  /** Walks up from the working directory, as the other spec readers do, so this
    * runs from any module.
    */
  private def swaggerPath(document: String): Path =
    Iterator
      .iterate(Paths.get("").toAbsolutePath)(_.getParent)
      .takeWhile(_ != null)
      .map(_.resolve(document))
      .find(Files.isRegularFile(_))
      .getOrElse(throw IllegalStateException(s"Cannot find $document"))
