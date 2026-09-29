package io.worxbend.gitea4s.backend.zio

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.{ArchiveParams, ContentsParams, GiteaDownloadRequest, GiteaRequests, GiteaResponseMapper}
import sttp.capabilities.zio.ZioStreams
import sttp.client4.*
import zio.{Duration, Task, durationInt}
import zio.stream.ZStream

import java.util.concurrent.TimeoutException

/** Streaming binary downloads, exposed only on `backend-zio`.
  *
  * Unlike `GiteaClient`'s `repos.rawFile` / `repos.mediaFile` / `repos.archive`,
  * which buffer the whole body into a `Chunk[Byte]`, these stream the response
  * lazily as `ZStream[Any, GiteaError, Byte]`, so large files and archives never
  * have to fit in memory. This requires a `ZioStreams` stream backend, which the
  * OkHttp bridge cannot provide — hence it lives here rather than on
  * `GiteaClient`.
  *
  * The returned stream is consume-once. Nothing happens until it is run; running
  * it sends the request and hands the still-open HTTP response to the stream,
  * which from that point owns the connection. The connection is released when the
  * stream reaches its end, fails, or the scope it runs in closes — so an early
  * exit such as `runHead`, `take(n)` or interruption cancels the download instead
  * of reading the rest of the body. The value cannot be replayed: running it a
  * second time sends a second request rather than re-reading the first response.
  *
  * These streams are not retried: a partially consumed body cannot be safely
  * replayed. Use the buffered `GiteaClient` methods where retry matters.
  *
  * They also emit no `GiteaObserver` events. The request is sent straight at
  * the backend rather than through `GiteaRequestExecutor`, which is what owns
  * the observer — so a configured observer sees every `GiteaClient` call and
  * none of these.
  *
  * A stalled download fails rather than hanging. The budget measures the gap
  * between chunks, not the total download time, so an archive that takes an
  * hour to arrive is never penalised as long as it keeps arriving — see
  * [[ZioGiteaDownloads.stallTimeout]].
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

final class ZioGiteaDownloads private (
    config: GiteaConfig,
    backend: StreamBackend[Task, ZioStreams],
    stallTimeout: Duration
) extends GiteaDownloads:
  /** Uses the standard stall budget; callers do not configure it per download. */
  def this(config: GiteaConfig, backend: StreamBackend[Task, ZioStreams]) =
    this(config, backend, ZioGiteaDownloads.stallTimeout)

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

  private type DownloadBody = Either[String, ZStream[Any, Throwable, Byte]]

  private def stream(descriptor: GiteaDownloadRequest): ZStream[Any, GiteaError, Byte] =
    ZStream.unwrap(fetch(descriptor).map(responseStream))

  private def fetch(descriptor: GiteaDownloadRequest): zio.IO[GiteaError, Response[DownloadBody]] =
    // The unsafe response description transfers the open body to the returned stream.
    // Safe variants close it when their callback returns; non-2xx bodies are still
    // read fully as strings before the response is returned.
    basicRequest
      .get(descriptor.uri)
      .headers(descriptor.headers)
      .readTimeout(descriptor.timeout)
      .response(asStreamUnsafe(ZioStreams))
      .send(backend)
      .mapError(GiteaError.TransportError.apply)

  private def responseStream(response: Response[DownloadBody]): ZStream[Any, GiteaError, Byte] =
    response.body match
      case Right(bytes) => protectBody(bytes)
      case Left(errorBody) => ZStream.fail(GiteaResponseMapper.toError(response.copy(body = errorBody)))

  private def protectBody(bytes: ZStream[Any, Throwable, Byte]): ZStream[Any, GiteaError, Byte] =
    // The JDK request timeout stops applying once headers arrive. Bound each
    // subsequent pull so stalled bodies fail without limiting total download time.
    bytes
      .mapError(GiteaError.TransportError.apply)
      .timeoutFail(ZioGiteaDownloads.stalled(stallTimeout))(stallTimeout)

object ZioGiteaDownloads:
  /** How long a running download may go without producing a single byte.
    *
    * Deliberately generous: this is a backstop against a connection that has
    * stopped producing, not a latency target.
    */
  val stallTimeout: Duration = 5.minutes

  /** Builds a downloader with a shorter budget, so the stall path can be
    * exercised against a real clock instead of racing a `TestClock`.
    */
  private[zio] def withStallTimeout(
      config: GiteaConfig,
      backend: StreamBackend[Task, ZioStreams],
      stallTimeout: Duration
  ): ZioGiteaDownloads =
    new ZioGiteaDownloads(config, backend, stallTimeout)

  private def stalled(after: Duration): GiteaError =
    GiteaError.TransportError(
      new TimeoutException(s"Gitea download produced no data for $after")
    )
