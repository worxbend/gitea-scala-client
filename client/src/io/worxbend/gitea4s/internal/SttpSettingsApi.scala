package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.SettingsApi
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GiteaRequests
import io.worxbend.gitea4s.model.{GeneralAPISettings, GeneralAttachmentSettings, GeneralRepoSettings, GeneralUISettings}
import zio.IO

private[gitea4s] final class SttpSettingsApi(config: GiteaConfig, executor: GiteaRequestExecutor) extends SettingsApi:
  override def api: IO[GiteaError, GeneralAPISettings] =
    executor.send(GiteaRequests.generalAPISettings(config))

  override def attachments: IO[GiteaError, GeneralAttachmentSettings] =
    executor.send(GiteaRequests.generalAttachmentSettings(config))

  override def repositories: IO[GiteaError, GeneralRepoSettings] =
    executor.send(GiteaRequests.generalRepositorySettings(config))

  override def ui: IO[GiteaError, GeneralUISettings] =
    executor.send(GiteaRequests.generalUISettings(config))
