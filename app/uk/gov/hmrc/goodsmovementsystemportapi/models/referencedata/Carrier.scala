/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import play.api.libs.json.{Json, OFormat}

case class Carrier(
  carrierId:   Int,
  carrierName: String,
  countryCode: Option[String]
)

object Carrier {
  implicit val format: OFormat[Carrier] = Json.format[Carrier]
}
