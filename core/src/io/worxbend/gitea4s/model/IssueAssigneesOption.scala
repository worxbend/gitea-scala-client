package io.worxbend.gitea4s.model

import zio.Chunk
import zio.json.*

final case class IssueAssigneesOption(assignees: Chunk[String])

object IssueAssigneesOption:
  given JsonCodec[IssueAssigneesOption] = DeriveJsonCodec.gen[IssueAssigneesOption]
