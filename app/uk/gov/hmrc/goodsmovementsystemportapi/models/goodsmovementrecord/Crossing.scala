/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
