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
)
