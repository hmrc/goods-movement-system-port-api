/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.goodsmovementsystemportapi.models

import play.api.libs.json.{Json, OFormat}

case class SubscriptionFieldsResponse(clientId: String, apiContext: String, apiVersion: String, fieldsId: String, fields: Map[String, String])

object SubscriptionFieldsResponse {
  implicit val format: OFormat[SubscriptionFieldsResponse] = Json.format

  val portIdKey = "portId"
}
