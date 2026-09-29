package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GitHook(
    @jsonField("content") content: Option[String] = None,
    @jsonField("is_active") isActive: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None
)

object GitHook:
  given JsonCodec[GitHook] = DeriveJsonCodec.gen[GitHook]
