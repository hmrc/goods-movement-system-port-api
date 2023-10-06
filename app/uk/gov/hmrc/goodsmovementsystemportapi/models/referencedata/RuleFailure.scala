/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models.referencedata

import play.api.libs.json.{Json, OFormat}

case class RuleFailure(ruleId: String, ruleDescription: String)

object RuleFailure {
  implicit val format: OFormat[RuleFailure] = Json.format[RuleFailure]
}
