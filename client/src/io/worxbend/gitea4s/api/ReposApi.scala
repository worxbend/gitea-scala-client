package io.worxbend.gitea4s.api

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.http.RepoListParams
import io.worxbend.gitea4s.model.Repository
import zio.{Chunk, IO}
import zio.stream.ZStream

trait ReposApi:
  def get(owner: String, repo: String): IO[GiteaError, Repository]

  def list(owner: String, params: RepoListParams): ZStream[Any, GiteaError, Repository]

  def topics(owner: String, repo: String): IO[GiteaError, Chunk[String]]
