package io.worxbend.gitea4s

import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigValueType}
import io.worxbend.gitea4s.model.Auth
import io.worxbend.gitea4s.observability.GiteaObserver
import sttp.model.Uri
import zio.{ZIO, ZLayer}

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*
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
):
  /** Returns a copy with the base URL replaced. */
  def withBaseUrl(value: Uri): GiteaConfig = copy(baseUrl = value)

  /** Returns a copy authenticating differently. */
  def withAuth(value: Auth): GiteaConfig = copy(auth = value)

  /** Returns a copy with a different per-request read timeout. */
  def withTimeout(value: FiniteDuration): GiteaConfig = copy(timeout = value)

  /** Returns a copy requesting a different number of items per page. */
  def withPageSize(value: Int): GiteaConfig = copy(pageSize = value)

  /** Returns a copy sending a different `User-Agent`. */
  def withUserAgent(value: Option[String]): GiteaConfig = copy(userAgent = value)

  /** Returns a copy sending the given one-time password as `X-Gitea-OTP`. */
  def withOtp(value: Option[String]): GiteaConfig = copy(otp = value)

  /** Returns a copy retrying failed idempotent requests a different number of times. */
  def withMaxRetries(value: Int): GiteaConfig = copy(maxRetries = value)

  /** Returns a copy reporting completed requests to the given observer. */
  def withObserver(value: GiteaObserver): GiteaConfig = copy(observer = value)

  /** The request headers for a given `Accept` value, derived once per config.
    *
    * Every input except `accept` is fixed for the lifetime of a config, so
    * there is no reason to rebuild these per request. For
    * [[io.worxbend.gitea4s.model.Auth.Basic]] that matters beyond the object
    * count: deriving the header on each call left a fresh cleartext
    * `username:password` string and its backing byte array on the heap every
    * time, multiplying the copies a heap dump or a swap file could recover.
    *
    * The three `Accept` values this library actually sends are memoised; any
    * other value is still handled, just without the memoisation, so adding an
    * endpoint that negotiates a new content type cannot silently be served the
    * wrong `Accept` header.
    */
  private[gitea4s] def headersAccepting(accept: String): Map[String, String] =
    accept match
      case GiteaConfig.applicationJson => jsonHeaders
      case GiteaConfig.octetStream => octetStreamHeaders
      case GiteaConfig.textPlain => textPlainHeaders
      case other => buildHeaders(other)

  private[gitea4s] lazy val jsonHeaders: Map[String, String] = buildHeaders(GiteaConfig.applicationJson)
  private[gitea4s] lazy val octetStreamHeaders: Map[String, String] = buildHeaders(GiteaConfig.octetStream)
  private[gitea4s] lazy val textPlainHeaders: Map[String, String] = buildHeaders(GiteaConfig.textPlain)

  private def buildHeaders(accept: String): Map[String, String] =
    List(
      Some("Accept" -> accept),
      GiteaConfig.authorizationHeader(auth),
      userAgent.map("User-Agent" -> _),
      otp.map("X-Gitea-OTP" -> _)
    ).flatten.toMap

  /** Redacts the one-time password.
    *
    * `auth` redacts itself. `otp` is a bare `Option[String]` on this case
    * class, so without an override the compiler-generated `toString` prints
    * it — and `s"starting with $config"` is the most natural diagnostic line
    * anyone writes.
    */
  override def toString: String =
    val redactedOtp = otp.map(_ => "***")
    s"GiteaConfig($baseUrl, $auth, $timeout, $pageSize, $userAgent, $redactedOtp, $maxRetries, $observer)"

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
    val userAgent: String = "GITEA_USER_AGENT"

    /** The `X-Gitea-OTP` header value.
      *
      * Present for parity with the HOCON reader, which has always accepted it.
      * Be aware of what it can and cannot do: `X-Gitea-OTP` carries a
      * *time-based* one-time password, so a value fixed in the environment is
      * stale within about thirty seconds. It suits a short-lived command, not a
      * long-running process.
      */
    val otp: String = "GITEA_OTP"

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

  private[gitea4s] val applicationJson: String = "application/json"
  private[gitea4s] val octetStream: String = "application/octet-stream"
  private[gitea4s] val textPlain: String = "text/plain"

  val defaultTimeout: FiniteDuration = 30.seconds
  val defaultPageSize: Int = 50

  /** How many times an idempotent request is retried by default.
    *
    * Only GET and HEAD requests are ever retried, so this cannot duplicate a
    * write. Delays are jittered, capped, and honour `Retry-After` when the
    * server sends one.
    */
  val defaultMaxRetries: Int = 3

  def default(baseUrl: Uri, auth: Auth = Auth.Anonymous): GiteaConfig =
    GiteaConfig(
      baseUrl = baseUrl,
      auth = auth,
      timeout = defaultTimeout,
      pageSize = defaultPageSize,
      userAgent = Some("gitea4s"),
      otp = None,
      maxRetries = defaultMaxRetries
    )

  private[gitea4s] def authorizationHeader(auth: Auth): Option[(String, String)] =
    auth match
      case Auth.Token(value) => Some("Authorization" -> s"token $value")
      case Auth.OAuth2(token) => Some("Authorization" -> s"Bearer $token")
      case Auth.Basic(username, password) =>
        val raw = s"$username:$password".getBytes(StandardCharsets.UTF_8)
        Some("Authorization" -> s"Basic ${Base64.getEncoder.encodeToString(raw)}")
      case Auth.Anonymous => None

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
      maxRetries <- nonNegativeIntFromEnv(env, Env.maxRetries, defaultMaxRetries)
      // Through the same guard as the credentials: both become header content,
      // and reading them with a plain `nonBlank` would reopen the injection
      // hole that guard exists to close.
      userAgent <- headerSafe(nonBlank(env, Env.userAgent), GiteaConfigError.InvalidEnv(Env.userAgent, _))
      otp <- headerSafe(nonBlank(env, Env.otp), GiteaConfigError.InvalidEnv(Env.otp, _))
    yield
      val baseConfig = default(baseUrl, auth)
      baseConfig.copy(
        timeout = timeout,
        pageSize = pageSize,
        maxRetries = maxRetries,
        // Same fallback as the HOCON reader, so both sources default alike.
        userAgent = userAgent.orElse(baseConfig.userAgent),
        otp = otp
      )

  def fromTypesafeConfig(config: Config, path: String = Typesafe.root): Either[GiteaConfigError, GiteaConfig] =
    for
      section <- typesafeSection(config, path)
      baseUrl <- requiredBaseUrlFromConfig(section, qualified(path, Typesafe.url))
      auth <- authFromConfig(section, path)
      pageSize <- positiveIntFromConfig(section, qualified(path, Typesafe.pageSize), Typesafe.pageSize, defaultPageSize)
      timeout <- finiteDurationFromConfig(section, qualified(path, Typesafe.timeout), Typesafe.timeout, defaultTimeout)
      maxRetries <- nonNegativeIntFromConfig(
        section,
        qualified(path, Typesafe.maxRetries),
        Typesafe.maxRetries,
        defaultMaxRetries
      )
      userAgent <- optionalHeaderFromConfig(section, qualified(path, Typesafe.userAgent), Typesafe.userAgent)
      otp <- optionalHeaderFromConfig(section, qualified(path, Typesafe.otp), Typesafe.otp)
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
      .map(error => GiteaConfigError.ConfigUnavailable(parseFailureMessage(error)))
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
        .mapError(error => GiteaConfigError.ConfigUnavailable(parseFailureMessage(error)))
        .flatMap(config => ZIO.fromEither(fromTypesafeConfig(config)))
    )

  private def requiredBaseUrl(env: Map[String, String]): Either[GiteaConfigError, Uri] =
    nonBlank(env, Env.url) match
      case None => Left(GiteaConfigError.MissingRequiredEnv(Env.url))
      case Some(raw) =>
        parseBaseUrl(raw, GiteaConfigError.InvalidEnv(Env.url, "must be an absolute HTTP(S) URL"))

  private def authFromEnv(env: Map[String, String]): Either[GiteaConfigError, Auth] =
    def header(name: String): Either[GiteaConfigError, Option[String]] =
      headerSafe(nonBlank(env, name), GiteaConfigError.InvalidEnv(name, _))

    for
      token <- header(Env.token)
      username <- header(Env.username)
      password <- header(Env.password)
      auth <- credentials(token, username, password, Env.username, Env.password, Env.token)
    yield auth

  private def credentials(
      token: Option[String],
      username: Option[String],
      password: Option[String],
      usernameLabel: String,
      passwordLabel: String,
      tokenLabel: String
  ): Either[GiteaConfigError, Auth] =
    token match
      case Some(value) => Right(Auth.Token(value))
      case None =>
        (username, password) match
          case (Some(user), Some(secret)) => Right(Auth.Basic(user, secret))
          case (None, None) => Right(Auth.Anonymous)
          case _ =>
            Left(
              GiteaConfigError.InvalidCredentialEnv(
                s"$usernameLabel and $passwordLabel must be set together when $tokenLabel is absent"
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
      if config.hasPath(path) then
        val section = config.getConfig(path)
        misspelledKey(section, path).toLeft(section)
      else Left(GiteaConfigError.MissingRequiredConfig(path))
    catch case error: ConfigException => Left(GiteaConfigError.InvalidConfig(path, safeMessage(error)))

  /** The nine settings this reader understands. */
  private val knownTypesafeKeys: List[String] =
    List(
      Typesafe.url,
      Typesafe.token,
      Typesafe.username,
      Typesafe.password,
      Typesafe.pageSize,
      Typesafe.timeout,
      Typesafe.userAgent,
      Typesafe.otp,
      Typesafe.maxRetries
    )

  /** Catches a setting spelled the way the environment spells it.
    *
    * Typesafe Config does no normalisation, so `maxRetries` in a `gitea4s { }`
    * block is simply a different key from `max-retries` and was read by nobody —
    * the config loaded cleanly and the setting did nothing. The environment
    * names actively invite that mistake: someone who knows `GITEA_MAX_RETRIES`
    * writes `maxRetries` or `max_retries` far more readily than `max-retries`.
    *
    * Only *near misses* are rejected: a key that collapses onto a known setting
    * once case and separators are ignored, but is not spelled like it.
    * Genuinely unrelated keys are left alone, because applications legitimately
    * keep their own settings beside these, and failing on those would turn a
    * silent typo into a broken startup for configurations that work today.
    */
  private def misspelledKey(section: Config, path: String): Option[GiteaConfigError] =
    def normalise(key: String): String = key.toLowerCase.filter(_.isLetterOrDigit)

    val knownByNormalised = knownTypesafeKeys.map(key => normalise(key) -> key).toMap

    section
      .root()
      .keySet()
      .asScala
      .toList
      .sorted
      .flatMap(key => knownByNormalised.get(normalise(key)).filterNot(_ == key).map(key -> _))
      .headOption
      .map { (written, known) =>
        GiteaConfigError.InvalidConfig(qualified(path, written), s"is not a known setting; did you mean '$known'?")
      }

  private def requiredBaseUrlFromConfig(config: Config, path: String): Either[GiteaConfigError, Uri] =
    optionalStringFromConfig(config, path, Typesafe.url).flatMap {
      case None => Left(GiteaConfigError.MissingRequiredConfig(path))
      case Some(raw) => parseBaseUrl(raw, GiteaConfigError.InvalidConfig(path, "must be an absolute HTTP(S) URL"))
    }

  private def authFromConfig(config: Config, rootPath: String): Either[GiteaConfigError, Auth] =
    val tokenPath = qualified(rootPath, Typesafe.token)
    val usernamePath = qualified(rootPath, Typesafe.username)
    val passwordPath = qualified(rootPath, Typesafe.password)

    def header(path: String, key: String): Either[GiteaConfigError, Option[String]] =
      optionalStringFromConfig(config, path, key)
        .flatMap(value => headerSafe(value, GiteaConfigError.InvalidConfig(path, _)))

    for
      token <- header(tokenPath, Typesafe.token)
      username <- header(usernamePath, Typesafe.username)
      password <- header(passwordPath, Typesafe.password)
      auth <- credentials(token, username, password, usernamePath, passwordPath, tokenPath)
    yield auth

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
    else if isUnitlessDuration(config, localPath) then
      Left(GiteaConfigError.InvalidConfig(path, durationRequirement))
    else
      try
        config.getDuration(localPath).toScala match
          case duration: FiniteDuration if duration > Duration.Zero => Right(duration)
          case _ => Left(GiteaConfigError.InvalidConfig(path, durationRequirement))
      catch case _: ConfigException => Left(GiteaConfigError.InvalidConfig(path, durationRequirement))

  /** Whether a HOCON duration was written without a unit.
    *
    * Typesafe Config reads a bare number in duration position as
    * *milliseconds*. So `timeout = 30` parsed happily as 30ms, cleared the
    * positive check, and gave every request a 30-millisecond budget — while the
    * identical `GITEA_TIMEOUT=30` was rejected by the environment reader with a
    * message telling the user to write `30s`. The same text meant two very
    * different things depending on where it was written, and the wrong one was
    * silent: every call then failed as a transport timeout, three retries deep,
    * with nothing pointing at the config file.
    *
    * A quoted `"30"` is read the same way, so strings are checked too — a
    * duration string always ends in a unit letter.
    *
    * Only the unitless case is rejected here. `getDuration` still handles
    * everything else, because HOCON accepts spellings Scala's own `Duration`
    * parser does not (`30 m`, `1 day`), and reading them with a shared grammar
    * would break configuration files that already work.
    */
  private def isUnitlessDuration(config: Config, localPath: String): Boolean =
    config.getValue(localPath).valueType match
      case ConfigValueType.NUMBER => true
      case ConfigValueType.STRING => !config.getString(localPath).trim.lastOption.exists(_.isLetter)
      case _ => false

  private val durationRequirement: String = "must be a positive finite duration such as 30s"

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
        if uri.isAbsolute && uri.host.exists(_.nonEmpty) && validScheme then Right(stripUserInfo(uri))
        else Left(invalid)
      }

  // A base URL of the form https://user:secret@gitea.example was accepted and
  // kept whole. Nothing ever sends those credentials — neither the JDK client
  // nor OkHttp transmits URI userinfo, and Gitea's REST API does not honour it
  // — but the authority is rendered into every sttp exception message, so a
  // connect failure would surface the password inside a TransportError.
  // Stripping rather than rejecting keeps a URL that works today working.
  private def stripUserInfo(uri: Uri): Uri =
    uri.authority.flatMap(_.userInfo) match
      case None => uri
      case Some(_) => uri.copy(authority = uri.authority.map(_.userInfo(None)))

  private def qualified(rootPath: String, localPath: String): String =
    s"$rootPath.$localPath"

  private def safeMessage(error: Throwable): String =
    Option(error.getMessage).getOrElse(error.getClass.getName)

  // A HOCON syntax error quotes the offending source text back at you, and the
  // offending text is frequently adjacent to the token — a missing `=` after
  // `token` yields "Key 'token ghp_SUPERSECRET' may not be followed by token".
  // Report the position instead, which is what a person needs to fix the file.
  // Only parse failures are narrowed: the type errors raised elsewhere in this
  // object name a path and a type and never quote a value.
  private def parseFailureMessage(error: Throwable): String =
    error match
      case parse: ConfigException.Parse =>
        val origin = parse.origin
        val line = if origin.lineNumber >= 0 then s":${origin.lineNumber}" else ""
        s"could not parse HOCON at ${origin.description}$line"
      case other => safeMessage(other)

  // Trims before returning, not only before the emptiness test. Reading a
  // secret out of a file leaves a trailing newline on it, and an untrimmed
  // token produces an unrecoverable failure: the JDK rejects a header value
  // containing LF with an IllegalArgumentException that quotes the credential,
  // and basic auth is worse still, because the newline base64-encodes into a
  // syntactically valid header and the server just answers 401 forever. Every
  // other reader here already compensated individually with `raw.trim`.
  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).map(_.trim).filter(_.nonEmpty)

  /** Rejects a value that cannot legally become an HTTP header.
    *
    * Trimming, above, only strips the ends. A CR or LF in the *middle* of a
    * token survived into `s"token $value"` and failed later — as an untyped
    * `IllegalArgumentException` from the JDK that quotes the credential back,
    * after the request had already been retried. On a backend that does not
    * validate, an embedded newline is header injection rather than a crash.
    *
    * Checking at parse time turns all of that into one typed error, raised
    * where the setting is named. It covers the values that become header
    * content: the credentials, the user agent, and the one-time password.
    *
    * Scope limit: `GiteaConfig.withToken` and `Auth.Token(...)` are public and
    * construct a config without going through here, so this hardens the two
    * parsing entry points, not every way to build a `GiteaConfig`.
    */
  private def headerSafe(
      value: Option[String],
      invalid: String => GiteaConfigError
  ): Either[GiteaConfigError, Option[String]] =
    value match
      case Some(raw) if raw.exists(isControlCharacter) =>
        // Names the setting, never the value: this runs on secrets.
        Left(invalid("must not contain control characters"))
      case other => Right(other)

  private def isControlCharacter(character: Char): Boolean =
    character.isControl

  /** Reads a HOCON string that will be sent as a header value. */
  private def optionalHeaderFromConfig(
      config: Config,
      path: String,
      key: String
  ): Either[GiteaConfigError, Option[String]] =
    optionalStringFromConfig(config, path, key)
      .flatMap(value => headerSafe(value, GiteaConfigError.InvalidConfig(path, _)))
