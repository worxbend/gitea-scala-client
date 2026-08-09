package io.worxbend.gitea4s.model

import zio.json.*

/** The wire-value mapping every enum in this package needs, written once.
  *
  * Each companion previously repeated the same three declarations — a
  * `byJsonValue` map, a `fromString`, and a `given JsonCodec` built by
  * `transformOrFail` — differing only in the enum and the phrasing of the
  * error. Adding an enum meant copying them again, and the lenient decoder
  * below would otherwise have had to be copied eight times too.
  *
  * `private[model]` keeps it out of the Scala-visible API: nothing outside this
  * package can name it. It is still reachable in bytecode — Scala compiles
  * qualified-private to public — so it does appear in `api-snapshot/core.txt`,
  * as `ownedClient` and friends already do in the backend modules. It is not
  * part of the supported surface and carries no compatibility promise.
  */
private[model] final class JsonValueLookup[A](
    values: Array[A],
    label: String,
    jsonValue: A => String
):
  private val byJsonValue: Map[String, A] =
    values.map(value => jsonValue(value) -> value).toMap

  /** Strict: an unrecognised value is an error.
    *
    * This is what write positions need — sending a value the server will
    * reject is worth failing on, loudly and locally.
    */
  def fromString(value: String): Either[String, A] =
    byJsonValue.get(value).toRight(s"Unknown $label: $value")

  def codec: JsonCodec[A] =
    summon[JsonCodec[String]].transformOrFail(fromString, jsonValue)

  /** Lenient: an unrecognised value decodes as `None` rather than failing.
    *
    * Gitea adds enum members between minor versions, and these fields are read
    * positions on responses. Without this, one unrecognised string anywhere in
    * a page fails the decode of the *whole* page — `GiteaResponseMapper`
    * decodes a page as a single `Chunk[A]` — so the caller loses every other
    * item on it, and the `ZStream` from `Pagination` then fails, losing every
    * page after it too. Trading one unreadable field for a whole collection is
    * the wrong way round when the server is free to evolve independently.
    *
    * `null` and an absent field still decode as `None`, exactly as before; the
    * only behaviour that changes is a present-but-unknown string.
    *
    * Resolution note: this is more specific than zio-json's generic
    * `JsonDecoder.option`, so placing it in a companion makes it the instance
    * chosen for that enum's `Option` fields.
    */
  def lenientOptionDecoder: JsonDecoder[Option[A]] =
    JsonDecoder.option[String].map(_.flatMap(byJsonValue.get))
