package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaRequest
import sttp.client4.Backend
import zio.{IO, Task, ZIO}

final class GiteaRequestExecutor(backend: Backend[Task]):
  def send[A](request: GiteaRequest[A]): IO[GiteaError, A] =
    request.request
      .send(backend)
      .mapError(GiteaError.TransportError.apply)
      .flatMap(response => ZIO.fromEither(request.decode(response)))
