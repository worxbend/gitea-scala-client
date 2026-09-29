package io.worxbend.gitea4s.model

import zio.Chunk
import zio.json.*

import java.util.concurrent.ConcurrentHashMap

final case class Page[A](
    data: Chunk[A],
    totalCount: Option[Long],
    page: Int,
    pageSize: Int,
    hasNext: Boolean
)

object Page:
  /** Reuses a page codec for each element codec, avoiding repeated derivation.
    *
    * The cache holds at most 64 entries so callers that create fresh element
    * codecs cannot retain unbounded state. Once full, uncached element codecs
    * still derive a working page codec. Cache misses share a lock to keep the
    * bound strict and publish one codec per cached key; hits remain lock-free.
    */
  given [A](using elementCodec: JsonCodec[A]): JsonCodec[Page[A]] =
    val cached = cache.get(elementCodec)
    if cached ne null then cached.asInstanceOf[JsonCodec[Page[A]]]
    else
      cache.synchronized {
        val existing = cache.get(elementCodec)
        if existing ne null then existing.asInstanceOf[JsonCodec[Page[A]]]
        else
          val derived = DeriveJsonCodec.gen[Page[A]]
          // Capacity checks and insertion share the lock so concurrent misses
          // cannot exceed the bound or publish competing codecs for one key.
          if cache.size < maxCachedElementCodecs then
            val _ = cache.put(elementCodec, derived.asInstanceOf[JsonCodec[Page[Any]]])
          derived
      }

  private val maxCachedElementCodecs: Int = 64

  private val cache: ConcurrentHashMap[JsonCodec[?], JsonCodec[Page[Any]]] =
    new ConcurrentHashMap[JsonCodec[?], JsonCodec[Page[Any]]]()
