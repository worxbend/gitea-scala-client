package io.worxbend.gitea4s.model

import zio.json.*

final case class CreateRepo(
    name: String,
    description: Option[String] = None,
    @jsonField("private") isPrivate: Option[Boolean] = None,
    @jsonField("auto_init") autoInit: Option[Boolean] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    gitignores: Option[String] = None,
    license: Option[String] = None,
    readme: Option[String] = None
)

object CreateRepo:
  given JsonEncoder[CreateRepo] = DeriveJsonEncoder.gen[CreateRepo]
  given JsonDecoder[CreateRepo] = DeriveJsonDecoder.gen[CreateRepo]
