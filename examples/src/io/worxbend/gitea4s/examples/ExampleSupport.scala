package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{ApiReference, NotificationThread, Release, Repository, User}
import io.worxbend.gitea4s.{GiteaConfig, GiteaConfigError}

private[examples] object ExampleSupport:
  val referenceLine: String = s"gitea4s targets Gitea API ${ApiReference.gitea1262.version}"

  val credentialsHint: String =
    "Set GITEA_URL with GITEA_TOKEN or GITEA_USERNAME/GITEA_PASSWORD to run the live example."

  def liveConfigFromEnv: Either[GiteaConfigError, Option[GiteaConfig]] =
    val env = sys.env.toMap
    if nonBlank(env, GiteaConfig.Env.url).isEmpty then Right(None)
    else if hasNoCredentials(env) then Right(None)
    else GiteaConfig.fromEnv(env).map(Some(_))

  def describeFailure(error: GiteaError | Throwable): String =
    error match
      case giteaError: GiteaError => describe(giteaError)
      case throwable: Throwable => s"backend initialization failed: ${throwable.getMessage}"

  def repositoryName(repository: Repository): String =
    repository.fullName
      .orElse(repository.name)
      .orElse(repository.id.map(id => s"repository#$id"))
      .getOrElse("<unknown repository>")

  def userName(user: User): String =
    user.login
      .orElse(user.loginName)
      .orElse(user.fullName)
      .orElse(user.id.map(id => s"user#$id"))
      .getOrElse("<unknown user>")

  def notificationSummary(thread: NotificationThread): String =
    val id = thread.id.map(_.toString).getOrElse("?")
    val state = if thread.unread.contains(true) then "unread" else "read"
    val repo = thread.repository.map(repositoryName).getOrElse("<unknown repository>")
    val title = thread.subject.flatMap(_.title).getOrElse("<untitled notification>")
    s"#$id [$state] $repo - $title"

  def releaseSummary(release: Release): String =
    val title =
      release.name
        .orElse(release.tagName)
        .orElse(release.id.map(id => s"release#$id"))
        .getOrElse("<untitled release>")
    val tag = release.tagName.filterNot(_ == title).fold("")(value => s" ($value)")
    val state =
      if release.draft.contains(true) then "draft"
      else if release.prerelease.contains(true) then "prerelease"
      else "published"
    val published = release.publishedAt.fold("")(instant => s" at $instant")
    s"$title$tag [$state$published]"

  private def hasNoCredentials(env: Map[String, String]): Boolean =
    nonBlank(env, GiteaConfig.Env.token).isEmpty &&
      nonBlank(env, GiteaConfig.Env.username).isEmpty &&
      nonBlank(env, GiteaConfig.Env.password).isEmpty

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).map(_.trim).filter(_.nonEmpty)

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
