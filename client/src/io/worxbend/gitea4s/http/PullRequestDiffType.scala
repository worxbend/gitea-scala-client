package io.worxbend.gitea4s.http

enum PullRequestDiffType(val pathValue: String):
  case Diff extends PullRequestDiffType("diff")
  case Patch extends PullRequestDiffType("patch")
