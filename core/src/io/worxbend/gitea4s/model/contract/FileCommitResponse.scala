package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class FileCommitResponse(
    @jsonField("author") author: Option[CommitUser] = None,
    @jsonField("committer") committer: Option[CommitUser] = None,
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("parents") parents: Option[List[CommitMeta]] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("tree") tree: Option[CommitMeta] = None,
    @jsonField("url") url: Option[String] = None
)

object FileCommitResponse:
  given JsonCodec[FileCommitResponse] = DeriveJsonCodec.gen[FileCommitResponse]
