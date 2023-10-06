/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import play.api.libs.json.{Json, OFormat}

case class GvmsReferenceData(
  routes:          List[Route],
  ports:           List[Port],
  carriers:        List[Carrier],
  locations:       Option[List[Location]],
  inspectionTypes: Option[List[InspectionType]],
  ruleFailures:    List[RuleFailure]
)

object GvmsReferenceData {
  implicit val format: OFormat[GvmsReferenceData] = Json.format[GvmsReferenceData]
}
