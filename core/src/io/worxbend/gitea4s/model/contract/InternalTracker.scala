package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class InternalTracker(
    @jsonField("allow_only_contributors_to_track_time") allowOnlyContributorsToTrackTime: Option[Boolean] = None,
    @jsonField("enable_issue_dependencies") enableIssueDependencies: Option[Boolean] = None,
    @jsonField("enable_time_tracker") enableTimeTracker: Option[Boolean] = None
)

object InternalTracker:
  given JsonCodec[InternalTracker] = DeriveJsonCodec.gen[InternalTracker]
