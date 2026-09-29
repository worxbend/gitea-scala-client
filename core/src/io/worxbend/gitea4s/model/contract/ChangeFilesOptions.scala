package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ChangeFilesOptions(
    @jsonField("author") author: Option[Identity] = None,
    @jsonField("branch") branch: Option[String] = None,
    @jsonField("committer") committer: Option[Identity] = None,
    @jsonField("dates") dates: Option[CommitDateOptions] = None,
    @jsonField("files") files: List[ChangeFileOperation],
    @jsonField("force_push") forcePush: Option[Boolean] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("new_branch") newBranch: Option[String] = None,
    @jsonField("signoff") signoff: Option[Boolean] = None
)

object ChangeFilesOptions:
  given JsonCodec[ChangeFilesOptions] = DeriveJsonCodec.gen[ChangeFilesOptions]
