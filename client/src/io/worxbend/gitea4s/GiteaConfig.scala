package io.worxbend.gitea4s

import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.observability.GiteaObserver
import sttp.model.Uri
import zio.{ZIO, ZLayer}

import scala.concurrent.duration.*
import scala.jdk.DurationConverters.*
import scala.util.Try

final case class GiteaConfig(
    baseUrl: Uri,
    auth: Auth,
    timeout: FiniteDuration,
    pageSize: Int,
    userAgent: Option[String],
    otp: Option[String],
    maxRetries: Int,
    observer: GiteaObserver = GiteaObserver.noop
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

  final case class MissingRequiredConfig(path: String) extends GiteaConfigError:
    val message: String = s"$path is required"

  final case class InvalidConfig(path: String, reason: String) extends GiteaConfigError:
    val message: String = s"$path $reason"

  final case class ConfigUnavailable(reason: String) extends GiteaConfigError:
    val message: String = s"typesafe config is unavailable: $reason"

object GiteaConfig:
  object Env:
    val url: String = "GITEA_URL"
    val token: String = "GITEA_TOKEN"
    val username: String = "GITEA_USERNAME"
    val password: String = "GITEA_PASSWORD"
    val pageSize: String = "GITEA_PAGE_SIZE"
    val timeout: String = "GITEA_TIMEOUT"
    val maxRetries: String = "GITEA_MAX_RETRIES"

  object Typesafe:
    val root: String = "gitea4s"
    val url: String = "url"
    val token: String = "token"
    val username: String = "username"
    val password: String = "password"
    val pageSize: String = "page-size"
    val timeout: String = "timeout"
    val userAgent: String = "user-agent"
    val otp: String = "otp"
    val maxRetries: String = "max-retries"

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
      maxRetries <- nonNegativeIntFromEnv(env, Env.maxRetries, 0)
    yield default(baseUrl, auth).copy(
      timeout = timeout,
      pageSize = pageSize,
      maxRetries = maxRetries
    )

  def fromTypesafeConfig(config: Config, path: String = Typesafe.root): Either[GiteaConfigError, GiteaConfig] =
    for
      section <- typesafeSection(config, path)
      baseUrl <- requiredBaseUrlFromConfig(section, qualified(path, Typesafe.url))
      auth <- authFromConfig(section, path)
      pageSize <- positiveIntFromConfig(section, qualified(path, Typesafe.pageSize), Typesafe.pageSize, defaultPageSize)
      timeout <- finiteDurationFromConfig(section, qualified(path, Typesafe.timeout), Typesafe.timeout, defaultTimeout)
      maxRetries <- nonNegativeIntFromConfig(section, qualified(path, Typesafe.maxRetries), Typesafe.maxRetries, 0)
      userAgent <- optionalStringFromConfig(section, qualified(path, Typesafe.userAgent), Typesafe.userAgent)
      otp <- optionalStringFromConfig(section, qualified(path, Typesafe.otp), Typesafe.otp)
    yield
      val baseConfig = default(baseUrl, auth)
      baseConfig.copy(
        timeout = timeout,
        pageSize = pageSize,
        userAgent = userAgent.orElse(baseConfig.userAgent),
        otp = otp,
        maxRetries = maxRetries
      )

  def fromTypesafeString(hocon: String, path: String = Typesafe.root): Either[GiteaConfigError, GiteaConfig] =
    Try(ConfigFactory.parseString(hocon).resolve()).toEither
      .left
      .map(error => GiteaConfigError.ConfigUnavailable(safeMessage(error)))
      .flatMap(fromTypesafeConfig(_, path))

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

  def layerFromTypesafeConfig(
      config: Config,
      path: String = Typesafe.root
  ): ZLayer[Any, GiteaConfigError, GiteaConfig] =
    ZLayer.fromZIO(ZIO.fromEither(fromTypesafeConfig(config, path)))

  def layerFromTypesafeString(
      hocon: String,
      path: String = Typesafe.root
  ): ZLayer[Any, GiteaConfigError, GiteaConfig] =
    ZLayer.fromZIO(ZIO.fromEither(fromTypesafeString(hocon, path)))

  val typesafeLayer: ZLayer[Any, GiteaConfigError, GiteaConfig] =
    ZLayer.fromZIO(
      ZIO
        .attempt(ConfigFactory.load().resolve())
        .mapError(error => GiteaConfigError.ConfigUnavailable(safeMessage(error)))
        .flatMap(config => ZIO.fromEither(fromTypesafeConfig(config)))
    )

  private def requiredBaseUrl(env: Map[String, String]): Either[GiteaConfigError, Uri] =
    nonBlank(env, Env.url) match
      case None => Left(GiteaConfigError.MissingRequiredEnv(Env.url))
      case Some(raw) =>
        parseBaseUrl(raw, GiteaConfigError.InvalidEnv(Env.url, "must be an absolute HTTP(S) URL"))

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

  private def nonNegativeIntFromEnv(
      env: Map[String, String],
      name: String,
      defaultValue: Int
  ): Either[GiteaConfigError, Int] =
    nonBlank(env, name) match
      case None => Right(defaultValue)
      case Some(raw) =>
        raw.trim.toIntOption.filter(_ >= 0).toRight(GiteaConfigError.InvalidEnv(name, "must be zero or a positive integer"))

  private def typesafeSection(config: Config, path: String): Either[GiteaConfigError, Config] =
    try
      if config.hasPath(path) then Right(config.getConfig(path))
      else Left(GiteaConfigError.MissingRequiredConfig(path))
    catch case error: ConfigException => Left(GiteaConfigError.InvalidConfig(path, safeMessage(error)))

  private def requiredBaseUrlFromConfig(config: Config, path: String): Either[GiteaConfigError, Uri] =
    optionalStringFromConfig(config, path, Typesafe.url).flatMap {
      case None => Left(GiteaConfigError.MissingRequiredConfig(path))
      case Some(raw) => parseBaseUrl(raw, GiteaConfigError.InvalidConfig(path, "must be an absolute HTTP(S) URL"))
    }

  private def authFromConfig(config: Config, rootPath: String): Either[GiteaConfigError, Auth] =
    val tokenPath = qualified(rootPath, Typesafe.token)
    val usernamePath = qualified(rootPath, Typesafe.username)
    val passwordPath = qualified(rootPath, Typesafe.password)

    optionalStringFromConfig(config, tokenPath, Typesafe.token).flatMap {
      case Some(token) => Right(Auth.Token(token))
      case None =>
        (
          optionalStringFromConfig(config, usernamePath, Typesafe.username),
          optionalStringFromConfig(config, passwordPath, Typesafe.password)
        ) match
          case (Right(Some(username)), Right(Some(password))) => Right(Auth.Basic(username, password))
          case (Right(None), Right(None)) => Right(Auth.Anonymous)
          case (Right(_), Right(_)) =>
            Left(
              GiteaConfigError.InvalidCredentialEnv(
                s"$usernamePath and $passwordPath must be set together when $tokenPath is absent"
              )
            )
          case (Left(error), _) => Left(error)
          case (_, Left(error)) => Left(error)
    }

  private def positiveIntFromConfig(
      config: Config,
      path: String,
      localPath: String,
      defaultValue: Int
  ): Either[GiteaConfigError, Int] =
    optionalIntFromConfig(config, path, localPath).flatMap {
      case None => Right(defaultValue)
      case Some(value) if value > 0 => Right(value)
      case Some(_) => Left(GiteaConfigError.InvalidConfig(path, "must be a positive integer"))
    }

  private def nonNegativeIntFromConfig(
      config: Config,
      path: String,
      localPath: String,
      defaultValue: Int
  ): Either[GiteaConfigError, Int] =
    optionalIntFromConfig(config, path, localPath).flatMap {
      case None => Right(defaultValue)
      case Some(value) if value >= 0 => Right(value)
      case Some(_) => Left(GiteaConfigError.InvalidConfig(path, "must be zero or a positive integer"))
    }

  private def finiteDurationFromConfig(
      config: Config,
      path: String,
      localPath: String,
      defaultValue: FiniteDuration
  ): Either[GiteaConfigError, FiniteDuration] =
    if !config.hasPath(localPath) then Right(defaultValue)
    else
      try
        config.getDuration(localPath).toScala match
          case duration: FiniteDuration if duration > Duration.Zero => Right(duration)
          case _ => Left(GiteaConfigError.InvalidConfig(path, "must be a positive finite duration such as 30s"))
      catch case _: ConfigException => Left(GiteaConfigError.InvalidConfig(path, "must be a positive finite duration such as 30s"))

  private def optionalStringFromConfig(
      config: Config,
      path: String,
      localPath: String
  ): Either[GiteaConfigError, Option[String]] =
    if !config.hasPath(localPath) then Right(None)
    else
      try Right(Option(config.getString(localPath)).map(_.trim).filter(_.nonEmpty))
      catch case error: ConfigException => Left(GiteaConfigError.InvalidConfig(path, safeMessage(error)))

  private def optionalIntFromConfig(
      config: Config,
      path: String,
      localPath: String
  ): Either[GiteaConfigError, Option[Int]] =
    if !config.hasPath(localPath) then Right(None)
    else
      try Right(Some(config.getInt(localPath)))
      catch case _: ConfigException => Left(GiteaConfigError.InvalidConfig(path, "must be an integer"))

  private def parseBaseUrl(raw: String, invalid: GiteaConfigError): Either[GiteaConfigError, Uri] =
    Uri.parse(raw.trim)
      .left
      .map(_ => invalid)
      .flatMap { uri =>
        val validScheme = uri.scheme.exists(s => s.equalsIgnoreCase("http") || s.equalsIgnoreCase("https"))
        if uri.isAbsolute && uri.host.exists(_.nonEmpty) && validScheme then Right(uri)
        else Left(invalid)
      }

  private def qualified(rootPath: String, localPath: String): String =
    s"$rootPath.$localPath"

  private def safeMessage(error: Throwable): String =
    Option(error.getMessage).getOrElse(error.getClass.getName)

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).filter(_.trim.nonEmpty)
