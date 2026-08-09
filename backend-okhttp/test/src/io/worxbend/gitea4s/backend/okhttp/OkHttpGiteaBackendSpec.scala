package io.worxbend.gitea4s.backend.okhttp

import com.sun.net.httpserver.{HttpHandler, HttpServer}
import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import okhttp3.{OkHttpClient, Request as OkHttpRequest}
import sttp.client4.*
import sttp.model.Uri
import zio.{Task, ZIO}
import zio.test.*

import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import scala.concurrent.duration.*

object OkHttpGiteaBackendSpec extends ZIOSpecDefault:
  private val baseUrl = uri"https://gitea.example"
  private val config = GiteaConfig.default(baseUrl, Auth.Anonymous)

  def spec =
    suite("OkHttpGiteaBackend")(
      test("builds a scoped live client layer") {
        ZIO.service[GiteaClient]
          .provideLayer(OkHttpGiteaBackend.configured(config))
          .map(client => assertTrue(client != null))
      },
      test("builds a client layer from a caller-owned OkHttpClient") {
        val okHttpClient = OkHttpClient()

        ZIO.service[GiteaClient]
          .provideLayer(OkHttpGiteaBackend.usingClient(config, okHttpClient))
          .map(client => assertTrue(client != null))
      },
      test("builds token and anonymous convenience layers") {
        for
          tokenClient <- ZIO.service[GiteaClient].provideLayer(OkHttpGiteaBackend.withToken(baseUrl, "secret"))
          anonymousClient <- ZIO.service[GiteaClient].provideLayer(OkHttpGiteaBackend.anonymous(baseUrl))
        yield assertTrue(tokenClient != null, anonymousClient != null)
      },
      test("gives its client the read timeout that requests will ask for") {
        // sttp compares the request's read timeout against the client's in
        // `OkHttpBackend.updateClientIfCustomReadTimeout` and, when they differ,
        // builds a throwaway client for that one call. gitea4s puts
        // `config.timeout` on every request, so the client has to carry the same
        // value for that comparison to come out equal and the copy to be skipped.
        val timeout = 17.seconds
        val client = OkHttpGiteaBackend.ownedClient(config.withTimeout(timeout))
        val request = basicRequest.get(baseUrl).readTimeout(timeout)

        assertTrue(
          client.readTimeoutMillis().toLong == request.options.readTimeout.toMillis,
          // sttp wraps this backend in a `FollowRedirectsBackend`, so OkHttp
          // must leave redirects alone, exactly as sttp's own default client does.
          !client.followRedirects(),
          !client.followSslRedirects()
        )
      },
      test("evicts the connection pool of the client it owns when the scope closes") {
        withLocalServer { serverUrl =>
          val localConfig = GiteaConfig.default(serverUrl, Auth.Anonymous)

          ZIO
            .scoped {
              for
                client <- OkHttpGiteaBackend.scopedOwnedClient(localConfig)
                _ <- fetchOnce(client, serverUrl)
              yield (client, client.connectionPool().connectionCount())
            }
            .map { (client, pooledInsideScope) =>
              assertTrue(
                // Without a live connection in the pool the eviction assertion
                // below would hold for a client that was never used at all.
                pooledInsideScope > 0,
                client.connectionPool().connectionCount() == 0,
                client.dispatcher().executorService().isShutdown
              )
            }
        }
      }
    )

  /** Runs one real request so that a keep-alive connection lands in the pool. */
  private def fetchOnce(client: OkHttpClient, url: Uri): Task[Unit] =
    ZIO.attemptBlocking {
      val response = client.newCall(OkHttpRequest.Builder().url(url.toString).build()).execute()
      try
        // The connection only goes back to the pool once the body has been read
        // to the end and closed, so drain it rather than discarding the response.
        val _ = response.body().string()
      finally response.close()
    }

  private val emptyJsonHandler: HttpHandler = exchange =>
    val body = "{}".getBytes(StandardCharsets.UTF_8)
    exchange.sendResponseHeaders(200, body.length.toLong)
    val out = exchange.getResponseBody
    try out.write(body)
    finally out.close()

  /** Serves `{}` on any path from a loopback port picked by the OS. */
  private def withLocalServer[A](use: Uri => Task[A]): Task[A] =
    ZIO.acquireReleaseWith(
      ZIO.attempt {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        val _ = server.createContext("/", emptyJsonHandler)
        server.start()
        server
      }
    )(server => ZIO.succeed(server.stop(0)))(server =>
      use(Uri.unsafeParse(s"http://127.0.0.1:${server.getAddress.getPort}/"))
    )
