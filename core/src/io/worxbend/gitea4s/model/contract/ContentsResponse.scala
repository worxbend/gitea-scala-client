package io.worxbend.gitea4s.model.contract

import zio.json.*

final case class ContentsResponse(
    @jsonField("_links") Links: Option[FileLinksResponse] = None,
    @jsonField("content") content: Option[String] = None,
    @jsonField("download_url") downloadUrl: Option[String] = None,
    @jsonField("encoding") encoding: Option[String] = None,
    @jsonField("git_url") gitUrl: Option[String] = None,
    @jsonField("html_url") htmlUrl: Option[String] = None,
    @jsonField("last_author_date") lastAuthorDate: Option[java.time.Instant] = None,
    @jsonField("last_commit_message") lastCommitMessage: Option[String] = None,
    @jsonField("last_commit_sha") lastCommitSha: Option[String] = None,
    @jsonField("last_committer_date") lastCommitterDate: Option[java.time.Instant] = None,
    @jsonField("lfs_oid") lfsOid: Option[String] = None,
    @jsonField("lfs_size") lfsSize: Option[Long] = None,
    @jsonField("name") name: Option[String] = None,
    @jsonField("path") path: Option[String] = None,
    @jsonField("sha") sha: Option[String] = None,
    @jsonField("size") size: Option[Long] = None,
    @jsonField("submodule_git_url") submoduleGitUrl: Option[String] = None,
    @jsonField("target") target: Option[String] = None,
    @jsonField("type") `type`: Option[String] = None,
    @jsonField("url") url: Option[String] = None
)

object ContentsResponse:
  given JsonCodec[ContentsResponse] = DeriveJsonCodec.gen[ContentsResponse]
