package io.worxbend.gitea4s.http

import sttp.model.Uri

import scala.concurrent.duration.FiniteDuration

/** A prepared binary-download request.
  *
  * It carries everything a backend needs to issue the GET — the resolved URI,
  * the headers (including `Accept: application/octet-stream` and auth), and the
  * read timeout — without committing to a buffered or a streaming response. The
  * buffered path is `GiteaClient`'s `repos.rawFile`/`mediaFile`/`archive`; the
  * streaming path is backend-zio's `GiteaDownloads`.
  */
final case class GiteaDownloadRequest(
    endpoint: GiteaEndpoint,
    uri: Uri,
    headers: Map[String, String],
    timeout: FiniteDuration
):
  /** Redacts the credential-bearing headers.
    *
    * By the time a request reaches this type the token has already been
    * flattened into `headers`, so redacting [[io.worxbend.gitea4s.model.Auth]]
    * alone would not cover it. Read `headers` directly for the real values.
    */
  override def toString: String =
    val safeHeaders = headers.map { (name, value) =>
      if GiteaDownloadRequest.redactedHeaders.contains(name.toLowerCase) then s"$name -> ***"
      else s"$name -> $value"
    }
    s"GiteaDownloadRequest($endpoint, $uri, Map(${safeHeaders.mkString(", ")}), $timeout)"

object GiteaDownloadRequest:
  private val redactedHeaders: Set[String] = Set("authorization", "x-gitea-otp")
