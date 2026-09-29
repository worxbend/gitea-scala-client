package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ExternalTracker(
    @jsonField("external_tracker_format") externalTrackerFormat: Option[String] = None,
    @jsonField("external_tracker_regexp_pattern") externalTrackerRegexpPattern: Option[String] = None,
    @jsonField("external_tracker_style") externalTrackerStyle: Option[String] = None,
    @jsonField("external_tracker_url") externalTrackerUrl: Option[String] = None
)

object ExternalTracker:
  given JsonCodec[ExternalTracker] = DeriveJsonCodec.gen[ExternalTracker]
