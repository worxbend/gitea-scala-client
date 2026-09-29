package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Page
import zio.IO
import zio.ZIO
import zio.stream.ZStream

object Pagination:
  /** Streams every item of a collection, starting at the first page. */
  def paginated[A](
      fetchPage: Int => IO[GiteaError, Page[A]]
  ): ZStream[Any, GiteaError, A] =
    paginatedFrom(1)(fetchPage)

  /** Streams every item from `start` onwards.
    *
    * A page-offset scan of a live collection, not a snapshot: each page is a
    * separate request, so an insert or delete between two of them shifts
    * everything after it and an item can be emitted twice or skipped. Callers
    * that need exactly-once handling should key on the item's id.
    *
    * Starts below one are normalized to the first page. If the server reports
    * another page after `Int.MaxValue`, the stream emits the current page and
    * then fails with a `DecodeError` rather than issuing a negative page number.
    */
  def paginatedFrom[A](start: Int)(
      fetchPage: Int => IO[GiteaError, Page[A]]
  ): ZStream[Any, GiteaError, A] =
    ZStream.paginateChunkZIO(math.max(1, start).toLong) { page =>
      if page > Int.MaxValue then
        ZIO.fail(GiteaError.DecodeError("Pagination exceeded the maximum supported page number", ""))
      else
        fetchPage(page.toInt).map { result =>
          // An empty page terminates even when the server advertises another.
          val next = Option.when(result.hasNext && result.data.nonEmpty)(page + 1)
          (result.data, next)
        }
    }
