package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class GeneralRepoSettings(
    @jsonField("http_git_disabled") httpGitDisabled: Option[Boolean] = None,
    @jsonField("lfs_disabled") lfsDisabled: Option[Boolean] = None,
    @jsonField("migrations_disabled") migrationsDisabled: Option[Boolean] = None,
    @jsonField("mirrors_disabled") mirrorsDisabled: Option[Boolean] = None,
    @jsonField("stars_disabled") starsDisabled: Option[Boolean] = None,
    @jsonField("time_tracking_disabled") timeTrackingDisabled: Option[Boolean] = None
)

object GeneralRepoSettings:
  given JsonCodec[GeneralRepoSettings] = DeriveJsonCodec.gen[GeneralRepoSettings]
