package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.{Accept, GiteaConfig}
import io.worxbend.gitea4s.model.contract
import zio.json.*

object GeneratedUsersRequests:
  def userGetOauth2Application(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.OAuth2Application]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userGetOauth2Application, List("user", "applications", "oauth2"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.OAuth2Application]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCreateOAuth2Application(config: GiteaConfig, body: contract.CreateOAuth2ApplicationOptions): GiteaRequest[contract.OAuth2Application] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCreateOAuth2Application, List("user", "applications", "oauth2"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.OAuth2Application](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userGetOAuth2Application(config: GiteaConfig, id: Long): GiteaRequest[contract.OAuth2Application] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userGetOAuth2Application, List("user", "applications", "oauth2", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.OAuth2Application](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userDeleteOAuth2Application(config: GiteaConfig, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userDeleteOAuth2Application, List("user", "applications", "oauth2", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userUpdateOAuth2Application(config: GiteaConfig, id: Long, body: contract.CreateOAuth2ApplicationOptions): GiteaRequest[contract.OAuth2Application] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userUpdateOAuth2Application, List("user", "applications", "oauth2", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.OAuth2Application](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userUpdateAvatar(config: GiteaConfig, body: contract.UpdateUserAvatarOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userUpdateAvatar, List("user", "avatar"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userDeleteAvatar(config: GiteaConfig): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userDeleteAvatar, List("user", "avatar"),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListBlocks(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.User]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListBlocks, List("user", "blocks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.User]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCheckUserBlock(config: GiteaConfig, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCheckUserBlock, List("user", "blocks", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userBlockUser(config: GiteaConfig, username: String, note: Option[String] = None): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userBlockUser, List("user", "blocks", username.toString),
      note.toList.map(value => ("note", value.toString)), None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userUnblockUser(config: GiteaConfig, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userUnblockUser, List("user", "blocks", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListEmails(config: GiteaConfig): GiteaRequest[zio.Chunk[contract.Email]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListEmails, List("user", "emails"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Email]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userAddEmail(config: GiteaConfig, body: contract.CreateEmailOption): GiteaRequest[zio.Chunk[contract.Email]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userAddEmail, List("user", "emails"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Email]](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userDeleteEmail(config: GiteaConfig, body: contract.DeleteEmailOption): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userDeleteEmail, List("user", "emails"),
      Nil, Some(body.toJson), response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListFollowers(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.User]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListFollowers, List("user", "followers"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.User]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListFollowing(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.User]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListFollowing, List("user", "following"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.User]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentCheckFollowing(config: GiteaConfig, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentCheckFollowing, List("user", "following", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentPutFollow(config: GiteaConfig, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentPutFollow, List("user", "following", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentDeleteFollow(config: GiteaConfig, username: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentDeleteFollow, List("user", "following", username.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getVerificationToken(config: GiteaConfig): GiteaRequest[String] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getVerificationToken, List("user", "gpg_key_token"),
      Nil, None, response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.TextPlain)

  def userVerifyGPGKey(config: GiteaConfig): GiteaRequest[contract.GPGKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userVerifyGPGKey, List("user", "gpg_key_verify"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.GPGKey](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListGPGKeys(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.GPGKey]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListGPGKeys, List("user", "gpg_keys"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.GPGKey]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentPostGPGKey(config: GiteaConfig, body: contract.CreateGPGKeyOption): GiteaRequest[contract.GPGKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentPostGPGKey, List("user", "gpg_keys"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.GPGKey](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentGetGPGKey(config: GiteaConfig, id: Long): GiteaRequest[contract.GPGKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentGetGPGKey, List("user", "gpg_keys", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.GPGKey](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentDeleteGPGKey(config: GiteaConfig, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentDeleteGPGKey, List("user", "gpg_keys", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListHooks(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Hook]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListHooks, List("user", "hooks"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Hook]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCreateHook(config: GiteaConfig, body: contract.CreateHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCreateHook, List("user", "hooks"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userGetHook(config: GiteaConfig, id: Long): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userGetHook, List("user", "hooks", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userDeleteHook(config: GiteaConfig, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userDeleteHook, List("user", "hooks", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userEditHook(config: GiteaConfig, id: Long, body: contract.EditHookOption): GiteaRequest[contract.Hook] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userEditHook, List("user", "hooks", id.toString),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.Hook](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListKeys(config: GiteaConfig, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.PublicKey]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListKeys, List("user", "keys"),
      fingerprint.toList.map(value => ("fingerprint", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.PublicKey]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentPostKey(config: GiteaConfig, body: contract.CreateKeyOption): GiteaRequest[contract.PublicKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentPostKey, List("user", "keys"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.PublicKey](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentGetKey(config: GiteaConfig, id: Long): GiteaRequest[contract.PublicKey] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentGetKey, List("user", "keys", id.toString),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.PublicKey](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentDeleteKey(config: GiteaConfig, id: Long): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentDeleteKey, List("user", "keys", id.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListCurrentUserOrgs(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Organization]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListCurrentUserOrgs, List("user", "orgs"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Organization]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListRepos(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListRepos, List("user", "repos"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def getUserSettings(config: GiteaConfig): GiteaRequest[contract.UserSettings] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.getUserSettings, List("user", "settings"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.UserSettings](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def updateUserSettings(config: GiteaConfig, body: contract.UserSettingsOptions): GiteaRequest[contract.UserSettings] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.updateUserSettings, List("user", "settings"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.UserSettings](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListStarred(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListStarred, List("user", "starred"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentCheckStarring(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentCheckStarring, List("user", "starred", owner.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentPutStar(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentPutStar, List("user", "starred", owner.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentDeleteStar(config: GiteaConfig, owner: String, repo: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentDeleteStar, List("user", "starred", owner.toString, repo.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentListSubscriptions(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentListSubscriptions, List("user", "subscriptions"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListTeams(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Team]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListTeams, List("user", "teams"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Team]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCurrentTrackedTimes(config: GiteaConfig, page: Option[Int] = None, limit: Option[Int] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None): GiteaRequest[zio.Chunk[contract.TrackedTime]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCurrentTrackedTimes, List("user", "times"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)) ++ since.toList.map(value => ("since", value.toString)) ++ before.toList.map(value => ("before", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.TrackedTime]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListActivityFeeds(config: GiteaConfig, username: String, onlyPerformedBy: Option[Boolean] = None, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Activity]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListActivityFeeds, List("users", username.toString, "activities", "feeds"),
      onlyPerformedBy.toList.map(value => ("only-performed-by", value.toString)) ++ date.toList.map(value => ("date", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Activity]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCheckFollowing(config: GiteaConfig, username: String, target: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCheckFollowing, List("users", username.toString, "following", target.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListGPGKeys(config: GiteaConfig, username: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.GPGKey]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListGPGKeys, List("users", username.toString, "gpg_keys"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.GPGKey]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userGetHeatmapData(config: GiteaConfig, username: String): GiteaRequest[zio.Chunk[contract.UserHeatmapData]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userGetHeatmapData, List("users", username.toString, "heatmap"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.UserHeatmapData]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListKeys(config: GiteaConfig, username: String, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.PublicKey]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListKeys, List("users", username.toString, "keys"),
      fingerprint.toList.map(value => ("fingerprint", value.toString)) ++ page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.PublicKey]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgListUserOrgs(config: GiteaConfig, username: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Organization]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgListUserOrgs, List("users", username.toString, "orgs"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Organization]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def orgGetUserPermissions(config: GiteaConfig, username: String, org: String): GiteaRequest[contract.OrganizationPermissions] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.orgGetUserPermissions, List("users", username.toString, "orgs", org.toString, "permissions"),
      Nil, None, response => GiteaResponseMapper.decodeJsonAt[contract.OrganizationPermissions](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListStarred(config: GiteaConfig, username: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListStarred, List("users", username.toString, "starred"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userListSubscriptions(config: GiteaConfig, username: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.Repository]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userListSubscriptions, List("users", username.toString, "subscriptions"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.Repository]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userGetTokens(config: GiteaConfig, username: String, page: Option[Int] = None, limit: Option[Int] = None): GiteaRequest[zio.Chunk[contract.AccessToken]] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userGetTokens, List("users", username.toString, "tokens"),
      page.toList.map(value => ("page", value.toString)) ++ limit.toList.map(value => ("limit", value.toString)), None, response => GiteaResponseMapper.decodeJsonAt[zio.Chunk[contract.AccessToken]](response, sttp.model.StatusCode(200)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userCreateToken(config: GiteaConfig, username: String, body: contract.CreateAccessTokenOption): GiteaRequest[contract.AccessToken] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userCreateToken, List("users", username.toString, "tokens"),
      Nil, Some(body.toJson), response => GiteaResponseMapper.decodeJsonAt[contract.AccessToken](response, sttp.model.StatusCode(201)), sttp.model.MediaType.ApplicationJson, Accept.Json)

  def userDeleteAccessToken(config: GiteaConfig, username: String, token: String): GiteaRequest[Unit] =
    GiteaRequests.fromContract(config, GeneratedEndpoints.userDeleteAccessToken, List("users", username.toString, "tokens", token.toString),
      Nil, None, response => if response.code.code == 204 then Right(()) else Left(GiteaResponseMapper.toError(response)), sttp.model.MediaType.ApplicationJson, Accept.Json)
