package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class RepoTransfer(
    @jsonField("doer") doer: Option[User] = None,
    @jsonField("recipient") recipient: Option[User] = None,
    @jsonField("teams") teams: Option[List[Team]] = None
)

object RepoTransfer:
  given JsonCodec[RepoTransfer] = DeriveJsonCodec.gen[RepoTransfer]
