package io.worxbend.gitea4s.backend.zio

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{ArchiveParams, ContentsParams, GiteaDownloadRequest, GiteaRequests, GiteaResponseMapper}
import sttp.capabilities.zio.ZioStreams
import sttp.client4.*
import zio.Task
import zio.stream.ZStream

/** Streaming binary downloads, exposed only on `backend-zio`.
  *
  * Unlike `GiteaClient`'s `repos.rawFile` / `repos.mediaFile` / `repos.archive`,
  * which buffer the whole body into a `Chunk[Byte]`, these stream the response
  * lazily as `ZStream[Any, GiteaError, Byte]`, so large files and archives never
  * have to fit in memory. This requires a `ZioStreams` stream backend, which the
  * OkHttp bridge cannot provide — hence it lives here rather than on
  * `GiteaClient`.
  *
  * These streams are not retried: a partially consumed body cannot be safely
  * replayed. Use the buffered `GiteaClient` methods where retry matters.
  */
trait GiteaDownloads:
  def rawFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): ZStream[Any, GiteaError, Byte]

  def mediaFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): ZStream[Any, GiteaError, Byte]

  def archive(
      owner: String,
      repo: String,
      archive: String,
      params: ArchiveParams = ArchiveParams.default
  ): ZStream[Any, GiteaError, Byte]

final class ZioGiteaDownloads(config: GiteaConfig, backend: StreamBackend[Task, ZioStreams]) extends GiteaDownloads:
  override def rawFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): ZStream[Any, GiteaError, Byte] =
    stream(GiteaRequests.rawFileDownload(config, owner, repo, filepath, params))

  override def mediaFile(
      owner: String,
      repo: String,
      filepath: String,
      params: ContentsParams = ContentsParams.default
  ): ZStream[Any, GiteaError, Byte] =
    stream(GiteaRequests.mediaFileDownload(config, owner, repo, filepath, params))

  override def archive(
      owner: String,
      repo: String,
      archive: String,
      params: ArchiveParams = ArchiveParams.default
  ): ZStream[Any, GiteaError, Byte] =
    stream(GiteaRequests.archiveDownload(config, owner, repo, archive, params))

  private def stream(descriptor: GiteaDownloadRequest): ZStream[Any, GiteaError, Byte] =
    val request =
      basicRequest
        .get(descriptor.uri)
        .headers(descriptor.headers)
        .readTimeout(descriptor.timeout)
        .response(asStreamUnsafe(ZioStreams))

    ZStream.unwrap {
      request
        .send(backend)
        .mapError(GiteaError.TransportError.apply)
        .map { response =>
          response.body match
            case Right(bytes) => bytes.mapError(GiteaError.TransportError.apply)
            case Left(errorBody) => ZStream.fail(GiteaResponseMapper.toError(response.copy(body = errorBody)))
        }
    }
