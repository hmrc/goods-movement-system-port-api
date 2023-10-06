/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord

import play.api.libs.json.{Json, OFormat}

case class GetControlledArrivalsGmrReducedResponse(
  gmrId:                   String,
  isUnaccompanied:         Boolean,
  reportToLocations:       Option[List[ReportLocations]],
  vehicleRegNum:           Option[String],
  trailerRegistrationNums: Option[List[String]],
  containerReferenceNums:  Option[List[String]],
  actualCrossing:          Crossing)

object GetControlledArrivalsGmrReducedResponse {

  implicit val format: OFormat[GetControlledArrivalsGmrReducedResponse] = Json.format

  def apply(response: GetControlledArrivalsGmrResponse): GetControlledArrivalsGmrReducedResponse = new GetControlledArrivalsGmrReducedResponse(
    gmrId                   = response.gmrId,
    isUnaccompanied         = response.isUnaccompanied,
    reportToLocations       = response.reportToLocations,
    vehicleRegNum           = response.vehicleRegNum,
    trailerRegistrationNums = response.trailerRegistrationNums,
    containerReferenceNums  = response.containerReferenceNums,
    actualCrossing          = response.actualCrossing
  )

}
