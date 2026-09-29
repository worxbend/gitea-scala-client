package io.worxbend.gitea4s.model

import zio.json.*

final case class GeneralAPISettings(
    @jsonField("default_git_trees_per_page") defaultGitTreesPerPage: Option[Long] = None,
    @jsonField("default_max_blob_size") defaultMaxBlobSize: Option[Long] = None,
    @jsonField("default_max_response_size") defaultMaxResponseSize: Option[Long] = None,
    @jsonField("default_paging_num") defaultPagingNum: Option[Long] = None,
    @jsonField("max_response_items") maxResponseItems: Option[Long] = None
)

object GeneralAPISettings:
  given JsonCodec[GeneralAPISettings] = DeriveJsonCodec.gen[GeneralAPISettings]

final case class GeneralAttachmentSettings(
    @jsonField("allowed_types") allowedTypes: Option[String] = None,
    enabled: Option[Boolean] = None,
    @jsonField("max_files") maxFiles: Option[Long] = None,
    @jsonField("max_size") maxSize: Option[Long] = None
)

object GeneralAttachmentSettings:
  given JsonCodec[GeneralAttachmentSettings] = DeriveJsonCodec.gen[GeneralAttachmentSettings]

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

final case class GeneralUISettings(
    @jsonField("allowed_reactions") allowedReactions: Option[List[String]] = None,
    @jsonField("custom_emojis") customEmojis: Option[List[String]] = None,
    @jsonField("default_theme") defaultTheme: Option[String] = None
)

object GeneralUISettings:
  given JsonCodec[GeneralUISettings] = DeriveJsonCodec.gen[GeneralUISettings]
