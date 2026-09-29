package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class MigrateRepoOptions(
    @jsonField("auth_password") authPassword: Option[String] = None,
    @jsonField("auth_token") authToken: Option[String] = None,
    @jsonField("auth_username") authUsername: Option[String] = None,
    @jsonField("aws_access_key_id") awsAccessKeyId: Option[String] = None,
    @jsonField("aws_secret_access_key") awsSecretAccessKey: Option[String] = None,
    @jsonField("clone_addr") cloneAddr: String,
    @jsonField("description") description: Option[String] = None,
    @jsonField("issues") issues: Option[Boolean] = None,
    @jsonField("labels") labels: Option[Boolean] = None,
    @jsonField("lfs") lfs: Option[Boolean] = None,
    @jsonField("lfs_endpoint") lfsEndpoint: Option[String] = None,
    @jsonField("milestones") milestones: Option[Boolean] = None,
    @jsonField("mirror") mirror: Option[Boolean] = None,
    @jsonField("mirror_interval") mirrorInterval: Option[String] = None,
    @jsonField("private") `private`: Option[Boolean] = None,
    @jsonField("pull_requests") pullRequests: Option[Boolean] = None,
    @jsonField("releases") releases: Option[Boolean] = None,
    @jsonField("repo_name") repoName: String,
    @jsonField("repo_owner") repoOwner: Option[String] = None,
    @jsonField("service") service: Option[String] = None,
    @jsonField("uid") uid: Option[Long] = None,
    @jsonField("wiki") wiki: Option[Boolean] = None
):
  override def toString: String = "MigrateRepoOptions(<redacted>)"

object MigrateRepoOptions:
  given JsonCodec[MigrateRepoOptions] = DeriveJsonCodec.gen[MigrateRepoOptions]
