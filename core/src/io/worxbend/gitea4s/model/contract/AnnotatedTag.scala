package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class AnnotatedTag(
    @jsonField("message") message: Option[String] = None,
    @jsonField("object") `object`: Option[AnnotatedTagObject] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("tag") tag: Option[String] = None,
    @jsonField("tagger") tagger: Option[CommitUser] = None,
    @jsonField("url") url: Option[String] = None,
    @jsonField("verification") verification: Option[PayloadCommitVerification] = None
)

object AnnotatedTag:
  given JsonCodec[AnnotatedTag] = DeriveJsonCodec.gen[AnnotatedTag]
