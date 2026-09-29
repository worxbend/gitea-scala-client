package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Commit(
    @jsonField("author") author: Option[User] = None,
    @jsonField("commit") commit: Option[RepoCommit] = None,
    @jsonField("committer") committer: Option[User] = None,
    @jsonField("created") created: Option[java.time.Instant] = None,
    @jsonField("files") files: Option[List[CommitAffectedFiles]] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("parents") parents: Option[List[CommitMeta]] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("stats") stats: Option[CommitStats] = None,
    @jsonField("url") url: Option[String] = None
)

object Commit:
  given JsonCodec[Commit] = DeriveJsonCodec.gen[Commit]
