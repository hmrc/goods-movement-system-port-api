/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers

import play.api.libs.json.{Json, OFormat}

case class NestedError(code: String, message: String, path: String)

object NestedError {
  implicit val format: OFormat[NestedError] = Json.format[NestedError]
}
