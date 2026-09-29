package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class AnnotatedTagObject(
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object AnnotatedTagObject:
  given JsonCodec[AnnotatedTagObject] = DeriveJsonCodec.gen[AnnotatedTagObject]
