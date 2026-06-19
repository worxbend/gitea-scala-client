package io.worxbend.gitea4s.model

import java.time.Instant

import zio.json.*

final case class ContentsResponse(
    @jsonField("_links") links: Option[FileLinksResponse] = None,
    content: Option[String] = None,
    @jsonField("download_url") downloadUrl: Option[String] = None,
    encoding: Option[String] = None,
    @jsonField("git_url") gitUrl: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("last_author_date") lastAuthorDate: Option[Instant] = None,
    @jsonField("last_commit_message") lastCommitMessage: Option[String] = None,
    @jsonField("last_commit_sha") lastCommitSha: Option[String] = None,
    @jsonField("last_committer_date") lastCommitterDate: Option[Instant] = None,
    @jsonField("lfs_oid") lfsOid: Option[String] = None,
    @jsonField("lfs_size") lfsSize: Option[Long] = None,
    name: Option[String] = None,
    path: Option[String] = None,
    sha: Option[String] = None,
    size: Option[Long] = None,
    @jsonField("submodule_git_url") submoduleGitUrl: Option[String] = None,
    target: Option[String] = None,
    `type`: Option[String] = None,
    url: Option[String] = None
)

object ContentsResponse:
  given JsonCodec[ContentsResponse] = DeriveJsonCodec.gen[ContentsResponse]

final case class FileLinksResponse(
    git: Option[String] = None,
    html: Option[String] = None,
    self: Option[String] = None
)

object FileLinksResponse:
  given JsonCodec[FileLinksResponse] = DeriveJsonCodec.gen[FileLinksResponse]
