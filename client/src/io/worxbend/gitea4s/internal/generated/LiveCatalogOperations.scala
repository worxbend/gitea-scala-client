package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.CatalogOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedCatalogRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveCatalogOperations extends CatalogOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def listGitignoresTemplates(): IO[GiteaError, zio.Chunk[String]] =
    executor.send(GeneratedCatalogRequests.listGitignoresTemplates(config))

  override def getGitignoreTemplateInfo(name: String): IO[GiteaError, contract.GitignoreTemplateInfo] =
    executor.send(GeneratedCatalogRequests.getGitignoreTemplateInfo(config, name))

  override def listLabelTemplates(): IO[GiteaError, zio.Chunk[String]] =
    executor.send(GeneratedCatalogRequests.listLabelTemplates(config))

  override def getLabelTemplateInfo(name: String): IO[GiteaError, zio.Chunk[contract.LabelTemplate]] =
    executor.send(GeneratedCatalogRequests.getLabelTemplateInfo(config, name))

  override def listLicenseTemplates(): IO[GiteaError, zio.Chunk[contract.LicensesTemplateListEntry]] =
    executor.send(GeneratedCatalogRequests.listLicenseTemplates(config))

  override def getLicenseTemplateInfo(name: String): IO[GiteaError, contract.LicenseTemplateInfo] =
    executor.send(GeneratedCatalogRequests.getLicenseTemplateInfo(config, name))

  override def renderMarkdown(body: contract.MarkdownOption): IO[GiteaError, String] =
    executor.send(GeneratedCatalogRequests.renderMarkdown(config, body))

  override def renderMarkdownRaw(body: String): IO[GiteaError, String] =
    executor.send(GeneratedCatalogRequests.renderMarkdownRaw(config, body))

  override def renderMarkup(body: contract.MarkupOption): IO[GiteaError, String] =
    executor.send(GeneratedCatalogRequests.renderMarkup(config, body))

  override def repoGetByID(id: Long): IO[GiteaError, contract.Repository] =
    executor.send(GeneratedCatalogRequests.repoGetByID(config, id))

  override def getSigningKey(): IO[GiteaError, String] =
    executor.send(GeneratedCatalogRequests.getSigningKey(config))

  override def getSigningKeySSH(): IO[GiteaError, String] =
    executor.send(GeneratedCatalogRequests.getSigningKeySSH(config))

  override def topicSearch(q: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, contract.TopicListResponse] =
    executor.send(GeneratedCatalogRequests.topicSearch(config, q, page, limit))

  override def getVersion(): IO[GiteaError, contract.ServerVersion] =
    executor.send(GeneratedCatalogRequests.getVersion(config))
