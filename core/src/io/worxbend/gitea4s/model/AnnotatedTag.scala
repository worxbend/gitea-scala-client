package io.worxbend.gitea4s.model

import zio.json.*

final case class AnnotatedTag(
    message: Option[String] = None,
    @jsonField("object") gitObject: Option[AnnotatedTagObject] = None,
    sha: Option[String] = None,
    tag: Option[String] = None,
    tagger: Option[CommitUser] = None,
    url: Option[String] = None,
    verification: Option[PayloadCommitVerification] = None
)

object AnnotatedTag:
  given JsonCodec[AnnotatedTag] = DeriveJsonCodec.gen[AnnotatedTag]

final case class AnnotatedTagObject(
    sha: Option[String] = None,
    `type`: Option[String] = None,
    url: Option[String] = None
)

object AnnotatedTagObject:
  given JsonCodec[AnnotatedTagObject] = DeriveJsonCodec.gen[AnnotatedTagObject]
