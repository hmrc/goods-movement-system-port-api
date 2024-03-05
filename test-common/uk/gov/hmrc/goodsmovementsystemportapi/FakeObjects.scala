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

package uk.gov.hmrc.goodsmovementsystemportapi

import play.api.libs.json.Json

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime}
import uk.gov.hmrc.goodsmovementsystemportapi.models.SubscriptionFieldsResponse
import uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord.{Crossing, GetControlledArrivalsGmrResponse, GetControlledDeparturesGmrResponse, GetPortDepartureExpandedGmrResponse}
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.SupportedDirection.UK_OUTBOUND
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata._

trait FakeObjects {

  val portId = "abc"

  val crossing: Crossing = Crossing(
    "C00011",
    LocalDateTime.parse("2021-08-11T10:58", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
  )

  val arrivalsGmrResponse: List[GetControlledArrivalsGmrResponse] = List(
    GetControlledArrivalsGmrResponse(
      "GMRA00003I3M",
      isUnaccompanied = false,
      inspectionRequired = Some(true),
      reportToLocations = None,
      vehicleRegNum = Some("AB12 CDE"),
      trailerRegistrationNums = None,
      containerReferenceNums = None,
      actualCrossing = crossing
    )
  )

  val departuresGmrResponse: List[GetControlledDeparturesGmrResponse] = List(
    GetControlledDeparturesGmrResponse(
      "GMRD00003I3M",
      isUnaccompanied = true,
      inspectionRequired = Some(true),
      reportToLocations = None,
      vehicleRegNum = None,
      trailerRegistrationNums = Some(List("AB12 CFR", "GB18 WRD")),
      containerReferenceNums = Some(List("ABXY2", "GB456")),
      checkedInCrossing = crossing
    )
  )

  val departuresExtendedGmrResponse: List[GetPortDepartureExpandedGmrResponse] = List(
    GetPortDepartureExpandedGmrResponse(
      "GMRD00003I3M",
      isUnaccompanied = true,
      updatedDateTime = Instant.parse("2021-07-03T16:33:51.000z"),
      inspectionRequired = Some(true),
      reportToLocations = None,
      vehicleRegNum = None,
      trailerRegistrationNums = Some(List("AB12 CFR", "GB18 WRD")),
      containerReferenceNums = Some(List("ABXY2", "GB456")),
      checkedInCrossing = crossing,
      readyToEmbark = None
    )
  )

  def subscriptionFieldsResponse: SubscriptionFieldsResponse =
    SubscriptionFieldsResponse("clientId", "apiContext", "1.0", "12345", Map("portId" -> "portId"))

  val gmsReferenceDataSummary: GvmsReferenceData = GvmsReferenceData(
    routes = List(Route("4", "UK_INBOUND", LocalDate.parse("2022-04-16"), 6, 1, 10, None)),
    ports = List(Port(1, Some("SOUTH"), LocalDate.parse("2022-04-16"), "HOLYHEAD", None, Some("567N"), Some("XY891"), "aaaaaaaa", None, None, "GB")),
    carriers = List(Carrier(1, "P&O", Some("GB"))),
    locations = Some(
      List(
        Location(
          locationId = "1",
          locationDescription = "BELFAST LOCATION 1",
          address = Address(List("1 Shamrock Lane", "Waldo"), Some("Belfast"), "NI1 6JG"),
          locationType = "BCP",
          locationEffectiveFrom = LocalDate.parse("2022-04-16"),
          locationEffectiveTo = None,
          supportedDirections = List(UK_OUTBOUND),
          supportedInspectionTypeIds = List(
            "1"
          ),
          requiredInspectionLocations = List(
            1
          )
        )
      )
    ),
    inspectionTypes = Some(
      List(
        InspectionType(
          inspectionTypeId = "1",
          description = "CUSTOMS"
        )
      )
    ),
    ruleFailures = List(
      RuleFailure("BR005", "A GMR can only be used in one crossing between two customs territiories"),
      RuleFailure("BR011", "This Customs declaration cannot be in more than one GMR")
    )
  )

  def gmsReferenceDataSummay: GvmsReferenceData =
    Json.parse(getClass.getResourceAsStream(s"/referencedata/test-gvms-reference-data.json")).as[GvmsReferenceData]

}
