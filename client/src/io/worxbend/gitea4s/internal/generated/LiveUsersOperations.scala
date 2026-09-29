package io.worxbend.gitea4s.internal.generated

import io.worxbend.gitea4s.GiteaConfig
import io.worxbend.gitea4s.api.generated.UsersOperations
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.GeneratedUsersRequests
import io.worxbend.gitea4s.internal.GiteaRequestExecutor
import io.worxbend.gitea4s.model.contract
import zio.IO

trait LiveUsersOperations extends UsersOperations:
  protected def config: GiteaConfig
  protected def executor: GiteaRequestExecutor

  override def userGetOauth2Application(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.OAuth2Application]] =
    executor.send(GeneratedUsersRequests.userGetOauth2Application(config, page, limit))

  override def userCreateOAuth2Application(body: contract.CreateOAuth2ApplicationOptions): IO[GiteaError, contract.OAuth2Application] =
    executor.send(GeneratedUsersRequests.userCreateOAuth2Application(config, body))

  override def userGetOAuth2Application(id: Long): IO[GiteaError, contract.OAuth2Application] =
    executor.send(GeneratedUsersRequests.userGetOAuth2Application(config, id))

  override def userDeleteOAuth2Application(id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userDeleteOAuth2Application(config, id))

  override def userUpdateOAuth2Application(id: Long, body: contract.CreateOAuth2ApplicationOptions): IO[GiteaError, contract.OAuth2Application] =
    executor.send(GeneratedUsersRequests.userUpdateOAuth2Application(config, id, body))

  override def userUpdateAvatar(body: contract.UpdateUserAvatarOption): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userUpdateAvatar(config, body))

  override def userDeleteAvatar(): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userDeleteAvatar(config))

  override def userListBlocks(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]] =
    executor.send(GeneratedUsersRequests.userListBlocks(config, page, limit))

  override def userCheckUserBlock(username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCheckUserBlock(config, username))

  override def userBlockUser(username: String, note: Option[String] = None): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userBlockUser(config, username, note))

  override def userUnblockUser(username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userUnblockUser(config, username))

  override def userListEmails(): IO[GiteaError, zio.Chunk[contract.Email]] =
    executor.send(GeneratedUsersRequests.userListEmails(config))

  override def userAddEmail(body: contract.CreateEmailOption): IO[GiteaError, zio.Chunk[contract.Email]] =
    executor.send(GeneratedUsersRequests.userAddEmail(config, body))

  override def userDeleteEmail(body: contract.DeleteEmailOption): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userDeleteEmail(config, body))

  override def userCurrentListFollowers(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]] =
    executor.send(GeneratedUsersRequests.userCurrentListFollowers(config, page, limit))

  override def userCurrentListFollowing(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.User]] =
    executor.send(GeneratedUsersRequests.userCurrentListFollowing(config, page, limit))

  override def userCurrentCheckFollowing(username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentCheckFollowing(config, username))

  override def userCurrentPutFollow(username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentPutFollow(config, username))

  override def userCurrentDeleteFollow(username: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentDeleteFollow(config, username))

  override def getVerificationToken(): IO[GiteaError, String] =
    executor.send(GeneratedUsersRequests.getVerificationToken(config))

  override def userVerifyGPGKey(): IO[GiteaError, contract.GPGKey] =
    executor.send(GeneratedUsersRequests.userVerifyGPGKey(config))

  override def userCurrentListGPGKeys(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.GPGKey]] =
    executor.send(GeneratedUsersRequests.userCurrentListGPGKeys(config, page, limit))

  override def userCurrentPostGPGKey(body: contract.CreateGPGKeyOption): IO[GiteaError, contract.GPGKey] =
    executor.send(GeneratedUsersRequests.userCurrentPostGPGKey(config, body))

  override def userCurrentGetGPGKey(id: Long): IO[GiteaError, contract.GPGKey] =
    executor.send(GeneratedUsersRequests.userCurrentGetGPGKey(config, id))

  override def userCurrentDeleteGPGKey(id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentDeleteGPGKey(config, id))

  override def userListHooks(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Hook]] =
    executor.send(GeneratedUsersRequests.userListHooks(config, page, limit))

  override def userCreateHook(body: contract.CreateHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedUsersRequests.userCreateHook(config, body))

  override def userGetHook(id: Long): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedUsersRequests.userGetHook(config, id))

  override def userDeleteHook(id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userDeleteHook(config, id))

  override def userEditHook(id: Long, body: contract.EditHookOption): IO[GiteaError, contract.Hook] =
    executor.send(GeneratedUsersRequests.userEditHook(config, id, body))

  override def userCurrentListKeys(fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.PublicKey]] =
    executor.send(GeneratedUsersRequests.userCurrentListKeys(config, fingerprint, page, limit))

  override def userCurrentPostKey(body: contract.CreateKeyOption): IO[GiteaError, contract.PublicKey] =
    executor.send(GeneratedUsersRequests.userCurrentPostKey(config, body))

  override def userCurrentGetKey(id: Long): IO[GiteaError, contract.PublicKey] =
    executor.send(GeneratedUsersRequests.userCurrentGetKey(config, id))

  override def userCurrentDeleteKey(id: Long): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentDeleteKey(config, id))

  override def orgListCurrentUserOrgs(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]] =
    executor.send(GeneratedUsersRequests.orgListCurrentUserOrgs(config, page, limit))

  override def userCurrentListRepos(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedUsersRequests.userCurrentListRepos(config, page, limit))

  override def getUserSettings(): IO[GiteaError, contract.UserSettings] =
    executor.send(GeneratedUsersRequests.getUserSettings(config))

  override def updateUserSettings(body: contract.UserSettingsOptions): IO[GiteaError, contract.UserSettings] =
    executor.send(GeneratedUsersRequests.updateUserSettings(config, body))

  override def userCurrentListStarred(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedUsersRequests.userCurrentListStarred(config, page, limit))

  override def userCurrentCheckStarring(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentCheckStarring(config, owner, repo))

  override def userCurrentPutStar(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentPutStar(config, owner, repo))

  override def userCurrentDeleteStar(owner: String, repo: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCurrentDeleteStar(config, owner, repo))

  override def userCurrentListSubscriptions(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedUsersRequests.userCurrentListSubscriptions(config, page, limit))

  override def userListTeams(page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Team]] =
    executor.send(GeneratedUsersRequests.userListTeams(config, page, limit))

  override def userCurrentTrackedTimes(page: Option[Int] = None, limit: Option[Int] = None, since: Option[java.time.Instant] = None, before: Option[java.time.Instant] = None): IO[GiteaError, zio.Chunk[contract.TrackedTime]] =
    executor.send(GeneratedUsersRequests.userCurrentTrackedTimes(config, page, limit, since, before))

  override def userListActivityFeeds(username: String, onlyPerformedBy: Option[Boolean] = None, date: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Activity]] =
    executor.send(GeneratedUsersRequests.userListActivityFeeds(config, username, onlyPerformedBy, date, page, limit))

  override def userCheckFollowing(username: String, target: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userCheckFollowing(config, username, target))

  override def userListGPGKeys(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.GPGKey]] =
    executor.send(GeneratedUsersRequests.userListGPGKeys(config, username, page, limit))

  override def userGetHeatmapData(username: String): IO[GiteaError, zio.Chunk[contract.UserHeatmapData]] =
    executor.send(GeneratedUsersRequests.userGetHeatmapData(config, username))

  override def userListKeys(username: String, fingerprint: Option[String] = None, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.PublicKey]] =
    executor.send(GeneratedUsersRequests.userListKeys(config, username, fingerprint, page, limit))

  override def orgListUserOrgs(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Organization]] =
    executor.send(GeneratedUsersRequests.orgListUserOrgs(config, username, page, limit))

  override def orgGetUserPermissions(username: String, org: String): IO[GiteaError, contract.OrganizationPermissions] =
    executor.send(GeneratedUsersRequests.orgGetUserPermissions(config, username, org))

  override def userListStarred(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedUsersRequests.userListStarred(config, username, page, limit))

  override def userListSubscriptions(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.Repository]] =
    executor.send(GeneratedUsersRequests.userListSubscriptions(config, username, page, limit))

  override def userGetTokens(username: String, page: Option[Int] = None, limit: Option[Int] = None): IO[GiteaError, zio.Chunk[contract.AccessToken]] =
    executor.send(GeneratedUsersRequests.userGetTokens(config, username, page, limit))

  override def userCreateToken(username: String, body: contract.CreateAccessTokenOption): IO[GiteaError, contract.AccessToken] =
    executor.send(GeneratedUsersRequests.userCreateToken(config, username, body))

  override def userDeleteAccessToken(username: String, token: String): IO[GiteaError, Unit] =
    executor.send(GeneratedUsersRequests.userDeleteAccessToken(config, username, token))
