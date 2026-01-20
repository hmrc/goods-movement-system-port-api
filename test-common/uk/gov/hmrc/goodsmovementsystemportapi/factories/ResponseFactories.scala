/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.goodsmovementsystemportapi.factories

import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{Crossing, GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse, ReportLocations}

import java.time.Instant
import uk.gov.hmrc.goodsmovementsystemportapi.models.testdata.TestData.*

object ResponseFactories {

  def fakeArrivalsGmrResponse(
    gmrId:                   String = "GMRA00003I3M",
    isUnaccompanied:         Boolean = false,
    inspectionRequired:      Option[Boolean] = None,
    reportToLocations:       Option[List[ReportLocations]] = None,
    vehicleRegNum:           Option[String] = None,
    trailerRegistrationNums: Option[List[String]] = None,
    containerReferenceNums:  Option[List[String]] = None,
    actualCrossing:          Crossing = crossing
  ): List[GetControlledArrivalsGmrResponse] = List(
    GetControlledArrivalsGmrResponse(
      gmrId = gmrId,
      isUnaccompanied = isUnaccompanied,
      inspectionRequired = inspectionRequired,
      reportToLocations = reportToLocations,
      vehicleRegNum = vehicleRegNum,
      trailerRegistrationNums = trailerRegistrationNums,
      containerReferenceNums = containerReferenceNums,
      actualCrossing = actualCrossing
    )
  )

  def fakeDeparaturesGmrResponse(
    gmrId:                   String = "GMRD00003I3M",
    isUnaccompanied:         Boolean = true,
    inspectionRequired:      Option[Boolean] = None,
    reportToLocations:       Option[List[ReportLocations]] = None,
    vehicleRegNum:           Option[String] = None,
    trailerRegistrationNums: Option[List[String]] = None,
    containerReferenceNums:  Option[List[String]] = None,
    checkedInCrossing:       Crossing = crossing
  ): List[GetControlledDeparturesGmrResponse] = List(
    GetControlledDeparturesGmrResponse(
      gmrId = gmrId,
      isUnaccompanied = isUnaccompanied,
      inspectionRequired = inspectionRequired,
      reportToLocations = reportToLocations,
      vehicleRegNum = vehicleRegNum,
      trailerRegistrationNums = trailerRegistrationNums,
      containerReferenceNums = containerReferenceNums,
      checkedInCrossing = checkedInCrossing
    )
  )

  def fakeDeparturesExtendedGmrResponse(
    gmrId:                   String = "GMRD00003I3M",
    isUnaccompanied:         Boolean = true,
    updatedDateTime:         Instant = Instant.parse("2021-07-03T16:33:51.000z"),
    inspectionRequired:      Option[Boolean] = None,
    reportToLocations:       Option[List[ReportLocations]] = None,
    vehicleRegNum:           Option[String] = None,
    trailerRegistrationNums: Option[List[String]] = None,
    containerReferenceNums:  Option[List[String]] = None,
    checkedInCrossing:       Crossing = crossing,
    readyToEmbark:           Option[Boolean] = None
  ): List[GetPortDepartureExpandedGmrResponse] = List(
    GetPortDepartureExpandedGmrResponse(
      gmrId = gmrId,
      isUnaccompanied = isUnaccompanied,
      updatedDateTime = updatedDateTime,
      inspectionRequired = inspectionRequired,
      reportToLocations = reportToLocations,
      vehicleRegNum = vehicleRegNum,
      trailerRegistrationNums = trailerRegistrationNums,
      containerReferenceNums = containerReferenceNums,
      checkedInCrossing = checkedInCrossing,
      readyToEmbark = readyToEmbark
    )
  )

  def fakeSubscriptionFieldsResponse(
    clientId:   String = "clientId",
    apiContext: String = "apiContext",
    apiVersion: String = "1.0",
    fieldsId:   String = "12345",
    fields:     Map[String, String] = Map("portId" -> "portId")
  ): SubscriptionFieldsResponse =
    SubscriptionFieldsResponse(
      clientId = clientId,
      apiContext = apiContext,
      apiVersion = apiVersion,
      fieldsId = fieldsId,
      fields = fields
    )

}
