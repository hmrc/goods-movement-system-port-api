/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.goodsmovementrecord

import play.api.libs.json.{Json, OFormat}

case class ReportLocations(
  inspectionTypeId: String,
  locationIds:      List[String]
)

object ReportLocations {

  implicit val format: OFormat[ReportLocations] = Json.format
}
