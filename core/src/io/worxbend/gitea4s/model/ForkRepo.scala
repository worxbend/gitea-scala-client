package io.worxbend.gitea4s.model

import zio.json.*

final case class ForkRepo(
    organization: Option[String] = None,
    name: Option[String] = None
)

object ForkRepo:
  given JsonEncoder[ForkRepo] = DeriveJsonEncoder.gen[ForkRepo]
  given JsonDecoder[ForkRepo] = DeriveJsonDecoder.gen[ForkRepo]
