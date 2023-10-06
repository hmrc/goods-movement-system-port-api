/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}

case class Route(
  routeId:            String,
  routeDirection:     String,
  routeEffectiveFrom: LocalDate,
  departurePortId:    Int,
  arrivalPortId:      Int,
  carrierId:          Int,
  routeEffectiveTo:   Option[LocalDate]
)

object Route {
  implicit val format: OFormat[Route] = Json.format[Route]
}
