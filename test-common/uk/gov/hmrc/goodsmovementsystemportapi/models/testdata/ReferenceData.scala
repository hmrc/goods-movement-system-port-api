package uk.gov.hmrc.goodsmovementsystemportapi.models.testdata

import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.SupportedDirection.UK_OUTBOUND
import uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata.{Address, Carrier, GvmsReferenceData, InspectionType, Location, Port, Route, RuleFailure}

import java.time.LocalDate

object ReferenceData {

  private val routes = List(Route("4", "UK_INBOUND", LocalDate.parse("2022-04-16"), 6, 1, 10, None))

  private val ports = List(Port(1, Some("SOUTH"), LocalDate.parse("2022-04-16"), "HOLYHEAD", None, Some("567N"), Some("XY891"), "aaaaaaaa", None, None, "GB"))

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
}
