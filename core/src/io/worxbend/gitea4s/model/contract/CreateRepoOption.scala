package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class CreateRepoOption(
    @jsonField("auto_init") autoInit: Option[Boolean] = None,
    @jsonField("default_branch") defaultBranch: Option[String] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("gitignores") gitignores: Option[String] = None,
    @jsonField("issue_labels") issueLabels: Option[String] = None,
    @jsonField("license") license: Option[String] = None,
    @jsonField("name") name: String,
    @jsonField("object_format_name") objectFormatName: Option[String] = None,
    @jsonField("private") `private`: Option[Boolean] = None,
    @jsonField("readme") readme: Option[String] = None,
    @jsonField("template") template: Option[Boolean] = None,
    @jsonField("trust_model") trustModel: Option[String] = None
)

object CreateRepoOption:
  given JsonCodec[CreateRepoOption] = DeriveJsonCodec.gen[CreateRepoOption]
