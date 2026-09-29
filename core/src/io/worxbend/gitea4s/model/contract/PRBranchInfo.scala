package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PRBranchInfo(
    @jsonField("label") label: Option[String] = None,
    @jsonField("ref") ref: Option[String] = None,
    @jsonField("repo") repo: Option[Repository] = None,
    @jsonField("repo_id") repoId: Option[Long] = None,
    @jsonField("sha") sha: Option[String] = None
)

object PRBranchInfo:
  given JsonCodec[PRBranchInfo] = DeriveJsonCodec.gen[PRBranchInfo]
