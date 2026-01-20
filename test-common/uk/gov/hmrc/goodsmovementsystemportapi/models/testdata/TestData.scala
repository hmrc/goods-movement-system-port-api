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

package uk.gov.hmrc.goodsmovementsystemportapi.models.testdata

import uk.gov.hmrc.goodsmovementsystemportapi.factories.ResponseFactories.{fakeArrivalsGmrResponse, fakeDeparaturesGmrResponse, fakeDeparturesExtendedGmrResponse, fakeSubscriptionFieldsResponse}
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{Crossing, GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime}

object TestData {

  val crossing: Crossing = Crossing(
    "C00011",
    LocalDateTime.parse("2021-08-11T10:58", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
  )

  val arrivalsGmrResponse: List[GetControlledArrivalsGmrResponse] =
    fakeArrivalsGmrResponse(
      inspectionRequired = Some(true),
      vehicleRegNum = Some("AB12 CDE")
    )

  val departuresGmrResponse: List[GetControlledDeparturesGmrResponse] =
    fakeDeparaturesGmrResponse(
      inspectionRequired = Some(true),
      trailerRegistrationNums = Some(List("AB12 CFR", "GB18 WRD")),
      containerReferenceNums = Some(List("ABXY2", "GB456")),
      checkedInCrossing = crossing
    )

  val departuresExtendedGmrResponse: List[GetPortDepartureExpandedGmrResponse] =
    fakeDeparturesExtendedGmrResponse(
      inspectionRequired = Some(true),
      trailerRegistrationNums = Some(List("AB12 CFR", "GB18 WRD")),
      containerReferenceNums = Some(List("ABXY2", "GB456"))
    )

  val subscriptionFieldsResponse: SubscriptionFieldsResponse =
    fakeSubscriptionFieldsResponse("clientId", "apiContext", "1.0", "12345", Map("portId" -> "portId"))

}
