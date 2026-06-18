package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.ApiReference
import io.worxbend.gitea4s.{GiteaConfig, GiteaConfigError}
import zio.{Console, ZIO, ZIOAppDefault}

object ShowApiReference extends ZIOAppDefault:
  def run =
    val referenceLine = s"gitea4s targets Gitea API ${ApiReference.gitea1262.version}"

    liveConfig match
      case Right(None) =>
        Console.printLine(referenceLine) *>
          Console.printLine(
            "Set GITEA_URL with GITEA_TOKEN or GITEA_USERNAME/GITEA_PASSWORD to also call GET /user."
          )
      case Left(error) =>
        Console.printLine(referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)
      case Right(Some(config)) =>
        Console.printLine(referenceLine) *>
          ZIO.serviceWithZIO[io.worxbend.gitea4s.GiteaClient](_.me)
            .provideLayer(ZioGiteaBackend.configured(config))
            .foldZIO(
              error => Console.printLineError(s"GET /user failed: ${describeFailure(error)}") *> ZIO.fail(error),
              user => Console.printLine(s"Authenticated as ${user.login.getOrElse("<unknown>")}")
            )

  private def liveConfig: Either[GiteaConfigError, Option[GiteaConfig]] =
    if sys.env.get(GiteaConfig.Env.url).forall(_.trim.isEmpty) then Right(None)
    else GiteaConfig.fromEnv(sys.env.toMap).map(Some(_))

  private def describeFailure(error: GiteaError | Throwable): String =
    error match
      case giteaError: GiteaError => describe(giteaError)
      case throwable: Throwable => s"backend initialization failed: ${throwable.getMessage}"

  private def describe(error: GiteaError): String =
    error match
      case GiteaError.BadRequest(message, _) => s"bad request: $message"
      case GiteaError.Unauthorized(message, _) => s"unauthorized: $message"
      case GiteaError.Forbidden(message, _) => s"forbidden: $message"
      case GiteaError.NotFound(message, _) => s"not found: $message"
      case GiteaError.Conflict(message, _) => s"conflict: $message"
      case GiteaError.UnprocessableEntity(message, _) => s"unprocessable entity: $message"
      case GiteaError.RateLimited(resetAt, _) => s"rate limited; reset at ${resetAt.fold("unknown")(_.toString)}"
      case GiteaError.ServerError(status, _) => s"server error: HTTP $status"
      case GiteaError.DecodeError(message, _) => s"decode error: $message"
      case GiteaError.TransportError(cause) => s"transport error: ${cause.getMessage}"
