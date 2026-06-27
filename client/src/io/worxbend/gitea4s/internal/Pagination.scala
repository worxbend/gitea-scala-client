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
        // Only advance when the server signalled a next page *and* this page
        // returned data. The empty-page guard means a missing or misleading
        // `rel="next"`/total-count header can never make us fetch a trailing
        // empty page (or loop forever) past the end of the collection.
        val next = Option.when(result.hasNext && result.data.nonEmpty)(page + 1)
        (result.data, next)
      }
    }
