package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.RepoListParams
import io.worxbend.gitea4s.model.{Branch, NewIssuePinsAllowed, Repository, Tag}
import zio.{Chunk, IO}
import zio.stream.ZStream

trait ReposApi:
  def get(owner: String, repo: String): IO[GiteaError, Repository]

  def list(owner: String, params: RepoListParams): ZStream[Any, GiteaError, Repository]

  def newIssuePinsAllowed(owner: String, repo: String): IO[GiteaError, NewIssuePinsAllowed]

  def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]]

  def branches(owner: String, repo: String): ZStream[Any, GiteaError, Branch]

  def tags(owner: String, repo: String): ZStream[Any, GiteaError, Tag]
