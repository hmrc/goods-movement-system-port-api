/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}

case class Port(
  portId:                           Int,
  portRegion:                       Option[String],
  portEffectiveFrom:                LocalDate,
  portDescription:                  String,
  timezoneId:                       Option[String],
  chiefPortCode:                    Option[String],
  cdsPortCode:                      Option[String],
  officeOfTransitCustomsOfficeCode: String,
  isOperatingArrivedExportsProcess: Option[Boolean],
  portEffectiveTo:                  Option[LocalDate],
  portCountryCode:                  String
)

object Port {
  implicit val format: OFormat[Port] = Json.format[Port]
}
