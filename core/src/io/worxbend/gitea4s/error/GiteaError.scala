package io.worxbend.gitea4s.error

import java.time.Instant

/** The error channel for every gitea4s call.
  *
  * HTTP failures keep the response body; resource-state statuses map to explicit
  * cases (`MethodNotAllowed` 405, `PreconditionFailed` 412, `Locked` 423, …);
  * `RateLimited` carries the reset time when Gitea sends one; `DecodeError` keeps
  * the raw body; and `TransportError` preserves the underlying cause.
  */
sealed trait GiteaError extends Product with Serializable:
  /** What went wrong, in one line, for every case.
    *
    * Nine of the thirteen cases already carried a `message`, but there was no
    * way to ask a `GiteaError` for one without enumerating all thirteen — so
    * the commonest thing a caller wants to do with an error, log it or show
    * it, required a total match that has to be revisited whenever a case is
    * added. The four that carry no message field derive one.
    *
    * Deliberately not a constructor parameter anywhere: adding a field to a
    * published case class would rewrite its `apply`, `copy` and `unapply`.
    * This adds a method and takes nothing away.
    *
    * For the full server response, match on the case and read `body`; this is
    * the summary, not a replacement for it.
    */
  def message: String

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
  final case class RateLimited(resetAt: Option[Instant], body: String) extends GiteaError:
    def message: String = resetAt.fold("Rate limited")(instant => s"Rate limited until $instant")

  final case class ServerError(status: Int, body: String) extends GiteaError:
    def message: String = s"HTTP $status"

  final case class DecodeError(message: String, body: String) extends GiteaError

  final case class TransportError(cause: Throwable) extends GiteaError:
    // A `Throwable` may have a null message, and `getClass.getName` is more use
    // to a reader than the word "null".
    def message: String = Option(cause.getMessage).getOrElse(cause.toString)
