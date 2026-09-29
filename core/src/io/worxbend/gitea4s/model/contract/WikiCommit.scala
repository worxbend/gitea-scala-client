package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class WikiCommit(
    @jsonField("author") author: Option[CommitUser] = None,
    @jsonField("commiter") commiter: Option[CommitUser] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("sha") sha: Option[String] = None
)

object WikiCommit:
  given JsonCodec[WikiCommit] = DeriveJsonCodec.gen[WikiCommit]
