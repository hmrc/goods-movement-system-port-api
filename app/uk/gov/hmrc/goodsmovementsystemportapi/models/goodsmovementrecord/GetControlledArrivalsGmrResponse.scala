/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord

import play.api.libs.json.{Json, OFormat}

case class GetControlledArrivalsGmrResponse(
  gmrId:                   String,
  isUnaccompanied:         Boolean,
  inspectionRequired:      Option[Boolean],
  reportToLocations:       Option[List[ReportLocations]],
  vehicleRegNum:           Option[String],
  trailerRegistrationNums: Option[List[String]],
  containerReferenceNums:  Option[List[String]],
  actualCrossing:          Crossing)

object GetControlledArrivalsGmrResponse {

  implicit val format: OFormat[GetControlledArrivalsGmrResponse] = Json.format[GetControlledArrivalsGmrResponse]

}
