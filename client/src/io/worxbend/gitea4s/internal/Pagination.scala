package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Page
import zio.IO
import zio.stream.ZStream

object Pagination:
  /** Streams every item of a collection, starting at the first page. */
  def paginated[A](
      fetchPage: Int => IO[GiteaError, Page[A]]
  ): ZStream[Any, GiteaError, A] =
    paginatedFrom(1)(fetchPage)

  /** Streams every item from `start` onwards.
    *
    * This exists because the two paging fields on the public `*Params` types
    * were not honoured symmetrically: `limit` reached the request, while `page`
    * was overwritten with 1 on every call and so was silently discarded. A
    * caller resuming an interrupted crawl with `page = Some(7)` started again
    * at page 1 and re-emitted everything before it.
    */
  def paginatedFrom[A](start: Int)(
      fetchPage: Int => IO[GiteaError, Page[A]]
  ): ZStream[Any, GiteaError, A] =
    ZStream.paginateChunkZIO(math.max(1, start)) { page =>
      fetchPage(page).map { result =>
        // Only advance when the server signalled a next page *and* this page
        // returned data. The empty-page guard means a missing or misleading
        // `rel="next"`/total-count header can never make us fetch a trailing
        // empty page (or loop forever) past the end of the collection.
        val next = Option.when(result.hasNext && result.data.nonEmpty)(page + 1)
        (result.data, next)
      }
    }
