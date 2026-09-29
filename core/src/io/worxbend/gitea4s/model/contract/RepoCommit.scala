package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RepoCommit(
    @jsonField("author") author: Option[CommitUser] = None,
    @jsonField("committer") committer: Option[CommitUser] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("tree") tree: Option[CommitMeta] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("verification") verification: Option[PayloadCommitVerification] = None
)

object RepoCommit:
  given JsonCodec[RepoCommit] = DeriveJsonCodec.gen[RepoCommit]
