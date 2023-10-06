/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord

import play.api.libs.json.{Json, OFormat}

case class GetControlledDeparturesGmrResponse(
  gmrId:                   String,
  isUnaccompanied:         Boolean,
  inspectionRequired:      Option[Boolean],
  reportToLocations:       Option[List[ReportLocations]],
  vehicleRegNum:           Option[String],
  trailerRegistrationNums: Option[List[String]],
  containerReferenceNums:  Option[List[String]],
  checkedInCrossing:       Crossing)

object GetControlledDeparturesGmrResponse {

  implicit val format: OFormat[GetControlledDeparturesGmrResponse] = Json.format

}
