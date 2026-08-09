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
  /** The codec for a page of `A`.
    *
    * Unlike the model codecs elsewhere in this package, this one takes a type
    * parameter, so the compiler cannot cache it in a lazy val: it becomes a
    * plain method whose body carries 58 allocations — the magnolia case-class
    * metadata, the field-name and default arrays, and 34 list cells — plus the
    * field-name trie its callees build. That work ran again on every summon, so
    * `pages.map(_.toJson)` re-derived the whole codec once per element.
    *
    * The derivation is now memoised against the element codec that produced it.
    * The cache is capped rather than unbounded: for the ordinary case the key is
    * one of this package's cached codec singletons, so the map holds one entry
    * per element type, but a caller whose own `given` builds a fresh codec per
    * summon would otherwise grow it without limit. Past the cap the codec is
    * simply derived as before, which is exactly the old behaviour.
    */
  given [A](using elementCodec: JsonCodec[A]): JsonCodec[Page[A]] =
    val cached = cache.get(elementCodec)
    if cached ne null then cached.asInstanceOf[JsonCodec[Page[A]]]
    else
      val derived = DeriveJsonCodec.gen[Page[A]]
      if cache.size < maxCachedElementCodecs then
        // Losing the race just means another thread cached an equivalent codec.
        val _ = cache.putIfAbsent(elementCodec, derived.asInstanceOf[JsonCodec[Page[Any]]])
      derived

  private val maxCachedElementCodecs: Int = 64

  private val cache: ConcurrentHashMap[JsonCodec[?], JsonCodec[Page[Any]]] =
    new ConcurrentHashMap[JsonCodec[?], JsonCodec[Page[Any]]]()
