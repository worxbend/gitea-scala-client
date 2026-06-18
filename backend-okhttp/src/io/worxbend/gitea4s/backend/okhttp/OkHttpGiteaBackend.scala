package io.worxbend.gitea4s.backend.okhttp

import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig}
import okhttp3.OkHttpClient
import sttp.capabilities.Effect
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.monad.{FunctionK, MapEffect}
import sttp.client4.okhttp.OkHttpFutureBackend
import sttp.client4.{Backend, GenericRequest, Response}
import sttp.model.Uri
import sttp.monad.{FutureMonad, MonadError}
import zio.{Runtime, Scope, Task, Unsafe, ZIO, ZLayer}

import scala.concurrent.{ExecutionContext, Future}

object OkHttpGiteaBackend:
  private given ExecutionContext = ExecutionContext.global

  val live: ZLayer[GiteaConfig, Throwable, GiteaClient] =
    ZLayer.scoped {
      for
        config <- ZIO.service[GiteaConfig]
        backend <- scopedBackend(OkHttpFutureBackend())
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
        backend <- scopedBackendSucceed(OkHttpFutureBackend.usingClient(okHttpClient))
      yield GiteaClient.fromBackend(config, backend)
    }

  def usingClient(config: GiteaConfig, okHttpClient: OkHttpClient): ZLayer[Any, Nothing, GiteaClient] =
    ZLayer.succeed(config) >>> usingClient(okHttpClient)

  private def scopedBackend(delegate: => Backend[Future]): ZIO[Scope, Throwable, Backend[Task]] =
    ZIO.acquireRelease(ZIO.attempt(FutureToZioBackend(delegate)))(_.close().ignore)

  private def scopedBackendSucceed(delegate: => Backend[Future]): ZIO[Scope, Nothing, Backend[Task]] =
    ZIO.acquireRelease(ZIO.succeed(FutureToZioBackend(delegate)))(_.close().ignore)

  private final class FutureToZioBackend(delegate: Backend[Future]) extends Backend[Task]:
    private val taskMonad = new RIOMonadAsyncError[Any]()
    private val futureMonad = new FutureMonad()

    private val taskToFuture =
      new FunctionK[Task, Future]:
        override def apply[A](task: => Task[A]): Future[A] =
          Unsafe.unsafe { implicit unsafe =>
            Runtime.default.unsafe.runToFuture(task)
          }

    private val futureToTask =
      new FunctionK[Future, Task]:
        override def apply[A](future: => Future[A]): Task[A] =
          ZIO.fromFuture(_ => future)

    override def send[T](request: GenericRequest[T, Effect[Task]]): Task[Response[T]] =
      val futureRequest = MapEffect(request, taskToFuture, futureToTask, taskMonad, futureMonad)
      ZIO.fromFuture(_ => delegate.send(futureRequest))

    override def close(): Task[Unit] =
      ZIO.fromFuture(_ => delegate.close()).unit

    override def monad: MonadError[Task] =
      taskMonad
