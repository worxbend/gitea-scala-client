package io.worxbend.gitea4s

import io.worxbend.gitea4s.model.Auth
import sttp.model.Uri

import scala.concurrent.duration.*

final case class GiteaConfig(
    baseUrl: Uri,
    auth: Auth,
    timeout: FiniteDuration,
    pageSize: Int,
    userAgent: Option[String],
    otp: Option[String],
    maxRetries: Int
)

object GiteaConfig:
  val defaultTimeout: FiniteDuration = 30.seconds
  val defaultPageSize: Int = 50

  def default(baseUrl: Uri, auth: Auth = Auth.Anonymous): GiteaConfig =
    GiteaConfig(
      baseUrl = baseUrl,
      auth = auth,
      timeout = defaultTimeout,
      pageSize = defaultPageSize,
      userAgent = Some("gitea4s"),
      otp = None,
      maxRetries = 0
    )
