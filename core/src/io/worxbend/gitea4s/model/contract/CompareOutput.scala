package io.worxbend.gitea4s.model.contract

enum CompareOutput(val value: String):
  case Diff extends CompareOutput("diff")
  case Patch extends CompareOutput("patch")
