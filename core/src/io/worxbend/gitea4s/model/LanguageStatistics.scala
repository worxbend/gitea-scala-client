package io.worxbend.gitea4s.model

import zio.json.*
import zio.json.ast.Json

final case class LanguageStatistics(bytesByLanguage: Map[String, Long] = Map.empty)

object LanguageStatistics:
  private val encoder: JsonEncoder[LanguageStatistics] =
    summon[JsonEncoder[Map[String, Long]]].contramap(_.bytesByLanguage)

  private val decoder: JsonDecoder[LanguageStatistics] =
    summon[JsonDecoder[Json]].mapOrFail {
      case Json.Obj(fields) =>
        fields
          .foldLeft[Either[String, Map[String, Long]]](Right(Map.empty)) {
            case (Right(bytesByLanguage), (language, Json.Num(value))) =>
              try Right(bytesByLanguage.updated(language, value.longValueExact()))
              catch case _: ArithmeticException =>
                Left(s"language byte count for '$language' must fit in a 64-bit integer")
            case (Right(_), (language, _)) =>
              Left(s"language byte count for '$language' must be a JSON integer")
            case (left @ Left(_), _) =>
              left
          }
          .map(LanguageStatistics.apply)
      case _ =>
        Left("LanguageStatistics must be a JSON object")
    }

  given JsonCodec[LanguageStatistics] =
    JsonCodec(encoder, decoder)
