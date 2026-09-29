package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedCatalogRequests:
  def listGitignoresTemplates(config: GiteaConfig): GiteaRequest[zio.Chunk[String]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listGitignoresTemplates, List("gitignore", "templates"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[String]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getGitignoreTemplateInfo(config: GiteaConfig, name: String): GiteaRequest[contract.GitignoreTemplateInfo] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getGitignoreTemplateInfo, List("gitignore", "templates", name.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.GitignoreTemplateInfo](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listLabelTemplates(config: GiteaConfig): GiteaRequest[zio.Chunk[String]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listLabelTemplates, List("label", "templates"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[String]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getLabelTemplateInfo(config: GiteaConfig, name: String): GiteaRequest[zio.Chunk[contract.LabelTemplate]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getLabelTemplateInfo, List("label", "templates", name.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.LabelTemplate]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def listLicenseTemplates(config: GiteaConfig): GiteaRequest[zio.Chunk[contract.LicensesTemplateListEntry]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.listLicenseTemplates, List("licenses"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.LicensesTemplateListEntry]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getLicenseTemplateInfo(config: GiteaConfig, name: String): GiteaRequest[contract.LicenseTemplateInfo] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getLicenseTemplateInfo, List("licenses", name.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.LicenseTemplateInfo](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def renderMarkdown(config: GiteaConfig, body: contract.MarkdownOption): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.renderMarkdown, List("markdown"),
      Nil, Some(body.toJson), response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Html)

  def renderMarkdownRaw(config: GiteaConfig, body: String): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.renderMarkdownRaw, List("markdown", "raw"),
      Nil, Some(body), response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.TextPlain, Accept.Html)

  def renderMarkup(config: GiteaConfig, body: contract.MarkupOption): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.renderMarkup, List("markup"),
      Nil, Some(body.toJson), response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Html)

  def repoGetByID(config: GiteaConfig, id: Long): GiteaRequest[contract.Repository] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.repoGetByID, List("repositories", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Repository](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getSigningKey(config: GiteaConfig): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getSigningKey, List("signing-key.gpg"),
      Nil, None, response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.TextPlain)

  def getSigningKeySSH(config: GiteaConfig): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getSigningKeySSH, List("signing-key.pub"),
      Nil, None, response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.TextPlain)

  def topicSearch(config: GiteaConfig, q: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[contract.TopicListResponse] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.topicSearch, List("topics", "search"),
      List(("q", q.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[contract.TopicListResponse](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getVersion(config: GiteaConfig): GiteaRequest[contract.ServerVersion] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getVersion, List("version"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.ServerVersion](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)
