package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Page
import zio.IO
import zio.stream.ZStream

object Pagination:
  def paginated[A](
      fetchPage: Int => IO[GiteaError, Page[A]]
  ): ZStream[Any, GiteaError, A] =
    ZStream.paginateChunkZIO(1) { page =>
      fetchPage(page).map { result =>
        val next = Option.when(result.hasNext)(page + 1)
        (result.data, next)
      }
    }
