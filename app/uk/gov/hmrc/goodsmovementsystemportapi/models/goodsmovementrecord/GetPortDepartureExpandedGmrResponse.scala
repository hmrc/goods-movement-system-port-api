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

import play.api.libs.json.{Json, OFormat, Writes}
import play.api.libs.json.Writes.TemporalFormatter.PatternInstantFormatter

import java.time.Instant

case class GetPortDepartureExpandedGmrResponse(
  gmrId:                   String,
  isUnaccompanied:         Boolean,
  updatedDateTime:         Instant,
  inspectionRequired:      Option[Boolean],
  vehicleRegNum:           Option[String],
  trailerRegistrationNums: Option[List[String]],
  containerReferenceNums:  Option[List[String]],
  reportToLocations:       Option[List[ReportLocations]],
  checkedInCrossing:       Crossing,
  readyToEmbark:           Option[Boolean]
)

object GetPortDepartureExpandedGmrResponse {

  implicit val instantWrite: Writes[Instant] = {
    val formatter = PatternInstantFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSz")

    Writes.temporalWrites(formatter)
  }
  implicit val format: OFormat[GetPortDepartureExpandedGmrResponse] = Json.format

}
