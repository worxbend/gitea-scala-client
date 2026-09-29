package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GenerateRepoOption(
    @jsonField("avatar") avatar: Option[Boolean] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("git_content") gitContent: Option[Boolean] = None,
    @jsonField("git_hooks") gitHooks: Option[Boolean] = None,
    @jsonField("labels") labels: Option[Boolean] = None,
    @jsonField("name") name: String,
    @jsonField("owner") owner: String,
    @jsonField("private") `private`: Option[Boolean] = None,
    @jsonField("protected_branch") protectedBranch: Option[Boolean] = None,
    @jsonField("topics") topics: Option[Boolean] = None,
    @jsonField("webhooks") webhooks: Option[Boolean] = None
)

object GenerateRepoOption:
  given JsonCodec[GenerateRepoOption] = DeriveJsonCodec.gen[GenerateRepoOption]
