package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class UpdateFileOptions(
    @jsonField("author") author: Option[Identity] = None,
    @jsonField("branch") branch: Option[String] = None,
    @jsonField("committer") committer: Option[Identity] = None,
    @jsonField("content") content: String,
    @jsonField("dates") dates: Option[CommitDateOptions] = None,
    @jsonField("force_push") forcePush: Option[Boolean] = None,
    @jsonField("from_path") fromPath: Option[String] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("new_branch") newBranch: Option[String] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("signoff") signoff: Option[Boolean] = None
)

object UpdateFileOptions:
  given JsonCodec[UpdateFileOptions] = DeriveJsonCodec.gen[UpdateFileOptions]
