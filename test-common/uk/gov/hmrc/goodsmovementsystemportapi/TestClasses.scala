/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.goodsmovementsystemportapi.errorhandlers.NestedError

object TestClasses {

  case class JsonApiErrorResponse(code: String, message: String, errors: Option[List[NestedError]])
  object JsonApiErrorResponse {
    implicit val format: OFormat[JsonApiErrorResponse] = Json.format
  }

}
