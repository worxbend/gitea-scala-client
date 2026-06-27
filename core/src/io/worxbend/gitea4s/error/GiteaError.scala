package io.worxbend.gitea4s.error

import java.time.Instant

/** The error channel for every gitea4s call.
  *
  * HTTP failures keep the response body; resource-state statuses map to explicit
  * cases (`MethodNotAllowed` 405, `PreconditionFailed` 412, `Locked` 423, …);
  * `RateLimited` carries the reset time when Gitea sends one; `DecodeError` keeps
  * the raw body; and `TransportError` preserves the underlying cause.
  */
sealed trait GiteaError extends Product with Serializable

object GiteaError:
  final case class BadRequest(message: String, body: String) extends GiteaError
  final case class Unauthorized(message: String, body: String) extends GiteaError
  final case class Forbidden(message: String, body: String) extends GiteaError
  final case class NotFound(message: String, body: String) extends GiteaError
  final case class MethodNotAllowed(message: String, body: String) extends GiteaError
  final case class Conflict(message: String, body: String) extends GiteaError
  final case class PreconditionFailed(message: String, body: String) extends GiteaError
  final case class UnprocessableEntity(message: String, body: String) extends GiteaError
  final case class Locked(message: String, body: String) extends GiteaError
  final case class RateLimited(resetAt: Option[Instant], body: String) extends GiteaError
  final case class ServerError(status: Int, body: String) extends GiteaError
  final case class DecodeError(message: String, body: String) extends GiteaError
  final case class TransportError(cause: Throwable) extends GiteaError
