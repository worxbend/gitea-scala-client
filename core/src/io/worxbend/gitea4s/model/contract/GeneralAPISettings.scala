package io.worxbend.gitea4s.model.contract

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
