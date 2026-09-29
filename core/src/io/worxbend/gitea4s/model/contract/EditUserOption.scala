package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class EditUserOption(
    @jsonField("active") active: Option[Boolean] = None,
    @jsonField("admin") admin: Option[Boolean] = None,
    @jsonField("allow_create_organization") allowCreateOrganization: Option[Boolean] = None,
    @jsonField("allow_git_hook") allowGitHook: Option[Boolean] = None,
    @jsonField("allow_import_local") allowImportLocal: Option[Boolean] = None,
    @jsonField("description") description: Option[String] = None,
    @jsonField("email") email: Option[String] = None,
    @jsonField("full_name") fullName: Option[String] = None,
    @jsonField("location") location: Option[String] = None,
    @jsonField("login_name") loginName: Option[String] = None,
    @jsonField("max_repo_creation") maxRepoCreation: Option[Long] = None,
    @jsonField("must_change_password") mustChangePassword: Option[Boolean] = None,
    @jsonField("password") password: Option[String] = None,
    @jsonField("prohibit_login") prohibitLogin: Option[Boolean] = None,
    @jsonField("restricted") restricted: Option[Boolean] = None,
    @jsonField("source_id") sourceId: Long,
    @jsonField("visibility") visibility: Option[String] = None,
    @jsonField("website") website: Option[String] = None
):
  override def toString: String = "EditUserOption(<redacted>)"

object EditUserOption:
  given JsonCodec[EditUserOption] = DeriveJsonCodec.gen[EditUserOption]
