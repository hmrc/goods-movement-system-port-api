/*
 * Copyright 2023 HM Revenue & Customs
 *
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
