package io.worxbend.gitea4s.backend.okhttp

import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import okhttp3.{Authenticator, Credentials, OkHttpClient, Request as OkHttpRequest, Response as OkHttpResponse, Route}
import sttp.capabilities.Effect
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.okhttp.OkHttpFutureBackend
import sttp.client4.{Backend, BackendOptions, GenericRequest, Response}
import sttp.model.Uri
import sttp.monad.MonadError
import zio.{Scope, Task, ZIO, ZLayer}

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

/** [[io.worxbend.gitea4s.GiteaClient]] layers backed by OkHttp.
  *
  * Every layer here owns or borrows exactly one `OkHttpClient` for its whole
  * lifetime, and every request goes through that one client. The layers that
  * build their own client (everything except the `usingClient` overloads) close
  * it when their scope closes, following OkHttp's documented shutdown: shut the
  * dispatcher's executor down *and* evict the connection pool.
  *
  * ==Fiber interruption does not cancel an in-flight call==
  *
  * Interrupting a fiber that is waiting on a request hands control back to the
  * interrupter immediately, but the underlying OkHttp call keeps running. Two
  * links in the chain drop the cancellation signal. sttp's `FutureMonad.async`
  * builds a `scala.concurrent.Promise`, calls the registration function and
  * throws away the `Canceler` it returns, so the OkHttp backend's
  * `Canceler(() => call.cancel())` is never wired up; and `ZIO.fromFuture`
  * installs a real interrupt handler only for a `zio.CancelableFuture`, which
  * `Promise#future` is not.
  *
  * The consequence is bounded rather than a leak. The orphaned call is capped by
  * OkHttp's call timeout, and when it does finish, sttp reads the body and closes
  * the response on OkHttp's dispatcher thread, which returns the connection to
  * the pool. What is lost is releasing the connection *early*, not releasing it
  * at all.
  *
  * `GiteaConfig.timeout` is not what bounds it. That value is the OkHttp *read*
  * timeout, which limits a single socket read rather than the call, so a server
  * dribbling one byte at a time never trips it. The cap is the separate call
  * timeout this module sets — see `callTimeoutMillis`.
  *
  * Where cancellation latency matters — a request racing a deadline shorter than
  * `GiteaConfig.timeout`, or a fiber that is interrupted often — prefer the
  * `gitea4s-backend-zio` module's `ZioGiteaBackend`, which is built on a native
  * ZIO backend and cancels the request when the fiber is interrupted.
  */
object OkHttpGiteaBackend:

  val live: ZLayer[GiteaConfig, Throwable, GiteaClient] =
    ZLayer.scoped {
      for
        config <- ZIO.service[GiteaConfig]
        executionContext <- backendExecutionContext
        client <- scopedOwnedClient(config)
        backend <- scopedBackend(OkHttpFutureBackend.usingClient(client)(using executionContext))
      yield GiteaClient.fromBackend(config, backend)
    }

  def configured(config: GiteaConfig): ZLayer[Any, Throwable, GiteaClient] =
    ZLayer.succeed(config) >>> live

  def withToken(baseUrl: Uri, token: String): ZLayer[Any, Throwable, GiteaClient] =
    configured(GiteaConfig.default(baseUrl, Auth.Token(token)))

  def withBasic(baseUrl: Uri, username: String, password: String): ZLayer[Any, Throwable, GiteaClient] =
    configured(GiteaConfig.default(baseUrl, Auth.Basic(username, password)))

  def anonymous(baseUrl: Uri): ZLayer[Any, Throwable, GiteaClient] =
    configured(GiteaConfig.default(baseUrl, Auth.Anonymous))

  def usingClient(okHttpClient: OkHttpClient): ZLayer[GiteaConfig, Nothing, GiteaClient] =
    ZLayer.scoped {
      for
        config <- ZIO.service[GiteaConfig]
        executionContext <- backendExecutionContext
        client <- ZIO.succeed(withRequestReadTimeout(okHttpClient, config))
        backend <- scopedBackend(OkHttpFutureBackend.usingClient(client)(using executionContext))
      yield GiteaClient.fromBackend(config, backend)
    }

  def usingClient(config: GiteaConfig, okHttpClient: OkHttpClient): ZLayer[Any, Nothing, GiteaClient] =
    ZLayer.succeed(config) >>> usingClient(okHttpClient)

  /** The execution context handed to sttp's `Future` machinery.
    *
    * sttp runs the tail of every response description through
    * `FutureMonad.eval`, which is `Future(...)` on whatever `ExecutionContext`
    * is in scope — for a JSON response that is the UTF-8 decode of the body.
    * Defaulting to `ExecutionContext.global` sent that work to the JVM-wide
    * fork-join pool no matter what executor the caller had configured with
    * `Runtime.setExecutor`. Taking the executor from ZIO instead keeps the work
    * inside the runtime the caller set up. The amount of work is small — one
    * `new String(bytes, charset)` per response — so this is about isolation, not
    * throughput: an application that deliberately confines gitea4s to a bounded
    * pool now actually gets that.
    *
    * The executor is read once, when the layer is built, because that is when
    * sttp's backend (and the `FutureMonad` inside it) is constructed.
    */
  private def backendExecutionContext: ZIO[Any, Nothing, ExecutionContext] =
    ZIO.executor.map(_.asExecutionContext)

  /** Builds the single `OkHttpClient` that a self-contained layer owns.
    *
    * The previous code called `OkHttpFutureBackend()`, which builds its client
    * with sttp's own one-minute default read timeout. Before every call sttp
    * compares that against the request's read timeout in
    * `OkHttpBackend.updateClientIfCustomReadTimeout` and, when they differ,
    * rebuilds the client. Every gitea4s request carries `config.timeout`
    * (30 seconds unless overridden), so the two never matched and each request
    * allocated a fresh client — copying the interceptor lists and re-deriving
    * the certificate pinner — before it could be sent. Giving the client the
    * same read timeout the requests carry turns that comparison into an
    * equality, and the copy stops happening.
    *
    * The rest mirrors sttp's `OkHttpBackend.defaultClient`, which cannot be
    * called from outside its own package. Redirects are handled by the
    * `FollowRedirectsBackend` wrapper sttp puts around this backend, so OkHttp
    * must not follow them as well; and the proxy is read from the same
    * `BackendOptions.Default` that sttp would have used, which means the JVM's
    * standard `http.proxyHost` / `socksProxyHost` system properties keep
    * working for anyone behind a corporate proxy.
    *
    * Package-private rather than private so the spec can assert on the client's
    * settings; it is not part of the published Scala API.
    */
  private[okhttp] def ownedClient(config: GiteaConfig): OkHttpClient =
    val options = BackendOptions.Default

    val builder = OkHttpClient
      .Builder()
      .followRedirects(false)
      .followSslRedirects(false)
      .connectTimeout(options.connectionTimeout.toMillis, TimeUnit.MILLISECONDS)
      .readTimeout(config.timeout.toMillis, TimeUnit.MILLISECONDS)
      .callTimeout(callTimeoutMillis, TimeUnit.MILLISECONDS)

    val withProxy = options.proxy.fold(builder) { proxy =>
      val routed = builder.proxySelector(proxy.asJavaProxySelector)
      proxy.auth.fold(routed)(auth => routed.proxyAuthenticator(proxyAuthenticator(auth)))
    }

    withProxy.build()

  private def proxyAuthenticator(auth: BackendOptions.ProxyAuth): Authenticator =
    new Authenticator:
      override def authenticate(route: Route, response: OkHttpResponse): OkHttpRequest =
        response
          .request()
          .newBuilder()
          .header("Proxy-Authorization", Credentials.basic(auth.username, auth.password))
          .build()

  /** Gives a caller-supplied client the read timeout gitea4s requests carry.
    *
    * This exists for the same reason as the read timeout in [[ownedClient]]:
    * without it sttp rebuilds the caller's client once per request. Ownership
    * does not change. `newBuilder` copies the dispatcher, the connection pool,
    * the interceptors and the TLS configuration by reference, so the derived
    * client is the caller's client in every respect that matters — including
    * that shutting theirs down shuts this one down too, and that gitea4s never
    * shuts down either.
    *
    * Package-private rather than private so the spec can assert on the derived
    * client's settings, the same reason [[ownedClient]] is; it is not part of
    * the published Scala API.
    */
  private[okhttp] def withRequestReadTimeout(client: OkHttpClient, config: GiteaConfig): OkHttpClient =
    val readTimeoutMatches = client.readTimeoutMillis().toLong == config.timeout.toMillis
    // Only when the caller left it at OkHttp's default of 0, which means "no
    // limit". A caller who chose their own call timeout keeps it.
    val needsCallTimeout = client.callTimeoutMillis() == 0

    if readTimeoutMatches && !needsCallTimeout then client
    else
      val builder = client.newBuilder().readTimeout(config.timeout.toMillis, TimeUnit.MILLISECONDS)
      (if needsCallTimeout then builder.callTimeout(callTimeoutMillis, TimeUnit.MILLISECONDS) else builder).build()

  /** A ceiling on one whole OkHttp call, which OkHttp does not set by default.
    *
    * `readTimeout` bounds one socket read, not the call, so a server that
    * emits a byte every 29 seconds against the default 30-second timeout keeps
    * a call alive indefinitely. That matters more here than it would
    * elsewhere: this backend cannot cancel an orphaned call (see the note on
    * the object above), and OkHttp's dispatcher runs five requests per host
    * and queues the rest without bound, so a handful of stuck calls stalls
    * every request behind them.
    *
    * Set from the executor's attempt budget rather than `GiteaConfig.timeout`:
    * by the time that budget is spent the caller's fiber has already given up,
    * so ending the call costs nothing — whereas `config.timeout` would kill a
    * legitimately slow archive fetch that is still making progress.
    */
  private val callTimeoutMillis: Long =
    GiteaRequestExecutor.defaultAttemptTimeout.toMillis

  /** Acquires the layer's own client and shuts it down when the scope closes.
    *
    * Package-private rather than private so the spec can run the whole acquire
    * and release cycle against a real connection; it is not part of the
    * published Scala API.
    */
  private[okhttp] def scopedOwnedClient(config: GiteaConfig): ZIO[Scope, Throwable, OkHttpClient] =
    ZIO.acquireRelease(ZIO.attempt(ownedClient(config)))(client => ZIO.succeed(shutdown(client)))

  /** OkHttp's documented shutdown, for a client nobody outside this layer holds.
    *
    * sttp's `OkHttpBackend.close` only shuts the dispatcher's executor service
    * down. That stops new work from being scheduled but leaves every pooled
    * keep-alive socket open until OkHttp's own idle sweep reclaims it five
    * minutes later — so a `ZIO.scoped` block that had exited was still holding
    * sockets it was supposed to have released. Evicting the pool is the second
    * half of the shutdown sequence OkHttp documents on `OkHttpClient`.
    */
  private def shutdown(client: OkHttpClient): Unit =
    client.dispatcher().executorService().shutdown()
    client.connectionPool().evictAll()

  private def scopedBackend(delegate: => Backend[Future]): ZIO[Scope, Nothing, Backend[Task]] =
    ZIO.acquireRelease(ZIO.succeed(FutureToZioBackend(delegate)))(_.close().ignore)

  private final class FutureToZioBackend(delegate: Backend[Future]) extends Backend[Task]:
    private val taskMonad = new RIOMonadAsyncError[Any]()

    override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
      // `Effect[F]` reaches a request only through a stream or websocket response
      // description, and this backend can express neither: the delegate is a
      // plain `Backend[Future]` with no stream or websocket capability. So the
      // request never mentions `Task` at runtime and the cast is a no-op — the
      // same one sttp's `MapEffect` performs on its `case _ => r` fall-through,
      // which is the only branch this backend could ever take.
      ZIO.fromFuture(_ => delegate.send(request.asInstanceOf[GenericRequest[T, Effect[Future]]]))

    override def close(): Task[Unit] =
      ZIO.fromFuture(_ => delegate.close()).unit

    override def monad: MonadError[Task] =
      taskMonad
