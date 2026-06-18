package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import sttp.client4.{Request, Response}

final case class GiteaRequest[A](
    endpoint: GiteaEndpoint,
    request: Request[String],
    decode: Response[String] => Either[GiteaError, A],
    retryable: Boolean
)

object GiteaRequest:
  private val readOnlyMethods = Set("GET", "HEAD")

  def isReadOnly(endpoint: GiteaEndpoint): Boolean =
    readOnlyMethods.contains(endpoint.method.toUpperCase)
