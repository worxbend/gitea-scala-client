package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import sttp.client4.{Request, Response}

final case class GiteaRequest[A](
    endpoint: GiteaEndpoint,
    request: Request[String],
    decode: Response[String] => Either[GiteaError, A]
)
