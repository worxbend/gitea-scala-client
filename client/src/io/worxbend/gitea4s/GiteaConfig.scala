package io.worxbend.gitea4s

import io.worxbend.gitea4s.model.Auth
import sttp.model.Uri
import zio.{ZIO, ZLayer}

import scala.concurrent.duration.*
import scala.util.Try

final case class GiteaConfig(
    baseUrl: Uri,
    auth: Auth,
    timeout: FiniteDuration,
    pageSize: Int,
    userAgent: Option[String],
    otp: Option[String],
    maxRetries: Int
)

sealed trait GiteaConfigError extends Product with Serializable:
  def message: String
  override final def toString: String = message

object GiteaConfigError:
  final case class MissingRequiredEnv(name: String) extends GiteaConfigError:
    val message: String = s"$name is required"

  final case class InvalidEnv(name: String, reason: String) extends GiteaConfigError:
    val message: String = s"$name $reason"

  final case class InvalidCredentialEnv(reason: String) extends GiteaConfigError:
    val message: String = reason

  final case class EnvironmentUnavailable(reason: String) extends GiteaConfigError:
    val message: String = s"environment variables are unavailable: $reason"

object GiteaConfig:
  object Env:
    val url: String = "GITEA_URL"
    val token: String = "GITEA_TOKEN"
    val username: String = "GITEA_USERNAME"
    val password: String = "GITEA_PASSWORD"
    val pageSize: String = "GITEA_PAGE_SIZE"
    val timeout: String = "GITEA_TIMEOUT"

  val defaultTimeout: FiniteDuration = 30.seconds
  val defaultPageSize: Int = 50

  def default(baseUrl: Uri, auth: Auth = Auth.Anonymous): GiteaConfig =
    GiteaConfig(
      baseUrl = baseUrl,
      auth = auth,
      timeout = defaultTimeout,
      pageSize = defaultPageSize,
      userAgent = Some("gitea4s"),
      otp = None,
      maxRetries = 0
    )

  def withToken(baseUrl: Uri, token: String): GiteaConfig =
    default(baseUrl, Auth.Token(token))

  def withBasic(baseUrl: Uri, username: String, password: String): GiteaConfig =
    default(baseUrl, Auth.Basic(username, password))

  def anonymous(baseUrl: Uri): GiteaConfig =
    default(baseUrl, Auth.Anonymous)

  def fromEnv(env: Map[String, String]): Either[GiteaConfigError, GiteaConfig] =
    for
      baseUrl <- requiredBaseUrl(env)
      auth <- authFromEnv(env)
      pageSize <- positiveIntFromEnv(env, Env.pageSize, defaultPageSize)
      timeout <- finiteDurationFromEnv(env, Env.timeout, defaultTimeout)
    yield default(baseUrl, auth).copy(
      timeout = timeout,
      pageSize = pageSize
    )

  def fromEnvironment: ZIO[Any, GiteaConfigError, GiteaConfig] =
    ZIO
      .attempt(scala.sys.env.toMap)
      .mapError(error =>
        GiteaConfigError.EnvironmentUnavailable(Option(error.getMessage).getOrElse(error.getClass.getName))
      )
      .flatMap(env => ZIO.fromEither(fromEnv(env)))

  def layerFromEnv(env: Map[String, String]): ZLayer[Any, GiteaConfigError, GiteaConfig] =
    ZLayer.fromZIO(ZIO.fromEither(fromEnv(env)))

  val environmentLayer: ZLayer[Any, GiteaConfigError, GiteaConfig] =
    ZLayer.fromZIO(fromEnvironment)

  private def requiredBaseUrl(env: Map[String, String]): Either[GiteaConfigError, Uri] =
    nonBlank(env, Env.url) match
      case None => Left(GiteaConfigError.MissingRequiredEnv(Env.url))
      case Some(raw) =>
        Uri.parse(raw.trim)
          .left
          .map(_ => GiteaConfigError.InvalidEnv(Env.url, "must be an absolute HTTP(S) URL"))
          .flatMap { uri =>
            val validScheme = uri.scheme.exists(s => s.equalsIgnoreCase("http") || s.equalsIgnoreCase("https"))
            if uri.isAbsolute && uri.host.exists(_.nonEmpty) && validScheme then Right(uri)
            else Left(GiteaConfigError.InvalidEnv(Env.url, "must be an absolute HTTP(S) URL"))
          }

  private def authFromEnv(env: Map[String, String]): Either[GiteaConfigError, Auth] =
    nonBlank(env, Env.token) match
      case Some(token) => Right(Auth.Token(token))
      case None =>
        (nonBlank(env, Env.username), nonBlank(env, Env.password)) match
          case (Some(username), Some(password)) => Right(Auth.Basic(username, password))
          case (None, None) => Right(Auth.Anonymous)
          case _ =>
            Left(
              GiteaConfigError.InvalidCredentialEnv(
                s"${Env.username} and ${Env.password} must be set together when ${Env.token} is absent"
              )
            )

  private def positiveIntFromEnv(
      env: Map[String, String],
      name: String,
      defaultValue: Int
  ): Either[GiteaConfigError, Int] =
    nonBlank(env, name) match
      case None => Right(defaultValue)
      case Some(raw) =>
        raw.trim.toIntOption.filter(_ > 0).toRight(GiteaConfigError.InvalidEnv(name, "must be a positive integer"))

  private def finiteDurationFromEnv(
      env: Map[String, String],
      name: String,
      defaultValue: FiniteDuration
  ): Either[GiteaConfigError, FiniteDuration] =
    nonBlank(env, name) match
      case None => Right(defaultValue)
      case Some(raw) =>
        Try(Duration(raw.trim)).toEither
          .left
          .map(_ => GiteaConfigError.InvalidEnv(name, "must be a positive finite duration such as 30s"))
          .flatMap {
            case duration: FiniteDuration if duration > Duration.Zero => Right(duration)
            case _ => Left(GiteaConfigError.InvalidEnv(name, "must be a positive finite duration such as 30s"))
          }

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).filter(_.trim.nonEmpty)
