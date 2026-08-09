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
  /** The published constructor. Unchanged: the stall budget is not a knob
    * callers have asked for, and adding a parameter to this signature would
    * break binary compatibility for 1.0.0.
    */
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

  private def stream(descriptor: GiteaDownloadRequest): ZStream[Any, GiteaError, Byte] =
    // `asStreamUnsafe` is the only response description that lets the body escape as a
    // stream: the safe variants close the body as soon as the function they are handed
    // returns, which would defeat the point here. Ownership passes to the stream built
    // below, which cancels the subscription when its scope closes. Error bodies are not
    // affected — `asStreamUnsafe` reads a non-2xx body fully as a `String` first.
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
            case Right(bytes) =>
              bytes
                .mapError(GiteaError.TransportError.apply)
                // The only limit on this path was `readTimeout`, which reaches
                // the JDK as `HttpRequest.timeout` and stops applying the
                // moment response headers arrive. So the path deliberately
                // used for the *largest* bodies was the one with no backstop
                // at all: a server that answered `200 OK` and then stopped
                // sending held the consumer open indefinitely.
                //
                // `ZStream#timeoutFail` bounds each pull rather than the whole
                // stream, which is what makes it safe here — a legitimately
                // slow multi-gigabyte archive is never cut off, only a server
                // that has stopped producing.
                .timeoutFail(ZioGiteaDownloads.stalled(stallTimeout))(stallTimeout)
            case Left(errorBody) => ZStream.fail(GiteaResponseMapper.toError(response.copy(body = errorBody)))
        }
    }

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
