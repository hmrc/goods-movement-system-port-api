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
