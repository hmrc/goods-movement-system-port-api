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

package uk.gov.hmrc.goodsmovementsystemportapi.models.testdata

import play.api.libs.json.Json
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.SupportedDirection.UK_OUTBOUND
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.{Address, Carrier, GvmsReferenceData, InspectionType, Location, Port, Route, RuleFailure}

import java.time.LocalDate

object ReferenceData {

  private val routes = List(Route("4", "UK_INBOUND", LocalDate.parse("2022-04-16"), 6, 1, 10, None))

  private val ports = List(
    Port(1, Some("SOUTH"), LocalDate.parse("2022-04-16"), "HOLYHEAD", None, Some("567N"), Some("XY891"), "aaaaaaaa", None, None, "GB")
  )

  private val carriers = List(Carrier(1, "P&O", Some("GB")))

  private val locations = Some(
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
  )

  private val inspectionTypes = Some(
    List(
      InspectionType(
        inspectionTypeId = "1",
        description = "CUSTOMS"
      )
    )
  )

  private val ruleFailures = List(
    RuleFailure("BR005", "A GMR can only be used in one crossing between two customs territiories"),
    RuleFailure("BR011", "This Customs declaration cannot be in more than one GMR")
  )

  implicit val referenceData: GvmsReferenceData =
    GvmsReferenceData(
      routes = routes,
      ports = ports,
      carriers = carriers,
      locations = locations,
      inspectionTypes = inspectionTypes,
      ruleFailures = ruleFailures
    )

  def referenceDataSummaryFromJson: GvmsReferenceData =
    Json.parse(getClass.getResourceAsStream(s"/referencedata/test-gvms-reference-data.json")).as[GvmsReferenceData]
}
