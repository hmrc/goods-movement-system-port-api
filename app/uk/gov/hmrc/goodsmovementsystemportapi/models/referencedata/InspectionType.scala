/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import play.api.libs.json.{Json, OFormat}

case class InspectionType(
  inspectionTypeId: String,
  description:      String
)

object InspectionType {
  implicit val format: OFormat[InspectionType] = Json.format
}
