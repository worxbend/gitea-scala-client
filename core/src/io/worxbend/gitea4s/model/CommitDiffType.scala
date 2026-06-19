package io.worxbend.gitea4s.model

enum CommitDiffType(val pathValue: String):
  case diff extends CommitDiffType("diff")
  case patch extends CommitDiffType("patch")
