/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import java.time.LocalDate
import play.api.libs.json.{Format, JsResult, JsString, JsSuccess, JsValue, Json, OFormat}

sealed trait SupportedDirection

object SupportedDirection {

  case object UK_INBOUND extends SupportedDirection
  case object UK_OUTBOUND extends SupportedDirection
  case object GB_TO_NI extends SupportedDirection
  case object NI_TO_GB extends SupportedDirection

  implicit val format: Format[SupportedDirection] = new Format[SupportedDirection] {
    override def writes(o: SupportedDirection): JsValue = o match {
      case UK_INBOUND  => JsString("UK_INBOUND")
      case UK_OUTBOUND => JsString("UK_OUTBOUND")
      case GB_TO_NI    => JsString("GB_TO_NI")
      case NI_TO_GB    => JsString("NI_TO_GB")
    }

    @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
    override def reads(json: JsValue): JsResult[SupportedDirection] = json.as[String] match {
      case "UK_INBOUND"  => JsSuccess(UK_INBOUND)
      case "UK_OUTBOUND" => JsSuccess(UK_OUTBOUND)
      case "GB_TO_NI"    => JsSuccess(GB_TO_NI)
      case "NI_TO_GB"    => JsSuccess(NI_TO_GB)
    }
  }
}

case class Location(
  locationId:                  String,
  locationDescription:         String,
  address:                     Address,
  locationType:                String,
  locationEffectiveFrom:       LocalDate,
  locationEffectiveTo:         Option[LocalDate],
  supportedDirections:         List[SupportedDirection],
  supportedInspectionTypeIds:  List[String],
  requiredInspectionLocations: List[Int]
)

object Location {
  implicit val format: OFormat[Location] = Json.format[Location]
}

case class Address(
  lines:    List[String],
  town:     Option[String],
  postcode: String
)

object Address {
  implicit val format: OFormat[Address] = Json.format[Address]
}
