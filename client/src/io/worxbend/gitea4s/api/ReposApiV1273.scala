package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{Branch, CreateBranchRepoOption, CreateForkOption, CreateRepoOption, EditGitHookOption, EditRepoOption, GitHook, RenameBranchRepoOption, Repository, TransferRepoOption, UpdateBranchRepoOption}
import zio.IO
import zio.Chunk

/** Additive v1.27.3 repository surface; includes operations missing from the published v1 client. */
trait ReposApiV1273 extends ReposApi:
  def createForCurrentUser(body: CreateRepoOption): IO[GiteaError, Repository]

  def edit(owner: String, repo: String, body: EditRepoOption): IO[GiteaError, Repository]

  /** Permanently deletes the named repository. */
  def delete(owner: String, repo: String): IO[GiteaError, Unit]

  def fork(owner: String, repo: String, body: CreateForkOption): IO[GiteaError, Repository]

  def createBranch(owner: String, repo: String, body: CreateBranchRepoOption): IO[GiteaError, Branch]

  def deleteBranch(owner: String, repo: String, branch: String): IO[GiteaError, Unit]

  def getBranch(owner: String, repo: String, branch: String): IO[GiteaError, Branch]

  def updateBranch(owner: String, repo: String, branch: String, body: UpdateBranchRepoOption): IO[GiteaError, Unit]

  def renameBranch(owner: String, repo: String, branch: String, body: RenameBranchRepoOption): IO[GiteaError, Unit]

  def gitHooks(owner: String, repo: String): IO[GiteaError, Chunk[GitHook]]

  def gitHook(owner: String, repo: String, id: String): IO[GiteaError, GitHook]

  def editGitHook(owner: String, repo: String, id: String, body: EditGitHookOption): IO[GiteaError, GitHook]

  def deleteGitHook(owner: String, repo: String, id: String): IO[GiteaError, Unit]

  /** Requests transfer of repository ownership to `newOwner`. */
  def transfer(owner: String, repo: String, body: TransferRepoOption): IO[GiteaError, Repository]

  def acceptTransfer(owner: String, repo: String): IO[GiteaError, Repository]

  def rejectTransfer(owner: String, repo: String): IO[GiteaError, Repository]

  /** Returns false for a 404, which can also mean an inaccessible repository. */
  def isAssignee(owner: String, repo: String, assignee: String): IO[GiteaError, Boolean]
