package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.Page
import zio.{Chunk, Ref, ZIO}
import zio.test.*

object PaginationSpec extends ZIOSpecDefault:
  private def page(values: Chunk[Int], pageNumber: Int, hasNext: Boolean): Page[Int] =
    Page(data = values, totalCount = None, page = pageNumber, pageSize = 2, hasNext = hasNext)

  def spec =
    suite("Pagination")(
      test("follows hasNext across non-empty pages") {
        val stream = Pagination.paginated { p =>
          ZIO.succeed(page(Chunk(p), p, hasNext = p < 3))
        }
        stream.runCollect.map(items => assertTrue(items == Chunk(1, 2, 3)))
      },
      test("stops after an empty page even when the page still claims hasNext") {
        for
          calls <- Ref.make(Chunk.empty[Int])
          stream = Pagination.paginated { p =>
            calls.update(_ :+ p) *>
              ZIO.succeed {
                if p == 1 then page(Chunk(1, 2), 1, hasNext = true)
                else page(Chunk.empty[Int], p, hasNext = true)
              }
          }
          items <- stream.runCollect
          fetched <- calls.get
        yield assertTrue(
          items == Chunk(1, 2),
          // page 1 (data) then page 2 (empty -> stop); page 3 is never requested
          fetched == Chunk(1, 2)
        )
      }
    )
