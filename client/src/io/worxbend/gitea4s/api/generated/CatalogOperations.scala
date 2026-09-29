package io.worxbend.gitea4s.api.generated

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.contract
import zio.IO

trait CatalogOperations:
  def listGitignoresTemplates(): IO[GiteaError, zio.Chunk[String]]
  def getGitignoreTemplateInfo(name: String): IO[GiteaError, contract.GitignoreTemplateInfo]
  def listLabelTemplates(): IO[GiteaError, zio.Chunk[String]]
  def getLabelTemplateInfo(name: String): IO[GiteaError, zio.Chunk[contract.LabelTemplate]]
  def listLicenseTemplates(): IO[GiteaError, zio.Chunk[contract.LicensesTemplateListEntry]]
  def getLicenseTemplateInfo(name: String): IO[GiteaError, contract.LicenseTemplateInfo]
  def renderMarkdown(body: contract.MarkdownOption): IO[GiteaError, String]
  def renderMarkdownRaw(body: String): IO[GiteaError, String]
  def renderMarkup(body: contract.MarkupOption): IO[GiteaError, String]
  def repoGetByID(id: Long): IO[GiteaError, contract.Repository]
  def getSigningKey(): IO[GiteaError, String]
  def getSigningKeySSH(): IO[GiteaError, String]
  def topicSearch(q: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.TopicListResponse]
  def getVersion(): IO[GiteaError, contract.ServerVersion]
