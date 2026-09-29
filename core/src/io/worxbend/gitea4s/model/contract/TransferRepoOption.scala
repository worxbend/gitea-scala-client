package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class TransferRepoOption(
    @jsonField("new_owner") newOwner: String,
    @jsonField("team_ids") teamIds: Option[List[Long]] = None
)

object TransferRepoOption:
  given JsonCodec[TransferRepoOption] = DeriveJsonCodec.gen[TransferRepoOption]
