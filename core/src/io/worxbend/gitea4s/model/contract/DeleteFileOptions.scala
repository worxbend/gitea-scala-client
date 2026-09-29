package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class DeleteFileOptions(
    @jsonField("author") author: Option[Identity] = None,
    @jsonField("branch") branch: Option[String] = None,
    @jsonField("committer") committer: Option[Identity] = None,
    @jsonField("dates") dates: Option[CommitDateOptions] = None,
    @jsonField("force_push") forcePush: Option[Boolean] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("new_branch") newBranch: Option[String] = None,
    @jsonField("sha") sha: String,
    @jsonField("signoff") signoff: Option[Boolean] = None
)

object DeleteFileOptions:
  given JsonCodec[DeleteFileOptions] = DeriveJsonCodec.gen[DeleteFileOptions]
