package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class PayloadCommit(
    @jsonField("added") added: Option[List[String]] = None,
    @jsonField("author") author: Option[PayloadUser] = None,
    @jsonField("committer") committer: Option[PayloadUser] = None,
    @jsonField("id") id: Option[String] = None,
    @jsonField("message") message: Option[String] = None,
    @jsonField("modified") modified: Option[List[String]] = None,
    @jsonField("removed") removed: Option[List[String]] = None,
    @jsonField("timestamp") timestamp: Option[java.time.Instant] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("verification") verification: Option[PayloadCommitVerification] = None
)

object PayloadCommit:
  given JsonCodec[PayloadCommit] = DeriveJsonCodec.gen[PayloadCommit]
