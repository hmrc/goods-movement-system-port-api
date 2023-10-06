/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import play.api.libs.json.{Format, Json, Reads, Writes}

case class Crossing(
  routeId:                String,
  localDateTimeOfArrival: LocalDateTime
)

object Crossing {
  implicit val customLocalDateTimeFormat: Format[LocalDateTime] = {
    val customLocalDateTimeWrites: Writes[LocalDateTime] = Writes.temporalWrites[LocalDateTime, DateTimeFormatter](
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    )

    val customLocalDateTimeReads: Reads[LocalDateTime] =
      Reads.localDateTimeReads[DateTimeFormatter](DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))

    Format(customLocalDateTimeReads, customLocalDateTimeWrites)
  }

  implicit val format: Format[Crossing] = Json.format
}
