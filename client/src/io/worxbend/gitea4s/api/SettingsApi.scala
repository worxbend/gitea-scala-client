package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{GeneralAPISettings, GeneralAttachmentSettings, GeneralRepoSettings, GeneralUISettings}
import zio.IO

/** Public instance settings reported by the Gitea server. */
trait SettingsApi:
  def api: IO[GiteaError, GeneralAPISettings]
  def attachments: IO[GiteaError, GeneralAttachmentSettings]
  def repositories: IO[GiteaError, GeneralRepoSettings]
  def ui: IO[GiteaError, GeneralUISettings]
