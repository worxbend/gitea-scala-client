package io.worxbend.gitea4s.model

enum ArchiveFormat(val pathSuffix: String):
  case Zip   extends ArchiveFormat(".zip")
  case TarGz extends ArchiveFormat(".tar.gz")
