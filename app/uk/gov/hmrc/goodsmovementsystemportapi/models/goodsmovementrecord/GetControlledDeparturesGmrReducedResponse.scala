/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord

import play.api.libs.json.{Json, OFormat}

case class GetControlledDeparturesGmrReducedResponse(
  gmrId:                   String,
  isUnaccompanied:         Boolean,
  reportToLocations:       Option[List[ReportLocations]],
  vehicleRegNum:           Option[String],
  trailerRegistrationNums: Option[List[String]],
  containerReferenceNums:  Option[List[String]],
  checkedInCrossing:       Crossing)

object GetControlledDeparturesGmrReducedResponse {

  implicit val format: OFormat[GetControlledDeparturesGmrReducedResponse] = Json.format

  def apply(response: GetControlledDeparturesGmrResponse): GetControlledDeparturesGmrReducedResponse = new GetControlledDeparturesGmrReducedResponse(
    gmrId                   = response.gmrId,
    isUnaccompanied         = response.isUnaccompanied,
    reportToLocations       = response.reportToLocations,
    vehicleRegNum           = response.vehicleRegNum,
    trailerRegistrationNums = response.trailerRegistrationNums,
    containerReferenceNums  = response.containerReferenceNums,
    checkedInCrossing       = response.checkedInCrossing
  )
}
