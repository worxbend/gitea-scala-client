package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class Branch(
    @jsonField("commit") commit: Option[PayloadCommit] = None,
    @jsonField("effective_branch_protection_name") effectiveBranchProtectionName: Option[String] = None,
    @jsonField("enable_status_check") enableStatusCheck: Option[Boolean] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("protected") `protected`: Option[Boolean] = None,
    @jsonField("required_approvals") requiredApprovals: Option[Long] = None,
    @jsonField("status_check_contexts") statusCheckContexts: Option[List[String]] = None,
    @jsonField("user_can_merge") userCanMerge: Option[Boolean] = None,
    @jsonField("user_can_push") userCanPush: Option[Boolean] = None
)

object Branch:
  given JsonCodec[Branch] = DeriveJsonCodec.gen[Branch]
